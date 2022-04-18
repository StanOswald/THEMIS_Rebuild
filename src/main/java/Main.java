import checker.ControlPlane;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.BGPMessage;
import process.HijackChecker;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.params.XReadGroupParams;
import redis.clients.jedis.resps.StreamEntry;
import dao.RedisPool;

import java.util.List;
import java.util.Map;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        int count = 0;
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        boolean flagFinish = false;
        int turn = 0;
        //xadd bgp_message * msg "{\"type\":\"A\",\"timestamp\":1635594484.4040916,\"peer_asn\":100,\"host\":\"\",\"path\":[100,100,200,300,400],\"communities\":[],\"prefix\":[\"10.0.0.0/8\",\"20.0.0.0/16\"]}"

        try (Jedis resource = RedisPool.getMessageConnection()) {

            ObjectMapper objectMapper = new ObjectMapper();
            List<Map.Entry<String, List<StreamEntry>>> list;
            XReadGroupParams blockParam = new XReadGroupParams().block(0);
            Map<String, StreamEntryID> entryIDMap = Map.of("bgp_message", StreamEntryID.UNRECEIVED_ENTRY);

            //Listening to new messages arrived (blocking)
            while ((list = resource.xreadGroup("jedis", "j1", blockParam, entryIDMap)) != null) {
                logger.info("Received message from streams.");
                logger.info("Message length: " + list.get(0).getValue().size());

                //For each message in the received list
                for (StreamEntry streamEntry : list.get(0).getValue()) {
                    String msgId = streamEntry.getID().toString();
                    String msgJSON = streamEntry.getFields().get("msg");

                    //Deserialize message to object
                    BGPMessage msgObj = objectMapper
                            .readerFor(BGPMessage.class)
                            .readValue(msgJSON);

                    logger.info(msgId);
                    ControlPlane controlPlane = new ControlPlane();
                    HijackChecker controlPlaneChecker = new HijackChecker(controlPlane, msgObj);

                    Thread t1 = new Thread(controlPlaneChecker);
                    t1.start();
                }
            }
        } catch (JedisConnectionException e) {
            logger.error("Failing to connect to Redis");
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            logger.error("BGP message parse error");
            e.printStackTrace();
        }
    }
}

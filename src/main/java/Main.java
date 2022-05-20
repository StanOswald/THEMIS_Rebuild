import adjudicator.BasicAdjudicator;
import adjudicator.MajorityRuling;
import adjudicator.MultimodeRuling;
import checker.ControlPlane;
import checker.DataPlane;
import checker.MixPlane;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.BGPMessage;
import process.DetectionResult;
import process.DetectorProcess;
import process.RulingResult;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.params.XReadGroupParams;
import redis.clients.jedis.resps.StreamEntry;
import dao.RedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    static ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {

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
                    String msgJSON = streamEntry.getFields().get("msg");

                    //Deserialize message to object
                    BGPMessage msgObj = objectMapper
                            .readerFor(BGPMessage.class)
                            .readValue(msgJSON);

                    //Clean loop's path for message
                    msgObj = msgObj.cleanLoops();
                    logger.info(msgObj.toString());

                    //Detector process initialized
                    DetectorProcess controlPlane = new DetectorProcess(new ControlPlane(), msgObj);
                    DetectorProcess dataPlane = new DetectorProcess(new DataPlane((int) (msgObj.getPath().size() * 0.8)), msgObj);
                    DetectorProcess mixPlane = new DetectorProcess(new MixPlane(20, 0.3, 0.5, 0.9), msgObj);

                    //Add detectors to list
                    List<DetectorProcess> detectorList = List.of(controlPlane, dataPlane, mixPlane);

                    List<Future<DetectionResult>> taskList = new ArrayList<>();
                    ArrayList<DetectionResult> resultList = new ArrayList<>();

                    //Submit every detection task to thread poll
                    //and add Future to taskList to call the result
                    detectorList.forEach(detector -> taskList.add(pool.submit(detector)));

                    taskList.forEach(task -> {
                        try {
                            //Obtain result from Future
                            resultList.add(task.get());
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    });

                    //Start adjudicate
                    BasicAdjudicator adjudicator = new MajorityRuling();
                    RulingResult ruling = adjudicator.ruling(resultList);

                    System.out.println(ruling);
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

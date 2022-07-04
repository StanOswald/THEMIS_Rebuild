package fzu.sdn504.THEMIS;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import fzu.sdn504.THEMIS.process.BGPMessage;
import redis.clients.jedis.Jedis;
import fzu.sdn504.THEMIS.repository.RedisPool;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
public class DataCollector extends WebSocketClient {

    private final Jedis pathConn = RedisPool.getPathConnection();
    private final Jedis histConn = RedisPool.getHistoryConnection();

    public DataCollector(URI serverUri) {
        super(serverUri);
        log.info("Data collector initialized, URI: " + serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("Websocket opened");
        this.send("""
                {
                    "type": "ris_subscribe",
                    "data": {
                        "type": "UPDATE",
                        "require": "announcements"
                    }
                }""");
    }

    @Override
    public void onMessage(String s) {
        log.info(s);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode data = mapper.readTree(s).get("data");
            RISMessage message = mapper.readerFor(RISMessage.class).readValue(data);

            List<Integer> p = message.path;
            int last = p.size() - 1;
            histConn.sadd(p.get(0) + "_" + p.get(last), p.toString());

            for (int i = 0; i < last; i++) {
                int ASn = p.get(i);
                int nextASn = p.get(i + 1);
                pathConn.sadd(ASn + "_" + nextASn, p.toString());
            }
        } catch (IOException | NullPointerException e) {
            log.info(s);
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        log.info("Websocket closed");
        histConn.close();
        pathConn.close();
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    public static void main(String[] args) throws URISyntaxException {
        DataCollector dataCollector = new DataCollector(new URI("wss://ris-live.ripe.net/v1/ws/"));
        dataCollector.connect();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class RISMessage {
        List<Integer> path;
        int peerASn;

        public RISMessage() {
        }

        @JsonSetter("path")
        public void setPath(List<Integer> path) {
            this.path = BGPMessage.cleanLoops(path);
        }

        @JsonSetter("peer_asn")
        public void setPeerASn(int peerASn) {
            this.peerASn = peerASn;
        }
    }
}


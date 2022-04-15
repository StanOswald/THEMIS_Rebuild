import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.WebSocket;

public class DataCollector extends WebSocketClient {

    private final Logger logger = LoggerFactory.getLogger(DataCollector.class);

    public DataCollector(URI serverUri) {
        super(serverUri);
        logger.info("Data collector initialized, URI: " + serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        logger.info("Websocket opened");
        this.send("{\"type\": \"ris_subscribe\"}");
    }

    @Override
    public void onMessage(String s) {
        System.out.println(s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        logger.info("Websocket closed");
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        DataCollector dataCollector = new DataCollector(new URI("wss://ris-live.ripe.net/v1/ws/"));
        dataCollector.connect();
    }

    private class RISParam{

    }
}


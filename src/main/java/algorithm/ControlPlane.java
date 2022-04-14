package algorithm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.BGPMessage;
import process.DetectionResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ControlPlane implements BasicChecker {

    Logger logger = LoggerFactory.getLogger(ControlPlane.class);

    List<String> getAnnouncedPrefixes(Integer ASn) {
        List<String> prefixesArr = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        //Get announced prefixes from RIPE
        String baseUrl = ("https://stat.ripe.net/data/announced-prefixes/data.json?resource=AS%s");
        String url = String.format(baseUrl, ASn);
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                //Read prefixes from response as JsonTree
                JsonNode prefixesNode = objectMapper
                        .readTree(response.getEntity().getContent())
                        .get("data").get("prefixes");

                for (JsonNode prefixNode : prefixesNode) {
                    //Add the prefix to prefixes array
                    prefixesArr.add(prefixNode.get("prefix").asText());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prefixesArr;
    }

    List<Integer> findChangePoint(List<Integer> localPath, List<Integer> newPath) {
        int localPathLen = localPath.size();
        int newPathLen = newPath.size();

        //Find the left change point
        int left = 0;
        while (left < localPathLen && left < newPathLen
                && localPath.get(left).equals(newPath.get(left))) {
            left++;
        }

        //Find the right change point
        int right = -1;
        while (right > -localPathLen && right > -newPathLen
                && localPath.get(localPathLen + right).equals(newPath.get(newPathLen + right))) {
            right--;
        }

        return left == right ? List.of(newPath.get(left)) : newPath.subList(left, right + 1);
    }

    List<Integer> findLocalPath(String prefix) {
        List<Integer> localPath = null;
        // Find path by prefix from local
        return localPath;
    }

    void saveNewPath(List<Integer> newPath) {
        //Save new path to local
    }

    @Override
    public DetectionResult hijackCheck(BGPMessage message) {
        logger.info("Received message: " + message);

        List<Integer> path = message.getPath();
        int lastASn = path.get(path.size() - 1);

        for (String prefix : message.getPrefixes()) {
            if (!getAnnouncedPrefixes(lastASn).contains(prefix))
                return new DetectionResult(true, "Prefix hijacking", lastASn, 1, 2, 3);

            List<Integer> localPath = findLocalPath(prefix);
            if (localPath == null) {
                saveNewPath(localPath);
                return new DetectionResult().setResult(false);
            }
            if (localPath.get(localPath.size() - 1) != lastASn)
                return new DetectionResult(true, null, lastASn, 1, 2, 3);

            List<Integer> changePoint = findChangePoint(localPath, path);
            List<Integer> newPath = path.subList(0, path.indexOf(changePoint.get(0)));

            if (newPath.size() == 0)
                return new DetectionResult().setResult(true).setType(1, 2, 3);
            else
                return new DetectionResult().setResult(true).setType(1, 2, 3);
        }
        return new DetectionResult().setResult(false);
    }
}

package algorithm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Detection {
    protected List<String> getAnnouncedPrefixes(Integer ASn) {
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

    protected boolean isSource(Integer ASn, List<String> prefixes) {
        List<String> announcedPrefixes = getAnnouncedPrefixes(ASn);
        for (String prefix : prefixes) {
            if (announcedPrefixes.contains(prefix))
                return true;
        }
        return false;
    }

    protected List<Integer> findChangePoint(List<Integer> localPath, List<Integer> newPath) {
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
        right+=newPathLen;
        return left == right ? List.of(newPath.get(left)) : newPath.subList(left, right + 1);
    }
}

package algorithm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.RedisMapper;
import dao.impl.RedisMapperImpl;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Detection {

    private final RedisMapper redisMapper = new RedisMapperImpl();

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

    protected boolean isNotSource(Integer ASn, List<String> prefixes) {
        List<String> announcedPrefixes = getAnnouncedPrefixes(ASn);
        for (String prefix : prefixes) {
            if (announcedPrefixes.contains(prefix))
                return false;
        }
        return true;
    }

    protected boolean connectivityCheck(int ASn) {
        String ip = redisMapper.getIP(ASn);
        try {
            InetAddress address = Inet4Address.getByName(ip);
            return address.isReachable(1000);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected boolean BGPCheck(List<Integer> path, List<String> prefixes) {
        int last = path.size() - 1;
        if (isNotSource(path.get(last), prefixes))
            return false;

        for (int i = 1; i < last; i++) {
            if (!redisMapper.isAdjacent(path.get(i), path.get(i - 1))
                    || !redisMapper.isPolicyRelativeCorrect(path.get(i), path.get(i - 1)))
                return false;
        }
        return true;
    }
}

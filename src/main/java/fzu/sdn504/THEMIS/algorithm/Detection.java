package fzu.sdn504.THEMIS.algorithm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fzu.sdn504.THEMIS.repository.RedisMapper;
import fzu.sdn504.THEMIS.repository.impl.RedisMapperJedisImpl;
import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Detection {

    private final RedisMapper redisMapper = new RedisMapperJedisImpl();
    static Logger logger = LoggerFactory.getLogger(Detection.class);

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

    protected boolean isAdjacent(int ASnX, int ASnY) {
        ObjectMapper objectMapper = new ObjectMapper();

        HashSet<Integer> set = new HashSet<>();
        String baseUrl = ("https://stat.ripe.net/data/asn-neighbours/data.json?resource=AS%s");
        String url = String.format(baseUrl, ASnY);
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                JsonNode neighboursNode = objectMapper
                        .readTree(response.getEntity().getContent())
                        .get("data").get("neighbours");

                for (JsonNode neighbour : neighboursNode) {
                    set.add(neighbour.get("asn").asInt());
                }
            }
            return set.contains(ASnX);
        } catch (IOException e) {
            return false;
        }
    }

    protected boolean connectivityCheck(int ASn) {
        List<String> announcedPrefixes = getAnnouncedPrefixes(ASn);
        if (announcedPrefixes.size() == 0) {
            return true;
        }
        boolean awaitRes = false;
        AtomicBoolean succeed = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);
        try {
            for (String prefix : announcedPrefixes) {
                IPAddress subnetAddr = new IPAddressString(prefix).toAddress();
                if (subnetAddr.toString().contains(":"))
                    continue;
                for (IPAddress ip : subnetAddr.getIterable()) {
                    InetAddress inet = ip.toInetAddress();
                    new Thread(() -> {
                        try {
                            if (inet.isReachable(500)) {
                                succeed.set(true);
                                latch.countDown();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();

                    if (succeed.get())
                        break;
                }
            }
            awaitRes = latch.await(5, TimeUnit.SECONDS);
        } catch (AddressStringException | InterruptedException e) {
            e.printStackTrace();
        }
        return succeed.get() || awaitRes;
    }

    protected boolean BGPCheck(List<Integer> path, List<String> prefixes) {
        int last = path.size() - 1;
        if (isNotSource(path.get(last), prefixes))
            return false;

        for (int i = 1; i < last; i++) {
            if (!isAdjacent(path.get(i), path.get(i + 1))
                    || !redisMapper.isPolicyRelativeCorrect(path.get(i), path.get(i + 1)))
                return false;
        }
        return true;
    }
}

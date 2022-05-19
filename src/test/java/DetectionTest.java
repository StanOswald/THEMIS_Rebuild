import algorithm.Detection;
import checker.BasicChecker;
import checker.ControlPlane;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DetectionTest extends Detection {

    @Test
    public void jsonTest() throws JsonProcessingException {
        String path = "[35600,57951,8309,61832,263327,263327,271225]";

        ObjectMapper mapper = new ObjectMapper();
        List<Integer> list = mapper.readerFor(List.class).readValue(path);

        System.out.println(list);
    }

    @Test
    public void FtCalcTest() {

        List<Integer> Ct = List.of(0, 0, 0, 0, 1, 0);
        List<Integer> Dt = List.of(1, 0, 0, 0, 0, 0);

        double Ft = 0, _D = 0;
        if (Ct.contains(1) && Dt.contains(1)) {
            int sumCt = Ct.stream().reduce(Integer::sum).orElse(0);
            int sumDt = Dt.stream().reduce(Integer::sum).orElse(0);
            double _C = (double) sumCt / Ct.size();
            _D = (double) sumDt / Dt.size();
            double final_D = _D;

            List<Double> C = Ct.stream().map(n -> n - _C).collect(Collectors.toList());
            List<Double> D = Dt.stream().map(n -> n - final_D).collect(Collectors.toList());

            double a = IntStream.range(0, C.size()).mapToDouble(i -> C.get(i) * D.get(i)).reduce(Double::sum).orElse(0.0);

            double b = C.stream().map(n -> n * n).reduce(Double::sum).orElse(0.0);
            double c = D.stream().map(n -> n * n).reduce(Double::sum).orElse(0.0);

            Ft = a / Math.sqrt(b * c);
        }

        System.out.println(Ft);
        System.out.println(_D);
    }

    @Test
    public void pingTest() throws IOException {
        InetAddress address = Inet4Address.getByName("127.0.0.1");
        boolean reachable = address.isReachable(10);
        assert reachable;
    }

    @Test
    public void getClassTest() {
        BasicChecker controlPlane = new ControlPlane();
        System.out.println(controlPlane.getClass().getSimpleName());
    }

    @Test
    public void testRedisConnection() {
        String testStr = "Test String";
        JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "192.168.186.128", 6379);
        System.out.println("Pool initialized: " + jedisPool.hashCode());

        Jedis resource = jedisPool.getResource();
        System.out.println("Get connection: " + resource);

        resource.lpush("test", testStr);
        String res = resource.lpop("test");
        System.out.println(testStr);

        assert testStr.equals(res);
        resource.close();
        jedisPool.close();
    }

    @Test
    public void connectivityCheckTest() {
        boolean b = connectivityCheck(4213);
        System.out.println(b);
    }

}

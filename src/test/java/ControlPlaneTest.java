import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import process.DetectionResult;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ControlPlaneTest {
    @Test
    public void test01() {
        System.out.println(
                new DetectionResult(
                        true,
                        null,
                        null,
                        1, 2, 3)
        );

        System.out.println(
                new DetectionResult()
                        .setResult(false)
                        .setType(0, 1, 3)
        );

        int[] arr = {1, 2, 3, 4};
        int size = arr.length;
        System.out.println(arr[size + (-1)]);
    }

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

            double a = IntStream
                    .range(0, C.size())
                    .mapToDouble(i -> C.get(i) * D.get(i))
                    .reduce(Double::sum).orElse(0.0);

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
}

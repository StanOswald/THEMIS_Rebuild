package checker;

import process.BGPMessage;
import process.DetectionResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MixPlane implements BasicChecker {

    Number xThreshold, yThreshold, normalThreshold;
    int T;

    public MixPlane(int T, Number xThreshold, Number yThreshold, Number normalThreshold) {
        this.T = T;
        this.xThreshold = xThreshold;
        this.yThreshold = yThreshold;
        this.normalThreshold = normalThreshold;
    }

    /**
     * def connectivity_check(asn) -> bool:
     * as_ip = get_as_ip(asn)
     * res = ping(as_ip, timeout=1, src_addr="192.168.122.1")
     * if res:
     * return True
     * return False
     */
    boolean connectivityCheck(int ASn) {
        return false;
    }

    boolean BGPCheck(List<Integer> path, List<Integer> prefix) {
        return false;
    }

    @Override
    public DetectionResult hijackCheck(BGPMessage message) {
        List<Integer> path = message.getPath();
        Integer srcAS = path.get(path.size() - 1);
        Integer dstAs = path.get(0);

        List<Integer> Ct = new ArrayList<>();
        List<Integer> Dt = new ArrayList<>();

        long start = new Date().getTime();

        while (new Date().getTime() - start <= T) {
            if (connectivityCheck(path.get(new Random().nextInt(path.size()))))
                Ct.add(1);
            else
                Ct.add(0);
        }
        return null;
    }
}

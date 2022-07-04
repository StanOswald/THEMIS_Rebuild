package fzu.sdn504.THEMIS.checker;

import fzu.sdn504.THEMIS.algorithm.Detection;
import fzu.sdn504.THEMIS.process.BGPMessage;
import fzu.sdn504.THEMIS.process.DetectionResult;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MixPlane extends Detection implements BasicChecker {

    int T;
    double xThreshold, yThreshold, normalThreshold;

    public MixPlane(int T, double xThreshold, double yThreshold, double normalThreshold) {
        this.T = T * 1000;
        this.xThreshold = xThreshold;
        this.yThreshold = yThreshold;
        this.normalThreshold = normalThreshold;
    }

    @Override
    public DetectionResult hijackCheck(BGPMessage message) {
        DetectionResult result = new DetectionResult(this.getClass());
        List<Integer> path = message.getPath();

        List<Integer> Ct = new ArrayList<>();
        List<Integer> Dt = new ArrayList<>();

        long start = new Date().getTime();

        while (new Date().getTime() - start <= T) {
            if (connectivityCheck(path.get(new Random().nextInt(path.size()))))
                Ct.add(1);
            else
                Ct.add(0);
            if (BGPCheck(path, message.getPrefixes()))
                Dt.add(1);
            else
                Dt.add(0);
        }

        if (Ct.size() != Dt.size()) {
            try {
                throw new Exception("The C-plane and D-plane data length is not equal!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        double Ft = 0, _D;
        if (Ct.contains(1) && Dt.contains(1)) {
            int sumCt = Ct.stream().reduce(Integer::sum).orElse(0);
            int sumDt = Dt.stream().reduce(Integer::sum).orElse(0);
            double _C = (double) sumCt / Ct.size();
            _D = (double) sumDt / Dt.size();
            double final_D = _D;

            List<Double> C = Ct.stream().map(n -> n - _C).toList();
            List<Double> D = Dt.stream().map(n -> n - final_D).toList();

            double a = IntStream
                    .range(0, C.size())
                    .mapToDouble(i -> C.get(i) * D.get(i))
                    .reduce(Double::sum).orElse(0.0);

            double b = C.stream().map(n -> n * n).reduce(Double::sum).orElse(0.0);
            double c = D.stream().map(n -> n * n).reduce(Double::sum).orElse(0.0);

            Ft = a / Math.sqrt(b * c);
        }

        if (xThreshold <= Ft && Ft < normalThreshold)
            return result.setResult(true);
        else if (-normalThreshold < Ft && Ft <= -xThreshold)
            return result.setResult(true);
        else if (-xThreshold < Ft && Ft < xThreshold)
            return result.setResult(true);
        return result.setResult(false);
    }

    @Override
    public String toString() {
        return "MixPlane{" +
                "T=" + T +
                ", xThreshold=" + xThreshold +
                ", yThreshold=" + yThreshold +
                ", normalThreshold=" + normalThreshold +
                '}';
    }
}

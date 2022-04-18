package checker;

import algorithm.Detection;
import process.BGPMessage;
import process.DetectionResult;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MixPlane extends Detection implements BasicChecker {

    int T;
    double xThreshold, yThreshold, normalThreshold;

    public MixPlane(int T, double xThreshold, double yThreshold, double normalThreshold) {
        this.T = T;
        this.xThreshold = xThreshold;
        this.yThreshold = yThreshold;
        this.normalThreshold = normalThreshold;
    }

    @Override
    public DetectionResult hijackCheck(BGPMessage message) {
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
        DetectionResult result = new DetectionResult();
        if (xThreshold <= Ft && Ft < normalThreshold)
            return result.setResult(true).setType(1, 2, 3);
        else if (-normalThreshold < Ft && Ft <= xThreshold)
            return result.setResult(true).setType(7);
        else if (-xThreshold < Ft && Ft < xThreshold)
            if (_D >= yThreshold)
                return result.setResult(true).setType(7);
            else
                return result.setResult(true).setType(6);
        return result.setResult(false);
    }
}

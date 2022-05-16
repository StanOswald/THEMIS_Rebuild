package checker;

import algorithm.Detection;
import process.BGPMessage;
import process.DetectionResult;

import java.util.List;

public class DataPlane extends Detection implements BasicChecker {

    int threshold;

    public DataPlane(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public DetectionResult hijackCheck(BGPMessage message) {
        DetectionResult result = new DetectionResult(this.getClass());
        int u = 0;
        List<Integer> path = message.getPath();
        for (Integer ASn : path) {
            if (!connectivityCheck(ASn)) {
                u++;
                if (u > threshold)
                    return result.setResult(true);
            }
        }
        return result.setResult(false);
    }

    @Override
    public String toString() {
        return "DataPlane{" +
                "threshold=" + threshold +
                '}';
    }
}

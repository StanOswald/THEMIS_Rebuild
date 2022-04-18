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
        int u = 0;
        List<Integer> path = message.getPath();
        for (Integer ASn : path) {
            if (!connectivityCheck(ASn)) {
                u++;
                if (u > threshold)
                    return new DetectionResult().setResult(true).setType(6);
            }
        }
        return new DetectionResult().setResult(false);
    }
}

package checker;

import process.BGPMessage;
import process.DetectionResult;

public class DataPlane implements BasicChecker {

    @Override
    public DetectionResult hijackCheck(BGPMessage message) {
        return null;
    }
}

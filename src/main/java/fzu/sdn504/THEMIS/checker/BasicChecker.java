package fzu.sdn504.THEMIS.checker;

import fzu.sdn504.THEMIS.process.BGPMessage;
import fzu.sdn504.THEMIS.process.DetectionResult;

public interface BasicChecker {
    DetectionResult hijackCheck(BGPMessage message);
}

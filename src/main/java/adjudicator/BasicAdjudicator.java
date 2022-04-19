package adjudicator;

import process.DetectionResult;

import java.util.List;

public interface BasicAdjudicator {
    DetectionResult adjudicate(List<DetectionResult> resultList);
}

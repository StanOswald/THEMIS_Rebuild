package adjudicator;

import process.DetectionResult;
import process.RulingResult;

import java.util.List;

public interface BasicAdjudicator {
    RulingResult ruling(List<DetectionResult> dataList);
}

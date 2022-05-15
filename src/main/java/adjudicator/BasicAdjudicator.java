package adjudicator;

import process.DetectionResult;

import java.util.List;
import java.util.concurrent.Future;

public interface BasicAdjudicator {


    DetectionResult adjudicate(List<Future<DetectionResult>> taskList);
}

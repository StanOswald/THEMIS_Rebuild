package process;

import checker.BasicChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class DetectorProcess implements Callable<DetectionResult> {

    Logger logger = LoggerFactory.getLogger(DetectorProcess.class);

    private final BasicChecker checker;
    private final BGPMessage message;
    private final String checkerName;
    private Thread t;

    public DetectorProcess(BasicChecker checker, BGPMessage message) {
        this.checker = checker;
        this.message = message;
        checkerName = checker.getClass().getSimpleName();
        logger.info(checkerName + " initialized: " + checker);
    }

    @Override
    public DetectionResult call() {
        logger.info("Running " + checkerName + "...");
        DetectionResult detectionResult = checker.hijackCheck(message);
        logger.info("Received result from " + checkerName + ": " + detectionResult);
        return detectionResult;
    }
}

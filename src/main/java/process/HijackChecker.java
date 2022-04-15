package process;

import checker.BasicChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HijackChecker implements Runnable {

    Logger logger = LoggerFactory.getLogger(HijackChecker.class);

    private final BasicChecker checker;
    private final BGPMessage message;
    private Thread t;

    public HijackChecker(BasicChecker checker, BGPMessage message) {
        this.checker = checker;
        this.message = message;
    }

    @Override
    public void run() {
        logger.info("Running Checker...");
        DetectionResult detectionResult = checker.hijackCheck(message);
        logger.info("Received result from checker: " + detectionResult);
    }
}

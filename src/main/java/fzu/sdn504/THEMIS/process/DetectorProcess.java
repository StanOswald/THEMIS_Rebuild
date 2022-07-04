package fzu.sdn504.THEMIS.process;

import fzu.sdn504.THEMIS.checker.BasicChecker;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

@Slf4j
public class DetectorProcess implements Callable<DetectionResult> {


    private final BasicChecker checker;
    private final BGPMessage message;
    private final String checkerName;

    public DetectorProcess(BasicChecker checker, BGPMessage message) {
        this.checker = checker;
        this.message = message;
        checkerName = checker.getClass().getSimpleName();
        log.info(checkerName + " initialized: " + checker);
    }

    @Override
    public DetectionResult call() {
        log.info("Running " + checkerName + "...");
        DetectionResult detectionResult = checker.hijackCheck(message);
        log.info("Received result from " + checkerName + ": " + detectionResult);
        return detectionResult;
    }
}

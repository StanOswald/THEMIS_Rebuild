package fzu.sdn504.THEMIS.process;

import fzu.sdn504.THEMIS.checker.BasicChecker;

public class DetectionResult {
    Boolean result;
    long timestamp;
    Class<? extends BasicChecker> checker;

    public DetectionResult(Class<? extends BasicChecker> checker) {
        timestamp = System.currentTimeMillis();
        this.checker = checker;
    }

    public boolean equals(boolean b) {
        return b == result;
    }

    public DetectionResult setResult(Boolean result) {
        timestamp = System.currentTimeMillis();
        this.result = result;
        return this;
    }

    public Class<? extends BasicChecker> getChecker() {
        return checker;
    }

    @Override
    public String toString() {
        return "DetectionResult{" +
                "result=" + result +
                ", timestamp=" + timestamp +
                ", fzu.sdn504.THEMIS.checker=" + checker +
                '}';
    }
}

package process;

import java.util.Arrays;
import java.util.Date;

public class DetectionResult {
    Boolean result;
    String victim;
    Integer attacker;
    int[] type;
    Date time;

    public DetectionResult() {
        this.time = new Date();
    }

    public DetectionResult(Boolean result, String victim, Integer attacker, int... type) {
        this.result = result;
        this.victim = victim;
        this.attacker = attacker;
        this.type = type;
        this.time = new Date();
    }

    public DetectionResult setResult(Boolean result) {
        this.result = result;
        return this;
    }

    public DetectionResult setVictim(String victim) {
        this.victim = victim;
        return this;
    }

    public DetectionResult setAttacker(Integer attacker) {
        this.attacker = attacker;
        return this;
    }

    public DetectionResult setType(int... type) {
        this.type = type;
        return this;
    }

    public Boolean getResult() {
        return result;
    }

    public String getVictim() {
        return victim;
    }

    public Integer getAttacker() {
        return attacker;
    }

    public int[] getType() {
        return type;
    }

    public Date getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "DetectionResult{" +
                "result=" + result +
                ", victim='" + victim + '\'' +
                ", attacker=" + attacker +
                ", type=" + Arrays.toString(type) +
                ", time=" + time.getTime() +
                '}';
    }
}

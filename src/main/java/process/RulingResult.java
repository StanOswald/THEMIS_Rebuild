package process;

import checker.BasicChecker;

import java.util.List;

public class RulingResult {
    Boolean result;
    List<Class<? extends BasicChecker>> suspiciousList;

    public void setResult(Boolean result) {
        this.result = result;
    }

    public void setSuspiciousList(List<Class<? extends BasicChecker>> suspiciousList) {
        this.suspiciousList = suspiciousList;
    }

    @Override
    public String toString() {
        return "RulingResult{" +
                "result=" + result +
                ", suspiciousList=" + suspiciousList +
                '}';
    }
}

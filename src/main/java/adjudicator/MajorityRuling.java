package adjudicator;

import checker.BasicChecker;
import process.DetectionResult;
import process.RulingResult;

import java.util.ArrayList;
import java.util.List;

public class MajorityRuling implements BasicAdjudicator {
    @Override
    public RulingResult ruling(List<DetectionResult> dataList) {
        RulingResult result = new RulingResult();

        ArrayList<DetectionResult> trueList = new ArrayList<>();
        ArrayList<DetectionResult> falseList = new ArrayList<>();

        dataList.forEach(data -> {
                    if (data.equals(true)) {
                        trueList.add(data);
                    } else {
                        falseList.add(data);
                    }
                }
        );

        result.setResult(trueList.size() > falseList.size());
        result.setSuspiciousList(checkerList(trueList.size() < falseList.size() ? trueList : falseList));
        return result;
    }

    private List<Class<? extends BasicChecker>> checkerList(List<DetectionResult> list) {
        List<Class<? extends BasicChecker>> suspiciousList = new ArrayList<>();
        list.forEach(result -> suspiciousList.add(result.getChecker()));
        return suspiciousList;
    }
}

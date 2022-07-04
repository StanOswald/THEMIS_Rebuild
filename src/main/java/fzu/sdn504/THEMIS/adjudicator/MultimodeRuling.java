package fzu.sdn504.THEMIS.adjudicator;

import fzu.sdn504.THEMIS.process.DetectionResult;
import fzu.sdn504.THEMIS.process.RulingResult;

import java.util.LinkedList;
import java.util.List;

public class MultimodeRuling implements BasicAdjudicator {

    public RulingResult ruling(List<DetectionResult> dataList) {
        return null;
    }

    public LinkedList<int[]> resIndexPairs(int n) {
        LinkedList<int[]> res = new LinkedList<>();
        for (int i = 1; i < n; i++) {
            for (int j = i + 1; j <= n; j++)
                res.add(new int[]{i, j});
        }
        return res;
    }
}

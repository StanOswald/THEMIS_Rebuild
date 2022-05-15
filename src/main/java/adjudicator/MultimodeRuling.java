package adjudicator;

import process.DetectionResult;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MultimodeRuling implements BasicAdjudicator {

    int count = 0;
    int round;
    boolean[] flag = new boolean[3];
    boolean flagFinish = false;
    int turn = 0;

    @Override
    public DetectionResult adjudicate(List<Future<DetectionResult>> taskList) {
        turn++;
        int taskLen = taskList.size();

        round = (int) Math.ceil(taskLen / 2.0);
        while (count < round)
            for (int i = 0; i < taskLen; i++)
                if (taskList.get(i).isDone() && !flag[i]) {
                    count++;
                    flag[i] = true;
                }

        ArrayList<DetectionResult> resultData = new ArrayList<>();
        if (count == round) {
            for (int[] pair : resIndexPairs(taskLen)) {
                int n = pair[0];
                int m = pair[1];

                if (flag[n] == flag[m]) {
                    try {
                        resultData.add(taskList.get(n).get());
                        resultData.add(taskList.get(m).get());
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
            ruling(resultData);
        }
        return null;
    }

    private DetectionResult ruling(ArrayList<DetectionResult> resultData) {
        if(count==round){
            if(resultData.get(0).getResult()==resultData.get(0).getResult())
                return new DetectionResult();
        }
        return new DetectionResult();
    }

    LinkedList<int[]> resIndexPairs(int n) {
        LinkedList<int[]> res = new LinkedList<>();
        for (int i = 1; i < n; i++) {
            for (int j = i + 1; j <= n; j++)
                res.add(new int[]{i, j});
        }
        return res;
    }
}

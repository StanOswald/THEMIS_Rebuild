package fzu.sdn504.THEMIS.adjudicator;

import fzu.sdn504.THEMIS.process.DetectionResult;
import fzu.sdn504.THEMIS.process.RulingResult;

import java.util.List;

public interface BasicAdjudicator {
    RulingResult ruling(List<DetectionResult> dataList);
}

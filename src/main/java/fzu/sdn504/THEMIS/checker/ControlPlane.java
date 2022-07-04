package fzu.sdn504.THEMIS.checker;

import fzu.sdn504.THEMIS.algorithm.Detection;
import fzu.sdn504.THEMIS.repository.RedisMapper;
import fzu.sdn504.THEMIS.repository.impl.RedisMapperJedisImpl;
import fzu.sdn504.THEMIS.process.BGPMessage;
import fzu.sdn504.THEMIS.process.DetectionResult;

import java.util.List;

public class ControlPlane extends Detection implements BasicChecker {

    private static final RedisMapper mapper = new RedisMapperJedisImpl();

    @Override
    public DetectionResult hijackCheck(BGPMessage message) {
        DetectionResult result = new DetectionResult(this.getClass());
        List<Integer> path = message.getPath();
        int lastASn = path.get(path.size() - 1);

        if (isNotSource(lastASn, message.getPrefixes()))
            return result.setResult(true);

        for (String prefix : message.getPrefixes()) {
            List<Integer> localPath = mapper.findLocalPath(prefix);
            if (localPath == null) {
                mapper.saveNewPath(prefix, path);
                return result.setResult(false);
            }
            if (localPath.get(localPath.size() - 1) != lastASn)
                return result.setResult(true);

            return result.setResult(true);
        }
        return result.setResult(false);
    }

    @Override
    public String toString() {
        return "ControlPlane{}";
    }
}

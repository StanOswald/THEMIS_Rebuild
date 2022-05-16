package checker;

import algorithm.Detection;
import dao.RedisMapper;
import dao.impl.RedisMapperImpl;
import process.BGPMessage;
import process.DetectionResult;

import java.util.List;

public class ControlPlane extends Detection implements BasicChecker {

    private static final RedisMapper mapper = new RedisMapperImpl();

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

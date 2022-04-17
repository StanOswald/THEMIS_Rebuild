package checker;

import algorithm.Detection;
import dao.RedisMapper;
import dao.impl.RedisMapperImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.BGPMessage;
import process.DetectionResult;

import java.util.List;

public class ControlPlane extends Detection implements BasicChecker {

    private final Logger logger = LoggerFactory.getLogger(ControlPlane.class);
    private static final RedisMapper mapper = new RedisMapperImpl();

    @Override
    public DetectionResult hijackCheck(BGPMessage message) {
        logger.info("Received message: " + message);

        List<Integer> path = message.getPath();
        int lastASn = path.get(path.size() - 1);

        if (!isSource(lastASn, message.getPrefixes()))
            return new DetectionResult(true, "Prefix hijacking", lastASn, 1, 2, 3);

        for (String prefix : message.getPrefixes()) {
            List<Integer> localPath = mapper.findLocalPath(prefix);
            if (localPath == null) {
                mapper.saveNewPath(prefix, path);
                return new DetectionResult().setResult(false);
            }
            if (localPath.get(localPath.size() - 1) != lastASn)
                return new DetectionResult(true, null, lastASn, 1, 2, 3);

            List<Integer> changePoint = findChangePoint(localPath, path);
            List<Integer> newPath = path.subList(0, path.indexOf(changePoint.get(0)));

            if (newPath.size() == 0)
                return new DetectionResult().setResult(true).setType(1, 2, 3);
            else
                return new DetectionResult().setResult(true).setType(1, 2, 3);
        }
        return new DetectionResult().setResult(false);
    }
}

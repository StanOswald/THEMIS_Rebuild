package fzu.sdn504.THEMIS.repository.impl;

import fzu.sdn504.THEMIS.repository.RedisMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

public class RedisMapperSpringDataImpl implements RedisMapper {

    @Override
    public List<Integer> findLocalPath(String prefix) {
        return null;
    }

    @Override
    public List<Integer> getOriginalPaths(int ASnL, int ASnR) {
        return null;
    }

    @Override
    public List<Integer> getHistoryPaths(int srcASn, int peerASn) {
        return null;
    }

    @Override
    public boolean isPolicyRelativeCorrect(int ASnX, int ASnY) {
        return true;
    }

    @Override
    public String getIP(int ASn) {
        return null;
    }

    @Override
    public void setASAndIP(int ASn, String IP) {

    }

    @Override
    public void saveNewPath(String prefix, List<Integer> newPath) {

    }
}

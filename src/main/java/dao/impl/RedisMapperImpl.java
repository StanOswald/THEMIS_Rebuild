package dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.RedisMapper;
import redis.clients.jedis.Jedis;
import util.JedisPoolUtils;

import java.util.List;

public class RedisMapperImpl implements RedisMapper {

    private static final Jedis ctrlResource = JedisPoolUtils.getResource(8);

    @Override
    public List<Integer> findLocalPath(String prefix) {
        // Find path by prefix from local
        String path = ctrlResource.get(prefix);
        try {
            return new ObjectMapper().readerFor(List.class).readValue(path);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
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
        return false;
    }

    @Override
    public boolean isAdjacent(int ASnX, int ASnY) {
        return false;
    }

    @Override
    public boolean isSource(int ASn, List<Integer> prefixes) {
        return false;
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
        ctrlResource.set(prefix, String.valueOf(newPath));
    }
}

package dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.RedisMapper;
import redis.clients.jedis.Jedis;
import util.RedisPool;

import java.util.List;

public class RedisMapperImpl implements RedisMapper {

    @Override
    public List<Integer> findLocalPath(String prefix) {
        try (Jedis ctrlConn = RedisPool.getControlConnection()) {
            String path = ctrlConn.get(prefix);
            return new ObjectMapper().readerFor(List.class).readValue(path);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
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
        try (Jedis ctrlConn = RedisPool.getControlConnection()) {
            ctrlConn.set(prefix, String.valueOf(newPath));
        }
    }
}

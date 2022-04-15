package dao.impl;

import dao.RedisMapper;

import java.util.List;

public class RedisMapperImpl implements RedisMapper {

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
}

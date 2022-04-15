package dao;

import java.util.List;

public interface RedisMapper {

    List<Integer> findLocalPath(String prefix);

    List<Integer> getOriginalPaths(int ASnL, int ASnR);

    List<Integer> getHistoryPaths(int srcASn, int peerASn);

    boolean isPolicyRelativeCorrect(int ASnX, int ASnY);

    boolean isAdjacent(int ASnX, int ASnY);

    boolean isSource(int ASn, List<Integer> prefixes);

    String getIP(int ASn);

    void setASAndIP(int ASn, String IP);

    void saveNewPath(String prefix, List<Integer> newPath);
}

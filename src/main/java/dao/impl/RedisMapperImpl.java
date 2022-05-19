package dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.RedisMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import redis.clients.jedis.Jedis;
import dao.RedisPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RedisMapperImpl implements RedisMapper {

    @Override
    public List<Integer> findLocalPath(String prefix) {
        try (Jedis ctrlConn = RedisPool.getControlConnection()) {
            String path = ctrlConn.get(prefix);
            return new ObjectMapper().readerFor(List.class).readValue(path);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public List<Integer> getOriginalPaths(int ASnL, int ASnR) {
        try (Jedis pathConn = RedisPool.getPathConnection()) {
            return pathConn.smembers(ASnL + "_" + ASnR).stream()
                    .map(Integer::parseInt).collect(Collectors.toList());
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public List<Integer> getHistoryPaths(int srcASn, int peerASn) {
        try (Jedis historyConn = RedisPool.getHistoryConnection()) {
            return historyConn.smembers(srcASn + "_" + peerASn).stream()
                    .map(Integer::parseInt).collect(Collectors.toList());
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public boolean isPolicyRelativeCorrect(int ASnX, int ASnY) {
        return true;
    }

    @Override
    public String getIP(int ASn) {
        try (Jedis ASIPConn = RedisPool.getASIPConnection()) {
            return ASIPConn.smembers(String.valueOf(ASn)).iterator().next();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public void setASAndIP(int ASn, String IP) {
        try (Jedis ASIPConn = RedisPool.getASIPConnection()) {
            ASIPConn.sadd(String.valueOf(ASn), IP);
        }
    }

    @Override
    public void saveNewPath(String prefix, List<Integer> newPath) {
        try (Jedis ctrlConn = RedisPool.getControlConnection()) {
            ctrlConn.set(prefix, String.valueOf(newPath));
        }
    }
}

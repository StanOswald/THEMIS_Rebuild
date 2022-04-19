package dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {

    public static final int BGP_MESSAGE_INDEX = 0;
    public static final int PATH_INDEX = 1;
    public static final int HISTORY_INDEX = 2;
    public static final int AS_IP_INDEX = 3;
    public static final int CONTROL_INDEX = 8;

    private static final JedisPool jedisPool;
    static Logger logger = LoggerFactory.getLogger(RedisPool.class);

    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPool = new JedisPool(jedisPoolConfig, "192.168.179.128", 6379);
        logger.info("Jedis pool initialized.");
    }

    private static Jedis getResource(int index) {
        Jedis resource = jedisPool.getResource();
        resource.select(index);
        return resource;
    }

    public static Jedis getMessageConnection() {
        return getResource(BGP_MESSAGE_INDEX);
    }

    public static Jedis getHistoryConnection() {
        return getResource(HISTORY_INDEX);
    }

    public static Jedis getPathConnection() {
        return getResource(PATH_INDEX);
    }

    public static Jedis getControlConnection() {
        return getResource(CONTROL_INDEX);
    }

    public static Jedis getASIPConnection() {
        return getResource(AS_IP_INDEX);
    }
}

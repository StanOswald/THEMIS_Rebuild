package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolUtils {
    private static final JedisPool jedisPool;
    static Logger logger = LoggerFactory.getLogger(JedisPoolUtils.class);

    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPool = new JedisPool(jedisPoolConfig, "192.168.179.128", 6379);
        logger.info("Jedis pool initialized.");
    }

    public static Jedis getResource() {
        Jedis resource = jedisPool.getResource();
        logger.info("Get resource from pool: " + resource.hashCode());
        return resource;
    }

    public static Jedis getResource(int index) {
        Jedis resource = jedisPool.getResource();
        resource.select(index);
        logger.info("Get resource(DB-" + resource.getDB() + ") from pool: " + resource.hashCode());
        return resource;
    }
}

package queue;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import queue.impl.QsRedisQueue;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.github.ddth.commons.utils.SerializationUtils;

/**
 * Redis-implementation of {@link QueueApi}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.3.0
 */
public class RedisQueueApi extends QueueApi {

    private final static Charset UTF8 = Charset.forName("UTF-8");

    private String _metadataRedisHashName = "queue_metadata";
    private byte[] metadataRedisHashName = _metadataRedisHashName.getBytes(UTF8);

    private JedisPool jedisPool;
    private boolean myOwnJedisPool = true;
    private String redisHostAndPort = "localhost:6379";

    public String getMetadataRedisHashName() {
        return _metadataRedisHashName;
    }

    public RedisQueueApi setMetadataRedisHashName(String metadataRedisHashName) {
        _metadataRedisHashName = metadataRedisHashName;
        this.metadataRedisHashName = _metadataRedisHashName.getBytes(UTF8);
        return this;
    }

    /**
     * Redis' host and port scheme (format {@code host:port}).
     * 
     * @return
     */
    public String getRedisHostAndPort() {
        return redisHostAndPort;
    }

    /**
     * Sets Redis' host and port scheme (format {@code host:port}).
     * 
     * @param redisHostAndPort
     * @return
     */
    public RedisQueueApi setRedisHostAndPort(String redisHostAndPort) {
        this.redisHostAndPort = redisHostAndPort;
        return this;
    }

    protected JedisPool getJedisPool() {
        return jedisPool;
    }

    public RedisQueueApi setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        myOwnJedisPool = false;
        return this;
    }

    public RedisQueueApi init() {
        if (jedisPool == null) {
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(32);
            poolConfig.setMinIdle(1);
            poolConfig.setMaxIdle(16);
            poolConfig.setMaxWaitMillis(10000);
            // poolConfig.setTestOnBorrow(true);
            poolConfig.setTestWhileIdle(true);

            String[] tokens = redisHostAndPort.split(":");
            String redisHost = tokens.length > 0 ? tokens[0] : "localhost";
            int redisPort = tokens.length > 1 ? Integer.parseInt(tokens[1]) : 6379;
            jedisPool = new JedisPool(poolConfig, redisHost, redisPort);
            myOwnJedisPool = true;
        }

        super.init();

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        if (jedisPool != null && myOwnJedisPool) {
            try {
                jedisPool.destroy();
            } catch (Exception e) {
            }
        }

        super.destroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean initQueueMetadata(String queueName) {
        if (!isValidQueueName(queueName)) {
            return false;
        }

        String normalizedQueueName = normalizeQueueName(queueName);
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, Object> queueMetadata = new HashMap<String, Object>();
            queueMetadata.put("queue_name", normalizedQueueName);
            queueMetadata.put("queue_timestamp_create", new Date());
            byte[] data = SerializationUtils.toJsonString(queueMetadata).getBytes(UTF8);
            byte[] field = normalizedQueueName.getBytes(UTF8);
            Long result = jedis.hset(metadataRedisHashName, field, data);
            return result != null && result.longValue() > 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<String> getAllQueueNames() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hkeys(_metadataRedisHashName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void invalidateQueue(IQsQueue queue) {
        queue.destroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IQsQueue createNewQueueInstance(String normalizedQueueName) {
        QsRedisQueue redisQueue = new QsRedisQueue();
        redisQueue.setRedisHashName("queue_" + normalizedQueueName + "_h")
                .setRedisListName("queue_" + normalizedQueueName + "_l")
                .setRedisSortedSetName("queue_" + normalizedQueueName + "_s")
                .setJedisPool(jedisPool);
        redisQueue.init();
        return redisQueue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean queueExists(String queueName) {
        return true;
        // Collection<String> allQueueNames = getAllQueueNames();
        // return allQueueNames.contains(normalizeQueueName(queueName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean initQueue(String queueName) {
        return initQueueMetadata(queueName);
    }

}

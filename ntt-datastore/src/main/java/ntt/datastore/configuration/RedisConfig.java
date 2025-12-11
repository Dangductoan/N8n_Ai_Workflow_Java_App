package ntt.datastore.configuration;

import lombok.extern.slf4j.Slf4j;
import ntt.datastore.properties.Settings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

@Slf4j
@Configuration
public class RedisConfig {

    private final Settings settings;
    public RedisConfig(Settings settings) {
        this.settings = settings;
    }

    /**
     * Initialize Redis client (JedisPool)
     * Equivalent to Python's init_redis_client() function
     */
    @Bean
    public JedisPool initRedisClient() {
        String host = settings.getRedisHost();
        Integer port = settings.getRedisPort();
        Integer database = settings.getRedisDatabase();
        String password = settings.getRedisPassword();
        try {
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(8);
            poolConfig.setMaxIdle(8);
            poolConfig.setMinIdle(0);
            poolConfig.setTestOnBorrow(true);
            //JMX stands for Java Management Extensions -
            // it's a Java technology that provides tools for monitoring and managing Java applications,
            // system objects, devices, and service-oriented networks.
            poolConfig.setJmxEnabled(false);  // ← Add this line
            JedisPool jedisPool;
            if (password != null && !password.isEmpty()) {
                jedisPool = new JedisPool(poolConfig, host, port, 2000, password, database);
            } else {
                jedisPool = new JedisPool(poolConfig, host, port, 2000, null, database);
            }

            // Test connection with ping (like Python's client.ping())
            try (Jedis jedis = jedisPool.getResource()) {
                String response = jedis.ping();
                if ("PONG".equals(response)) {
                    log.info("✅ Redis client initialized successfully.");
                    return jedisPool;
                } else {
                    log.error("❌ Ping to Redis server failed.");
                    throw new RuntimeException("Ping to Redis server failed.");
                }
            }

        } catch (JedisConnectionException ce) {
            log.error("❌ Cannot connect to Redis at {}:{} - {}", host, port, ce.getMessage());
            throw new RuntimeException("Cannot connect to Redis at " + host + ":" + port, ce);
        } catch (JedisException re) {
            log.error("❌ Redis error: {}", re.getMessage());
            throw new RuntimeException("Redis error during initialization.", re);
        } catch (Exception e) {
            log.error("❌ Failed to initialize Redis client: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize Redis client.", e);
        }
    }
}

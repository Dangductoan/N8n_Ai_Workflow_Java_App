/**
 * -----------------------------------------------------------------------------
 * Author      : TacjLee
 * Created On  : 2025-11-19
 * Description : Create RedisUserSessionCache
 * -----------------------------------------------------------------------------
 * Copyright (c) 2025 Ntt. All rights reserved.
 * -----------------------------------------------------------------------------
 */

package ntt.datastore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ntt.common.utility.JacksonUtility;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;

/**
 * Redis-based user session cache.
 * Stores user session data + access token safely.
 */
@Slf4j
@Service
public class RedisUserSessionCache {

    private final JedisPool redisPool;
    private final int ttlSeconds;

    public RedisUserSessionCache(JedisPool redisPool) {
        this.redisPool = redisPool;
        this.ttlSeconds = 600;
    }

    // -----------------
    // Key Generation Methods
    // -----------------
    private String tokenKey(int userId) {
        return "user_session_token:" + userId;
    }

    private String dataKey(int userId) {
        return "user_session_data:" + userId;
    }

    private String refreshKey(int userId) {
        return "refresh_token:" + userId;
    }

    private String lastActiveKey(int userId) {
        return "last_active:" + userId;
    }

    // -----------------
    // Session Access Token
    // -----------------
    public void setAccessToken(int userId, String token, int expireSeconds) {
        try (Jedis jedis = redisPool.getResource()) {
            jedis.setex(tokenKey(userId), expireSeconds, token);
        }
    }

    public String getAccessToken(int userId) {
        try (Jedis jedis = redisPool.getResource()) {
            return jedis.get(tokenKey(userId));
        }
    }

    // -----------------
    // User Profile Data
    // -----------------
    public void setUserData(int userId, Object data) {
        try (Jedis jedis = redisPool.getResource()) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("data", data);
            payload.put("created_at", System.currentTimeMillis() / 1000.0);

            var objectMapper = new ObjectMapper();
            String jsonPayload = objectMapper.writeValueAsString(payload);
            jedis.setex(dataKey(userId), ttlSeconds, jsonPayload);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize user data for user_id={}", userId, e);
            throw new RuntimeException("Failed to serialize user data", e);
        }
    }

    @SuppressWarnings("unchecked")
    public String getUserData(int userId) {
        try (Jedis jedis = redisPool.getResource()) {
            String jsonPayload = jedis.get(dataKey(userId));
            if (jsonPayload == null) {
                log.warn("No user data found for user_id={}", userId);
                return null;
            }
            var objectMapper = new ObjectMapper();
            Map<String, Object> payload = objectMapper.readValue(jsonPayload, Map.class);
            Object data = payload.get("data");
            // Convert data back to JSON string
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize user data for user_id={}", userId, e);
            throw new RuntimeException("Failed to deserialize user data", e);
        }
    }

    // -----------------
    // Refresh Token
    // -----------------
    public void setRefreshToken(int userId, String token, int expireSeconds) {
        try (Jedis jedis = redisPool.getResource()) {
            jedis.setex(refreshKey(userId), expireSeconds, token);
        }
    }

    public String getRefreshToken(int userId) {
        try (Jedis jedis = redisPool.getResource()) {
            return jedis.get(refreshKey(userId));
        }
    }

    public void validateRefreshToken(int userId, String refreshToken) {
        String stored = getRefreshToken(userId);

        if (stored == null || !stored.equals(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
    }

    // -----------------
    // Last Active Tracking
    // -----------------
    public void setLastActive(int userId) {
        try (Jedis jedis = redisPool.getResource()) {
            jedis.setex(lastActiveKey(userId), ttlSeconds, "1");
        }
    }

    public boolean isActive(int userId) {
        try (Jedis jedis = redisPool.getResource()) {
            return jedis.get(lastActiveKey(userId)) != null;
        }
    }

    // -----------------
    // Clear All Sessions
    // -----------------
    public void clear(int userId) {
        try (Jedis jedis = redisPool.getResource()) {
            jedis.del(
                    tokenKey(userId),
                    dataKey(userId),
                    refreshKey(userId),
                    lastActiveKey(userId)
            );
            log.info("Cleared all Redis sessions for user_id={}", userId);
        }
    }
}

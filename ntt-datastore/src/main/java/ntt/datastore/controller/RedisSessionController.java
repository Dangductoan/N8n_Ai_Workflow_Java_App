package ntt.datastore.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ntt.common.api.datastore.redis.SetAccessTokenRequest;
import ntt.common.api.datastore.redis.SetRefreshTokenRequest;
import ntt.common.api.datastore.redis.SetUserDataRequest;
import ntt.common.api.datastore.redis.ValidateRefreshTokenRequest;
import ntt.datastore.service.RedisUserSessionCache;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Redis User Session Cache operations
 */
@Slf4j
@RestController
@RequestMapping("/api/redis/session")
@RequiredArgsConstructor
public class RedisSessionController {

    private final RedisUserSessionCache redisUserSessionCache;

    // -----------------
    // Access Token Endpoints
    // -----------------

    @PostMapping("/access-token")
    public Boolean setAccessToken(@RequestBody SetAccessTokenRequest request) {
        try {
            redisUserSessionCache.setAccessToken(
                    request.getUserId(),
                    request.getToken(),
                    request.getExpireSeconds()
            );
            return true;
        } catch (Exception e) {
            log.error("Failed to set access token for user_id={}", request.getUserId(), e);
            return null;
        }
    }

    @GetMapping("/access-token")
    public String getAccessToken(@RequestParam("userId") int userId) {
        try {
            String token = redisUserSessionCache.getAccessToken(userId);
            return token;
        } catch (Exception e) {
            log.error("Failed to get access token for user_id={}", userId, e);
            return null;
        }
    }

    // -----------------
    // User Data Endpoints
    // -----------------

    @PostMapping("/user-data")
    public Boolean setUserData(@RequestParam("userId") int userId, @RequestBody SetUserDataRequest data) {
        try {
            redisUserSessionCache.setUserData(userId, data);
            return true;
        } catch (Exception e) {
            log.error("Failed to set user data for user_id={}", userId, e);
            return null;
        }
    }

    @GetMapping("/user-data")
    public String getUserData(@RequestParam("userId") int userId) {
        try {
            String result = redisUserSessionCache.getUserData(userId);
            return result;
        } catch (Exception e) {
            log.error("Failed to get user data for user_id={}", userId, e);
            return null;
        }
    }

    // -----------------
    // Refresh Token Endpoints
    // -----------------

    @PostMapping("/refresh-token")
    public Boolean setRefreshToken(@RequestBody SetRefreshTokenRequest request) {
        try {
            redisUserSessionCache.setRefreshToken(
                    request.getUserId(),
                    request.getToken(),
                    request.getExpireSeconds()
            );
            return true;
        } catch (Exception e) {
            log.error("Failed to set refresh token for user_id={}", request.getUserId(), e);
            return null;
        }
    }

    @GetMapping("/refresh-token")
    public String getRefreshToken(@RequestParam("userId") int userId) {
        try {
            String token = redisUserSessionCache.getRefreshToken(userId);
            return token;
        } catch (Exception e) {
            log.error("Failed to get refresh token for user_id={}", userId, e);
            return null;
        }
    }

    @PostMapping("/refresh-token/validate")
    public Boolean validateRefreshToken(@RequestBody ValidateRefreshTokenRequest request) {
        try {
            redisUserSessionCache.validateRefreshToken(
                    request.getUserId(),
                    request.getRefreshToken()
            );
            return true;
        } catch (Exception e) {
            log.error("Failed to validate refresh token for user_id={}", request.getUserId(), e);
            return null;
        }
    }

    // -----------------
    // Activity Tracking Endpoints
    // -----------------

    @PostMapping("/last-active")
    public Boolean setLastActive(@RequestParam("userId") int userId) {
        try {
            redisUserSessionCache.setLastActive(userId);
            return true;
        } catch (Exception e) {
            log.error("Failed to set last active for user_id={}", userId, e);
            return null;
        }
    }

    @GetMapping("/is-active")
    public Boolean isActive(@RequestParam("userId") int userId) {
        try {
            boolean active = redisUserSessionCache.isActive(userId);
            return active;
        } catch (Exception e) {
            log.error("Failed to check active status for user_id={}", userId, e);
            return null;
        }
    }

    // -----------------
    // Session Management Endpoints
    // -----------------

    @DeleteMapping("/clear")
    public Boolean clear(@RequestParam("userId") int userId) {
        try {
            redisUserSessionCache.clear(userId);
            return true;
        } catch (Exception e) {
            log.error("Failed to clear session for user_id={}", userId, e);
            return null;
        }
    }
}
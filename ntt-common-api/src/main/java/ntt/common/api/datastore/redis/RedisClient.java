/**
 * -----------------------------------------------------------------------------
 * Author      : TacjLee
 * Created On  : 2025-11-19
 * Description : Create common Redis Client
 * -----------------------------------------------------------------------------
 * Copyright (c) 2025 Ntt. All rights reserved.
 * -----------------------------------------------------------------------------
 */
package ntt.common.api.datastore.redis;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Feign Client for Redis User Session Cache operations
 */
@FeignClient(
        name = "redis-session",
        url = "${clients.storage.redis.url}"
)
public interface RedisClient {

    // -----------------
    // Access Token Endpoints
    // -----------------

    @PostMapping("/api/redis/session/access-token")
    Boolean setAccessToken(@RequestBody SetAccessTokenRequest request);

    @GetMapping("/api/redis/session/access-token")
    String getAccessToken(@RequestParam("userId") int userId);

    // -----------------
    // User Data Endpoints
    // -----------------

    @PostMapping("/api/redis/session/user-data")
    Boolean setUserData(@RequestParam("userId") int userId, @RequestBody SetUserDataRequest request);

    @GetMapping("/api/redis/session/user-data")
    String getUserData(@RequestParam("userId") int userId);

    // -----------------
    // Refresh Token Endpoints
    // -----------------

    @PostMapping("/api/redis/session/refresh-token")
    Boolean setRefreshToken(@RequestBody SetRefreshTokenRequest request);

    @GetMapping("/api/redis/session/refresh-token")
    String getRefreshToken(@RequestParam("userId") int userId);

    @PostMapping("/api/redis/session/refresh-token/validate")
    Boolean validateRefreshToken(@RequestBody ValidateRefreshTokenRequest request);

    // -----------------
    // Activity Tracking Endpoints
    // -----------------

    @PostMapping("/api/redis/session/last-active")
    Boolean setLastActive(@RequestParam("userId") int userId);

    @GetMapping("/api/redis/session/is-active")
    Boolean isActive(@RequestParam("userId") int userId);

    // -----------------
    // Session Management Endpoints
    // -----------------

    @DeleteMapping("/api/redis/session/clear")
    Boolean clear(@RequestParam("userId") int userId);
}
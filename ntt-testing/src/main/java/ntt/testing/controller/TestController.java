package ntt.testing.controller;

import lombok.AllArgsConstructor;
import ntt.common.api.datastore.minio.MinioClient;
import ntt.common.api.datastore.redis.RedisClient;
import ntt.common.api.datastore.redis.SetAccessTokenRequest;
import ntt.common.api.datastore.redis.SetUserDataRequest;
import ntt.common.utility.DataConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

//Swagger: http://localhost:8000/swagger-ui/index.html

@RestController
@AllArgsConstructor
public class TestController {

    private static final String HTTP_FILE_UPLOAD_URL = "http://localhost:8090";
    MinioClient minioClient;
    RedisClient redisClient;

    @GetMapping("/api/message")
    public ResponseEntity<String> testMessage() {
        return ResponseEntity.ok("Welcome to this program!");
    }

    @PostMapping("/api/create/bucket")
    public String testCreatingBucket() {
        String uuid = "ee2338ec-d016-4bb7-b94f-2d4c8bab75f9";
        String bucketName = "bucket1";
        return this.minioClient.createBucket(bucketName);
    }

    @PostMapping("/api/delete/bucket")
    public String testDeletingBucket() {
        String uuid = "ee2338ec-d016-4bb7-b94f-2d4c8bab75f9";
        String bucketName = "bucket1";
        return this.minioClient.deleteBucket(bucketName);
    }

    @PostMapping("/api/redis/session/access-token")
    public Object testRedisSetAccessToken() {
        String fakeToken = "jwtToken";
        var userId = 123;
        SetAccessTokenRequest tokenRequest = new SetAccessTokenRequest();
        tokenRequest.setUserId(userId);
        tokenRequest.setToken(fakeToken);
        tokenRequest.setExpireSeconds(60);
        Boolean result = this.redisClient.setAccessToken(tokenRequest);
        String token = this.redisClient.getAccessToken(userId);
        return token;
    }

    @PostMapping("/api/redis/session/user-data")
    public Object testRedisUserData(@RequestParam("userId") int userId, @RequestBody SetUserDataRequest request) throws IOException {
        Boolean result = this.redisClient.setUserData(userId, request);
        String data = this.redisClient.getUserData(userId);
        SetUserDataRequest model2 = DataConverter.parseJsonToObject(data, SetUserDataRequest.class);
        return model2;
    }

    @PostMapping("/api/redis/session/user-active")
    public Object testRedisUserActive(@RequestParam("userId") int userId) {
        this.redisClient.setLastActive(userId);
        Boolean data = this.redisClient.isActive(userId);
        return data;
    }


}

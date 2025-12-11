package ntt.common.api.datastore.redis;

import lombok.Data;

@Data
public class SetRefreshTokenRequest {
    private int userId;
    private String token;
    private int expireSeconds;
}
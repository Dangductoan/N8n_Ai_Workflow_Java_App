package ntt.common.api.datastore.redis;

import lombok.Data;

@Data
public class ValidateRefreshTokenRequest {
    private int userId;
    private String refreshToken;
}
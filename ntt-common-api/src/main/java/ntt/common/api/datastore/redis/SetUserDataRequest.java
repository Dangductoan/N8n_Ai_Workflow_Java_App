package ntt.common.api.datastore.redis;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SetUserDataRequest {
    private String username;
    private String email;
    private String phone;
    private Integer age;
    private String role;
    private Boolean active;
    private String address;
    private String city;
    private String country;
    private List<String> permissions;
    private Map<String, Object> metadata;
}

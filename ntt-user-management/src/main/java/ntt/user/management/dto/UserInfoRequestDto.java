package ntt.user.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoRequestDto {
    private String username;
    private String email;
    private String role;
    private String orgUnitName;
    private Integer  orgUnitId;
    private String fullname;
    private int skip;
    private int limit;
}

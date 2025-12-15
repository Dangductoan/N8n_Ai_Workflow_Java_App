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
public class DetailUserInfoDto {
    private Long id;
    private String userName;
    private String email;
    private String fullName;
    private Long orgUnitId;
    private String orgUnitName;
    private UUID orgUnitParentUuid;
    private String companyName;
    private Boolean isActive;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DetailUserInfoDto(Integer id, String username, String email, String fullname, Integer integer, String orgUnitName, UUID orgUnitParentUuid, String companyName, Boolean isActive, String role, LocalDateTime createdAt, LocalDateTime updatedAt) {
    }
}

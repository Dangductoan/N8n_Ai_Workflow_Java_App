package ntt.workflow.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private int id;
    private String email;
    private String username;
    private String fullname;
    private boolean isActive;
    private boolean isSuperuser;
    private String role;
    private Integer orgUnitId;         // nullable
    private String avatarUrl;          // nullable
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

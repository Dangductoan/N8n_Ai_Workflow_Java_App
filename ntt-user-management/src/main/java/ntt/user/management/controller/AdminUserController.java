package ntt.user.management.controller;

import lombok.RequiredArgsConstructor;
import ntt.user.management.dto.DetailUserInfoDto;
import ntt.user.management.dto.PaginatedResponse;
import ntt.user.management.dto.UserInfoRequestDto;
import ntt.user.management.service.AdminUserService;
import org.springframework.web.bind.annotation.*;

// Swagger:
// http://localhost:8091/swagger-ui/index.html

@RestController
@RequestMapping("/api/user-management")
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminUserService adminUserService;


    @PostMapping("/admin-user/users")
    public PaginatedResponse<DetailUserInfoDto> listUsers(@RequestBody UserInfoRequestDto rq) {
        return this.adminUserService.listUsers(rq);
    }
}

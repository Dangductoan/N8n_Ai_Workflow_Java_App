package ntt.common.api.shared.tenant;

import ntt.common.api.shared.tenant.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "tenant-management",
        url = "${clients.shared.tenant.url}"
)
public interface TenantSpaceHelper {

    //Create Company Space
    @PostMapping("/tenant/create-company-space/{uuid}/{name}")
    CompanySpaceDto createCompanySpace(
            @PathVariable("uuid") String uuid,
            @PathVariable("name") String name);

    //Delete Company Space
    @DeleteMapping("/tenant/delete-company-space/{uuid}/{name}")
    DeleteCompanySpaceDto deleteCompanySpace(
            @PathVariable("uuid") String uuid,
            @PathVariable("name") String name);

    @GetMapping("/tenant/get-company-space/{uuid}/{name}")
    CompanySpaceDto getCompanySpace(
            @PathVariable("uuid") String uuid,
            @PathVariable("name") String name);

    @PostMapping("/tenant/create-user-spaces/{uuid}/{userId}")
    UserSpacesDto createUserSpaces(@PathVariable("uuid") String uuid,
                                   @PathVariable("userId") int userId);

    @PostMapping("/tenant/create-project-spaces")
    ProjectSpacesDto createProjectSpaces(@RequestBody ProjectSpacesRequestDto rq);
}
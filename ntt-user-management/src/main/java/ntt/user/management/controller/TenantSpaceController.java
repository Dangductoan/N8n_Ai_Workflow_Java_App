package ntt.user.management.controller;

import lombok.RequiredArgsConstructor;
import ntt.common.api.shared.tenant.dto.*;
import ntt.user.management.service.TenantSpaceService;
import org.springframework.web.bind.annotation.*;

// Swagger:
// http://localhost:8091/swagger-ui/index.html

@RestController
@RequestMapping("/api/user-management")
@RequiredArgsConstructor
public class TenantSpaceController {
    private final TenantSpaceService tenantSpaceService;

    //Create Company Space
    @PostMapping("/tenant/create-company-space/{uuid}/{name}")
    public CompanySpaceDto createCompanySpace(@PathVariable(value="uuid") String uuid,
                                              @PathVariable(value="name") String name) {

        return this.tenantSpaceService.createCompanySpaces(uuid,name);
    }

    //Delete Company Space
    @DeleteMapping("/tenant/delete-company-space/{uuid}/{name}")
    public DeleteCompanySpaceDto deleteCompanySpace(@PathVariable(value="uuid") String uuid,
                                                    @PathVariable(value="name") String name) {

        return this.tenantSpaceService.deleteCompanySpace(uuid,name);
    }

    @GetMapping("/tenant/get-company-space/{uuid}/{name}")
    CompanySpaceDto getCompanySpace(
            @PathVariable("uuid") String uuid,
            @PathVariable("name") String name) {
        return this.tenantSpaceService.getCompanySpaceInfo(uuid,name);
    }
    @PostMapping("/tenant/create-user-spaces/{uuid}/{userId}")
    UserSpacesDto createUserSpaces(@PathVariable("uuid") String uuid,
                                   @PathVariable("userId") int userId) {
        return this.tenantSpaceService.createUserSpaces(uuid,userId);
    }

    @PostMapping("/tenant/create-project-spaces")
    ProjectSpacesDto createProjectSpaces(@RequestBody ProjectSpacesRequestDto rq) {
        return this.tenantSpaceService.createProjectSpaces(rq);
    }

    @PostMapping("/tenant/create-knowledge-base-spaces")
    KBSpacesDto createKnowledgeBaseSpaces(@RequestBody KBSpacesRequestDto rq) {
        return this.tenantSpaceService.createKnowledgeBaseSpaces(rq);
    }
    @PostMapping("/tenant/delete-knowledge-base-spaces")
    DeleteKBSpacesDto deleteKnowledgeBaseSpaces(@RequestBody KBSpacesRequestDto rq) {
        return this.tenantSpaceService.deleteKnowledgeBaseSpaces(rq);
    }
    @PostMapping("/tenant/delete-project-spaces")
    ProjectSpacesDto deleteProjectSpaces(@RequestBody ProjectSpacesRequestDto rq) {
        return this.tenantSpaceService.deleteProjectSpaces(rq);
    }
}

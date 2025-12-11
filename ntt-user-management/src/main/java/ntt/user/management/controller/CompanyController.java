package ntt.user.management.controller;

import lombok.RequiredArgsConstructor;
import ntt.common.api.shared.tenant.dto.CompanyDto;
import ntt.user.management.service.CompanyService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

// Swagger:
// http://localhost:8091/swagger-ui/index.html

@RestController
@RequestMapping("/api/user-management")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping("/company/uuid/{uuid}")

    public CompanyDto getCompanyByUuid(@PathVariable(value="uuid") String uuid) {
        CompanyDto company = companyService.getCompanyByUuid(uuid);
        return company;
    }

    @GetMapping("/company/name/{name}")
    public CompanyDto getCompanyByName(@PathVariable(value="name") String name) {
        CompanyDto company = companyService.getCompanyByName(name);
        return company;
    }

    @GetMapping("/company/{uuid}/spaces")
    public Map<String, String> getCompanySpaces(@PathVariable(value="uuid") String uuid) {
        Optional<Map<String, String>> spacesOtp = companyService.getCompanySpaces(uuid);
        if (spacesOtp.isPresent()) {
            return spacesOtp.get();
        }
        return null;
    }

    @GetMapping("/company/bucket-name/{uuid}/{name}")
    public Map<String, String> getCompanyBucketName(
            @PathVariable(value="uuid") String uuid,
            @PathVariable(value="name") String name){
        String bucketName = companyService.getCompanyBucketName(uuid, name);
        return Map.of("bucketName", bucketName);
    }

    @PostMapping("/company/ensure-bucket-exists/{uuid}/{name}")
    public Map<String, String> ensureCompanyBucketExists(
            @PathVariable(value="uuid") String uuid,
            @PathVariable(value="name") String name){
        String bucketName = companyService.ensureCompanyBucketExists(uuid, name);
        return Map.of("bucketName", bucketName);
    }
}

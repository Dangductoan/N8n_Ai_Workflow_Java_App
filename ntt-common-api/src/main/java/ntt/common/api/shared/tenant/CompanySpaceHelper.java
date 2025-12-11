package ntt.common.api.shared.tenant;

import ntt.common.api.shared.tenant.dto.CompanyDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Map;

@FeignClient(
        name = "company-management",
        url = "${clients.shared.tenant.url}"
)
public interface CompanySpaceHelper {
    @GetMapping("/company/uuid/{uuid}")
    CompanyDto getCompanyByUuid(@PathVariable("uuid") String uuid);

    @GetMapping("/company/name/{name}")
    CompanyDto getCompanyByName(@PathVariable("name") String name);

    @GetMapping("/company/{uuid}/spaces")
    Map<String, String> getCompanySpaces(@PathVariable("uuid") String uuid);

    @GetMapping("/company/bucket-name/{uuid}/{name}")
    Map<String, String> getCompanyBucketName(
            @PathVariable("uuid") String uuid,
            @PathVariable("name") String name);

    @PostMapping("/company/ensure-bucket-exists/{uuid}/{name}")
    Map<String, String> ensureCompanyBucketExists(
            @PathVariable("uuid") String uuid,
            @PathVariable("name") String name);
}
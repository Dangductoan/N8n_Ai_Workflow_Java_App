package ntt.testing.controller;

import lombok.AllArgsConstructor;
import ntt.common.api.shared.tenant.dto.CompanyDto;
import ntt.common.api.shared.tenant.CompanySpaceHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TenantController {
    CompanySpaceHelper companySpaceHelper;

    @GetMapping("/api/tenant/company-name")
    public ResponseEntity<String> testMessage() {
        CompanyDto company = this.companySpaceHelper.getCompanyByName("company123");
        return ResponseEntity.ok("Welcome to this program!");
    }


}
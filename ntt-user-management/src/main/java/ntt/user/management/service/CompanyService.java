package ntt.user.management.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ntt.common.api.shared.tenant.dto.CompanyDto;
import ntt.common.api.shared.tenant.dto.OrganizationUnitDto;
import ntt.common.jdbc.repository.IJdbcRepository;
import ntt.common.jpa.repository.IEntityRepository;
import ntt.common.utility.DataConverter;
import ntt.user.management.entity.Company;
import ntt.user.management.entity.OrganizationUnit;
import ntt.user.management.entity.User;
import ntt.user.management.specification.CompanySpec;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
@AllArgsConstructor
public class CompanyService {

    private final IJdbcRepository jdbcRepository;
    private final IEntityRepository entityRepository;

    /**
     * Lấy thông tin spaces của company từ database
     */
    public Optional<Map<String, String>> getCompanySpaces(String companyUuid) {
        try {
            Company company = findCompanyByUuid(companyUuid);
            if (company != null && company.getMinioBucketName() != null && company.getQdrantCollectionName() != null) {
                Map<String, String> spaces = new HashMap<>();
                spaces.put("minio_bucket_name", company.getMinioBucketName());
                spaces.put("qdrant_collection_name", company.getQdrantCollectionName());
                spaces.put("mysql_database_name", company.getMysqlDatabaseName());
                return Optional.of(spaces);
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("Failed to get company spaces for {}: {}", companyUuid, e.getMessage());
            return Optional.empty();
        }
    }


    public CompanyDto getCompanyByUuid(String companyUuid) {
        Company company = this.findCompanyByUuid(companyUuid);
        if (company != null) {
            return DataConverter.copy(company, CompanyDto.class);
        }
        return null;
    }

    public CompanyDto getCompanyByName(String name) {
        CompanySpec x = new CompanySpec();
        Specification groupSpec = Specification.where(x.name(name));
        Optional<Company> pageOpt = this.entityRepository.findOne(Company.class, groupSpec);
        if (pageOpt.isPresent()) {
            return DataConverter.copy(pageOpt.get(), CompanyDto.class);
        }
        return null;
    }

    private OrganizationUnitDto getOrgUnitByUuid(String uuid) {
        OrganizationUnit organizationUnit = this.findOrgUnitByUuid(uuid);
        if (organizationUnit != null) {
            return DataConverter.copy(organizationUnit, OrganizationUnitDto.class);
        }
        return null;
    }

    /**
     * Lấy company UUID của user bằng cách traverse org unit hierarchy
     */
    public Optional<String> getUserCompanyUuid(User user) {
        if (user.getOrgUnitId() == null) {
            return Optional.empty();
        }
        OrganizationUnit currentOrg = user.getOrgUnit();
        while (currentOrg != null && currentOrg.getParentUuid() != null) {
            Company company = findCompanyByUuid(currentOrg.getParentUuid());
            if (company != null) {
                return Optional.of(company.getUuid());
            }
            currentOrg = findOrgUnitByUuid(currentOrg.getParentUuid());
        }
        return Optional.empty();
    }

    /**
     * Lấy bucket name theo company (tương tự TenantSpaceManager)
     */
    public String getCompanyBucketName(String companyUuid, String companyName) {
        String sanitized = companyName.toLowerCase();
        sanitized = Pattern.compile("[^a-z0-9\\-]").matcher(sanitized).replaceAll("-");
        sanitized = Pattern.compile("-+").matcher(sanitized).replaceAll("-");
        sanitized = sanitized.replaceAll("^-+|-+$", "");

        if (!sanitized.isEmpty() && !Character.isLetterOrDigit(sanitized.charAt(0))) {
            sanitized = "c" + sanitized;
        }

        if (!sanitized.isEmpty() && !Character.isLetterOrDigit(sanitized.charAt(sanitized.length() - 1))) {
            sanitized = sanitized + "0";
        }

        if (sanitized.length() > 63) {
            sanitized = sanitized.substring(0, 63);
            if (!Character.isLetterOrDigit(sanitized.charAt(sanitized.length() - 1))) {
                sanitized = sanitized.substring(0, sanitized.length() - 1) + "0";
            }
        }

        return String.format("company-%s-%s", companyUuid, sanitized);
    }
    /**
     * Lấy bucket name của company mà user thuộc về
     */
    public Optional<String> getUserCompanyBucketName(User user) {
        Optional<String> companyUuid = getUserCompanyUuid(user);
        if (companyUuid.isEmpty()) {
            return Optional.empty();
        }
        Company company = findCompanyByUuid(companyUuid.get());
        if (company == null) {
            return Optional.empty();
        }
        return Optional.of(getCompanyBucketName(companyUuid.get(), company.getName()));
    }

    /**
     * Đảm bảo bucket của company tồn tại, nếu không thì tạo mới
     */
    public String ensureCompanyBucketExists(String companyUuid, String companyName) {
        String bucketName = getCompanyBucketName(companyUuid, companyName);

//        try {
//            if (!tenantSpaceManager.getMinioClient().bucketExists(bucketName)) {
//                tenantSpaceManager.createMinioBucket(bucketName);
//                logger.info("Created missing bucket: {}", bucketName);
//            } else {
//                logger.info("Bucket already exists: {}", bucketName);
//            }
//        } catch (Exception e) {
//            logger.error("Failed to ensure bucket exists {}: {}", bucketName, e.getMessage());
//            throw new RuntimeException("Failed to ensure bucket exists: " + bucketName, e);
//        }
        return bucketName;
    }

    private OrganizationUnit findOrgUnitByUuid(String uuid) {
        CompanySpec x = new CompanySpec();
        Specification groupSpec = Specification.where(x.uuid(uuid));
        Optional<OrganizationUnit> orgUnitOpt = this.entityRepository.findOne(OrganizationUnit.class, groupSpec);
        if (orgUnitOpt.isPresent()) {
            return orgUnitOpt.get();
        }
        return null;
    }

    private Company findCompanyByUuid(String companyUuid) {
        CompanySpec x = new CompanySpec();
        Specification groupSpec = Specification.where(x.uuid(companyUuid));
        Optional<Company> companyOpt = this.entityRepository.findOne(Company.class, groupSpec);
        if (companyOpt.isPresent()) {
            return companyOpt.get();
        }
        return null;
    }
}

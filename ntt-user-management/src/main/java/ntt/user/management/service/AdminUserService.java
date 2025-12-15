package ntt.user.management.service;

import lombok.extern.slf4j.Slf4j;
import ntt.common.jdbc.repository.IJdbcRepository;
import ntt.common.jpa.repository.IEntityRepository;
import ntt.user.management.dto.DetailUserInfoDto;
import ntt.user.management.dto.PaginatedResponse;
import ntt.user.management.dto.UserInfoRequestDto;
import ntt.user.management.entity.OrganizationUnit;
import ntt.user.management.entity.User;
import ntt.user.management.specification.UserSpec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminUserService {
    private final IJdbcRepository jdbcRepository;
    private final IEntityRepository entityRepository;

    public AdminUserService(IJdbcRepository jdbcRepository, IEntityRepository entityRepository) {
        this.jdbcRepository = jdbcRepository;
        this.entityRepository = entityRepository;
    }


    public PaginatedResponse<DetailUserInfoDto> listUsers(UserInfoRequestDto rq) {

        Pageable pageable = PageRequest.of(rq.getSkip()/rq.getLimit(),rq.getLimit());

        // Build the Specification using UserSpec
        UserSpec x = new UserSpec();
        Specification groupSpec = Specification.where(x.username(rq.getUsername()).
                and(x.email(rq.getEmail()).
                        and(x.role(rq.getRole()).
                                and(x.orgUnitId(rq.getOrgUnitId()).
                                                and(x.fullname(rq.getFullname()))))));
        Page<User> userPage = this.entityRepository.findAll(User.class,groupSpec,pageable);

        List<DetailUserInfoDto> detailUserInfos = userPage.getContent().stream()
                .map(this::mapUserToDetailUserInfo)
                .collect(Collectors.toList());

        return new PaginatedResponse<DetailUserInfoDto>(
                (int) userPage.getTotalElements(),
                detailUserInfos,
                rq.getLimit(),
                rq.getSkip()
        );
    }
    // Helper method to map User entity to DetailUserInfo DTO
    private DetailUserInfoDto mapUserToDetailUserInfo(User user) {
        String orgUnitName = null;
        UUID orgUnitParentUuid = null;
        String companyName = null;

        // The User entity now has a direct @ManyToOne relationship with OrganizationUnit
        if (user.getOrgUnit() != null) {
            OrganizationUnit ou = user.getOrgUnit();
            orgUnitName = ou.getName();
            orgUnitParentUuid = UUID.fromString(ou.getParentUuid());
            // Removed getCompanyNameForOrgUnit as its logic needs to be handled differently or in a dedicated service if still needed for this DTO transformation.
            // For now, assuming companyName is not directly fetched here or needs a different approach.
            // If companyName logic is crucial here, it needs to be fetched based on ou.getParentUuid
            // which would require injecting CompanyRepository or a CompanyService.
        }

        return new DetailUserInfoDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullname(),
                user.getOrgUnit() != null ? user.getOrgUnit().getId() : null,
                orgUnitName,
                orgUnitParentUuid,
                companyName, // This will be null without the company repository/service lookup
                user.getIsActive(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }




}

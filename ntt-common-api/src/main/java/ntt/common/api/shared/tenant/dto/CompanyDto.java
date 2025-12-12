package ntt.common.api.shared.tenant.dto;


import lombok.*;
import ntt.common.api.shared.AbstractBaseModel;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto extends AbstractBaseModel {
    private Integer id;
    private String uuid;
    private String name;
    private Integer nodePosX;
    private Integer nodePosY;

    // Multi-tenant fields
    private String minioBucketName;
    private String qdrantCollectionName;
    private String mysqlDatabaseName;


}
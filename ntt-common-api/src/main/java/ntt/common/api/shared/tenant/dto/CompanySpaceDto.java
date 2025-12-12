package ntt.common.api.shared.tenant.dto;

import lombok.*;
import ntt.common.api.shared.AbstractBaseModel;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanySpaceDto extends AbstractBaseModel {

    private String minioBucketName;
    private String qdrantCollectionName;
    private String mysqlDatabaseName;

}

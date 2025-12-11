package ntt.common.api.shared.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ntt.common.api.shared.AbstractBaseModel;


@Getter
@Setter
@AllArgsConstructor
public class KBSpacesRequestDto extends AbstractBaseModel {


    private String companyUUID; // company_uuid: UUID của company

    private Integer kbId; //kb_id: ID của knowledge base

    private String kbName; //kb_name: Tên knowledge base

    private String companyName; // company_name: Tên company (optional)

    private Integer  projectId; //project_id: ID của project (optional) - nếu có thì tạo trong project folder

    private Integer  userId; // user_id: ID của user (optional) - để tạo thư mục user trong documents
}

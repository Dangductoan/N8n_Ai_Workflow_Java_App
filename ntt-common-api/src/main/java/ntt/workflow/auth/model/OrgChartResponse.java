package ntt.workflow.auth.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrgChartResponse extends OrgChartNode {

    public OrgChartResponse(Integer id, String uuid, String parentUuid, String name,
                            String type, Boolean isCompany, Integer nodePosX, Integer nodePosY) {
        super(id, uuid, parentUuid, name, type, isCompany, nodePosX, nodePosY);
    }
}
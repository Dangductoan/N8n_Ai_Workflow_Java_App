package ntt.workflow.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrgChartNode {

    private Integer id;             // Optional[int]
    private String uuid;            // required
    private String parentUuid;      // Optional[str]
    private String name;            // Optional[str]
    private String type;            // Optional[str]
    private Boolean isCompany = false;  // default False
    private Integer nodePosX;       // Optional[int]
    private Integer nodePosY;       // Optional[int]

}
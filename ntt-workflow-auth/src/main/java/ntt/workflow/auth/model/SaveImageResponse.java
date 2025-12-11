package ntt.workflow.auth.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveImageResponse {
    private String path;
    private String id;
    private String category;
    private String type;

}
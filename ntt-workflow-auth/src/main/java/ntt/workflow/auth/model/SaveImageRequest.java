package ntt.workflow.auth.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveImageRequest {

    private String id;
    private String name;
    private String userId;
    private String category;
    private String type;
    private String path;
    private String uri;
}
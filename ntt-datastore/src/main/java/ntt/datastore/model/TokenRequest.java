package ntt.datastore.model;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequest {

    private String userId;
    private String projectId;
    private List<String> permissions;
}
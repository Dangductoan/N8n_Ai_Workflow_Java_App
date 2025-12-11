package ntt.common.jdbc.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class QueryInfo {
    private String query;
    private List<Object> params = new ArrayList();

}

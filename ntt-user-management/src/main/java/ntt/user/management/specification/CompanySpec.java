package ntt.user.management.specification;

import ntt.common.jpa.criteria.SpecOperation;
import ntt.common.jpa.specification.CommonSpecification;
import org.springframework.data.jpa.domain.Specification;

public class CompanySpec {
    public Specification uuid(String uuid){
        return new CommonSpecification("uuid", SpecOperation.EQUAL, uuid);
    }

    public Specification name(String name){
        return new CommonSpecification("name", SpecOperation.EQUAL, name);
    }
}

package ntt.user.management.specification;

import ntt.common.jpa.criteria.SpecOperation;
import ntt.common.jpa.specification.CommonSpecification;
import org.springframework.data.jpa.domain.Specification;

public class UserSpec {
    public Specification username(String username){
        return new CommonSpecification("username", SpecOperation.EQUAL, username);
    }

    public Specification email(String email){

        return new CommonSpecification("email", SpecOperation.EQUAL, email);
    }
    public Specification role(String role){

        return new CommonSpecification("role", SpecOperation.EQUAL, role);
    }
    public Specification orgUnitId(Integer orgUnitId){

        return new CommonSpecification("orgUnitId", SpecOperation.EQUAL, orgUnitId);
    }
    public Specification fullname(String fullname){

        return new CommonSpecification("fullname", SpecOperation.EQUAL, fullname);
    }
}

package ntt.common.jpa.specification;

import ntt.common.jpa.criteria.SpecOperation;
import ntt.common.jpa.criteria.SpecSearchCriteria;
import ntt.common.jpa.entity.AbstractBaseEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;


public class CommonSpecification<T extends AbstractBaseEntity> implements Specification<T> {

    private SpecSearchCriteria criteria;

    public CommonSpecification(final SpecSearchCriteria criteria) {
        super();
        this.criteria = criteria;
    }

    public CommonSpecification(final String key, final SpecOperation operation, final Object value ){
        super();
        this.criteria = new SpecSearchCriteria(key, operation, value);
    }

    public SpecSearchCriteria getCriteria() {
        return criteria;
    }

    @Override
    public Predicate toPredicate(final Root<T> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
        switch (criteria.getOperation()) {
            case EQUAL:
                if(criteria.getValue() == null){
                    return builder.isNull(root.get(criteria.getKey()));
                }
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NOT_EQUAL:
                if(criteria.getValue() == null){
                    return builder.isNotNull(root.get(criteria.getKey()));
                }
                return builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN:
                return builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case GREATER_THAN_OR_EQUAL:
                return builder.greaterThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN:
                return builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN_OR_EQUAL:
                return builder.lessThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE:
                return builder.like(root.get(criteria.getKey()), criteria.getValue().toString());
            case STARTS_WITH:
                return builder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
            case ENDS_WITH:
                return builder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS:
                return builder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
            case IN:
                return builder.in(root.get(criteria.getKey())).value(criteria.getValue());
            case IS_NULL:
                return builder.isNull(root.get(criteria.getKey()));
            default:
                return null;
        }
    }

}
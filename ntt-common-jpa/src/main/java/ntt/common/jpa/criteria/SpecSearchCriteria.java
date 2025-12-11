package ntt.common.jpa.criteria;

public class SpecSearchCriteria {

    private String key;
    private SpecOperation operation;
    private Object value;
    private boolean orPredicate;

    public SpecSearchCriteria() {

    }

    public SpecSearchCriteria(final String key, final SpecOperation operation, final Object value) {
        super();
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public SpecSearchCriteria(final String orPredicate, final String key, final SpecOperation operation, final Object value) {
        super();
        this.orPredicate = orPredicate != null && orPredicate.equalsIgnoreCase(SpecOperation.OR_PREDICATE_FLAG);
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public SpecOperation getOperation() {
        return operation;
    }

    public void setOperation(final SpecOperation operation) {
        this.operation = operation;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(final Object value) {
        this.value = value;
    }

    public boolean isOrPredicate() {
        return orPredicate;
    }

    public void setOrPredicate(boolean orPredicate) {
        this.orPredicate = orPredicate;
    }

}

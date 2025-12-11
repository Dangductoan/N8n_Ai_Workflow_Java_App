package ntt.common.jpa.criteria;

public enum SpecOperation {
    EQUAL, NOT_EQUAL,
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL,
    LESS_THAN,
    LESS_THAN_OR_EQUAL,
    LIKE,
    STARTS_WITH,
    ENDS_WITH,
    CONTAINS,
    IN,
    IS_NULL;

    public static final String OR_PREDICATE_FLAG = "or";

}
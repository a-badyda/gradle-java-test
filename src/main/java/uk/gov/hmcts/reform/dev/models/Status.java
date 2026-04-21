package uk.gov.hmcts.reform.dev.models;

//technically risky, would need to check in with product etc to know what statuses are valid.
public enum Status {
    PENDING,
    IN_PROGRESS,
    STOPPED,
    CANCELLED,
    COMPLETED,
    DELETED //in case we want to 'delete' records from user view as filter but without actually getting rid of them
}

package uk.gov.hmcts.reform.dev.exceptions;

public class CaseNotFound extends RuntimeException {

    public CaseNotFound(String id) {
        super("Case ID: " + id);
    }
}

package uk.gov.hmcts.reform.dev.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * whenever a specific case cannot be found by resourceValue, use this exception.
 */
@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CaseNotFound extends RuntimeException {

    private final String errorCode = "CASE_NOT_FOUND";
    private final String resourceType;
    private final String resourceValue;

    // Original ID constructor
    public CaseNotFound(String id) {
        super("Case with ID " + id + " could not be found");
        this.resourceType = "id";
        this.resourceValue = id;
    }

    private CaseNotFound(String field, String value) {
        super("Case with " + field + " '" + value + "' could not be found");
        this.resourceType = field;
        this.resourceValue = value;
    }

    // New Case Number constructor
    public static CaseNotFound byCaseNumber(String caseNumber) {
        return new CaseNotFound("caseNumber", caseNumber);
    }
}

package uk.gov.hmcts.reform.dev.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CaseNotFound extends RuntimeException {

    private final String errorCode = "CASE_NOT_FOUND"; // Machine-readable code for JS
    private final String resourceId;

    public CaseNotFound(String id) {
        super("Case with ID " + id + " could not be found"); // Human-readable message
        this.resourceId = id;
    }
}

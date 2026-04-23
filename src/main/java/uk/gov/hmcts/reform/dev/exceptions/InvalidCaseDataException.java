package uk.gov.hmcts.reform.dev.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
/**
 * Handles any issues with validation - contains all issues in an object in a map of errors
 */
public class InvalidCaseDataException extends RuntimeException {

    private final Map<String, String> errors;

    public InvalidCaseDataException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }
}

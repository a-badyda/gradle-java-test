package uk.gov.hmcts.reform.dev.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
/**
 * handle all exceptions globally
 */
public class GlobalExceptionHandler {

    @ExceptionHandler(CaseNotFound.class)
    public ResponseEntity<Map<String, Object>> handleCaseNotFound(CaseNotFound ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("code", ex.getErrorCode());
        body.put("message", ex.getMessage());
        body.put(
            "details", Map.of(
                "field", ex.getResourceType(),
                "value", ex.getResourceValue()
            )
        );

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(InvalidCaseDataException.class)
    public ResponseEntity<Object> handleInvalidCaseData(InvalidCaseDataException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("fieldErrors", ex.getErrors());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}

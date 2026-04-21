package uk.gov.hmcts.reform.dev.validation;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.dev.exceptions.InvalidCaseDataException;
import uk.gov.hmcts.reform.dev.models.NewCaseDTO;
import uk.gov.hmcts.reform.dev.models.UpdateCaseDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class CaseValidator {

    private static final Pattern MALICIOUS_CHARS = Pattern.compile(
        "<script>|javascript:|onerror=",
        Pattern.CASE_INSENSITIVE
    );
    private static final int MAX_TITLE_LENGTH = 250;
    private static final int MAX_DESC_LENGTH = 2000;

    public void validateNewCase(NewCaseDTO request) {
        Map<String, String> errors = new HashMap<>();

        validateCommonFields(request.getTitle(), request.getDescription(), errors);
        validateAndParseDate(request.getDueDate(), errors);

        if (request.getCaseNumber() == null || request.getCaseNumber().isBlank()) {
            errors.put("caseNumber", "Case number is mandatory");
        }

        if (!errors.isEmpty()) {
            throw new InvalidCaseDataException("Validation failed for new case", errors);
        }
    }

    //todo: could probably restructure because the update and add new are so similar
    public void validateUpdate(UpdateCaseDTO request) {
        Map<String, String> errors = new HashMap<>();

        validateCommonFields(request.getTitle(), request.getDescription(), errors);
        validateAndParseDate(request.getDueDate(), errors);

        if (!errors.isEmpty()) {
            throw new InvalidCaseDataException("Validation failed for new case", errors);
        }
    }

    private void validateCommonFields(String title, String description, Map<String, String> errors) {
        // Title validation
        if (title != null) {
            if (title.length() > MAX_TITLE_LENGTH) {
                errors.put("title", "Title exceeds maximum length of " + MAX_TITLE_LENGTH + " characters");
            }
            if (MALICIOUS_CHARS.matcher(title).find()) {
                errors.put("title", "Title contains disallowed characters or patterns (OWASP A03)");
            }
        }

        // Description validation
        if (description != null) {
            if (description.length() > MAX_DESC_LENGTH) {
                errors.put("description", "Description exceeds maximum length of " + MAX_DESC_LENGTH + " characters");
            }
            if (MALICIOUS_CHARS.matcher(description).find()) {
                errors.put("description", "Description contains disallowed characters or patterns");
            }
        }
    }

    private void validateAndParseDate(String dateStr, Map<String, String> errors) {
        if (dateStr == null || dateStr.isBlank()) {
            errors.put("dueDate", "Due date is mandatory");
            return;
        }

        try {
            LocalDateTime parsedDate = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
            if (parsedDate.isBefore(LocalDateTime.now())) {
                errors.put("dueDate", "Due date cannot be in the past");
            }
        } catch (DateTimeParseException e) {
            errors.put("dueDate", "Invalid date format. Expected ISO-8601 (e.g., 2026-12-31T23:59:59.000Z)");
        }
    }
}

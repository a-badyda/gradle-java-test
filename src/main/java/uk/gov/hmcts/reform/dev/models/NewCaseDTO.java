package uk.gov.hmcts.reform.dev.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class NewCaseDTO {

    @NotBlank(message = "Case number is required")
    private String caseNumber;
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
}

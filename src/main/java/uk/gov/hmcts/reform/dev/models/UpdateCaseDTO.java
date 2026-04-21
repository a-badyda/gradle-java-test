package uk.gov.hmcts.reform.dev.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class UpdateCaseDTO {

    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    @NotNull(message = "Status is required")
    private Status status;
    @NotBlank(message = "due date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private String dueDate;
}

package uk.gov.hmcts.reform.dev.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class CaseDTO {

    private String id;
    private String caseNumber;
    private String title;
    private String description;
    private String status;
    //string more readable for output in json
    private String createdDate;
}

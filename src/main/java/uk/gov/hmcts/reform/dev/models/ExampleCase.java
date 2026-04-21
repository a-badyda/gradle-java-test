package uk.gov.hmcts.reform.dev.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
//todo - turn to record?
public class ExampleCase {
    // i don't really want to mess with the schema in case other services already agreed upon it
    // but I do at least want to add the last updated
    private int id;
    //todo
    //private String assignedTo; would seem p critical for a case to know who if anyone is managing it
    //ideally would include an actual audit trail, like createdBy/ lastUpdatedBy too
    private String caseNumber;
    private String title;
    private String description;
    @Builder.Default
    private Status status = Status.PENDING;
    @Builder.Default
    private LocalDateTime createdDate = LocalDateTime.now(); //timezone risk
    @Builder.Default
    private LocalDateTime lastUpdatedDate = LocalDateTime.now();
}

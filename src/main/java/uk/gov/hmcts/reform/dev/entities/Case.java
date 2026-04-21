package uk.gov.hmcts.reform.dev.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.dev.models.Status;

@Entity
@Table(name = "cases")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Case extends BaseEntity {

    //private String assignedTo; would seem p critical but I don't have the time to add all of that
    private String caseNumber;
    private String title;
    private String description;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

}

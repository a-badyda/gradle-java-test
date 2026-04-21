package uk.gov.hmcts.reform.dev.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.hmcts.reform.dev.models.Status;

@Entity
@Table(name = "cases")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Case extends BaseEntity {

    //private String assignedTo; seem p critical info but I don't have the time to add all of that
    private String caseNumber;
    private String title;
    private String description;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

}

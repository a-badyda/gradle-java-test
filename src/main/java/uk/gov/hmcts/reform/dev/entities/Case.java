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

import java.time.LocalDateTime;

@Entity
@Table(name = "cases")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Case extends BaseEntity {

    //i'd like to add a field of 'assignedTo' but I can't know that it'll be coming back from UI
    private String caseNumber;
    private String title;
    private String description;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;
    private LocalDateTime dueDate;

}

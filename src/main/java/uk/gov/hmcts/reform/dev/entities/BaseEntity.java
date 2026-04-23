package uk.gov.hmcts.reform.dev.entities;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@Data
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Default DB entity - groups any common values like id, created and last updated dates.
 * contains any shared methods
 */
public abstract class BaseEntity {

    @Id
    @Column(length = 26)
    private String id;
    @Builder.Default
    private LocalDateTime createdDate = LocalDateTime.now(); //timezone risk
    @Builder.Default
    private LocalDateTime lastUpdatedDate = LocalDateTime.now();
    //i'd like to add user logs too like 'createdBy' and 'updatedBy' but don't have the time to add users / roles
    //private String createdBy;
    //private String lastUpdatedBy;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UlidCreator.getUlid().toString();
        }
    }
}

package uk.gov.hmcts.reform.dev.entities;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
public abstract class BaseEntity {

    // i don't really want to mess with the schema in case other services already agreed upon it
    // but I do at least want to make it a bit more observable
    @Id
    @Column(length = 26) // ULIDs are always 26 characters
    private String id;

    private LocalDateTime createdDate = LocalDateTime.now(); //timezone risk
    private LocalDateTime lastUpdatedDate = LocalDateTime.now();

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            // Generates a sortable ULID: e.g., 01ARZ3NDEKTSV4RRFFQ6KHNQES
            this.id = UlidCreator.getUlid().toString();
        }
    }
}

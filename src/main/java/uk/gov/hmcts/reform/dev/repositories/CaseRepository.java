package uk.gov.hmcts.reform.dev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.dev.entities.Case;

import java.util.Optional;

@Repository
public interface CaseRepository extends JpaRepository<Case, String> {

    Optional<Case> findByCaseNumber(String caseNumber);
}

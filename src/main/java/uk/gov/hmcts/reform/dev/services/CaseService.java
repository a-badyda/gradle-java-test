package uk.gov.hmcts.reform.dev.services;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.dev.entities.Case;
import uk.gov.hmcts.reform.dev.exceptions.CaseNotFound;
import uk.gov.hmcts.reform.dev.mappers.CaseMapper;
import uk.gov.hmcts.reform.dev.models.CaseDTO;
import uk.gov.hmcts.reform.dev.models.NewCaseDTO;
import uk.gov.hmcts.reform.dev.models.Status;
import uk.gov.hmcts.reform.dev.models.UpdateCaseDTO;
import uk.gov.hmcts.reform.dev.repositories.CaseRepository;
import uk.gov.hmcts.reform.dev.validation.CaseValidator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
/**
 * Case Service manager - contains all CRUD methods, along with some specialised searches.
 * Delegates validation to @Class CaseValidator
 */
public class CaseService {

    private final CaseRepository caseRepository;
    private final CaseMapper caseMapper;
    private final CaseValidator caseValidator;

    @Cacheable(value = "cases", key = "#id")
    public CaseDTO findById(String id) {
        var found = caseRepository.findById(id)
            .orElseThrow(() -> new CaseNotFound(id));
        return caseMapper.toDto(found);
    }

    public CaseDTO findByCaseNumber(String caseNumber) {
        var found = caseRepository.findByCaseNumber(caseNumber)
            .orElseThrow(() -> CaseNotFound.byCaseNumber(caseNumber));
        return caseMapper.toDto(found);
    }

    public Page<CaseDTO> getAllCases(Pageable pageable) {
        return caseRepository.findAll(pageable).map(caseMapper::toDto);
    }

    @Transactional
    public CaseDTO add(NewCaseDTO request) {

        caseValidator.validateNewCase(request);

        Case newCase = Case.builder()
            .caseNumber(request.getCaseNumber())
            .title(request.getTitle())
            .description(request.getDescription())
            .dueDate(LocalDateTime.parse(request.getDueDate(), DateTimeFormatter.ISO_DATE_TIME))
            .build();

        Case savedCase = caseRepository.save(newCase);
        return caseMapper.toDto(savedCase);
    }

    @Transactional
    @CachePut(value = "cases", key = "#id")
    public CaseDTO update(String id, UpdateCaseDTO request) {

        caseValidator.validateUpdate(request);
        CaseDTO existingCase = this.findById(id);

        Case updateCase = Case.builder()
            .id(id)
            .caseNumber(existingCase.getCaseNumber())
            .description(request.getDescription())
            .title(request.getTitle())
            .status(request.getStatus())
            .dueDate(LocalDateTime.parse(request.getDueDate(), DateTimeFormatter.ISO_DATE_TIME))
            .lastUpdatedDate(LocalDateTime.now())
            .build();

        Case savedCase = caseRepository.save(updateCase);

        return caseMapper.toDto(savedCase);
    }

    //sometimes you may want a record to be deleted for users view/interaction
    // but for legal reasons to continue to exist.
    public CaseDTO setStatusToDeleted(String id) {
        CaseDTO existingCase = this.findById(id);
        return this.update(
            id,
            new UpdateCaseDTO(
                existingCase.getTitle(),
                existingCase.getDescription(),
                Status.DELETED,
                existingCase.getDueDate()
            )
        );
    }

    //a method to actually delete the record - for GDPR reasons usually
    @Transactional
    @CacheEvict(value = "cases", key = "#id")
    public void actuallyDelete(String id) {
        if (!caseRepository.existsById(id)) {
            throw new CaseNotFound(id);
        }
        caseRepository.deleteById(id);
    }
}

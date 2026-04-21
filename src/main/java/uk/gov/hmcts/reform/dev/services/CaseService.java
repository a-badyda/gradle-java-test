package uk.gov.hmcts.reform.dev.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.dev.entities.Case;
import uk.gov.hmcts.reform.dev.exceptions.CaseNotFound;
import uk.gov.hmcts.reform.dev.mappers.CaseMapper;
import uk.gov.hmcts.reform.dev.models.CaseDTO;
import uk.gov.hmcts.reform.dev.models.NewCaseDTO;
import uk.gov.hmcts.reform.dev.repositories.CaseRepository;

@Service
@RequiredArgsConstructor
public class CaseService {

    private final CaseRepository caseRepository;
    private final CaseMapper caseMapper;

    public CaseDTO getById(String id) {
        var found = caseRepository.findById(id)
            .orElseThrow(() -> new CaseNotFound(id));
        return caseMapper.toDto(found);
    }

    public Page<CaseDTO> getAllCases(Pageable pageable) {
        return caseRepository.findAll(pageable).map(caseMapper::toDto);
    }

    @Transactional
    public CaseDTO add(NewCaseDTO request) {

        //todo some kind of validation class too for the inputs ideally

        Case newCase = Case.builder()
            .caseNumber(request.getCaseNumber())
            .title(request.getTitle())
            .description(request.getDescription())
            .build();

        Case savedCase = caseRepository.save(newCase);

        return caseMapper.toDto(savedCase);
    }

}

package uk.gov.hmcts.reform.dev.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import uk.gov.hmcts.reform.dev.entities.Case;
import uk.gov.hmcts.reform.dev.exceptions.CaseNotFound;
import uk.gov.hmcts.reform.dev.exceptions.InvalidCaseDataException;
import uk.gov.hmcts.reform.dev.mappers.CaseMapper;
import uk.gov.hmcts.reform.dev.models.CaseDTO;
import uk.gov.hmcts.reform.dev.models.NewCaseDTO;
import uk.gov.hmcts.reform.dev.models.Status;
import uk.gov.hmcts.reform.dev.models.UpdateCaseDTO;
import uk.gov.hmcts.reform.dev.repositories.CaseRepository;
import uk.gov.hmcts.reform.dev.validation.CaseValidator;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaseServiceTest {

    @Mock
    private CaseRepository caseRepository;
    @Mock
    private CaseMapper caseMapper;
    @Mock
    private CaseValidator caseValidator;

    @InjectMocks
    private CaseService caseService;

    @Test
    @DisplayName("Should return CaseDTO when valid ID is provided")
    void getById_Success() {
        String id = "01KPRC5WE2CHJBD275T3CRQHH2";
        Case entity = Case.builder().id(id).title("Test Case").build();
        CaseDTO dto = CaseDTO.builder().id(id).title("Test Case").build();

        when(caseRepository.findById(id)).thenReturn(Optional.of(entity));
        when(caseMapper.toDto(entity)).thenReturn(dto);

        CaseDTO result = caseService.getById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(caseRepository).findById(id);
    }

    @Test
    @DisplayName("Should throw CaseNotFound exception when case does not exist")
    void getById_NotFound() {
        String id = "invalid-id";
        when(caseRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CaseNotFound.class, () -> caseService.getById(id));
    }

    @Test
    @DisplayName("Should validate and save new case")
    void add_Success() {
        NewCaseDTO request = NewCaseDTO.builder()
            .caseNumber("ABC-123")
            .title("New Title")
            .dueDate("2026-12-31T23:59:59.000Z")
            .build();

        Case.builder().id("generated-bulid").build();

        caseService.add(request);

        verify(caseValidator).validateNewCase(request); // Verify security check was called
        verify(caseRepository).save(any(Case.class));
    }

    @Test
    @DisplayName("Should return paged results")
    void getAllCases_Success() {
        Pageable pageable = Pageable.unpaged();
        List<Case> cases = List.of(new Case());
        Page<Case> casePage = new PageImpl<>(cases);

        when(caseRepository.findAll(pageable)).thenReturn(casePage);
        when(caseMapper.toDto(any())).thenReturn(CaseDTO.builder().build());

        Page<CaseDTO> result = caseService.getAllCases(pageable);

        assertEquals(1, result.getTotalElements());
        verify(caseRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should fail when date string contains script tags (A03 Injection)")
    void add_DateInjectionFail() {
        NewCaseDTO request = NewCaseDTO.builder()
            .caseNumber("ABC-123")
            .dueDate("<script>alert('xss')</script>") // Malicious payload
            .build();

        doThrow(new InvalidCaseDataException(
            "Validation failed for new case",
            Map.of("dueDate", "Invalid date format. Expected ISO-8601 (e.g., 2026-12-31T23:59:59.000Z)")
        )).when(caseValidator).validateNewCase(request);

        assertThrows(InvalidCaseDataException.class, () -> caseService.add(request));
    }

    @Test
    @DisplayName("Should fail when date format is garbage")
    void add_DateFormatFail() {
        NewCaseDTO request = NewCaseDTO.builder()
            .caseNumber("ABC-123")
            .dueDate("not-a-date")
            .build();

        doThrow(new InvalidCaseDataException(
            "Validation failed for new case",
            Map.of("dueDate", "Invalid date format. Expected ISO-8601 (e.g., 2026-12-31T23:59:59.000Z)")
        )).when(caseValidator).validateNewCase(request);

        assertThrows(InvalidCaseDataException.class, () -> caseService.add(request));
    }

    @Test
    @DisplayName("Should successfully update an existing case")
    void update_Success() {

        String id = "01KPRC5WE2CHJBD275T3CRQHH2";
        UpdateCaseDTO request = UpdateCaseDTO.builder()
            .title("Updated Title")
            .description("Updated Description")
            .status(Status.IN_PROGRESS)
            .dueDate("2026-12-31T23:59:59.000Z")
            .build();

        Case existingEntity = Case.builder()
            .id(id)
            .caseNumber("ABC-123")
            .build();

        Case savedEntity = Case.builder()
            .id(id)
            .title(request.getTitle())
            .build();

        CaseDTO expectedDto = CaseDTO.builder()
            .id(id)
            .title(request.getTitle())
            .build();

        when(caseRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(caseMapper.toDto(existingEntity)).thenReturn(CaseDTO.builder().caseNumber("ABC-123").build());

        when(caseRepository.save(any(Case.class))).thenReturn(savedEntity);
        when(caseMapper.toDto(savedEntity)).thenReturn(expectedDto);

        CaseDTO result = caseService.update(id, request);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        verify(caseValidator).validateUpdate(request);
        verify(caseRepository).save(argThat(c ->
                                                c.getId().equals(id)
                                                    && c.getStatus().equals(Status.IN_PROGRESS)
        ));
    }

    @Test
    @DisplayName("Update should fail if description contains XSS (OWASP A03)")
    void update_XssInDescription_Fail() {
        String id = "01KPRC5WE2CHJBD275T3CRQHH2";
        UpdateCaseDTO request = UpdateCaseDTO.builder()
            .title("Normal Title")
            .description("<img src=x onerror=alert(1)>") // XSS Payload
            .status(Status.IN_PROGRESS)
            .dueDate("2026-12-31T23:59:59.000Z")
            .build();

        doThrow(new InvalidCaseDataException(
            "Validation failed for update case",
            Map.of("dueDate", "Invalid date format. Expected ISO-8601 (e.g., 2026-12-31T23:59:59.000Z)")
        )).when(caseValidator).validateUpdate(request);

        assertThrows(InvalidCaseDataException.class, () -> caseService.update(id, request));
        verify(caseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update should fail if title is excessively long (OWASP A04)")
    void update_TitleTooLong_Fail() {
        String id = "01KPRC5WE2CHJBD275T3CRQHH2";
        String longTitle = "A".repeat(251); // One over the limit
        UpdateCaseDTO request = UpdateCaseDTO.builder()
            .title(longTitle)
            .status(Status.IN_PROGRESS)
            .dueDate("2026-12-31T23:59:59.000Z")
            .build();

        doThrow(new InvalidCaseDataException(
            "Validation failed for update case",
            Map.of(
                "title",
                "Description exceeds maximum length of 250 characters"
            )
        )).when(caseValidator).validateUpdate(request);

        assertThrows(InvalidCaseDataException.class, () -> caseService.update(id, request));
    }

    @Test
    @DisplayName("Update should fail if dueDate format is invalid")
    void update_InvalidDateFormat_Fail() {
        String id = "01KPRC5WE2CHJBD275T3CRQHH2";
        UpdateCaseDTO request = UpdateCaseDTO.builder()
            .title("Title")
            .status(Status.IN_PROGRESS)
            .dueDate("31-12-2026") // Wrong format (Not ISO)
            .build();

        doThrow(new InvalidCaseDataException(
            "Validation failed for update case",
            Map.of("dueDate", "Invalid date format. Expected ISO-8601 (e.g., 2026-12-31T23:59:59.000Z)")
        )).when(caseValidator).validateUpdate(request);

        assertThrows(InvalidCaseDataException.class, () -> caseService.update(id, request));
    }

    @Test
    @DisplayName("Update should fail if Case ID does not exist (IDOR Prevention)")
    void update_CaseNotFound_Fail() {
        String id = "non-existent-id";
        UpdateCaseDTO request = UpdateCaseDTO.builder()
            .title("Title")
            .status(Status.IN_PROGRESS)
            .dueDate("2026-12-31T23:59:59.000Z")
            .build();

        when(caseRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CaseNotFound.class, () -> caseService.update(id, request));
        verify(caseValidator).validateUpdate(request); // Validator should still run first
        verify(caseRepository, never()).save(any());
    }


    @Test
    @DisplayName("Should successfully delete when ID exists")
    void actuallyDelete_Success() {
        String id = "01KPRC5WE2CHJBD275T3CRQHH2";

        when(caseRepository.existsById(id)).thenReturn(true);
        doNothing().when(caseRepository).deleteById(id);

        assertDoesNotThrow(() -> caseService.actuallyDelete(id));

        verify(caseRepository).existsById(id);
        verify(caseRepository).deleteById(id);
    }

    @Test
    @DisplayName("Delete should throw CaseNotFound when ID does not exist")
    void actuallyDelete_NotFound_Fail() {
        String id = "non-existent-ulid";

        when(caseRepository.existsById(id)).thenReturn(false);
        assertThrows(CaseNotFound.class, () -> caseService.actuallyDelete(id));

        verify(caseRepository, never()).deleteById(anyString());
        verify(caseRepository).existsById(id);
    }

}

package uk.gov.hmcts.reform.dev.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.dev.exceptions.CaseNotFound;
import uk.gov.hmcts.reform.dev.exceptions.InvalidCaseDataException;
import uk.gov.hmcts.reform.dev.models.CaseDTO;
import uk.gov.hmcts.reform.dev.models.NewCaseDTO;
import uk.gov.hmcts.reform.dev.models.Status;
import uk.gov.hmcts.reform.dev.models.UpdateCaseDTO;
import uk.gov.hmcts.reform.dev.services.CaseService;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@WebMvcTest(CaseController.class)
class CaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CaseService caseService;

    @Test
    @DisplayName("GET /v1/cases - Should return paginated cases")
    void shouldReturnPaginatedCases() throws Exception {
        CaseDTO caseDto = CaseDTO.builder()
            .id("01KPRC5WE2CHJBD275T3CRQHH2")
            .caseNumber("ABC12345")
            .title("Test Case")
            .status("PENDING")
            .build();

        Page<CaseDTO> page = new PageImpl<>(List.of(caseDto));

        when(caseService.getAllCases(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/v1/cases")
                            .param("page", "0")
                            .param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].caseNumber").value("ABC12345"))
            .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /v1/cases/{id} - Should return a single case")
    void shouldReturnCaseById() throws Exception {
        String id = "01KPRC5WE2CHJBD275T3CRQHH2";
        CaseDTO caseDto = CaseDTO.builder()
            .id(id)
            .title("Specific Case")
            .build();

        when(caseService.getById(id)).thenReturn(caseDto);

        mockMvc.perform(get("/v1/cases/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.title").value("Specific Case"));
    }

    @Test
    @DisplayName("POST /v1/cases/add - Should create a case and return 201")
    void shouldCreateCase() throws Exception {
        NewCaseDTO request = NewCaseDTO.builder()
            .caseNumber("NEW-999")
            .title("New Case Title")
            .dueDate("2026-12-31T23:59:59.000Z")
            .build();

        CaseDTO created = CaseDTO.builder()
            .id("NEW-ULID-123")
            .caseNumber("NEW-999")
            .build();

        when(caseService.add(any(NewCaseDTO.class))).thenReturn(created);

        mockMvc.perform(post("/v1/cases/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").value("NEW-ULID-123"));
    }

    @Test
    @DisplayName("PUT /v1/cases/update/{id} - Should update case and return 200")
    void shouldUpdateCase() throws Exception {
        String id = "EXISTING-ID";
        UpdateCaseDTO request = UpdateCaseDTO.builder()
            .title("Updated Title")
            .status(Status.IN_PROGRESS)
            .dueDate("2026-12-31T23:59:59.000Z")
            .build();

        CaseDTO updated = CaseDTO.builder().id(id).title("Updated Title").build();

        when(caseService.update(eq(id), any(UpdateCaseDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/v1/cases/update/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    @DisplayName("DELETE /v1/cases/delete/{id} - Should return 200 OK")
    void shouldDeleteCase() throws Exception {
        String id = "DELETE-ME";
        doNothing().when(caseService).actuallyDelete(id);

        mockMvc.perform(delete("/v1/cases/delete/{id}", id))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /v1/cases/{id} - Should return 404 when case not found")
    void shouldReturn404WhenNotFound() throws Exception {
        String id = "missing-id";
        when(caseService.getById(id)).thenThrow(new CaseNotFound(id));

        mockMvc.perform(get("/v1/cases/{id}", id))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /v1/cases/add - Should return 400 on validation failure")
    void shouldReturn400OnInvalidInput() throws Exception {
        // NewCaseDTO requires caseNumber and title
        NewCaseDTO invalidRequest = NewCaseDTO.builder()
            .description("Missing required fields")
            .build();

        mockMvc.perform(post("/v1/cases/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /v1/cases/add - Should return 400 when required fields are missing")
    void shouldReturn400WhenAddRequestIsInvalid() throws Exception {
        // Missing 'title' and 'caseNumber' which are @NotBlank
        NewCaseDTO invalidRequest = NewCaseDTO.builder()
            .description("Some description")
            .build();

        mockMvc.perform(post("/v1/cases/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /v1/cases/update/{id} - Should return 400 when status is null")
    void shouldReturn400WhenUpdateStatusIsNull() throws Exception {
        String id = "some-id";
        // Status is @NotNull in UpdateCaseDTO
        UpdateCaseDTO invalidRequest = UpdateCaseDTO.builder()
            .title("Valid Title")
            .status(null)
            .dueDate("2026-12-31T23:59:59.000Z")
            .build();

        mockMvc.perform(put("/v1/cases/update/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /v1/cases/add - Should return 400 when title is too long")
    void shouldReturn400WhenTitleExceedsLimit() throws Exception {
        String longTitle = "A".repeat(251); // Exceeds MAX_TITLE_LENGTH
        NewCaseDTO request = NewCaseDTO.builder()
            .caseNumber("TEST-1")
            .title(longTitle)
            .dueDate("2026-12-31T23:59:59.000Z")
            .build();

        when(caseService.add(request)).thenThrow(new InvalidCaseDataException(
            "message",
            Map.of("title","Title exceeds maximum length of 250 characters")
        ));

        mockMvc.perform(post("/v1/cases/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.title").exists());
    }
}


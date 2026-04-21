package uk.gov.hmcts.reform.dev.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.hmcts.reform.dev.models.CaseDTO;
import uk.gov.hmcts.reform.dev.models.NewCaseDTO;
import uk.gov.hmcts.reform.dev.services.CaseService;

import java.net.URI;

import static org.springframework.http.ResponseEntity.ok;

@RestController()
@RequestMapping("/v1/cases")
public class CaseController {

    @Autowired
    CaseService caseService;

    //todo - add parameters for filtering - eg status / to-from date / search by title starting with?
    @Operation(summary = "Get all cases with pagination")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved cases")
    @GetMapping(value = "/get-all", produces = "application/json")
    public ResponseEntity<Page<CaseDTO>> getAllCases(
        @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC)
        Pageable pageable) {

        Page<CaseDTO> cases = caseService.getAllCases(pageable);
        return ok(cases);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<CaseDTO> getById(@PathVariable String id) {
        return ok(caseService.getById(id));
    }

    @PostMapping(value = "/add", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Create a new case")
    public ResponseEntity<CaseDTO> addCase(@Valid @RequestBody NewCaseDTO request) {
        CaseDTO createdCase = caseService.add(request);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdCase.getId())
            .toUri();

        return ResponseEntity.created(location).body(createdCase);
    }
    //
    //    @PutMapping(value = "/update{id}", produces = "application/json")
    //    public ResponseEntity<CaseDTO> updateCase(@PathVariable int id, ) {
    //        return ok(new CaseDTO(1, "ABC12345", "Case Title",
    //                                  "Case Description", "Case Status", LocalDateTime.now()
    //        ));
    //    }
    //
    //    //actually deleting forever seems like a bad idea, just a system status instead maybe?
    //    //or just logs idk
    //    @DeleteMapping(value = "/delete{id}", produces = "application/json")
    //    public ResponseEntity<?> deleteCase(@PathVariable int id) {
    //        return ResponseEntity.ok(caseService.delete(id))
    //            .orElseGet(() -> notFound().build());
    //    }
}

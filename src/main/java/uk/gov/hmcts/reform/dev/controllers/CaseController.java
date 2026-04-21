package uk.gov.hmcts.reform.dev.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.reform.dev.models.ExampleCase;
import uk.gov.hmcts.reform.dev.models.Status;
import uk.gov.hmcts.reform.dev.services.CaseService;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController()
@RequestMapping("/v1/cases")
public class CaseController {

    @Autowired
    CaseService caseService;

    //todo removeme
    @GetMapping(value = "/get-example-case", produces = "application/json")
    public ResponseEntity<ExampleCase> getExampleCase() {
        return ok(new ExampleCase(1, "ABC12345", "Case Title",
                                  "Case Description", Status.PENDING, LocalDateTime.now(), LocalDateTime.now()
        ));
    }

    //todo - change to paginated results
    //todo - add parameters for filtering - eg status / to-from date / search by title starting with?
    @GetMapping(value = "/get-all", produces = "application/json")
    public ResponseEntity<List<ExampleCase>> getCases() {
        return ok(List.of((new ExampleCase(1, "ABC12345", "Case Title",
                                           "Case Description", Status.PENDING, LocalDateTime.now(), LocalDateTime.now()
        ))));
    }

}

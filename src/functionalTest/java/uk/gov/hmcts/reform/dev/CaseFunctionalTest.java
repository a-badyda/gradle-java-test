package uk.gov.hmcts.reform.dev;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.reform.dev.models.NewCaseDTO;
import uk.gov.hmcts.reform.dev.models.Status;
import uk.gov.hmcts.reform.dev.models.UpdateCaseDTO;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class CaseFunctionalTest {

    @Value("${TEST_URL:http://localhost:8080}")
    private String testUrl;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    @DisplayName("Should create, retrieve, and delete a case successfully")
    void fullCaseLifecycleTest() {
        NewCaseDTO newCase = NewCaseDTO.builder()
            .caseNumber("FN-12345")
            .title("Functional Test Case")
            .description("Verifying end-to-end flow")
            .dueDate("2026-12-31T23:59:59.000Z")
            .build();

        Response postResponse = given()
            .contentType(ContentType.JSON)
            .body(newCase)
            .when()
            .post("/v1/cases/add")
            .then()
            .statusCode(201)
            .body("caseNumber", equalTo("FN-12345"))
            .extract().response();

        String caseId = postResponse.jsonPath().getString("id");

        given()
            .contentType(ContentType.JSON)
            .when()
            .get("/v1/cases/{id}", caseId)
            .then()
            .statusCode(200)
            .body("id", equalTo(caseId))
            .body("title", equalTo("Functional Test Case"));

        UpdateCaseDTO updateRequest = UpdateCaseDTO.builder()
            .title("Updated Functional Title")
            .status(Status.DRAFT)
            .dueDate("2027-01-01T12:00:00.000Z")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(updateRequest)
            .when()
            .put("/v1/cases/update/{id}", caseId)
            .then()
            .statusCode(200)
            .body("status", equalTo("SUBMITTED"))
            .body("title", equalTo("Updated Functional Title"));

        given()
            .when()
            .delete("/v1/cases/delete/{id}", caseId)
            .then()
            .statusCode(200);

        given()
            .when()
            .get("/v1/cases/{id}", caseId)
            .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Should return 400 when creating case with invalid data (OWASP A04)")
    void createCase_ValidationFailure() {
        // Missing mandatory title and caseNumber
        NewCaseDTO invalidCase = NewCaseDTO.builder()
            .description("Missing fields")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(invalidCase)
            .when()
            .post("/v1/cases/add")
            .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Should prevent SQL Injection/XSS in search or input fields (OWASP A03)")
    void security_InputSanitizationTest() {
        NewCaseDTO maliciousCase = NewCaseDTO.builder()
            .caseNumber("SQL-INJ")
            .title("Case' OR '1'='1") // Common SQL Injection attempt
            .description("<script>alert('XSS')</script>") // Stored XSS attempt
            .dueDate("2026-12-31T23:59:59.000Z")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(maliciousCase)
            .when()
            .post("/v1/cases/add")
            .then()
            .statusCode(400);
    }
}

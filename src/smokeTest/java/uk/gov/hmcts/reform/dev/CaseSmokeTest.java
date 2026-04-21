package uk.gov.hmcts.reform.dev;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CaseSmokeTest {

    @Value("${TEST_URL:http://localhost:8080}")
    private String testUrl;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    @DisplayName("Smoke Test: Verify the Case API is reachable and returning data")
    void shouldRetrieveCasesFromDatabase() {
        given()
            .contentType(ContentType.JSON)
            .when()
            .get("/v1/cases") //
            .then()
            .statusCode(200) // Confirms the controller and service are wired
            .body("content", notNullValue()) // Confirms DB connectivity and pagination
            .log().ifValidationFails();
    }
}

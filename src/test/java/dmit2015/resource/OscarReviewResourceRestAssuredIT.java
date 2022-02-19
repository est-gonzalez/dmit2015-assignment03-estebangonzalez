package dmit2015.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import dmit2015.estebangonzalez.assignment03.entity.OscarReview;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

/**
 * https://github.com/rest-assured/rest-assured
 * https://github.com/rest-assured/rest-assured/wiki/Usage
 * http://www.mastertheboss.com/jboss-frameworks/resteasy/restassured-tutorial
 * https://eclipse-ee4j.github.io/jsonb-api/docs/user-guide.html
 * https://github.com/FasterXML/jackson-databind
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OscarReviewResourceRestAssuredIT {

    String reviewsResourceUrl = "http://localhost:8080/dmit2015-1212-oe01-assignment03-est-gonzalez/OscarReviews";
    String testDataResourceLocation;

    @Order(1)
    @Test
    void shouldListAll() throws JsonProcessingException {
        Response response = given()
//                .accept(ContentType.JSON)
                .when()
                .get(reviewsResourceUrl)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();
        String jsonBody = response.getBody().asString();

        Jsonb jsonb = JsonbBuilder.create();
        List<OscarReview> reviews = jsonb.fromJson(jsonBody, new ArrayList<OscarReview>(){}.getClass().getGenericSuperclass());

        assertEquals(4, reviews.size());
        OscarReview firstOscarReview = reviews.get(0);
        assertEquals("Film", firstOscarReview.getCategory());
        assertEquals("House of Gucci", firstOscarReview.getNominee());
        assertEquals("Thrilling to watch", firstOscarReview.getReview());
        assertEquals("GucciLover", firstOscarReview.getUsername());
        assertEquals(false, firstOscarReview.isComplete());

        OscarReview lastOscarReview = reviews.get(reviews.size() - 1);
        assertEquals("Film", firstOscarReview.getCategory());
        assertEquals("Eternals", firstOscarReview.getNominee());
        assertEquals("Not as good as Endgame", firstOscarReview.getReview());
        assertEquals("HarshReviewer", firstOscarReview.getUsername());
        assertEquals(false, lastOscarReview.isComplete());

    }

    @Order(2)
    @Test
    void shouldCreate() throws JsonProcessingException {
        OscarReview newOscarReview = new OscarReview();
        newOscarReview.setCategory("New category");
        newOscarReview.setNominee("New nominee");
        newOscarReview.setReview("New review");
        newOscarReview.setUsername("New username");
        newOscarReview.setComplete(false);

        Jsonb jsonb = JsonbBuilder.create();
        String jsonBody = jsonb.toJson(newOscarReview);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .post(reviewsResourceUrl)
                .then()
                .statusCode(201)
                .extract()
                .response();
        testDataResourceLocation = response.getHeader("location");
    }

    @Order(3)
    @Test
    void shouldFineOne() throws JsonProcessingException {
        Response response = given()
                .accept(ContentType.JSON)
                .when()
                .get(testDataResourceLocation)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();
        String jsonBody = response.getBody().asString();

        Jsonb jsonb = JsonbBuilder.create();
        OscarReview existingOscarReview = jsonb.fromJson(jsonBody, OscarReview.class);

        assertNotNull(existingOscarReview);
        assertEquals("New Category", existingOscarReview.getCategory());
        assertEquals("New Nominee", existingOscarReview.getNominee());
        assertEquals("New Review", existingOscarReview.getReview());
        assertEquals("New Username", existingOscarReview.getUsername());
        assertFalse(existingOscarReview.isComplete());
    }

    @Order(4)
    @Test
    void shouldUpdate() throws JsonProcessingException {
        Response response = given()
                .accept(ContentType.JSON)
                .when()
                .get(testDataResourceLocation)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();

        String jsonBody = response.getBody().asString();

        Jsonb jsonb = JsonbBuilder.create();
        OscarReview existingOscarReview = jsonb.fromJson(jsonBody, OscarReview.class);

        assertNotNull(existingOscarReview);
        existingOscarReview.setCategory("Updated Category");
        existingOscarReview.setNominee("Updated Nominee");
        existingOscarReview.setReview("Updated Review");
        existingOscarReview.setUsername("Updated Username");
        existingOscarReview.setComplete(true);

        String jsonRequestBody = jsonb.toJson(existingOscarReview);
        given()
                .contentType(ContentType.JSON)
                .body(jsonRequestBody)
                .when()
                .put(testDataResourceLocation)
                .then()
                .statusCode(200);
    }

    @Order(5)
    @Test
    void shouldDelete() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .delete(testDataResourceLocation)
                .then()
                .statusCode(204);
    }

}
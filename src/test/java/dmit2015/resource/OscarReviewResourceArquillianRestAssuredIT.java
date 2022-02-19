package dmit2015.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import common.config.ApplicationConfig;
import common.config.JAXRSConfiguration;
import dmit2015.estebangonzalez.assignment03.entity.OscarReview;
import dmit2015.estebangonzalez.assignment03.repository.OscarReviewRepository;
import dmit2015.estebangonzalez.assignment03.resource.OscarReviewResource;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

/**
 * https://github.com/rest-assured/rest-assured
 * https://github.com/rest-assured/rest-assured/wiki/Usage
 * http://www.mastertheboss.com/jboss-frameworks/resteasy/restassured-tutorial
 * https://github.com/FasterXML/jackson-databind
 *
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ArquillianExtension.class)                  // Run with JUnit 5 instead of JUnit 4
public class OscarReviewResourceArquillianRestAssuredIT {

    static String mavenArtifactIdId;
    static String resourceUrl;
    static final String OscarReviewResourcePath = "webapi/OscarReviews";

    @Deployment
    public static WebArchive createDeployment() throws IOException, XmlPullParserException {
        PomEquippedResolveStage pomFile = Maven.resolver().loadPomFromFile("pom.xml");
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader("pom.xml"));
        mavenArtifactIdId = model.getArtifactId();
        resourceUrl = String.format("http://localhost:8080/%s/%s", mavenArtifactIdId, OscarReviewResourcePath);
        final String archiveName = model.getArtifactId() + ".war";
        return ShrinkWrap.create(WebArchive.class,archiveName)
                .addAsLibraries(pomFile.resolve("com.h2database:h2:1.4.200").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("org.hsqldb:hsqldb:2.6.1").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("com.microsoft.sqlserver:mssql-jdbc:10.2.0.jre17").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("com.oracle.database.jdbc:ojdbc11:21.5.0.0").withTransitivity().asFile())
                .addClasses(ApplicationConfig.class, JAXRSConfiguration.class)
                .addClasses(OscarReview.class, OscarReviewRepository.class, OscarReviewResource.class)
                .addPackage("common.jpa")
                .addPackage("common.validator")
//                .addPackage("dmit2015.repository")
//                .addPackage("dmit2015.resource")
                .addAsResource("META-INF/persistence.xml")
                .addAsResource("META-INF/sql/import-data.sql")
//                .setWebXML(new File("src/main/webapp/WEB-INF/web.xml"))
                .addAsWebInfResource(EmptyAsset.INSTANCE,"beans.xml");
    }

    @Test
    @RunAsClient
    public void checkSiteIsUp() {
        given().when().get("https://www.nait.ca/").then().statusCode(200);
        given().when().get(resourceUrl).then().statusCode(200);
    }

    String testDataResourceLocation;

    @Order(1)
    @Test
    @RunAsClient
    void shouldListAll() throws JsonProcessingException {
        Response response = given()
        		.urlEncodingEnabled(false)
                .accept(ContentType.JSON)
                .when()
                .get(resourceUrl)
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
    @RunAsClient
    void shouldCreate() throws JsonProcessingException {
        OscarReview newOscarReview = new OscarReview();
        newOscarReview.setCategory("New Category");
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
                .post(resourceUrl)
                .then()
                .statusCode(201)
                .extract()
                .response();
        testDataResourceLocation = response.getHeader("location");
    }

    @Order(3)
    @Test
    @RunAsClient
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
    @RunAsClient
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
    @RunAsClient
    void shouldDelete() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .delete(testDataResourceLocation)
                .then()
                .statusCode(204);
    }

}
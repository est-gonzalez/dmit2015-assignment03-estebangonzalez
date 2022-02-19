package repository;

import common.config.ApplicationConfig;

import dmit2015.estebangonzalez.assignment03.entity.OscarReview;
import dmit2015.estebangonzalez.assignment03.repository.OscarReviewRepository;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ArquillianExtension.class)                  // Run with JUnit 5 instead of JUnit 4

class OscarReviewRepositoryIT {

    @Inject
    private OscarReviewRepository _OscarReviewRepository;

    static OscarReview currentOscarReview;  // the OscarReview that is currently being added, find, update, or delete

    @Deployment
    public static WebArchive createDeployment() {
        PomEquippedResolveStage pomFile = Maven.resolver().loadPomFromFile("pom.xml");

        return ShrinkWrap.create(WebArchive.class,"test.war")
//                .addAsLibraries(pomFile.resolve("groupId:artifactId:version").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("com.h2database:h2:1.4.200").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("org.h2database").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("com.microsoft.sqlserver:mssql-jdbc:9.4.1.jre11").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("com.oracle.database.jdbc:ojdbc11:21.4.0.0.1").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("org.hamcrest:hamcrest:2.2").withTransitivity().asFile())
                .addClass(ApplicationConfig.class)
                .addClasses(OscarReview.class, OscarReviewRepository.class)
                .addAsResource("META-INF/persistence.xml")
                .addAsResource("META-INF/sql/import-data.sql")
                .addAsWebInfResource(EmptyAsset.INSTANCE,"beans.xml");
    }

    @Order(2)
    @Test
    void shouldCreate() {
        currentOscarReview = new OscarReview();
        currentOscarReview.setCategory("New Category");
        currentOscarReview.setNominee("New Nominee");
        currentOscarReview.setReview("New Review");
        currentOscarReview.setUsername("New Username");
        currentOscarReview.setComplete(true);
        _OscarReviewRepository.create(currentOscarReview);

        Optional<OscarReview> optionalOscarReview = _OscarReviewRepository.findOptional(currentOscarReview.getId());
        assertTrue(optionalOscarReview.isPresent());
        OscarReview existingOscarReview = optionalOscarReview.get();
        assertNotNull(existingOscarReview);
        assertEquals(currentOscarReview.getCategory(), existingOscarReview.getCategory());
        assertEquals(currentOscarReview.getNominee(), existingOscarReview.getNominee());
        assertEquals(currentOscarReview.getReview(), existingOscarReview.getReview());
        assertEquals(currentOscarReview.getUsername(), existingOscarReview.getUsername());
        assertEquals(currentOscarReview.isComplete(), existingOscarReview.isComplete());

    }

    @Order(3)
    @Test
    void shouldFindOne() {
        final Long todoId = currentOscarReview.getId();
        Optional<OscarReview> optionalOscarReview= _OscarReviewRepository.findOptional(todoId);
        assertTrue(optionalOscarReview.isPresent());
        OscarReview existingOscarReview = optionalOscarReview.get();
        assertNotNull(existingOscarReview);
        assertEquals(currentOscarReview.getCategory(), existingOscarReview.getCategory());
        assertEquals(currentOscarReview.getNominee(), existingOscarReview.getNominee());
        assertEquals(currentOscarReview.getReview(), existingOscarReview.getReview());
        assertEquals(currentOscarReview.getUsername(), existingOscarReview.getUsername());
        assertEquals(currentOscarReview.isComplete(), existingOscarReview.isComplete());

    }

    @Order(1)
    @Test
    void shouldFindAll() {
        List<OscarReview> queryResultList = _OscarReviewRepository.list();
        assertEquals(4, queryResultList.size());

        OscarReview firstOscarReview = queryResultList.get(0);
        assertEquals("Film", firstOscarReview.getCategory());
        assertEquals("House of Gucci", firstOscarReview.getNominee());
        assertEquals("Thrilling to watch", firstOscarReview.getReview());
        assertEquals("GucciLover", firstOscarReview.getUsername());
        assertEquals(false, firstOscarReview.isComplete());

        OscarReview lastOscarReview = queryResultList.get(queryResultList.size() - 1);
        assertEquals("Film", firstOscarReview.getCategory());
        assertEquals("Eternals", firstOscarReview.getNominee());
        assertEquals("Not as good as Endgame", firstOscarReview.getReview());
        assertEquals("HarshReviewer", firstOscarReview.getUsername());
        assertEquals(false, lastOscarReview.isComplete());

    }

    @Order(4)
    @Test
    void shouldUpdate() {
        currentOscarReview.setCategory("Updated Category");
        currentOscarReview.setNominee("Updated Nominee");
        currentOscarReview.setReview("Updated Review");
        currentOscarReview.setUsername("Updated Username");
        currentOscarReview.setComplete(false);
        _OscarReviewRepository.update(currentOscarReview);

        Optional<OscarReview> optionalUpdatedOscarReview = _OscarReviewRepository.findOptional(currentOscarReview.getId());
        assertTrue(optionalUpdatedOscarReview.isPresent());
        OscarReview updatedOscarReview = optionalUpdatedOscarReview.get();
        assertNotNull(updatedOscarReview);
        assertEquals(currentOscarReview.getCategory(), updatedOscarReview.getCategory());
        assertEquals(currentOscarReview.getNominee(), updatedOscarReview.getNominee());
        assertEquals(currentOscarReview.getReview(), updatedOscarReview.getReview());
        assertEquals(currentOscarReview.getUsername(), updatedOscarReview.getUsername());
        assertEquals(currentOscarReview.isComplete(), updatedOscarReview.isComplete());

    }

    @Order(5)
    @Test
    void shouldDelete() {
        final Long reviewId = currentOscarReview.getId();
        Optional<OscarReview> optionalOscarReview = _OscarReviewRepository.findOptional(reviewId);
        assertTrue(optionalOscarReview.isPresent());
        OscarReview existingOscarReview = optionalOscarReview.get();
        assertNotNull(existingOscarReview);
        _OscarReviewRepository.remove(existingOscarReview.getId());
        optionalOscarReview = _OscarReviewRepository.findOptional(reviewId);
        assertTrue(optionalOscarReview.isEmpty());
    }
}
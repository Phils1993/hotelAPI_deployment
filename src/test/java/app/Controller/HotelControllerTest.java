package app.Controller;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.routes.HotelRoutes;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HotelControllerTest {

    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private static int createdHotelId;
    public static ApplicationConfig appConfig = ApplicationConfig.getInstance();

    @BeforeAll
    static void setup() {
        // TRUNCATE tables
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery("TRUNCATE TABLE room RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE hotel RESTART IDENTITY CASCADE").executeUpdate();
            em.getTransaction().commit();
        }

        // Start test server with HotelRoutes
        appConfig.initiateServer()
                .setRoute(new HotelRoutes().getRoutes())
                .startServer(7076);

        // Populate test data
        TestPopulator.populate(emf);

        // Configure RestAssured
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7076;
        RestAssured.basePath = "/api/v1";
    }

    @AfterAll
    static void tearDown() {
        emf.close();
    }

    @Test
    @Order(1)
    void getAllHotels() {
        given()
                .when().get("/hotel")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(3))
                .body("[0].hotelName", notNullValue());
    }

    @Test
    @Order(2)
    void getSpecificHotel() {
        given()
                .when().get("/hotel/{id}", 1)
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("hotelName", notNullValue())
                .body("hotelAddress", notNullValue());
    }

    @Test
    @Order(3)
    void getRoomsForSpecificHotel() {
        given()
                .when().get("/hotel/{id}/rooms", 1)
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1))
                .body("[0].price", greaterThan(0.0f));
    }

    @Test
    @Order(4)
    void createNewHotel() {
        String newHotelJson = """
            {
              "hotelName": "Test Hotel",
              "hotelAddress": "Test Street 123",
              "rooms": []
            }
            """;

        ValidatableResponse res = given()
                .contentType(ContentType.JSON)
                .body(newHotelJson)
                .when().post("/hotel")
                .then()
                .statusCode(201)
                .body("hotelName", equalTo("Test Hotel"))
                .body("hotelAddress", equalTo("Test Street 123"))
                .body("id", notNullValue());

        createdHotelId = res.extract().path("id");
    }

    @Test
    @Order(5)
    void updateHotel() {
        String updatedHotelJson = """
            {
              "hotelName": "Updated Test Hotel",
              "hotelAddress": "Updated Address 456",
              "rooms": []
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(updatedHotelJson)
                .when().put("/hotel/{id}", createdHotelId)
                .then()
                .statusCode(202)
                .body("hotelName", equalTo("Updated Test Hotel"))
                .body("hotelAddress", equalTo("Updated Address 456"))
                .body("rooms", hasSize(0));
    }

    /*
    @Test
    @Order(6)
    void deleteHotel() {
        given()
                .when().delete("/hotel/{id}", createdHotelId)
                .then()
                .statusCode(202)
                .body(equalTo("Hotel with id " + createdHotelId + " deleted"));

        given()
                .when().get("/hotel/{id}", createdHotelId)
                .then()
                .statusCode(404);
    }

     */
}

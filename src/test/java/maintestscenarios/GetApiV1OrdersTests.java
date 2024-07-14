package maintestscenarios;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

public class GetApiV1OrdersTests {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void bodyReturnedListOfOrders() {
        given()
                .get("/api/v1/orders")
                .then().assertThat().body("orders", notNullValue())
                .and()
                .statusCode(200);
    }
}

package additionaltestscenarios;

import Serialsclasses.CourierSerials;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;

public class DeleteApiV1CourierIdTests {
    CourierSerials courier;
    int id;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";

        courier = new CourierSerials("Aleksss", "1234567", "Alex");
        given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        id = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier/login") // отправка GET-запроса
                .then().extract().body().path("id");
    }

    @Test
    public void canDeleteCourier(){
        given()
                .delete("/api/v1/courier/{id}", id)
                .then().assertThat().body("ok", notNullValue())
                .and()
                .statusCode(200);

    }

    @Test
    public void cannotDeleteCourierWithWrongId(){
        given()
                .delete("/api/v1/courier/{id}", 1234567890)
                .then().assertThat().body("message", containsString("Курьера с таким id нет"))
                .and()
                .statusCode(404);

    }

    @Test
    public void cannotDeleteCourierWithoutId(){
        given()
                .delete("/api/v1/courier/{id}", "")
                .then().assertThat().body("message", containsString("Недостаточно данных"))
                .and()
                .statusCode(400);

    }

    @After
    public void clearAll(){
        try {
            given()
                    .delete("/api/v1/courier/{id}", id);
        } catch (Exception exception) {
            id = 0;
        }
    }
}

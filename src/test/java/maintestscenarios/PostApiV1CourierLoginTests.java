package maintestscenarios;

import Serialsclasses.CourierSerials;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;

public class PostApiV1CourierLoginTests {
    CourierSerials courier;
    Response response;
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
    }

    @Test
    public void courierCanLogin(){
        CourierSerials courierLogin = new CourierSerials("Aleksss", "1234567");
        response = given()
                .header("Content-type", "application/json")
                .body(courierLogin)
                .when()
                .post("/api/v1/courier/login");
        response.then().assertThat().body("id", notNullValue())
                .and()
                .statusCode(200);
    }

    @Test
    public void cannotLoginWithoutLogin(){
        CourierSerials courierLogin = new CourierSerials("", "1234567");
        response = given()
                .header("Content-type", "application/json")
                .body(courierLogin)
                .when()
                .post("/api/v1/courier/login");
        response.then().assertThat().body("message", containsString("Недостаточно данных"))
                .and()
                .statusCode(400);
    }

    @Test
    public void cannotLoginWithoutPassword(){
        CourierSerials courierLogin = new CourierSerials("Aleksss", "");
        response = given()
                .header("Content-type", "application/json")
                .body(courierLogin)
                .when()
                .post("/api/v1/courier/login");
        response.then().assertThat().body("message", containsString("Недостаточно данных"))
                .and()
                .statusCode(400);
    }

    @Test
    public void cannotLoginWithWrongLogin(){
        CourierSerials courierLogin = new CourierSerials("AAAleXXX", "1234567");
        response = given()
                .header("Content-type", "application/json")
                .body(courierLogin)
                .when()
                .post("/api/v1/courier/login");
        response.then().assertThat().body("message", containsString("Учетная запись не найдена"))
                .and()
                .statusCode(404);
    }

    @Test
    public void cannotLoginWithWrongPassword(){
        CourierSerials courierLogin = new CourierSerials("Aleksss", "7654321");
        response = given()
                .header("Content-type", "application/json")
                .body(courierLogin)
                .when()
                .post("/api/v1/courier/login");
        response.then().assertThat().body("message", containsString("Учетная запись не найдена"))
                .and()
                .statusCode(404);
    }

    @After
    public void clearAll(){
        try {
            id = response.then().extract().body().path("id");
            given()
                    .delete("/api/v1/courier/{id}", id)
                    .then().assertThat().statusCode(200);
        } catch (Exception exception) {
            id = 0;
        }
    }
}

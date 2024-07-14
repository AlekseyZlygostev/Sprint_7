package maintestscenarios;
import Serialsclasses.CourierSerials;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

public class PostApiV1CourierTests {
    CourierSerials courier;
    int id;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void canCreateCourier(){
        courier = new CourierSerials("Aleksss", "1234567", "Alex");

        given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then().assertThat().body("ok", notNullValue())
                .and()
                .statusCode(201);
    }

    @Test
    public void canCreateCourierWithRequiredFields(){
        courier = new CourierSerials("Alekssss", "1234567", "");

        given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then().assertThat().body("ok", notNullValue())
                .and()
                .statusCode(201);
    }

    @Test
    public void cannotCreateTwoIdenticalCouriers(){
        courier = new CourierSerials("Aleksss", "1234567", "Alex");

        given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");

        given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then().assertThat().body("message", containsString("Этот логин уже используется"))
                .and()
                .statusCode(409);
    }

    @Test
    public void cannotCreateCourierWithoutLogin(){
        courier = new CourierSerials("", "1234567", "Alex");

        given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then().assertThat().body("message", containsString("Недостаточно данных"))
                .and()
                .statusCode(400);
    }

    @Test
    public void cannotCreateCourierWithoutPassword(){
        courier = new CourierSerials("Aleksss", "", "Alex");

        given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then().assertThat().body("message", containsString("Недостаточно данных"))
                .and()
                .statusCode(400);
    }

    @After
    public void clearAll(){
        try {
        id = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier/login") // отправка GET-запроса
                .then().extract().body().path("id");
        //System.out.println(id);
        given()
                .delete("/api/v1/courier/{id}", id)
                .then().assertThat().statusCode(200);
        } catch (Exception exception) {
            id = 0;
        }
    }
}

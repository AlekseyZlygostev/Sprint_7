package additionaltestscenarios;

import Serialsclasses.CourierSerials;
import Serialsclasses.OrdersSerials;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;

public class PutApiV1OrdersAcceptIdTests {
    CourierSerials courier;
    OrdersSerials order;
    int courierId;
    int track;
    int orderId;
    String[] blackGrey = {"BLACK", "GREY"};

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";

        courier = new CourierSerials("Aleksss", "1234567", "Alex");
        given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        courierId = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier/login") // отправка GET-запроса
                .then().extract().body().path("id");
        //System.out.println("cID " + courierId);

        order = new OrdersSerials(blackGrey);
        track = given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("/api/v1/orders") // отправка GET-запроса
                .then().extract().body().path("track");
        //System.out.println("t " + track);
        orderId = given()
                .queryParam("t", track)
                .get("/api/v1/orders/track")
                .then().extract().body().path("order.id");
        //System.out.println("oID " + orderId);
    }

    @Test
    public void canAcceptOrder() {
        given()
                .queryParam("courierId", courierId)
                .put("/api/v1/orders/accept/{orderId}", orderId)
                .then().assertThat().body("ok", notNullValue())
                .and()
                .statusCode(200);
    }

    @Test
    public void cannotAcceptOrderWithoutCourierId() {
        given()
                .queryParam("courierId", "")
                .put("/api/v1/orders/accept/{orderId}", orderId)
                .then().assertThat().body("message", containsString("Недостаточно данных"))
                .and()
                .statusCode(400);
    }

    @Test
    public void cannotAcceptOrderWithoutOrderId() {
        given()
                .queryParam("courierId", courierId)
                .put("/api/v1/orders/accept/{orderId}", "")
                .then().assertThat().body("message", containsString("Недостаточно данных"))
                .and()
                .statusCode(400);
    }

    @Test
    public void cannotAcceptOrderWithWrongCourierId() {
        given()
                .queryParam("courierId", 1234567890)
                .put("/api/v1/orders/accept/{orderId}", orderId)
                .then().assertThat().body("message", containsString("Курьера с таким id не существует"))
                .and()
                .statusCode(404);
    }

    @Test
    public void cannotAcceptOrderWithWrongOrderId() {
        given()
                .queryParam("courierId", courierId)
                .put("/api/v1/orders/accept/{orderId}", 1234567890)
                .then().assertThat().body("message", containsString("Заказа с таким id не существует"))
                .and()
                .statusCode(404);
    }

    @After
    public void clearAll(){
        try {
            given()
                    .delete("/api/v1/courier/{courierId}", courierId);
        } catch (Exception exception) {
            courierId = 0;
        }

        try {
            String cancelBody = "{\"track\": " + track + "}";
            //System.out.println(cancelBody);
            given()
                    .header("Content-type", "application/json")
                    .body(cancelBody)
                    .put("/api/v1/orders/cancel");
        } catch (Exception exception) {
            track = 0;
        }
    }
}

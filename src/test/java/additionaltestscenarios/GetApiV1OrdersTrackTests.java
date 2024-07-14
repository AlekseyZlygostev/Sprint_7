package additionaltestscenarios;

import Serialsclasses.OrdersSerials;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;

public class GetApiV1OrdersTrackTests {
    OrdersSerials order;
    int track;
    String[] blackGrey = {"BLACK", "GREY"};

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";

        order = new OrdersSerials(blackGrey);
        track = given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("/api/v1/orders") // отправка GET-запроса
                .then().extract().body().path("track");
    }

    @Test
    public void canGetOrder() {
        given()
                .queryParam("t", track)
                .get("/api/v1/orders/track")
                .then().assertThat().body("order", notNullValue())
                .and()
                .statusCode(200);

    }

    @Test
    public void cannotGetOrderWithoutNumber() {
        given()
                .queryParam("t", "")
                .get("/api/v1/orders/track")
                .then().assertThat().body("message", containsString("Недостаточно данных"))
                .and()
                .statusCode(400);

    }

    @Test
    public void cannotGetOrderWithWrongNumber() {
        given()
                .queryParam("t", "1234567890")
                .get("/api/v1/orders/track")
                .then().assertThat().body("message", containsString("Заказ не найден"))
                .and()
                .statusCode(404);

    }

    @After
    public void clearAll(){
        try {
            String cancelBody = "{\"track\": " + track + "}";
            //System.out.println(cancelBody);
            given()
                    .header("Content-type", "application/json")
                    .body(cancelBody)
                    .put("/api/v1/orders/cancel");
            //.then().assertThat().statusCode(200);   Здесь должна быть отмена заказа, чтоб он удалился из системы. Однако ручка PUT: /api/v1/orders/cancel возвращает 400
        } catch (Exception exception) {
            track = 0;
        }
    }
}

package maintestscenarios;

import Serialsclasses.OrdersSerials;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class PostApiV1OrdersTests {
    private final String[] color;
    public static String[] blackGrey = {"BLACK", "GREY"};
    public static String[] black = {"BLACK"};
    public static String[] grey = {"GREY"};
    public static String[] notColor = {};
    OrdersSerials order;
    int track;

    public PostApiV1OrdersTests(String[] color){
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] getSumData() {
        return new Object[][] {
                {blackGrey},
                {black},
                {grey},
                {notColor},
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void createOrder(){
        order = new OrdersSerials(color);

        given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("/api/v1/orders")
                .then().assertThat().body("track", notNullValue())
                .and()
                .statusCode(201);
    }


    @After
    public void clearAll(){
        try {
            track = given()
                    .header("Content-type", "application/json")
                    .body(order)
                    .when()
                    .post("/api/v1/orders") // отправка GET-запроса
                    .then().extract().body().path("track");
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

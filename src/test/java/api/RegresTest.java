package api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.http.ContentType;
import io.restassured.response.ResponseBodyExtractionOptions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class RegresTest {

    private static final String BASE_URL = "https://restful-booker.herokuapp.com/";
    private static final String PATH_URL = "booking/";

    private Integer createdBookingId; //variable to save new booking id then use it for further tests
    private ResponseBodyExtractionOptions body;

    @Test
    public void createBooking() {

        BookingSite bs = new BookingSite();
        ObjectNode newBooking = bs.createNewBookingBody();

        given()
                .baseUri(BASE_URL)
                .basePath(PATH_URL)//endpoint for base_url
                .contentType(ContentType.JSON)
                .body(newBooking)
                .when().post()
                .then()
                .log().all()//log for monitoring
                .statusCode(200)//validate status code
                .body("booking.firstname", equalTo("Kevin")).and()
                .body("booking.lastname", equalTo("McCallister")).and()
                .body("booking.bookingdates.checkin", equalTo("2022-12-29"));

    }

    @Test
    public void getBookingDataByIdStatus200() {

        createdBookingId = 55; //for local run test separately

        BookingData bd =
                given()
                        .baseUri(BASE_URL).contentType(ContentType.JSON)
                        .basePath(PATH_URL + createdBookingId)//endpoint for base_url
                        .when().get()//GET method
                        .then().log().all()//log for monitoring
                        .statusCode(200)//validate status code
                        .extract().body().as(BookingData.class);

        assertThat(bd).extracting(BookingData::getFirstname).isEqualTo("Kevin");
        assertThat(bd).extracting(BookingData::getLastname).isEqualTo("McCallister");
        assertThat(bd).extracting(BookingData::getTotalprice).isEqualTo(1000);
        assertThat(bd).extracting(BookingData::getAdditionalneeds).isEqualTo("plain cheese pizza");

        //for example, we want to print values to console
        //just for validation, this is unnecessary part, delete when finish create the test
        System.out.println(bd.getAdditionalneeds() + ", " + bd.getBookingdates());

    }

    @Test
    public void getBookingDataByIdStatus404() {

        createdBookingId = 0; //not existed id

        //save response as string
        String rs =
                given()
                        .baseUri(BASE_URL).contentType(ContentType.JSON)
                        .basePath(PATH_URL + createdBookingId)//endpoint for base_url
                        .when().get()//GET method
                        .then().log().all()//log for monitoring
                        .statusCode(404)//validate status code
                        .extract().asString();
        //validate response message text
        if (rs.equals("Not Found")) {
            System.out.println("response text is: " + rs);
        } else {
            throw new RuntimeException("Response text is wrong");
        }
    }

    @Test
    public void getAuthToken() {

        BookingSite bs = new BookingSite();
        String token = bs.getAuthToken(BASE_URL);

    }

    @Test
    public void updateBooking() {

        createdBookingId = 55; // if you need run test separately

        BookingSite bs = new BookingSite();
        String token = bs.getAuthToken(BASE_URL);

        // create a JSON objects
        ObjectNode bookingDates = bs.getMapper().createObjectNode();
        bookingDates.put("checkin", "2222-12-29");
        bookingDates.put("checkout", "2223-12-31");

        ObjectNode booking = bs.getMapper().createObjectNode();
        booking.put("firstname", "Forest");
        booking.put("lastname", "Gump");
        booking.put("totalprice", 42);
        booking.put("depositpaid", true);
        booking.put("additionalneeds", "bubba shrimp");
        booking.set("bookingdates", bookingDates);

        BookingData bookingResponse = given()
                .baseUri(BASE_URL)
                .basePath(PATH_URL + createdBookingId)//endpoint for base_url
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(booking)
                .when().put()
                .then().log().all()//log for monitoring
                .statusCode(200)//validate status code
                .extract().body().as(BookingData.class);

        System.out.println("Values: " + bookingResponse.getFirstname() + ", " + bookingResponse.getLastname());

    }

    @Test
    public void deleteBooking() {

        createdBookingId = 55; //for run test separately
        //token = "678b5a46451d006";

        BookingSite bs = new BookingSite();
        String token = bs.getAuthToken(BASE_URL);

        String rs = given()
                .baseUri(BASE_URL)
                .basePath(PATH_URL + createdBookingId)
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when().delete()
                .then().log().all()//log for monitoring
                .statusCode(201)//validate status code
                .extract().body().asString();
        System.out.println("Response status: " + rs);

    }

    @Test
    public void getBookingIdsWithParams() {

        BookingSite bs = new BookingSite();
        ObjectNode idsList = bs.getMapper().createObjectNode();

        String param1 = "firstname";
        String value1 = "John";
        String param2 = "lastname";
        String value2 = "Smith";

        String params = String.format("?%1$s=%2$s&%3$s=%4$s", param1, value1, param2, value2);

        //save response as List
        List l = given()
                .baseUri(BASE_URL)
                .basePath(PATH_URL + params)
                .when().get()//GET method
                .then().log().all()//log for monitoring
                .statusCode(200)//validate status code
                .extract().as(List.class);

        int listSize = l.size();
        System.out.println(listSize);

    }
}

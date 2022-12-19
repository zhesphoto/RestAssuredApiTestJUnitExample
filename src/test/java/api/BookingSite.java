package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;


public class BookingSite {


    public ObjectMapper getMapper() {
        // create `ObjectMapper` instance
        return new ObjectMapper();
    }

    public String getAuthToken(String base_url) {

        // create `ObjectMapper` instance
        ObjectMapper mapper = getMapper();
        // create a JSON objects
        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.put("username", "admin");
        requestBody.put("password", "password123");

        String rs =
                given()
                        .baseUri(base_url).contentType(ContentType.JSON)
                        .basePath("auth")//endpoint for base_url
                        .body(requestBody)
                        .when().post()//GET method
                        .then().log().all()//log for monitoring
                        .statusCode(200)//validate status code
                        .extract().body().asString();

        String[] s1 = rs.split("(^.{10})");
        String token = s1[1].replaceAll("(.{2}$)", "");
        System.out.println(token);
        return token;
    }

    public ObjectNode createNewBookingBody() {

        // create a JSON objects
        ObjectNode bookingDates = getMapper().createObjectNode();
        bookingDates.put("checkin", "2022-12-29");
        bookingDates.put("checkout", "2022-12-31");

        ObjectNode newBooking = getMapper().createObjectNode();
        newBooking.put("firstname", "Kevin");
        newBooking.put("lastname", "McCallister");
        newBooking.put("totalprice", 1000);
        newBooking.put("depositpaid", false);
        newBooking.put("additionalneeds", "plain cheese pizza");
        newBooking.set("bookingdates", bookingDates);

        return newBooking;
    }

}

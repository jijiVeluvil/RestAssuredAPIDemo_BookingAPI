package com.testautomation.apitesting.tests;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import com.testautomation.apitesting.utils.BaseTest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.minidev.json.JSONObject;

public class PostAPIRequest extends BaseTest {
	@Test
	public void createBooking() {
		JSONObject booking = new JSONObject();
		JSONObject bookingDates = new JSONObject();
		booking.put("firstname", "api-testing");
		booking.put("lastname", "tutorial");
		booking.put("totalprice", 111);
		booking.put("depositpaid", true);
		bookingDates.put("checkin", "2024-03-01");
		bookingDates.put("checkout", "2024-09-01");
		booking.put("bookingdates", bookingDates);
		booking.put("additionalneeds", "Breakfast");
	
	Response response =
		RestAssured
		.given()
		.contentType("application/json")
		.body(booking.toString())
		.baseUri("https://restful-booker.herokuapp.com/booking")
		//.log().body() //print request boby // log().all() = print request body and headers
		//.log().headers()
		.when()
		.post()
		.then()
		//.log().body() //print respose body
		//.log().ifValidationFails()
		.assertThat()
		.statusCode(200)
		.body("booking.firstname", Matchers.equalTo("api-testing"))
		.body("booking.lastname", Matchers.equalTo("tutorial"))
		.body("booking.bookingdates.checkin", Matchers.equalTo("2024-03-01"))
		.log().body()
		.extract()
		.response();
	
	int bookingId = response.path("bookingid");
	System.out.println(bookingId);
	
	RestAssured
	 .given()
	 .contentType("application/json")
	 .baseUri("https://restful-booker.herokuapp.com/booking/")
	 .pathParam("BookingId", bookingId)
	 .when()
	 .get("{BookingId}")
	 .then()
	 .assertThat()
	 .body("firstname", Matchers.equalTo("api-testing"))
	 .body("lastname", Matchers.equalTo("tutorial"))
	 .body("bookingdates.checkin", Matchers.equalTo("2024-03-01"))
	 .log().body();
		
	}

}

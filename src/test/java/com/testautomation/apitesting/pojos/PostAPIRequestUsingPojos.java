package com.testautomation.apitesting.pojos;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testautomation.apitesting.utils.FileNameConstants;

import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

public class PostAPIRequestUsingPojos {
	@Test
	public void postAPIRequest() {
	try {	
		
		String jsonSchema = FileUtils.readFileToString(new File(FileNameConstants.JSON_SCHEMA),"UTF-8");
		BookingDates bookingdates = new BookingDates("2024-03-01", "2024-09-01");
		Booking booking = new Booking("api-testing", "tutorial", "Breakfast", 111, true, bookingdates);
		
		// Serialization
		ObjectMapper objectMapper = new ObjectMapper();
		    String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(booking);
			System.out.println(requestBody);
			// Deserialization
			Booking bookingDetails = objectMapper.readValue(requestBody, Booking.class);
			System.out.println(bookingDetails.getFirstname());
			System.out.println(bookingDetails.getBookingdates().getCheckin());
		Response response =
			RestAssured
			.given()
			 .contentType("application/json")
			 .body(requestBody)
			 .baseUri("https://restful-booker.herokuapp.com/booking")
			 .when()
			 .post()
			 .then()
			 .assertThat()
			 .statusCode(200)
			 .extract()
			 .response();
		int bookingId = response.path("bookingid");
		
			RestAssured
			.given()
			 .contentType("application/json")
			 .baseUri("https://restful-booker.herokuapp.com/booking")
			 .when()
			 .get("/{bookingId}",bookingId)
			 .then()
			 .log().body()
			 .assertThat()
			 .statusCode(200)
			  .body(JsonSchemaValidator.matchesJsonSchema (jsonSchema));

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

package com.testautomation.apitesting.tests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jayway.jsonpath.JsonPath;
import com.testautomation.apitesting.utils.BaseTest;
import com.testautomation.apitesting.utils.FileNameConstants;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;

public class PostAPIRequestUsingFile extends BaseTest {
	@Test
	public void postAPIRequest() {
		try {
			String postAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.POSE_API_REQUEST_BODY),
					"UTF-8");
			//System.out.println(postAPIRequestBody);
		Response response =
			RestAssured
			.given()
			.contentType("application/json")
			.body(postAPIRequestBody)
			.baseUri("https://restful-booker.herokuapp.com/booking")
			.when()
			.post()
			.then()
			.assertThat()
			.statusCode(200)
			.log().body()
			.extract()
			.response();
			
	//JsonPath API expressions
		JSONArray jsonArray = JsonPath.read(response.body().asString(), "$.booking..firstname");
		JSONArray jsonArrayCheckIn = JsonPath.read(response.body().asString(), "$.booking..bookingdates..checkin");
		String firstName = (String) jsonArray.get(0);
		String checkIn = (String) jsonArrayCheckIn.get(0);
		Assert.assertEquals(firstName, "api-testing");
		Assert.assertEquals(checkIn, "2024-03-01");
		int bookingId = JsonPath.read(response.body().asString(), "$.bookingid");
		RestAssured
		.given()
		.contentType("application/json")
		.baseUri("https://restful-booker.herokuapp.com/booking")
		.when()
		.get("/{bookingID}",bookingId)
		.then()
		.assertThat()
		.statusCode(200);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}

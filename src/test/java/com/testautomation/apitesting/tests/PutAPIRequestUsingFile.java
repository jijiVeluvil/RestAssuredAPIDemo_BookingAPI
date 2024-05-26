package com.testautomation.apitesting.tests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jayway.jsonpath.JsonPath;
import com.testautomation.apitesting.utils.FileNameConstants;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;

public class PutAPIRequestUsingFile {
	@Test
	public void putRequest() {
		try {
			String postAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.POSE_API_REQUEST_BODY),
					"UTF-8");
			//System.out.println(postAPIRequestBody);
			String tokenAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.TOKEN_API_REQUEST_BODY),
					"UTF-8");
			String putAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.PUT_API_REQUEST_BODY),
					"UTF-8");
			//post call
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
		String firstName = (String) jsonArray.get(0);
		Assert.assertEquals(firstName, "api-testing");
		int bookingId = JsonPath.read(response.body().asString(), "$.bookingid");
		
		//get call
		RestAssured
		.given()
		.contentType("application/json")
		.baseUri("https://restful-booker.herokuapp.com/booking")
		.when()
		.get("/{bookingID}",bookingId)
		.then()
		.assertThat()
		.statusCode(200);
    
	Response tokenAPIResponse =
		RestAssured
		.given()
		.contentType("application/json")
		.body(tokenAPIRequestBody)
		.baseUri("https://restful-booker.herokuapp.com/auth")
		.when()
		.post()
		.then()
		.assertThat()
		.statusCode(200)
		.extract()
		.response();
	String tokenId = JsonPath.read(tokenAPIResponse.body().asString(), "$.token");
	
	//put api call
	     RestAssured
	     .given()
	     .contentType("application/json")
	     .body(putAPIRequestBody)
	     .header("Cookie","token="+tokenId)
	     .baseUri("https://restful-booker.herokuapp.com/booking/")
	     .when()
	     .put("{bookingId}",bookingId)
		 .then()
		 .assertThat()
		 .statusCode(200)
		 .body("firstname", Matchers.equalTo("spec-flow"))
		 .body("lastname", Matchers.equalTo("selenium C#"));
	     
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}

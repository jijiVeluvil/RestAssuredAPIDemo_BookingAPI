package com.testautomation.apitesting.tests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jayway.jsonpath.JsonPath;
import com.testautomation.apitesting.listener.RestAssuredListener;
import com.testautomation.apitesting.utils.BaseTest;
import com.testautomation.apitesting.utils.FileNameConstants;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;
@Epic ("Epiv-01")
@Feature ("Create Update Delete Booking")
public class AllureReportGeneration extends BaseTest {
	
	private static Logger logger = LogManager.getLogger();
	@Story ("Story-01")
	@Test (description = "end to end api testing")
	@Description ("end to end testing")
	@Severity(SeverityLevel.CRITICAL)
	public void e2eAPITests() {
		logger.info("End to End test execution started...");
		try {
			String postAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.POSE_API_REQUEST_BODY),
					"UTF-8");
			//System.out.println(postAPIRequestBody);
			String tokenAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.TOKEN_API_REQUEST_BODY),
					"UTF-8");
			String putAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.PUT_API_REQUEST_BODY),
					"UTF-8");
			String patchAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.PATCH_API_REQUEST_BODY),
					"UTF-8");
			//post call
		Response response =
			RestAssured
			.given().filter(new AllureRestAssured()).filter(new RestAssuredListener())
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
		.given().filter(new AllureRestAssured()).filter(new RestAssuredListener())
		.contentType("application/json")
		.baseUri("https://restful-booker.herokuapp.com/booking")
		.when()
		.get("/{bookingID}",bookingId)
		.then()
		.assertThat()
		.statusCode(200);
    
	Response tokenAPIResponse =
		RestAssured
		.given().filter(new AllureRestAssured()).filter(new RestAssuredListener())
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
	     .given().filter(new AllureRestAssured()).filter(new RestAssuredListener())
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
	     
	 //patch api call
	     RestAssured
	     .given().filter(new AllureRestAssured()).filter(new RestAssuredListener())
	     .contentType("application/json")
	     .body(patchAPIRequestBody)
	     .header("Cookie","token="+tokenId)
	     .baseUri("https://restful-booker.herokuapp.com/booking/")
	     .when()
	     .patch("{bookingId}",bookingId)
		 .then()
		 .assertThat()
		 .statusCode(200)
		 .body("firstname", Matchers.equalTo("Testers Talk"));
	     
	  //delete api call
	     RestAssured
	     .given().filter(new AllureRestAssured()).filter(new RestAssuredListener())
	     .contentType("application/json")
	     .header("Cookie","token="+tokenId)
	     .baseUri("https://restful-booker.herokuapp.com/booking/")
	     .when()
	     .delete("{bookingId}",bookingId)
		 .then()
		 .assertThat()
		 .statusCode(201);
	     
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("End to End test execution started...");
		
	}

}

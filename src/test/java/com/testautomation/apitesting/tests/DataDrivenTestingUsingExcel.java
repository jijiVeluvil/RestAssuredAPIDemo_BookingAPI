package com.testautomation.apitesting.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testautomation.apitesting.listener.RestAssuredListener;
import com.testautomation.apitesting.pojos.Booking;
import com.testautomation.apitesting.pojos.BookingDates;
import com.testautomation.apitesting.utils.FileNameConstants;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class DataDrivenTestingUsingExcel {
	@Test(dataProvider = "excelTestData")
	public void dataDrivenTesting(Map<String,String> testData) throws JsonProcessingException {
		int totalPrice = Integer.parseInt(testData.get("TotalPrize"));
		BookingDates bookingdates = new BookingDates("2024-03-01", "2024-09-01");
		Booking booking = new Booking(testData.get("FirstName"),testData.get("LastName"), "Breakfast", totalPrice, true, bookingdates);
		
		// Serialization
		ObjectMapper objectMapper = new ObjectMapper();
		    String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(booking);
		Response response =
			RestAssured
			.given().filter(new RestAssuredListener())
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
		
	}

	@DataProvider(name = "excelTestData")
	public Object[][] getTestData() throws FilloException {
		String query = "select * from Sheet1 where Run= 'Yes'";
		Object[][] objArray = null;
		Map<String, String> testData = null;
		List<Map<String, String>> testDataList = null;
		Fillo fillo = new Fillo();
		Connection connection = null;
		Recordset recordset = null;
		connection = fillo.getConnection(FileNameConstants.Excel_TEST_DATA);
		recordset = connection.executeQuery(query);
		testDataList = new ArrayList<Map<String, String>>();
		while (recordset.next()) {
			testData = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
			for (String field : recordset.getFieldNames()) {
				testData.put(field, recordset.getField(field));
			}
			testDataList.add(testData);
		}
		objArray = new Object[testDataList.size()][1];
		for (int i = 0; i < testDataList.size(); i++) {
			objArray[i][0] = testDataList.get(i);
		}
		return objArray;
	}
}

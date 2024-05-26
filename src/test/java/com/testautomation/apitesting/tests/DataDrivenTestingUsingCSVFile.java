package com.testautomation.apitesting.tests;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.testautomation.apitesting.listener.RestAssuredListener;
import com.testautomation.apitesting.pojos.Booking;
import com.testautomation.apitesting.pojos.BookingDates;
import com.testautomation.apitesting.utils.FileNameConstants;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class DataDrivenTestingUsingCSVFile {
	@Test (dataProvider = "testDataUsingCsv")
	public void dataDrivenTesting(Map<String,String> testData) {
		try {
			int totalPrice = Integer.parseInt(testData.get("totalprice"));
			BookingDates bookingdates = new BookingDates("2024-03-01", "2024-09-01");
			Booking booking = new Booking(testData.get("firstname"),testData.get("lastname"), "Breakfast",totalPrice, true, bookingdates);
			
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
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
	}
	@DataProvider(name="testDataUsingCsv")
	public Object[][] getDataUsingCSV(){
		Object[][]objArray = null;
		Map<String,String> map = null;
		List<Map<String,String>> testDataList =null;
		try {
			CSVReader csvReader = new CSVReader (new FileReader (FileNameConstants.CSV_TEST_DATA));
			testDataList = new ArrayList<Map<String,String>>();
			String[] line = null;
			int count = 0;
            while ((line = csvReader.readNext())!=null) {
            	if (count==0) {
            		count++;
            		continue;
            	}
				map = new TreeMap<String,String>(String.CASE_INSENSITIVE_ORDER);
				map.put("firstname", line[0]);
				map.put("lastname", line[1]);
				map.put("totalprice", line[2]);
				testDataList.add(map);
			}
			objArray = new Object [testDataList.size()][1];
			for (int i=0; i<testDataList.size(); i++) {
				objArray[i][0] = testDataList.get(i);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (CsvValidationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return objArray;
				
	}

}

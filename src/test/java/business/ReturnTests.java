package business;

import static org.junit.Assert.*;

import java.util.GregorianCalendar;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dataaccess.ProductMapper;
import dataaccess.ProductToRentMapper;

public class ReturnTests {
	
	private Return returnItem;
	
	private static RentSys app;
	private static ProductToRent prod1, prod2, prod3;

	
	@BeforeClass   // run once
	public static void setUpBeforeClass() {
		try {
			app = new RentSys();
			app.start();
			
			prod1 = ProductToRentMapper.getProductToRentById(2001); 
			prod2 = ProductToRentMapper.getProductToRentById(2002);
			prod3 = ProductToRentMapper.getProductToRentById(2003);
		} 
		catch (Exception e) {
			fail("App didn't start or products could not be retrieved");
		}
	}
	
	@Before   // run before each test
	public void setup() {
		returnItem = new Return(1, new GregorianCalendar(2016,12,31).getTime(), 1);
	}
	
	@Test
	public void test_ReturnId() {
		assertEquals(returnItem.getId(), 1);
	}
	
	@Test
	public void test_ReturnClient() {
		assertEquals(returnItem.getClientId(), 1);
	}
	
	@Test
	public void test_emptyReturnProducts() {
		assertTrue(returnItem.getRentProducts().isEmpty());
	}
	
	
	@Test
	public void test_returnWithProducts_length() {
		
		returnItem.addProductToReturn(prod1);
		returnItem.addProductToReturn(prod2);
		
		assertEquals(returnItem.getRentProducts().size(), 2);
	}
	
	@Test
	public void test_close_open() {
		assertTrue(returnItem.isOpen());
		returnItem.close();
		assertFalse(returnItem.isOpen());
		returnItem.open();
		assertTrue(returnItem.isOpen());
	}
	
	@AfterClass
	public static void finishAfterClass() {
		
		try {
			app.stop();
		} catch (Exception e) {
			fail("App unable to finish");
		}
	}

}

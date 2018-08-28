package dataaccess;

import static org.junit.Assert.*;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import business.CatalogReturn;
import business.CatalogSale;
import business.ProductToRent;
import business.RentSys;
import business.Return;
import business.Sale;
import business.SaleSys;

public class ReturnMapperTests {
	
	private Return returnItem;
	
	private static RentSys app;
	private static ProductToRent prod1, prod2, prod3;
	private CatalogReturn returnCatalog;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		try {
			app = new RentSys();
			app.start();
			
			prod1 = ProductToRentMapper.getProductToRentById(2001); 
			prod2 = ProductToRentMapper.getProductToRentById(2002);
			prod3 = ProductToRentMapper.getProductToRentById(2003);
			
			
		} catch (Exception e) {
			fail("App didn't start or products could not be retrieved");
		}
	}
	
	@Before
	public void setUp() throws Exception {
		
		returnCatalog = new CatalogReturn();
		
		int return_id = ReturnMapper.insert(new GregorianCalendar(2016,12,31).getTime(), 1);
		
		ReturnProductMapper.insert(prod1.getId(), return_id);
		ReturnProductMapper.insert(prod2.getId(), return_id);
		ReturnProductMapper.insert(prod3.getId(), return_id);
		
		returnItem = ReturnMapper.getReturnById(return_id);
		returnItem.close();
		
		ReturnMapper.update(returnItem.getId(), returnItem.getStatus());
	}

	
	@Test
	public void test_correctSavedId() {
		try {
			Return savedReturn = ReturnMapper.getReturnById(returnItem.getId());
			
			assertEquals(returnItem.getId(), savedReturn.getId());
		} catch (PersistenceException e) {
			fail("Saved return could not be retrieved");
		}
	}
	
	@Test
	public void test_correctGetAll() {
		List<Return> list = getReturnInList();               

		assertTrue(list.size() == 1);                    
		assertTrue(list.get(0).getId() == returnItem.getId()); 
	}
	
	@Test
	public void test_correctUpdate() {
		
		try {
		    returnItem.open();  
		    ReturnMapper.update(returnItem.getId(), returnItem.getStatus());
		    

		    Return savedReturn = ReturnMapper.getReturnById(returnItem.getId());
		    assertTrue(savedReturn.isOpen());
		    			
		} catch (PersistenceException e) {
			fail("Return was not updated properly.");
		}
	}
	
	@Test
	public void test_correctDelete() {
		try {
			ReturnMapper.delete(returnItem.getId());
			assertTrue(getReturnInList().isEmpty());
		} catch (PersistenceException e) {
			fail("Return was not deleted");
		}
	}
	
	@After
	public void finish() {
		try {

			if (!getReturnInList().isEmpty())   
				ReturnMapper.delete(returnItem.getId());
		} catch (PersistenceException e) {
			fail("Return could not be deleted");
		}
	}

	private List<Return> getReturnInList() {
		try {
			return ReturnMapper.getAllReturn()   
					          .stream()
					          .filter(aReturn -> aReturn.getId() == returnItem.getId())
					          .collect(Collectors.toList());   
		} catch (PersistenceException e) {
			return null; 
		}
	}
	
}

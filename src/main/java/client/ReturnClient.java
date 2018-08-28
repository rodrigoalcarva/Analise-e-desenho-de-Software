package client;

import java.sql.SQLException;

import business.ApplicationException;
import business.CatalogReturn;
import business.RentSys;
import business.Return;
import use_cases.HandlerReturnItem;

/**
 * A simple application client that uses both services.
 *	
 * @author grupo11
 * @version 1.2 (11/02/2015)
 * 
 */

public class ReturnClient {
	/**
	 * A simple interaction with the application services
	 * 
	 * @param args Command line parameters
	 */
	public static void main(String[] args) {
		
		RentSys app = new RentSys();
		
		try {
			app.start();
		} catch (ApplicationException e) {
			System.out.println(e.getMessage());
			System.out.println("Application Message: " + e.getMessage());
			SQLException e1 = (SQLException) e.getCause().getCause();
			System.out.println("SQLException: " + e1.getMessage());
			System.out.println("SQLState: " + e1.getSQLState());
			System.out.println("VendorError: " + e1.getErrorCode());
			return;
		}
		
		// create catalog(s)
		CatalogReturn returnCatalog = new CatalogReturn();
		
		HandlerReturnItem hps = new HandlerReturnItem(returnCatalog); 
		int clientId = 1;
		
			
		try { // sample interaction		
			
			System.out.println("\n-- Add return and print it ----------------------------");
			
			// creates a new return
			Return returnItem = hps.newReturn(clientId);

			// adds two products to the database
			hps.addProductToReturn(returnItem, 2003);
			hps.addProductToReturn(returnItem, 2004);
			
			// close return
			hps.closeReturn(returnItem);
			
			//////////////////
			returnItem = returnCatalog.getReturn(returnItem.getId());
			System.out.println(returnItem);		
			
			//////////////////
			
			System.out.println("\n-- Print all returns ----------------------------------");

			System.out.println(returnCatalog);
			
			//////////////////
			
			hps.deleteReturn(returnItem);
			
			System.out.println("\n-- Print all returns after delete ---------------------");

			System.out.println(returnCatalog);	
						
			
			
		} catch (ApplicationException e) {
			System.out.println("Error: " + e.getMessage());
			// for debugging purposes only. Typically, in the application this information 
			// can be associated with a "details" button when the error message is displayed.
			if (e.getCause() != null) 
				System.out.println("Cause: ");
			e.printStackTrace();
		}
	
		app.stop();
	}
}

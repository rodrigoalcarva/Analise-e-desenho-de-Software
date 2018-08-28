package client;

import java.sql.SQLException;

import business.ApplicationException;
import business.CatalogRent;
import business.Rent;
import business.RentSys;
import use_cases.HandlerRentItem;

/**
 * A simple application client that uses both services.
 *	
 * @author grupo11
 * @version 1.2 (11/02/2015)
 * 
 */


public class RentClient {
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
		CatalogRent rentCatalog = new CatalogRent();
		
		HandlerRentItem hps = new HandlerRentItem(rentCatalog); 
		int clientId = 1;
		
			
		try { // sample interaction		
			
			System.out.println("\n-- Add rent and print it ----------------------------");
			
			// creates a new rent
			Rent rent = hps.newRent(clientId);

			// adds two products to the database
			hps.addProductToRent(rent, 2003);
			hps.addProductToRent(rent, 2004);
			
			// close rent
			hps.closeRent(rent);
			
			//////////////////
			rent = rentCatalog.getRent(rent.getId());
			System.out.println(rent);		
			
			//////////////////
			
			System.out.println("\n-- Print all rents ----------------------------------");

			System.out.println(rentCatalog);
			
			//////////////////
			
			hps.deleteRent(rent);
			
			System.out.println("\n-- Print all rents after delete ---------------------");

			System.out.println(rentCatalog);			
			
			
			
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

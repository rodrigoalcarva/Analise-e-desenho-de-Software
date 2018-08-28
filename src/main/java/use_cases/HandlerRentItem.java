package use_cases;

import business.ApplicationException;
import business.CatalogRent;
import business.Rent;

/**
 * Handles use case process rent. Version with two operations: 
 * newRent followed by an arbitrary number of addProductToRent
 * 
 * @author grupo11
 * @version 1.3 (06/01/2017)
 */

public class HandlerRentItem {

	private CatalogRent rentCatalog;
	
	/**
	 * Creates a handler for the process rent use case given 
	 * the rent, and product catalogs which contain the relevant
	 * operators to execute this use case methods
	 * 
	 * @param rentCatalog    A rent's catalog
	 * @param productCatalog A product's catalog
	 */
	
	public HandlerRentItem(CatalogRent rentCatalog) {
		this.rentCatalog = rentCatalog;
	}
	
	/**
	 * Creates a new rent
	 * 
	 * @throws ApplicationException In case the rent fails to be created
	 */

	public Rent newRent(int idClient) throws ApplicationException {
		return rentCatalog.newRent(idClient);
	}

	/**
	 * Adds a product to a rent
	 * 
	 * @param rent      The current rent 
	 * @param prod		The product id to be added to the rent 
	 * @throws ApplicationException When the rent is closed, the product code
	 * is not part of the product's catalog
	 */
	
	public void addProductToRent (Rent rent, int prod) throws ApplicationException {
		rentCatalog.addProductToRent(rent, prod);
	}

	/**
     * Closes an open rent
     * @param rent   The current rent
     * @throws ApplicationException 
     */
	
	public void closeRent(Rent rent) throws ApplicationException {
		rentCatalog.closeRent(rent);		
	}
	
	/**
     * Deletes rent
     * @param rent   The current rent 
     * @throws ApplicationException 
     */

	public void deleteRent(Rent rent) throws ApplicationException {
		rentCatalog.deleteRent(rent);		
	}
}

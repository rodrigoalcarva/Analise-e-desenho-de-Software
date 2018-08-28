package use_cases;

import business.ApplicationException;
import business.CatalogReturn;
import business.Return;

/**
 * Handles use case return rent. Version with two operations: 
 * newReturn followed by an arbitrary number of addProductToReturn
 * 
 * @author grupo11
 * @version 1.3 (06/01/2017)
 */

public class HandlerReturnItem {
	private CatalogReturn returnCatalog;
	
	/**
	 * Creates a handler for the return rent use case given 
	 * the return, and product catalogs which contain the relevant
	 * operators to execute this use case methods
	 * 
	 * @param returnCatalog    A return's catalog
	 * @param productCatalog A product's catalog
	 */
	
	public HandlerReturnItem(CatalogReturn returnCatalog) {
		this.returnCatalog = returnCatalog;
	}

	/**
	 * Creates a new return
	 * 
	 * @throws ApplicationException In case the return fails to be created
	 */
	
	public Return newReturn(int idClient) throws ApplicationException {
		return returnCatalog.newReturn(idClient);
	}

	/**
	 * Adds a product to a return
	 * 
	 * @param return      The current return 
	 * @param prod  	  The product id to be added to the return 
	 * @throws ApplicationException When the return is closed, the product code
	 * is not part of the product's catalog
	 */
	
	public void addProductToReturn (Return returnItem, int prod) throws ApplicationException {
		returnCatalog.addProductToReturn(returnItem, prod);
	}
	
	/**
     * Closes an open return
     * @param return   The current return 
     * @throws ApplicationException 
     */

	public void closeReturn(Return returnItem) throws ApplicationException {
		returnCatalog.closeReturn(returnItem);		
	}
	
	/**
     * Deletes return
     * @param return   The current return 
     * @throws ApplicationException 
     */

	public void deleteReturn(Return returnItem) throws ApplicationException {
		returnCatalog.deleteReturn(returnItem);		
	}
}

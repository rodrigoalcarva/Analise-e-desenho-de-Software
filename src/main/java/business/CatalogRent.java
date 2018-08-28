package business;

import java.util.Date;
import java.util.List;

import dataaccess.HistoryMapper;
import dataaccess.PersistenceException;
import dataaccess.ProductMapper;
import dataaccess.ProductToRentMapper;
import dataaccess.RentMapper;
import dataaccess.RentProductMapper;

/** 
 * Includes operations regarding Sales
 * 
 * @author grupo11
 *
 */

public class CatalogRent {	
	
	/**
	 * Creates a new rent, initially it open, and has a total of zero 
	 * @return
	 * @throws ApplicationException
	 */
	
	public Rent newRent(int client_id) throws ApplicationException {
		try {
			int rent_id = RentMapper.insert(new Date(), client_id);  // create new entry in the database
			
			return RentMapper.getRentById(rent_id);       
		} catch (PersistenceException e) {
			throw new ApplicationException("Unable to create new rent", e);
		}
	}
	
	/**
	 * Add a product to rent
	 * 
	 * @param rent    The current sale (must be open)
	 * @param prod_to_rent_id The product id to add (must exist)
	 * @throws ApplicationException If some of these assumptions does not hold
	 */
	public void addProductToRent(Rent rent, int prod_to_rent_id) throws ApplicationException {
		
		if (!rent.isOpen())    // check if it's open
			throw new ApplicationException("Rent " + rent.getId() + " is already closed!");
		    
		ProductToRent productToRent;
		ProductSpec product;
		
		// check if product exists and the stock is enough, if so update stock
		try {			
			
			productToRent = ProductToRentMapper.getProductToRentById(prod_to_rent_id);	
			product = ProductMapper.getProductById(productToRent.getProductId());
			
			if (product.getStock() < 1)   // not enough units?
				throw new ApplicationException("Current stock is not enough to rent 0 units of product " + product.getId());
			
			// otherwise, update stock
			product.setStock(product.getStock() - 1);
			ProductMapper.updateStockValue(product.getId(), product.getStock());
			
		} 
		catch (PersistenceException e) {
			throw new ApplicationException("Product to rent " + prod_to_rent_id + " does not exist!", e);
		}
		
		rent.addProductToRent(productToRent);  // add it to the object sale
		
		try {
			RentProductMapper.insert(productToRent.getId(), rent.getId());  // add it to the database
		} 
		catch (PersistenceException e) {
			throw new ApplicationException("Unable to add " + productToRent.getId() + 
					" to rent id " + rent.getId(), e);
		}
	}
	/**
	 * Close rent, updating the total and its status.
	 * If the rent was already closed, nothing happens.
	 * 
	 * @param rent the rent to be closed
	 * @throws ApplicationException 
	 */
	public void closeRent(Rent rent) throws ApplicationException {
		
		if (rent.isOpen()) {
			try {
				rent.close();
				
				for (RentProduct product : rent.getRentProducts()) {
					List<History> history = HistoryMapper.getAllHistory();
					boolean toInsert = true;
					
					for (History h : history) {
						if (h.getProductId() == product.getProductToRent().getProductId() && h.getClientId() == rent.getClientId()) {
							toInsert = false;
						}
					}
					
					if (toInsert)
						HistoryMapper.insert(product.getProductToRent().getProductId(), rent.getClientId(), "Nao devolvido", rent.getDate());
				}
				
				RentMapper.update(rent.getId(), rent.total(), rent.getStatus());
			} catch (PersistenceException e) {
				throw new ApplicationException("Unable to close " + rent.getId() + 
						", or unable to find it", e);
			}
		}
	}
	
	/**
	 * delete rent and its rent products
	 * 
	 * @param rent the rent to be deleted
	 * @throws ApplicationException
	 */
	public void deleteRent(Rent rent) throws ApplicationException {
		try {
			RentMapper.delete(rent.getId());
		} catch (PersistenceException e) {
			throw new ApplicationException("Unable to delete rent " + rent.getId(), e);
		}
	}
	
	public Rent getRent(int rent_id) throws ApplicationException {
		try {
			return RentMapper.getRentById(rent_id);
		} catch (PersistenceException e) {
			throw new ApplicationException("Unable to retrieve rent " + rent_id, e);
		}
	}
	
	public String toString() {
		try {
			List<Rent> rents = RentMapper.getAllRent();
			StringBuffer sb = new StringBuffer();

			for(Rent rent : rents) {
			  sb.append(rent);
			  sb.append("\n");
			}
			
			return sb.toString();			
		} catch (PersistenceException e) {
			System.out.println(e);
			return "N/A"; // something went wrong
		}
	}
	
}

package business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dataaccess.HistoryMapper;
import dataaccess.PersistenceException;
import dataaccess.ProductMapper;
import dataaccess.ProductToRentMapper;
import dataaccess.RentMapper;
import dataaccess.RentProductMapper;
import dataaccess.ReturnMapper;

public class CatalogReturn {
	public Return newReturn(int client_id) throws ApplicationException {
		try {
			int return_id = ReturnMapper.insert(new Date(), client_id);  // create new entry in the database
			return ReturnMapper.getReturnById(return_id);       
		} catch (PersistenceException e) {
			throw new ApplicationException("Unable to create new return", e);
		}
	}
	
	public void addProductToReturn(Return returnItem, int prod_to_rent_id) throws ApplicationException {
		
		if (!returnItem.isOpen())    // check if it's open
			throw new ApplicationException("Return " + returnItem.getId() + " is already closed!");
		    
		ProductToRent productToReturn;
		ProductSpec product;
		
		try {				
			productToReturn = ProductToRentMapper.getProductToRentById(prod_to_rent_id);	
			product = ProductMapper.getProductById(productToReturn.getProductId());
			
			// otherwise, update stock
			ProductMapper.updateStockValue(product.getId(), product.getStock());
		} 
		
		catch (PersistenceException e) {
			throw new ApplicationException("Product to rent " + prod_to_rent_id + " does not exist!", e);
		}
		
		returnItem.addProductToReturn(productToReturn); 
		
		try {
			RentProductMapper.insert(productToReturn.getId(), returnItem.getId());  // add it to the database
		} 
		catch (PersistenceException e) {
			throw new ApplicationException("Unable to add " + productToReturn.getId() + 
					" to return id " + returnItem.getId(), e);
		}
	}
	
	public void closeReturn(Return returnItem) throws ApplicationException {
		
		if (returnItem.isOpen()) {
			try {
				returnItem.close();
				
				for (RentProduct product : returnItem.getRentProducts()) {
					History history = HistoryMapper.getHistory(product.getProductToRent().getProductId(), returnItem.getClientId());
					
					HistoryMapper.update(history.getProductId(), history.getClientId(), "Devolvido");				
					
					Calendar returntDate = Calendar.getInstance();
					returntDate.setTime(returnItem.getDate());
					
					Calendar softLimit = Calendar.getInstance();
					softLimit.setTime(history.getInsertDate());
					softLimit.add(Calendar.DATE, product.getProductToRent().getSoftLimit());
					
					Calendar hardLimit = Calendar.getInstance();
					hardLimit.setTime(history.getInsertDate());
					hardLimit.add(Calendar.DATE, product.getProductToRent().getHardLimit());
					
					if (hardLimit.before(returntDate)) {
						System.out.println("Cliente tem que pagar restante valor do produto com id: " + product.getProductToRent().getProductId());
					}
					
					else if (hardLimit.before(returntDate)) {
						System.out.println("Cliente tem que pagar restante valor da multa do produto com id: " + product.getProductToRent().getProductId());
					}
				
				}
				
				System.out.println(" \n Atual historico do cliente");
				List<History> historico = HistoryMapper.getAllHistory();
				
				for (History history : historico) {
					if (history.getClientId() == returnItem.getClientId())
						System.out.println(history);
					
				}
				
				RentMapper.update(returnItem.getId(), returnItem.total(), returnItem.getStatus());
			} catch (PersistenceException e) {
				throw new ApplicationException("Unable to close " + returnItem.getId() + 
						", or unable to find it", e);
			}
		}
	}
	
	public void deleteReturn(Return returnItem) throws ApplicationException {
		try {
			ReturnMapper.delete(returnItem.getId());
		} catch (PersistenceException e) {
			throw new ApplicationException("Unable to delete return " + returnItem.getId(), e);
		}
	}
	
	public Return getReturn(int return_id) throws ApplicationException {
		try {
			return ReturnMapper.getReturnById(return_id);
		} catch (PersistenceException e) {
			throw new ApplicationException("Unable to retrieve return " + return_id, e);
		}
	}
	
	public String toString() {
		try {
			List<Return> returns = ReturnMapper.getAllReturn();
			StringBuffer sb = new StringBuffer();

			for(Return returnItem : returns) {
			  sb.append(returnItem);
			  sb.append("\n");
			}
			
			return sb.toString();			
		} catch (PersistenceException e) {
			System.out.println(e);
			return "N/A"; // something went wrong
		}
	}
}

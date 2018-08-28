package business;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Rent {
	
	private int id;
	private Date date;
	private String status;
	private int clientId;
	private List<RentProduct> rentProducts;
	
	public static final String open   = "O";
	public static final String closed = "C";
	
	/**
	 * Creates a new rent given the date it occurred and the customer that
	 * made the rent.
	 * 
	 * @param date The date that the rent occurred
	 */
	public Rent(int id, Date date, int clientId) {
		this.id = id;
		this.date = date;
		this.status = open; 
		this.clientId = clientId;
		this.rentProducts = new LinkedList<RentProduct>();
	}

	public int getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}
	
	public int getClientId() {
		return clientId;
	}
	
	public List<RentProduct> getRentProducts() {
		return rentProducts;
	}
	/**
	 * @return Whether the rent is open
	 */
	public boolean isOpen() {
		return status.equals(open);
	}
	
	public void close() {
		 status = closed;
	}
	
	/**
	 * @return The rent's total 
	 */
	public double total() {
		double total = 0.0;
		for (RentProduct sp : rentProducts)
			total += sp.getSubTotal();
		return total;
	}
	
	/**
	 * Adds a product to the rent
	 * 
	 * @param product The product to rent
	 * @throws ApplicationException 
	 */
	public void addProductToRent(ProductToRent product) {
		rentProducts.add(new RentProduct(product));
	}

	public String getStatus() {
		return status;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Rent @ " + date.toString() + "; " + (isOpen()?"open":"closed") + " Cliente: " + clientId + "; total of €" + total() + " with products: \n");
		for (RentProduct sp : rentProducts)
			sb.append(" [Produto " + sp.getProductToRent().getProductId() + ", 1º prazo: " + sp.getProductToRent().getSoftLimit() + " dias , 2º prazo: " + sp.getProductToRent().getHardLimit() + " dias] \n");
		return sb.toString();
	}
}

package business;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Return {
	private int id;
	private Date date;
	private String status;
	private int clientId;
	private List<RentProduct> returnProducts;
	
	public static final String open   = "O";
	public static final String closed = "C";
	
	public Return(int id, Date date, int clientId) {
		this.id = id;
		this.date = date;
		this.status = open; 
		this.clientId = clientId;
		this.returnProducts = new LinkedList<RentProduct>();
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
		return returnProducts;
	}

	public boolean isOpen() {
		return status.equals(open);
	}
	
	public void close() {
		 status = closed;
	}
	
	public void open() {
		 status = open;
	}
	
	public double total() {
		double total = 0.0;
		for (RentProduct sp : returnProducts) {
			total += sp.getSubTotal();
		}
		return total;
	}
	
	public void addProductToReturn(ProductToRent product) {
		returnProducts.add(new RentProduct(product));
	}

	public String getStatus() {
		return status;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Return @ " + date.toString() + "; " + (isOpen()?"open":"closed") + " Cliente: " + clientId + "; total of €" + total() + " with products: \n");
		for (RentProduct sp : returnProducts)
			sb.append(" [Produto " + sp.getProductToRent().getProductId() + ", 1º prazo: " + sp.getProductToRent().getSoftLimit() + " dias , 2º prazo: " + sp.getProductToRent().getHardLimit() + " dias] \n");
		return sb.toString();
	}
}

package business;

import java.util.Date;

public class History {
	
	private int id;
	private int productId;
	private int clientId;
	private String status;
	private Date insertDate;
	
	public History(int id, int productId, int clientId, String status, Date insertDate) {
		this.id = id;
		this.productId = productId;
		this.clientId = clientId;
		this.status = status;
		this.insertDate = insertDate;
	}

	public int getId() {
		return id;
	}

	public int getProductId() {
		return productId;
	}

	public int getClientId() {
		return clientId;
	}

	public String getStatus() {
		return status;
	}	
	
	public Date getInsertDate() {
		return insertDate;
	}
	
	@Override
	public String toString() {
		return " [Id " + id + ", Produto " + productId + " cliente " + clientId + ", Status: " + status + ", Data de inicio de aluguer: " + insertDate + "] \n"; 
	}
}

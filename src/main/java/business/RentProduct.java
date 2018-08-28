package business;

public class RentProduct {
	
	private int id;
	private ProductToRent productToRent;
	
	/**
	 * Creates a product that is part of a rent.
	 * 
	 * @param productToRent The product to be associated with the rent.
	 */
	public RentProduct(ProductToRent productToRent) {
		this.productToRent = productToRent;
	}
	
	/**
	 * @return The product of the product rent
	 */
	public ProductToRent getProductToRent() {
		return productToRent;
	}
	
	/**
	 * @return The sub total of the product rent
	 */
	public double getSubTotal() {
		return productToRent.getPrice();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;		
	}
	
	@Override
	public String toString() {
		return " [Produto " + productToRent.getProductId() + ", 1º prazo: " + productToRent.getSoftLimit() + ", 2º prazo: " + productToRent.getHardLimit() + "] \n"; 
	}
}

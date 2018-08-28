package business;

public class ProductToRent {
	
	private int id;
	private int productId;
	private double price;
	private int softLimit;
	private int hardLimit;
	private double fine;
	
	/**
	 * Creates a new product to rent given its id, productId, price, softLimit, 
	 * hardLimit, fine.
	 * 
	 * @param productId        The product code
	 * @param price  		   The value by which the product should be sold
	 * @param softLimit        int that corresponds the number of days until the end of the first term
	 * @param hardLimit        int that corresponds the number of days until the end of the second/last term
	 * @param fine         	   double that corresponds to the fine
	 */
	
	public ProductToRent(int id, int productId, double price, int softLimit, int hardLimit, double fine) {
		this.id = id;
		this.productId = productId;
		this.price = price;
		this.softLimit = softLimit;
		this.hardLimit = hardLimit;
		this.fine = fine;
	}

	public int getId() {
		return id;
	}

	/**
	 * @return The code of the product.
	 */
	
	public int getProductId() {
		return productId;
	}
	
	/**
	 * @return The rent price
	 */
	public double getPrice() {
		return price;
	}
	
	/**
	 * @return The rent softLimit
	 */
	public int getSoftLimit() {
		return softLimit;
	}
	
	/**
	 * @return The rent hardLimit
	 */
	public int getHardLimit() {
		return hardLimit;
	}
	
	/**
	 * @return The rent fine if passed the limits 
	 */
	public double getFine() {
		return fine;
	}
		
}

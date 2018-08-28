package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import business.ProductToRent;

public class ProductToRentMapper {
	
	// SQL statement: select product to rent with given id (the database id)
	private static final String GET_PRODUCT_TO_RENT_BY_ID_SQL = "SELECT * FROM product_to_rent WHERE id = ?";
		
	/**
	 * Gets a product to rent given its id
	 * @param id
	 * @return
	 * @throws PersistenceException
	 */
	public static ProductToRent getProductToRentById (int id) throws PersistenceException {
		try (PreparedStatement Statement = DataSource.INSTANCE.prepare(GET_PRODUCT_TO_RENT_BY_ID_SQL)) {
			// set statement arguments
			Statement.setInt(1, id);
			// execute SQL
			try (ResultSet rs = Statement.executeQuery()) {
				// creates a new product with the data retrieved from the database
				return loadProductToRent(rs);	
			}
		} catch (SQLException e) {
			throw new PersistenceException("Internal error getting product with id " + id, e);
		}
	}
		
	/**
	 * Creates a ProductToRent from a result set
	 * @param rs
	 * @return
	 * @throws RecordNotFoundException
	 */
	private static ProductToRent loadProductToRent(ResultSet rs) throws RecordNotFoundException {
		try {
			rs.next();
			
			ProductToRent product = new ProductToRent(rs.getInt("id"), 
						                              rs.getInt("product_id"),
						                              rs.getDouble("price"),
						                              rs.getInt("soft_limit"),
						                              rs.getInt("hard_limit"),
						                              rs.getDouble("fine"));				
			return product;
		} catch (SQLException e) {
			throw new RecordNotFoundException ("Product to rent not found! ", e);
		}
	}
}

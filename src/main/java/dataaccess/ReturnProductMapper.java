package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import business.ProductToRent;
import business.RentProduct;

public class ReturnProductMapper {
	
	// SQL statement: insert product in a return 
	private static final String INSERT_PRODUCT_RETURN_SQL = "INSERT INTO returnproduct (id, product_to_rent_id, return_id) VALUES (DEFAULT, ?, ?)";
	
	/**
	 * Insert product to rent in return products table
	 * @param product_to_rent_id
	 * @param return_id
	 * @return the return Product id
	 * @throws PersistenceException
	 */
	public static int insert (int product_to_rent_id, int return_id) throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepareGetGenKey(INSERT_PRODUCT_RETURN_SQL)) {
			statement.setInt(1, product_to_rent_id);    // set statement arguments
			statement.setInt(2, return_id);
			statement.executeUpdate();      // execute SQL
			  // Gets rent product Id generated automatically by the database engine
			try (ResultSet rs = statement.getGeneratedKeys()) {
				rs.next(); 
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new PersistenceException ("Internal error inserting product " + product_to_rent_id + " into rent " + return_id, e);
		}
	}
	
	// SQL statement: deletes product of return
	private static final String DELETE_RETURNPRODUCT_SQL = "DELETE FROM returnproduct WHERE return_id = ?";
	
	/**
	 * Delete product to rent in return products table
	 * @param return_id
	 * @throws PersistenceException
	 */
	public static void delete(int return_id) throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(DELETE_RETURNPRODUCT_SQL)) {
			statement.setDouble(1, return_id);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException ("Internal error!", e);
		}
	}

	
	// SQL statement: select the products of a return by return id 
	private static final String GET_RETURN_PRODUCTS_SQL = "SELECT * FROM returnproduct WHERE return_id = ?";

	/**
	 * Select products to rent in return products table from return id
	 * @param return_id
	 * @return The set of products that compose the return
	 * @throws PersistenceException
	 */
	public static List<RentProduct> getReturnProducts(int return_id) throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_RETURN_PRODUCTS_SQL)) {
			// set statement arguments
			statement.setInt(1, return_id);			
			// execute SQL
			try (ResultSet rs = statement.executeQuery()) {
				// creates the sale's products set with the data retrieved from the database
				return loadReturnProducts(rs);
			}
		} catch (SQLException e) {
			throw new PersistenceException("Internal error getting the products of sale " + return_id, e);
		}
	}
		
	
	/**
	 * Creates sets of products to return from a result set received
	 * @param rs
	 * @return the set of products of a return loaded from the database.
	 * @throws SQLException
	 * @throws PersistenceException
	 */
	private static List<RentProduct> loadReturnProducts(ResultSet rs) throws SQLException, PersistenceException {
		List<RentProduct> result = new LinkedList<RentProduct>();
		while (rs.next()) {
			ProductToRent product = ProductToRentMapper.getProductToRentById(rs.getInt("PRODUCT_TO_RENT_ID"));
			RentProduct newRentProduct = new RentProduct(product);
			newRentProduct.setId(rs.getInt("id"));
			result.add(newRentProduct);
		}
		return result;		
	}
}

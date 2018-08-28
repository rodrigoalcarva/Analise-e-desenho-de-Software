package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import business.ProductToRent;
import business.RentProduct;

public class RentProductMapper {
	
	// SQL statement: insert product in a rent 
	private static final String INSERT_PRODUCT_RENT_SQL = "INSERT INTO rentproduct (id, product_to_rent_id, rent_id) VALUES (DEFAULT, ?, ?)";
	
	/**
	 * Insert product to rent in rent products table
	 * @param product_to_rent_id
	 * @param rent_id
	 * @return the rent Product id
	 * @throws PersistenceException
	 */
	public static int insert (int product_to_rent_id, int rent_id) throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepareGetGenKey(INSERT_PRODUCT_RENT_SQL)) {
			statement.setInt(1, product_to_rent_id);    // set statement arguments
			statement.setInt(2, rent_id);
			statement.executeUpdate();      // execute SQL
			  // Gets rent product Id generated automatically by the database engine
			try (ResultSet rs = statement.getGeneratedKeys()) {
				
				rs.next(); 
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new PersistenceException ("Internal error inserting product " + product_to_rent_id + " into rent " + rent_id, e);
		}
	}
	
	// SQL statement: deletes rentProducts of rent
	private static final String DELETE_RENTPRODUCT_SQL = "DELETE FROM rentproduct WHERE rent_id = ?";
	
	/**
	 * Delete product to rent in rent products table
	 * @param rent_id
	 * @throws PersistenceException
	 */
	public static void delete(int rent_id) throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(DELETE_RENTPRODUCT_SQL)) {
			statement.setDouble(1, rent_id);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException ("Internal error!", e);
		}
	}

	// SQL statement: select the products of a rent by rent id 
	private static final String GET_RENT_PRODUCTS_SQL = "SELECT * FROM rentproduct WHERE rent_id = ?";

	/**
	 * Select products to rent in rent products table from rent id
	 * @param rent_id
	 * @return The set of products that compose the rent
	 * @throws PersistenceException
	 */
	public static List<RentProduct> getRentProducts(int rent_id) throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_RENT_PRODUCTS_SQL)) {
			statement.setInt(1, rent_id);		
			try (ResultSet rs = statement.executeQuery()) {
				return loadRentProducts(rs);
			}
		} catch (SQLException e) {
			throw new PersistenceException("Internal error getting the products of sale " + rent_id, e);
		}
	}
		
	/**
	 * Creates sets of products to rent from a result set received
	 * @param rs
	 * @return The set of products of a rent loaded from the database.
	 * @throws SQLException
	 * @throws PersistenceException
	 */
	private static List<RentProduct> loadRentProducts(ResultSet rs) throws SQLException, PersistenceException {
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

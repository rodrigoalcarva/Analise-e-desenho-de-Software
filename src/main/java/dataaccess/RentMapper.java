package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import business.Rent;
import business.RentProduct;

public class RentMapper {
	// the cache keeps all sales that were accessed during the current runtime
	static Map<Integer, Rent> cachedRents;
	
	static {
		// this code is initialized once per class
		cachedRents = new HashMap<Integer, Rent>();
	}
	
	/////////////////////////////////////////////////////////////////////////
	// SQL statement: inserts a new rent
	private static final String INSERT_RENT_SQL = "INSERT INTO rent (id, date, total, status, client_id) VALUES (DEFAULT, ?, ?, '" + Rent.open + "', ?)";
	
	/**
	 * Insert rent into database
	 * @param date
	 * @param clientId
	 * @return
	 * @throws PersistenceException
	 */
	public static int insert(java.util.Date date, int clientId) throws PersistenceException {	
		try (PreparedStatement statement = DataSource.INSTANCE.prepareGetGenKey(INSERT_RENT_SQL)) {  
			// set statement arguments
			statement.setDate(1, new java.sql.Date(date.getTime()));
			statement.setDouble(2, 0.0); // total
			statement.setInt(3, clientId);			
			// execute SQL
			statement.executeUpdate();
			// get sale Id generated automatically by the database engine
			try (ResultSet rs = statement.getGeneratedKeys()) {
				rs.next(); 
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new PersistenceException ("Error inserting a new rent!", e);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////
	// SQL statement: updates total and status from existing rent
	private static final String UPDATE_RENT_SQL = "UPDATE rent SET total = ?, status = ? WHERE id = ?";
	
	/**
	 * Update rent info from the database
	 * @param rent_id
	 * @param total
	 * @param status
	 * @throws PersistenceException
	 */
	public static void update(int rent_id, double total, String status) throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(UPDATE_RENT_SQL)) {
			statement.setDouble(1, total);
			statement.setString(2, status);
			statement.setInt(3, rent_id);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException ("Internal error!", e);
		}
		
		cachedRents.remove(rent_id);  // rent was changed, remove from cache
	}
	
	/////////////////////////////////////////////////////////////////////////
	// SQL statement: deletes rent 
	private static final String DELETE_RENT_SQL = "DELETE FROM rent WHERE id = ?";
	
	/**
	 * Deletes the rent data in the database.
	 * 
	 * @param rent_id The rent id to delete
	 * @throws PersistenceException If an error occurs during the operation
	 */
	public static void delete(int rent_id) throws PersistenceException {
		
		RentProductMapper.delete(rent_id);  // first remove its sale products
		
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(DELETE_RENT_SQL)) {
			statement.setDouble(1, rent_id);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException ("Internal error!", e);
		}
		
		cachedRents.remove(rent_id);  // sale was deleted, remove from cache
	}
	
	/////////////////////////////////////////////////////////////////////////
	// SQL statement: selects a rent by its id 
	private static final String GET_RENT_SQL = "SELECT * FROM rent WHERE id = ?";
	
	/**
	 * Gets a rent by its id 
	 * 
	 * @param rent_id The rent id to search for
	 * @return The new object that represents an in-memory rent
	 * @throws PersistenceException In case there is an error accessing the database.
	 */
	public static Rent getRentById(int rent_id) throws PersistenceException {
		
		if (cachedRents.containsKey(rent_id))  // perhaps this sale is cached?
			return cachedRents.get(rent_id);   //  yes, we don't need to query the database
		
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_RENT_SQL)) {
			
			// set statement arguments
			statement.setInt(1, rent_id);		
			// execute SQL
			try (ResultSet rs = statement.executeQuery()) {
				rs.next();
				Rent rent = loadRent(rs);             // creates sale object from result set
				cachedRents.put(rent.getId(), rent);  // inserts it into cache
				return rent;
			}
		} catch (SQLException e) {
			throw new PersistenceException("Internal error getting rent " + rent_id, e);
		} 
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	// SQL statement: get all rents 
	private static final String GET_ALL_RENTS_SQL = "SELECT * FROM rent";
	
	/**
	 * Retrieve all rents kept on database
	 * @return A list with all the rents
	 * @throws PersistenceException
	 */
	public static List<Rent> getAllRent() throws PersistenceException {
		
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_ALL_RENTS_SQL)) {		
			try (ResultSet rs = statement.executeQuery()) {

				List<Rent> rents = new LinkedList<Rent>();
				while(rs.next()) { // for each sale
					int rent_id = rs.getInt("id");          // get id of current sale
					if (cachedRents.containsKey(rent_id))   // check if it is cached
						rents.add(cachedRents.get(rent_id));
					else {
						Rent rent = loadRent(rs);           // if not, create a new sale object
						rents.add(rent);                    //  insert it to result list,
						cachedRents.put(rent_id, rent);     //  and cache it
					}
				}
				return rents;
			}
		} catch (SQLException e) {
			throw new PersistenceException("Unable to fetch all rents", e);
		} 
	}
	
	/**
	 * Creates a rent object from a result set retrieved from the database.
	 * 
	 * @requires rs.next() was already executed
	 * @param rs The result set with the information to create the rent.
	 * @return A new rent loaded from the database.
	 * @throws PersistenceException 
	 */
	private static Rent loadRent(ResultSet rs) throws PersistenceException {
		Rent rent;
		try {
			rent = new Rent(rs.getInt("id"), rs.getDate("date"), rs.getInt("client_id"));
			
			List<RentProduct> rentProducts = RentProductMapper.getRentProducts(rs.getInt("id"));
			
			for(RentProduct sp : rentProducts)
				rent.addProductToRent(sp.getProductToRent());
			
			if (rs.getString("status").equals(Rent.closed))
				rent.close();
			
		} catch (SQLException e) {
			throw new RecordNotFoundException ("Rent does not exist	", e);
		}		
		return rent;
	}
}

package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import business.RentProduct;
import business.Return;

public class ReturnMapper {
	static Map<Integer, Return> cachedReturns;
	
	static {
		cachedReturns = new HashMap<Integer, Return>();
	}
	
	// SQL statement: inserts a new return
	private static final String INSERT_RETURN_SQL = "INSERT INTO return (id, date, status, client_id) VALUES (DEFAULT, ?, '" + Return.open + "', ?)";
	
	/**
	 * Insert return into database
	 * @param date
	 * @param clientId
	 * @return
	 * @throws PersistenceException
	 */
	public static int insert(java.util.Date date, int clientId) throws PersistenceException {	
		try (PreparedStatement statement = DataSource.INSTANCE.prepareGetGenKey(INSERT_RETURN_SQL)) {  
			statement.setDate(1, new java.sql.Date(date.getTime()));
			statement.setInt(2, clientId);			
			statement.executeUpdate();
			try (ResultSet rs = statement.getGeneratedKeys()) {
				rs.next(); 
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new PersistenceException ("Error inserting a new rent!", e);
		}
	}
	
	// SQL statement: updates status from existing return
	private static final String UPDATE_RETURN_SQL = "UPDATE return SET status = ? WHERE id = ?";
	
	/**
	 * Update return info in database
	 * @param return_id
	 * @param status
	 * @throws PersistenceException
	 */
	public static void update(int return_id, String status) throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(UPDATE_RETURN_SQL)) {
			statement.setString(1, status);
			statement.setInt(2, return_id);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException ("Internal error!", e);
		}
		
		cachedReturns.remove(return_id);  
	}
	
	// SQL statement: deletes a return 
	private static final String DELETE_RETURN_SQL = "DELETE FROM return WHERE id = ?";
	
	/**
	 * Deletes the return info from the database
	 * @param return_id
	 * @throws PersistenceException
	 */
	public static void delete(int return_id) throws PersistenceException {
		
		ReturnProductMapper.delete(return_id);  // first remove its sale products
		
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(DELETE_RETURN_SQL)) {
			statement.setDouble(1, return_id);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException ("Internal error!", e);
		}
		
		cachedReturns.remove(return_id);  // sale was deleted, remove from cache
	}
	
	// SQL statement: selects a return by its id 
	private static final String GET_RETURN_SQL = "SELECT * FROM return WHERE id = ?";
	
	/**
	 * Gets a return by its id 
	 * 
	 * @param rent_id The return id to search for
	 * @return The new object that represents an in-memory return
	 * @throws PersistenceException In case there is an error accessing the database.
	 */
	public static Return getReturnById(int return_id) throws PersistenceException {
		
		if (cachedReturns.containsKey(return_id))
			return cachedReturns.get(return_id);
		
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_RETURN_SQL)) {
			
			// set statement arguments
			statement.setInt(1, return_id);		
			// execute SQL
			try (ResultSet rs = statement.executeQuery()) {
				rs.next();
				Return returnItem = loadReturn(rs);             // creates sale object from result set
				cachedReturns.put(returnItem.getId(), returnItem);  // inserts it into cache
				return returnItem;
			}
		} catch (SQLException e) {
			throw new PersistenceException("Internal error getting rent " + return_id, e);
		} 
	}
	
	
	// SQL statement: get all returns 
	private static final String GET_ALL_RETURNS_SQL = "SELECT * FROM return";
	
	/**
	 * Retrieve all returns kept on database
	 * @return A list with all the returns
	 * @throws PersistenceException
	 */
	public static List<Return> getAllReturn() throws PersistenceException {
		
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_ALL_RETURNS_SQL)) {		
			try (ResultSet rs = statement.executeQuery()) {

				List<Return> returns = new LinkedList<Return>();
				while(rs.next()) { 
					int return_id = rs.getInt("id");          
					if (cachedReturns.containsKey(return_id))   
						returns.add(cachedReturns.get(return_id));
					else {
						Return returnItem = loadReturn(rs);           
						returns.add(returnItem);                   
						cachedReturns.put(return_id, returnItem);    
					}
				}
				return returns;
			}
		} catch (SQLException e) {
			throw new PersistenceException("Unable to fetch all rents", e);
		} 
	}
	
	/**
	 * Creates a return object from a result set retrieved from the database.
	 * 
	 * @requires rs.next() was already executed
	 * @param rs The result set with the information to create the return.
	 * @return A new sale loaded from the database.
	 * @throws PersistenceException 
	 */
	private static Return loadReturn(ResultSet rs) throws PersistenceException {
		Return returnItem;
		try {
			returnItem = new Return(rs.getInt("id"), rs.getDate("date"), rs.getInt("client_id"));
			
			List<RentProduct> returnProducts = ReturnProductMapper.getReturnProducts(rs.getInt("id"));
			
			for(RentProduct sp : returnProducts)
				returnItem.addProductToReturn(sp.getProductToRent());
			
			if (rs.getString("status").equals(Return.closed))
				returnItem.close();
			
		} catch (SQLException e) {
			throw new RecordNotFoundException ("Return does not exist	", e);
		}		
		return returnItem;
	}
}

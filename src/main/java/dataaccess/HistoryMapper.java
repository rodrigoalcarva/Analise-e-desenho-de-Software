package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import business.History;

/**
 * @author Grupo11
 * 
 */

public class HistoryMapper {

	// SQL statement: insert a new History
	private static final String INSERT_HISTORY_SQL = "INSERT INTO history (id, product_id, client_id, status, insert_date) VALUES (DEFAULT, ?, ?, ?, ?)";
	
	/**
	 * Insert a new history into the database
	 * @param productId
	 * @param clientId
	 * @param status
	 * @param date
	 * @return int history id
	 * @throws PersistenceException
	 */
	public static int insert(int productId, int clientId,  String status,java.util.Date date) throws PersistenceException {	
		try (PreparedStatement statement = DataSource.INSTANCE.prepareGetGenKey(INSERT_HISTORY_SQL)) {  
			// set statement arguments
			statement.setInt(1, productId);
			statement.setInt(2, clientId);
			statement.setString(3, status);
			statement.setDate(4, new java.sql.Date(date.getTime()));
			// execute SQL
			statement.executeUpdate();
			try (ResultSet rs = statement.getGeneratedKeys()) {
				rs.next(); 
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new PersistenceException ("Error inserting a new history!", e);
		}
	}
	
	// SQL statement: Update existing History
	private static final String UPDATE_HISTORY_SQL = "UPDATE history SET status = ? WHERE product_id = ? AND client_id = ?";
	
	/**
	 * Insert a existing history in the database
	 * @param product_id
	 * @param client_id
	 * @param status
	 * @throws PersistenceException
	 */
	public static void update(int product_id, int client_id, String status) throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(UPDATE_HISTORY_SQL)) {
			statement.setString(1, status);
			statement.setInt(2, product_id);
			statement.setInt(3, client_id);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException ("Internal error!", e);
		}
	}
	
	// SQL statement: Select a history using product and client id
	private static final String GET_HISTORY_SQL = "SELECT ID, PRODUCT_ID, CLIENT_ID, STATUS, INSERT_DATE FROM history WHERE product_id = ? AND client_id = ?";
	
	/**
	 * Gets a history entry using its product and client id
	 * @param productId
	 * @param clientId
	 * @return History the history object that represents the database entry 
	 * @throws PersistenceException
	 */
	public static History getHistory(int productId, int clientId) throws PersistenceException {
		
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_HISTORY_SQL)) {
			
			// set statement arguments
			statement.setInt(1, productId);
			statement.setInt(2, clientId);
			// execute SQL
			try (ResultSet rs = statement.executeQuery()) {
				rs.next();
				History historyItem = loadHistory(rs);             // creates sale object from result set
				return historyItem;
			}
		} catch (SQLException e) {
			throw new PersistenceException("Internal error getting history " + productId, e);
		} 
	}
	
	// SQL statement: Select all history entries
	private static final String GET_ALL_HISTORY_SQL = "SELECT * FROM history";
	
	/**
	 * Get all history entries
	 * @return A list of History objects representing all the history entries
	 * @throws PersistenceException
	 */
	public static List<History> getAllHistory() throws PersistenceException {
		
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_ALL_HISTORY_SQL)) {		
			try (ResultSet rs = statement.executeQuery()) {

				List<History> histories = new LinkedList<History>();
				
				while(rs.next()) { // for each sale        // get id of current sale
					History history = loadHistory(rs); 	   // if not, create a new sale object
					histories.add(history);                //  insert it to result list
				}
				
				return histories;
			}
		} 
	
		catch (SQLException e) {
			throw new PersistenceException("Unable to fetch all rents", e);
		} 
	}
	
	/**
	 * Creates a History from a result set
	 * @param rs
	 * @return History object
	 * @throws PersistenceException
	 */
	private static History loadHistory(ResultSet rs) throws PersistenceException {
		History history;
		
		try {
			history = new History(rs.getInt("id"), rs.getInt("product_id"), rs.getInt("client_id"), rs.getString("status"), rs.getDate("insert_date"));
			
		} catch (SQLException e) {
			throw new RecordNotFoundException ("History does not exist	", e);
		}		
		return history;
	}
}

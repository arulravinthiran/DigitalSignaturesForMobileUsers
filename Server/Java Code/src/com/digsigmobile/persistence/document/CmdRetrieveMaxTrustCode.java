package com.digsigmobile.persistence.document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.digsigmobile.datatypes.TrustCode;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.exceptions.InvalidInputException;
import com.digsigmobile.persistence.Command;

public class CmdRetrieveMaxTrustCode extends Command
{
	/**
	 * TrustCode object to return the result
	 */
	private TrustCode maxTrCode = null;
	
	/**
	 * Initializes SQL command and sets up email
	 * @param email
	 */
	public CmdRetrieveMaxTrustCode() 
	{
		this.sqlCommand = "SELECT MAX(TrustCode) FROM `tbl_DocumentDetails`";
		this.isQuery = true;
	}
	
	/**
	 * method that retrieves the maximum trust code from the db if rows are present
	 * If the query returns empty set, method returns 9321456 as new trust code
	 */
	@Override
	public void run() throws DatabaseException {
		try 
		{
			if (resultSet.next()) 
			{
				int max = resultSet.getInt(1);
				if(max >= 0 && max <= 1712)
					maxTrCode = new TrustCode(1713);
				else
					maxTrCode = new TrustCode(++max);
			}
			else
				maxTrCode = new TrustCode(1713);
		} 
		catch (SQLException se) {
			
			throw new DatabaseException(se.getMessage());
		}
		
		catch (InvalidInputException e) {
			
			throw new DatabaseException(e.getMessage());
		}
		finally{
		     if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} 
				catch (SQLException e1) {
				    throw new DatabaseException(e1.getMessage());
				}
			}
			if (resultSet != null){
				try {
					resultSet.close();
				} catch (SQLException e1) {
				    throw new DatabaseException(e1.getMessage());
				}
			}
			}
		
	}

	
	/**
	 * Overrides method to prepare a statement specific for
	 * retrieving a trust code.
	 */
	@Override
	protected PreparedStatement prepareCommand(Connection conn) throws DatabaseException {
		try {
			preparedStatement = conn.prepareStatement(this.sqlCommand);
			return preparedStatement; 
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	/**
	 * Method to get the TrustCode retrieved from the database
	 * @return maxTrCode
	 */
	public TrustCode getMaxTrustCode() {
		//System.out.println("Max trust code is: "+maxTrCode);
		return maxTrCode;
	}

}

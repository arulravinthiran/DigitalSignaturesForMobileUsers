package com.digsigmobile.persistence.digitalsignature;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.digsigmobile.datatypes.TrustCode;
import com.digsigmobile.datatypes.UserId;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.persistence.Command;

public class CmdValidateCoSigner extends Command
{
	/**
	 * Receives a trust code object to identify
	 * a digital signature.
	 */
	private TrustCode trCode = null;
	
	/**
	 * Receives the user id to be validated
	 */
	private UserId userId;
	
	/**
	 * variable that identifies the matching user id and trust code
	 */
	private int count;
	
	/**
	 * Initializes SQL command and sets up trCode and userid 
	 * @param trCode
	 * @param userId
	 */
	public CmdValidateCoSigner(TrustCode trCode, UserId userId) 
	{
		this.trCode = trCode;
		this.userId = userId;
		
		this.sqlCommand = 
			"SELECT COUNT(*)" +
			"FROM TBL_DIGITALSIGNATUREDETAILS " +
			"WHERE TrustCode = ? AND UserID = ?";
		
		this.isQuery = true;
	}
	
	/**
	 * method retrieves the count of rows of matching user id and trust code
	 */
	@Override
	public void run() throws DatabaseException {
		try 
		{
			if (resultSet.next())
			 {
				this.count = resultSet.getInt(1);
			  }
		     } 
		catch (SQLException e1)  
		{
			  throw new DatabaseException(e1.getMessage());
		}
		
			
		finally
		{
			if (preparedStatement != null) 
			{
				try {
					preparedStatement.close();
				} catch (SQLException e1) {
					throw new DatabaseException(e1.getMessage());	
				}
			}
			if (resultSet != null)
			{
				try 
				{
					resultSet.close();
				}
				catch (SQLException e1) 
				{
					throw new DatabaseException(e1.getMessage());	
				}
		     }
		}
	}

	/**
	 * Overrides method to prepare a statement specific for
	 * retrieving a digital signature.
	 */
	@Override
	protected PreparedStatement prepareCommand(Connection conn) throws DatabaseException 
	{
		try 
		{
			preparedStatement = conn.prepareStatement(this.sqlCommand);
			preparedStatement.setInt(1, trCode.getTrustCode());
			preparedStatement.setInt(2, userId.getId());
			return preparedStatement; 
		} 
		catch (SQLException e) 
		{
			throw new DatabaseException(e.getMessage());
		}
	}
	
	/**
	 * if count is equal to 1, then the method returns true
	 * @return
	 */
	public boolean validateCoSigner() 
	{
		if(this.count == 1)
			return true;
		else
			return false;
	}

}

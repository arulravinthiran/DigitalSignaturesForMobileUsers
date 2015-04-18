package com.digsigmobile.persistence.certificate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.digsigmobile.datatypes.CertificateId;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.exceptions.InvalidInputException;
import com.digsigmobile.persistence.Command;

public class CmdRetrieveMaxCertId extends Command
{
	/**
	 * CertificateId object to return the result
	 */
	private CertificateId maxCertId = null;
	
	/**
	 * Initializes SQL command and sets up email
	 * @param email
	 */
	public CmdRetrieveMaxCertId() 
	{
		this.sqlCommand = "SELECT MAX(CertificateID) FROM `tbl_CertificateDetails`";
		this.isQuery = true;
	}
	
	/**
	 * method that retrieves the maximum certificate id from the db if rows are present
	 * If the query returns empty set, method returns 1 as new certificate id 
	 */
	@Override
	public void run() throws DatabaseException {
		try 
		{
			if (resultSet.next()) 
			{
				int max = resultSet.getInt(1);
				maxCertId = new CertificateId(++max);
			}
			else
				maxCertId = new CertificateId(1);
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
				} catch (SQLException e1) {
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
	 * retrieving s certificate id.
	 */
	@Override
	protected PreparedStatement prepareCommand(Connection conn) throws DatabaseException 
	{
		try {
			preparedStatement = conn.prepareStatement(this.sqlCommand);
			return preparedStatement; 
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	/**
	 * Method to get the CertificateId retrieved from the database
	 * @return maxcertId
	 */
	public CertificateId getMaxCertId() {
		return maxCertId;
	}

}
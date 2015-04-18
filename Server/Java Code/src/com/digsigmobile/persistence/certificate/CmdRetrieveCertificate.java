package com.digsigmobile.persistence.certificate;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.digsigmobile.beans.CertificateBean;
import com.digsigmobile.datatypes.CertificateId;
import com.digsigmobile.datatypes.UserId;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.exceptions.InvalidInputException;
import com.digsigmobile.persistence.Command;

public class CmdRetrieveCertificate extends Command
{
	/**
	 * Receives a UserId object to identify
	 * a certificate.
	 */
	private UserId userId = null;
	
	/**
	 * CertificateBean object to return the result
	 */
	private CertificateBean cert = null;
	
	/**
	 * Initializes SQL command and sets up email
	 * @param email
	 */
	public CmdRetrieveCertificate(UserId userId) 
	{
		this.userId = userId;
		this.sqlCommand = 
				"SELECT `CertificateID`,`CertificateFile`"
				+ "FROM `tbl_CertificateDetails`" 
				+ "WHERE `UserID` = ?";
				
		this.isQuery = true;
	}
	
	/**
	 * method  retrieves the certificate details 
	 * and sets the certificate bean if rows are present
	 */
	@Override
	public void run() throws DatabaseException 
	{
		try 
		{
			if (resultSet.next()) 
			{
				cert = new CertificateBean();
				cert.setCertificateId(new CertificateId(resultSet.getInt(1)));
				cert.setUserId(this.userId);
				
				Blob certFile = resultSet.getBlob(2);
				cert.setCertificateFile(certFile.getBytes(1, (int)certFile.length()));
				
			}
			
		} 
		catch (SQLException e1)  
		{
			  throw new DatabaseException(e1.getMessage());
		}
		catch (InvalidInputException e) 
		 {
			throw new DatabaseException(e.getMessage());
		 }
		
		finally
		{
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
	 * retrieving an user.
	 */
	@Override
	protected PreparedStatement prepareCommand(Connection conn) throws DatabaseException 
	{
		try {
			preparedStatement = conn.prepareStatement(this.sqlCommand);
			preparedStatement.setInt(1, this.userId.getId());
			return preparedStatement; 
		} 
		catch (SQLException e) {
			
			throw new DatabaseException(e.getMessage());
		}
		/*
		finally{
		    if (preparedStatement != null)
				try {
					preparedStatement.close();
				} catch (SQLException e1) {
				    throw new DatabaseException(e1.getMessage());
				}
		}
		*/
	}
	
	/**
	 * Method to get the CertificateBean retrieved from the database
	 * @return
	 */
	public CertificateBean getCert() {
		return cert;
	}

     
}
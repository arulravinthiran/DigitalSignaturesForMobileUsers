package com.digsigmobile.persistence.digitalsignature;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.digsigmobile.datatypes.TrustCode;
import com.digsigmobile.datatypes.UserId;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.persistence.Command;

public class CmdUpdateSignatureParameters extends Command
{
	/**
	 * Receives a UserId object to identify the digitalsignature
	 */
	private UserId userid;
	
	/**
	 * Receives a TrustCode object to identify the digitalsignature
	 */
	private TrustCode trCode;
	
	/**
	 * boolean to update hasSigned field
	 */
	private boolean hasSigned;
	
	/**
	 * boolean to update isValid field
	 */
	private boolean isValid;
	
	/**
	 * String to update reasonForSigning field
	 */
	private String signingReason;
	
	/**
	 * Byte array of signature
	 */
	private byte[] signedFile;
	
	/**
	 * Initializes SQL command and sets up userid and trust code
	 * @param user
	 */
	public CmdUpdateSignatureParameters(UserId id, TrustCode trCode,boolean hasSigned, 
			boolean isValid, String signingReason, byte[] signedFile )
	{
		this.userid = id;
		this.trCode = trCode;
		this.hasSigned = hasSigned;
		this.isValid = isValid;
		this.signingReason = signingReason;
		this.signedFile = signedFile;
		
		if(hasSigned)
		{
		this.sqlCommand = "UPDATE `tbl_digitalsignaturedetails` " +
				"SET `HasSigned` = ?,`IsValid` = ?,`ReasonForSigning` = ?," +
				"`SignedTime` = CURRENT_TIMESTAMP(), `SignedFile` = ? WHERE `UserID` = ? AND `TrustCode` = ?";
		}
		else
		{
			this.sqlCommand = "UPDATE `tbl_digitalsignaturedetails` " +
			"SET `HasSigned` = ?,`IsValid` = ?,`ReasonForSigning` = ? " +
			"WHERE `UserID` = ? AND `TrustCode` = ?";
			
		}
		this.isQuery = false;
	}
	
	/**
	 * Since this is an update command, there's no need
	 * to run any computations with the result set, so
	 * this method is a no-operation except closing of opened resources.
	 */
	@Override
	public void run() throws DatabaseException 
	{
		if (preparedStatement != null)
			try {
				preparedStatement.close();
			} 
			catch (SQLException e1) {
			    throw new DatabaseException(e1.getMessage());
			}
	}
	
	/**
	 * Overrides method to prepare a statement specific for
	 * updating hasSigned, isValid and reasonForSigning fields 
	 * of digigtalsignaturedetails table.
	 */
	@Override
	protected PreparedStatement prepareCommand(Connection conn) throws DatabaseException {
		try 
		{
			preparedStatement = conn.prepareStatement(this.sqlCommand);
			if(this.hasSigned)
				preparedStatement.setInt(1, 1);
			else
				preparedStatement.setInt(1, 0);
			
			if(this.isValid)
				preparedStatement.setInt(2, 1);
			else
				preparedStatement.setInt(2, 0);
			
			preparedStatement.setString(3,this.signingReason);
			if (hasSigned) {
				preparedStatement.setBytes(4, signedFile);
				preparedStatement.setInt(5, userid.getId());
				preparedStatement.setInt(6, trCode.getTrustCode());
			} else {
				preparedStatement.setInt(4, userid.getId());
				preparedStatement.setInt(5, trCode.getTrustCode());
			}
			return preparedStatement;
		} 
		catch (SQLException e) {
			
			throw new DatabaseException(e.getMessage());
		}
	}

}

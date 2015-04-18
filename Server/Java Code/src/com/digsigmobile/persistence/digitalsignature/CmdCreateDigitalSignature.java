package com.digsigmobile.persistence.digitalsignature;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.digsigmobile.beans.DigitalSignatureBean;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.persistence.Command;

public class CmdCreateDigitalSignature extends Command
{
	/**
	 * Receives a DigitalSignatureBean object complete with id
	 * included.
	 */
    private DigitalSignatureBean digSig;
    
    /**
	 * Initializes SQL command and sets up DigitalSignatureBean
	 * @param user
	 */
	public CmdCreateDigitalSignature(DigitalSignatureBean digSig) 
	{
		this.digSig = digSig;
		this.sqlCommand = "INSERT INTO TBL_DIGITALSIGNATUREDETAILS VALUES" +
				"(?,?,?,?,?,?, CURRENT_TIMESTAMP())";
		this.isQuery = false;
	}
	
	/**
	 * Since this is an insert command, there's no need
	 * to run any computations with the result set, so
	 * this method is a no-operation except closing of opened resources.
	 */
	@Override
	public void run() throws DatabaseException {
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
	 * creating a new digital signature.
	 */
	@Override
	protected PreparedStatement prepareCommand(Connection conn) throws DatabaseException {
		try {
			preparedStatement = conn.prepareStatement(this.sqlCommand);
			preparedStatement.setInt(1, digSig.getUserId().getId());
			preparedStatement.setInt(2, digSig.getTrustCode().getTrustCode());
			//preparedStatement.setString(3, digSig.getSignedFile().toString());
			preparedStatement.setBytes(3, digSig.getSignedFile());
			boolean hasSigned = digSig.hasSigned();
			if(hasSigned)
			    preparedStatement.setInt(4, 1);
			else
			    preparedStatement.setInt(4, 0);	
			boolean isValid = digSig.isValid();
			if(isValid)
				preparedStatement.setInt(6, 1);
			else
				preparedStatement.setInt(6, 0);
			preparedStatement.setString(5, digSig.getSigningReason());
			return preparedStatement;
		} 
		catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
	}
}

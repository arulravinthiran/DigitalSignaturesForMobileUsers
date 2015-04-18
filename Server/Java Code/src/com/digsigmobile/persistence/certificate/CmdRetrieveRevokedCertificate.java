package com.digsigmobile.persistence.certificate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.digsigmobile.beans.RevokedCertificateBean;
import com.digsigmobile.datatypes.CertificateId;
import com.digsigmobile.datatypes.EmailAddress;
import com.digsigmobile.datatypes.RevocationId;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.exceptions.InvalidInputException;
import com.digsigmobile.persistence.Command;

public class CmdRetrieveRevokedCertificate extends Command
{
	/**
	 * Receives an EmailAddress object to identify
	 * a revoked certificate.
	 */
	private EmailAddress email = null;
	
	/**
	 * Receives a ReceivedCertificateBean object complete with id
	 * included.
	 */
	private RevokedCertificateBean revkdCert;
	
	/**
	 * Initializes SQL command and sets up email
	 * @param email
	 */
	public CmdRetrieveRevokedCertificate(EmailAddress email) {
		this.email = email;
		this.sqlCommand = 
				"SELECT `RevocationID`, `ReasonForRevocation`, `CertificateId`,"
				+ "`RevokedTime`"
				+ "FROM `tbl_UserDetails` u, `tbl_CertificateDetails` c, `tbl_RevokedCertificateDetails` r "
				+ "WHERE u.`PrimaryEmailID` = ? AND u.`UserID` = c.`UserID` AND c.`CertificateID` = r.`CertificateID`";
				
		this.isQuery = true;
	}
	
	/**
	 * method retrieves the revoked certificate details 
	 * and sets the revoked certificate bean if rows are present
	 */
	@Override
	public void run() throws DatabaseException 
	{
		try {
			if (resultSet.next()) {
				revkdCert = new RevokedCertificateBean();
				revkdCert.setRevokdCertId(new RevocationId(resultSet.getInt(1)));
				revkdCert.setRevokingReason(resultSet.getString(2));
				revkdCert.setCertId(new CertificateId(resultSet.getInt(3)));
				revkdCert.setRevokedTime(resultSet.getTimestamp(4));
				
				}
			
		} catch (SQLException e1)  {
			  throw new DatabaseException(e1.getMessage());
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
	 * retrieving a revoked certificate.
	 */
	@Override
	protected PreparedStatement prepareCommand(Connection conn) throws DatabaseException {
		try {
			preparedStatement = conn.prepareStatement(this.sqlCommand);
			preparedStatement.setString(1, email.getText());
			return preparedStatement; 
		} 
		catch (SQLException e) {
			
			throw new DatabaseException(e.getMessage());
		}
	}
	
	/**
	 * Method to get the RevokedCertificateBean retrieved from the database
	 * @return revkdCert
	 */
	public RevokedCertificateBean getRevkdCert() {
		return revkdCert;
	}

}
package com.digsigmobile.persistence.certificate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.digsigmobile.beans.RevokedCertificateBean;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.persistence.Command;

public class CmdCreateRevokedCertificate extends Command
{
	
	/**
	 * Receives a ReceivedCertificateBean object complete with id
	 * included.
	 */
	private RevokedCertificateBean revkdCert;
	
	/**
	 * Initializes SQL command and sets up CertificateBean
	 * @param user
	 */
	public CmdCreateRevokedCertificate(RevokedCertificateBean revkdCert) 
	{
		this.revkdCert = revkdCert;
		this.sqlCommand = "INSERT INTO TBL_REVOKEDCERTIFICATEDETAILS VALUES(?,?,?,?)";
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
	 * creating a new revoked certificate.
	 */
	@Override
	protected PreparedStatement prepareCommand(Connection conn) throws DatabaseException {
		try {
			preparedStatement = conn.prepareStatement(this.sqlCommand);
			preparedStatement.setInt(1, revkdCert.getRevokdCertId().getId());
			preparedStatement.setString(2, revkdCert.getRevokingReason());
			preparedStatement.setInt(3, revkdCert.getCertId().getId());
			preparedStatement.setTimestamp(4, revkdCert.getRevokedTime());
			return preparedStatement;
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

}

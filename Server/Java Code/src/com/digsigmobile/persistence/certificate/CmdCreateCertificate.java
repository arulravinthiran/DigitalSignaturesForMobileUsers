package com.digsigmobile.persistence.certificate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;

import com.digsigmobile.beans.CertificateBean;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.persistence.Command;

public class CmdCreateCertificate extends Command
{
	/**
	 * Receives a CertificateBean object complete with id
	 * included.
	 */
	private CertificateBean cert;
	
	/**
	 * Initializes SQL command and sets up CertificateBean
	 * @param user
	 */
	public CmdCreateCertificate(CertificateBean cert) 
	{
		this.cert = cert;
		this.sqlCommand = "INSERT INTO TBL_CERTIFICATEDETAILS " +
				"VALUES(?,?,?)";
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
	 * creating a new certificate.
	 */
	@Override
	protected PreparedStatement prepareCommand(Connection conn) throws DatabaseException {
		try {
			preparedStatement = conn.prepareStatement(this.sqlCommand);
			preparedStatement.setInt(1, cert.getCertificateId().getId());
			preparedStatement.setInt(3, cert.getUserId().getId());
			preparedStatement.setBlob(2, new SerialBlob(cert.getCertificateFile()));
			return preparedStatement;
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

}
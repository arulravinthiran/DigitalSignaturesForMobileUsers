package com.digsigmobile.persistence.document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;

import com.digsigmobile.beans.DocumentBean;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.persistence.Command;

public class CmdCreateDocument extends Command
{
	/**
	 * Receives a DocumentBean object complete with id
	 * included.
	 */
	private DocumentBean doc;
	
	/**
	 * Initializes SQL command and sets up DocumentBean
	 * @param user
	 */
	public CmdCreateDocument(DocumentBean doc) 
	{
		this.doc = doc;
		this.sqlCommand = "INSERT INTO TBL_DOCUMENTDETAILS VALUES(?,?,CURRENT_TIMESTAMP()," +
				"?,?)";
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
	 * creating a new document.
	 */
	@Override
	protected PreparedStatement prepareCommand(Connection conn) throws DatabaseException {
		try {
			preparedStatement = conn.prepareStatement(this.sqlCommand);
			preparedStatement.setInt(1, doc.getTrustCode().getTrustCode());
			preparedStatement.setBlob(2, new SerialBlob(doc.getDocumentFile()));
			//preparedStatement.setTimestamp(3, doc.getUploadedTime());
			preparedStatement.setString(3, doc.getMimeType().toString());
			preparedStatement.setInt(4, doc.getUserId().getId());
			return preparedStatement;
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

}

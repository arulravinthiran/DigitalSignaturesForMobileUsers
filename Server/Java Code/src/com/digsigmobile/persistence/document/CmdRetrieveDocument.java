package com.digsigmobile.persistence.document;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import com.digsigmobile.beans.DocumentBean;
import com.digsigmobile.datatypes.TrustCode;
import com.digsigmobile.datatypes.UserId;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.exceptions.InvalidInputException;
import com.digsigmobile.persistence.Command;

public class CmdRetrieveDocument extends Command 
{
	/**
	 * Receives a trust code object to identify
	 * a document.
	 */
	private TrustCode trCode = null;
	
	/**
	 * DocumentBean object to return the result
	 */
	private DocumentBean document = null;
	
	/**
	 * Initializes SQL command and sets up trust code
	 * @param trCode
	 */
	public CmdRetrieveDocument(TrustCode trCode) 
	{
		this.trCode = trCode;
		this.sqlCommand = 
				"SELECT `OrigFile`,`UploadedTime`,`MIMEType`,`InitiatorUserId`"
				+ "FROM `tbl_DocumentDetails`" 
				+ "WHERE `TrustCode` = ?";
				
		this.isQuery = true;
	}

	/**
	 * Overrides method to prepare a statement specific for
	 * retrieving a document.
	 */
	@Override
	protected PreparedStatement prepareCommand(Connection conn)
			throws DatabaseException 
	{
		try 
		{
			preparedStatement = conn.prepareStatement(this.sqlCommand);
			preparedStatement.setInt(1, trCode.getTrustCode());
			return preparedStatement; 
		} 
		catch (SQLException e) {
			
			throw new DatabaseException(e.getMessage());
		}
		/*finally{
		    if (preparedStatement != null)
				try {
					preparedStatement.close();
				} catch (SQLException e1) {
				    throw new DatabaseException(e1.getMessage());
				}
				
		}*/
	}
	
	
	/**
	 * method  retrieves the document details 
	 * and sets the document bean if rows are present
	 */
	@Override
	public void run() throws DatabaseException, InvalidInputException, MimeTypeParseException 
	{
		try 
		{
			if (resultSet.next()) 
			{
				document = new DocumentBean();
				document.setTrustCode(trCode);
				Blob docFile = resultSet.getBlob(1);
				document.setDocumentFile(docFile.getBytes(1, (int)docFile.length()));
				
				document.setUploadedTime(resultSet.getTimestamp(2));
				//think ways to make these Mimetypes compatible to string in the db
				document.setMimeType(new MimeType(resultSet.getString(3)));
				document.setUserId(new UserId(resultSet.getInt(4)));
				
			}
			
		} 
		catch (SQLException e1)  
		{
			  throw new DatabaseException(e1.getMessage());
		}
		catch(InvalidInputException in)
		{
			throw new InvalidInputException(in.getMessage());
		}
		catch(MimeTypeParseException mi)
		{
			throw new InvalidInputException(mi.getMessage());
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
	 * Method to get the document corresponding to the  trust code from the database
	 * @return
	 */
	public DocumentBean getDocument() 
	{
		return document;
	}

}
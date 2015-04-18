package com.digsigmobile.datatypes;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import com.digsigmobile.exceptions.InvalidInputException;


public class SignatureFile extends SerialBlob implements Serializable
{
	/**
	 * serialVersionUID of Serializable interface
	 */
	/*private static final long serialVersionUID = 1L;*/


	private static final long serialVersionUID = 1237357252590625639L;

	private String sha256Hash;
	
	public SignatureFile(Blob blob, String hash) 
	throws InvalidInputException, SerialException, SQLException
	 {
		super(blob);
		setHash(hash);
	}
	
	public SignatureFile(byte[] bytes, String hash) 
	throws SerialException, SQLException, InvalidInputException {
		super(bytes);
		setHash(hash);
	}

	public String getHash() {
		return sha256Hash;
	}

	private void setHash(String hash) throws InvalidInputException {
		this.sha256Hash = hash;
		if (!isValid()) throw new InvalidInputException("Not a valid hash");
	}
	
   /**
    * As the signedFile can be empty in digitalSignaturedetails table, 
    * this can be null in which case it is a valid file.
    * If not null, then it should be 64 bytes long.
    * @return
    */
	private boolean isValid() {
		if (this.sha256Hash != null || !this.sha256Hash.isEmpty())
		{
			if(this.sha256Hash.length() == 64)
				return true;
			else
				return false;
		}	
		return true;
	}
	
}

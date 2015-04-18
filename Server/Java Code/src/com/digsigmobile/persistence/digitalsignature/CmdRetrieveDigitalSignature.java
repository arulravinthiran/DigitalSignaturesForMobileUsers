package com.digsigmobile.persistence.digitalsignature;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.digsigmobile.beans.DigitalSignatureBean;
import com.digsigmobile.beans.UserBean;
import com.digsigmobile.datatypes.Row;
import com.digsigmobile.datatypes.TrustCode;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.persistence.Command;

public class CmdRetrieveDigitalSignature extends Command
{
	/**
	 * Receives a trust code object to identify
	 * a digital signature.
	 */
	private TrustCode trCode = null;
	
	/**
	 * UserBean object to return the result
	 */
	private UserBean userBnObj = null;
	
	/**
	 * DigitalSignatureBean object to return the result
	 */
	private DigitalSignatureBean digSigBnObj = null;
	
	/**
	 * Map to return the result
	 */
	private Map<Integer, Row> rowMap =  null;
	
	/**
	 * Row Object to return the result
	 */
	private Row rowObj = null;
	
	/**
	 * Initializes SQL command and sets up trCode
	 * @param trCode
	 */
	
	public CmdRetrieveDigitalSignature(TrustCode trCode) 
	{
		this.trCode = trCode;
		this.sqlCommand = 
				"SELECT u.Name, u.FamilyName,"
				+ "s.HasSigned, s.SignedTime, s.IsValid, s.ReasonForSigning "
				+ "FROM tbl_UserDetails u, tbl_DigitalSignatureDetails s "
				+ "WHERE s.TrustCode = ? AND s.UserID =  u.UserID";
		this.isQuery = true;
	}
	
	/**
	 * method  retrieves the digital signature details and sets the
	 * user bean, certificate bean and digital signature bean if rows are present
	 */
	@Override
	public void run() throws DatabaseException 
	{
		try 
		{
			rowMap = new HashMap<Integer, Row>();
			int rowCount = 0;
			
			//get the row count and push the cursor before the first row of result set
			// note that this will execute only if the result set is non-empty
			if(resultSet.last())
			{
				rowCount = resultSet.getRow();
				resultSet.beforeFirst();
			}
			 
			 /**
			  * Map<Integer, Row> is set in this for loop
			  */
			for(int rowNumber = 0; rowNumber < rowCount; rowNumber++)
			{
				if(resultSet.next())
				{
					userBnObj = new UserBean();
					
					userBnObj.setName(resultSet.getString(1));
					userBnObj.setFamilyName(resultSet.getString(2));
					
					digSigBnObj = new DigitalSignatureBean();
					
					if(resultSet.getInt(3)==1)
					{
						digSigBnObj.setHasSigned(true);
					    digSigBnObj.setSignedTime(resultSet.getTimestamp(4));
					    digSigBnObj.setSigningReason(resultSet.getString(6));
					    
					    if(resultSet.getInt(5)==1)
							digSigBnObj.setIsValid(true);
						else
							digSigBnObj.setIsValid(false);
					}
					else
						digSigBnObj.setHasSigned(false);
					
                    rowObj = new Row(userBnObj, digSigBnObj);
                    
                    rowMap.put(rowNumber, rowObj);
				}
			}
		} 
		catch (SQLException e1)  
		{
			  throw new DatabaseException(e1.getMessage());
		}
			
		finally
		{
			if (preparedStatement != null) 
			{
				try {
					preparedStatement.close();
				} catch (SQLException e1) {
					throw new DatabaseException(e1.getMessage());	
				}
			}
			if (resultSet != null)
			{
				try 
				{
					resultSet.close();
				}
				catch (SQLException e1) 
				{
					throw new DatabaseException(e1.getMessage());	
				}
		     }
		}
	}

	/**
	 * Overrides method to prepare a statement specific for
	 * retrieving a digital signature.
	 */
	@Override
	protected PreparedStatement prepareCommand(Connection conn) throws DatabaseException 
	{
		try 
		{
			preparedStatement = conn.prepareStatement(this.sqlCommand);
			preparedStatement.setInt(1, trCode.getTrustCode());
			return preparedStatement; 
		} 
		catch (SQLException e) 
		{
			throw new DatabaseException(e.getMessage());
		}
	}
	
	/**
	 * method returns the HashMap set in the run method
	 * @return rowMap Map<Integer, Row>
	 */
	public Map<Integer,Row> getMap()
	{
		return rowMap;
	}
}

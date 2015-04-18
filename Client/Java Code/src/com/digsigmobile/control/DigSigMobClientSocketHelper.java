package com.digsigmobile.control;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Set;

import com.digsigmobile.beans.DigitalSignatureBean;
import com.digsigmobile.beans.DocumentBean;
import com.digsigmobile.beans.UserBean;
import com.digsigmobile.datatypes.Address;
import com.digsigmobile.datatypes.EmailAddress;
import com.digsigmobile.datatypes.PhoneNumber;
import com.digsigmobile.datatypes.TrustCode;
import com.digsigmobile.datatypes.UserId;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.exceptions.InvalidInputException;

/**
 * helper class of client socket
 * @author Arul
 */

public class DigSigMobClientSocketHelper extends DigSigMobClientSocket
 implements SocketStringConstants
{
	private TrustCode trCodeObj = null;
	
	/**
	 * method writes UserBean object to the server socket to register a user. 
	 * Note that this method writes ACTION_USER_REGISTRATION string constant
	 * for identification purposes of the server. 
	 * @param userBnObj UserBean to be written to the socket
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void sendUser(UserBean userBnObj) throws IOException
	{
		try 
		{
			boolean writing = true;
			socketObj = connect(writing);
			objOpStrObj = this.getObjectOutputStream(socketObj);
			
			this.writeString(objOpStrObj, ACTION_USER_REGISTRATION);
			this.writeUser(objOpStrObj, userBnObj);
			
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			close();
		}
	}

	/**
	 * method writes CertificateBean object to the server socket to save it. 
	 * Note that this method writes ACTION_ string constant
	 * for identification purposes of the server. 
	 * @param certBnObj CertificateBean to be written to the socket
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void sendPublicKey(PublicKey userPubKey,EmailAddress email ) 
	 throws IOException
	{
		try 
		{
			boolean writing = true;
			socketObj = connect(writing);
			objOpStrObj = this.getObjectOutputStream(socketObj);
			
			this.writeString(objOpStrObj, ACTION_CERTIFICATE_CREATION);
			this.writeObject(objOpStrObj, (PublicKey)userPubKey);
			this.writeEmailAddress(objOpStrObj, email);
			
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			close();
		}
	}
	
	/**
	 * method receives the integer sent by the server socket
	 * @param action String constant for authentication purposes
	 * @return valueFromServer integer written to the socket by the server
	 * @throws IOException
	 */
	public int receiveInt(String action) throws IOException 
	{
		try 
		{
			boolean writing = false;
			socketObj = connect(writing);
			objIpStrObj = this.getObjectInputStream(socketObj);
			
			if(action.equals(objIpStrObj.readUTF()))
			{
				int valueFromServer = objIpStrObj.readInt();
				return valueFromServer;
			}
		}
		catch(IOException e) 
		{
			e.printStackTrace();
		} 
		finally
		{
			close();
		}
		return -1000;
	}
	
	/**
	 * method receives the integer sent by the server socket
	 * @param action String constant for authentication purposes
	 * @return valueFromServer integer written to the socket by the server
	 * @throws IOException
	 */
	/*public CertificateBean receiveCertificate(String action) throws IOException 
	{
		try 
		{
			boolean writing = false;
			socketObj = connect(writing);
			objIpStrObj = this.getObjectInputStream(socketObj);
			
			if(action.equals(objIpStrObj.readUTF()))
			{
				certBnObj = (CertificateBean)objIpStrObj.readObject();
				return certBnObj;
			}
		}
		catch(ClassNotFoundException cnfEx)
		{
			cnfEx.printStackTrace();
		}
		catch(IOException e) 
		{
			e.printStackTrace();
		} 
		finally
		{
			close();
		}
		return null;
	}*/
	
	/**
	 * method writes EmailAddress objects to the server socket to validate 
	 * signers. 
	 * Note that this method writes ACTION_SIGNERS_VALIDATION string constant
	 * for identification purposes of the server. 
	 * @param emailObj EmailAddress to be written to the server socket
	 * @param coSigemailObj EmailAddress to be written to the server socket
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void sendSigners(EmailAddress emailObj, EmailAddress coSigEmailObj)
	throws IOException, ClassNotFoundException
	{
		try
		{
			boolean writing = false;
			socketObj = connect(writing);
			objOpStrObj = getObjectOutputStream(socketObj);
			
			writeString(objOpStrObj, ACTION_SIGNERS_VALIDATION);
			writeEmailAddress(objOpStrObj, emailObj);
			writeEmailAddress(objOpStrObj, coSigEmailObj);
		}
		catch(IOException ioEx)
		{
			ioEx.printStackTrace();
		}
		catch(ClassNotFoundException cnfEx)
		{
			cnfEx.printStackTrace();
		}
		finally
		{
			close();
		}
	}
	
	/**
	 * method writes EmailAddress, Trustcode and Byte[] objects to the server  
	 * socket to validate co-signer.
	 * Note that this method writes ACTION_COSIGNER_VALIDATION string constant
	 * for identification purposes of the server.
	 * @param emailObj EmailAddress to be written to the server socket
	 * @param signDocByte[] to be written to the server socket
	 * @param trCodeObj TrustCode to be written to the server socket
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void sendCoSignersEmail(TrustCode trCodeObj, EmailAddress emailObj, 
			DocumentBean signDoc) throws IOException, ClassNotFoundException
	{
		try
		{
			boolean writing = true;
			socketObj = connect(writing);
			objOpStrObj = getObjectOutputStream(socketObj);
			
			writeString(objOpStrObj, ACTION_COSIGNER_VALIDATION);
			writeTrustCode(objOpStrObj, trCodeObj);
			writeEmailAddress(objOpStrObj, emailObj);
			writeDocument(objOpStrObj, signDoc);	
		}
		catch(IOException ioEx)
		{
			ioEx.printStackTrace();
		}
		catch(ClassNotFoundException cnfEx)
		{
			cnfEx.printStackTrace();
		}
		finally
		{
			close();
		}
	}
	
	/**
	 * method writes a boolean, DigitalSignatureBean, DocumentBean and 
	 * CertificateBean objects to the server socket to create a digital signature.
	 * Note that this method writes ACTION_SIGNATURE_CREATION string constant
	 * for identification purposes of the server.
	 * @param digSigBnObj DigitalSignatureBean object
	 * @param docBnObj DocumentBean object
	 * @param certBnObj CertificateBean object
	 * @param isInitiator boolean
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void sendSignatureDetails(EmailAddress email, EmailAddress reqdCoSigner,   
			DigitalSignatureBean digSigBnObj, DocumentBean docBnObj,boolean isInitiator) 
	throws IOException, DatabaseException, ClassNotFoundException
	{
		try
		{
			boolean writing = true;
			socketObj = connect(writing);
			objOpStrObj = getObjectOutputStream(socketObj);
			
			writeString(objOpStrObj, ACTION_SIGNATURE_CREATION);
			writeEmailAddress(objOpStrObj, email);
			writeEmailAddress(objOpStrObj, reqdCoSigner);
			writeDigitalSignature(objOpStrObj, digSigBnObj);
			writeDocument(objOpStrObj, docBnObj);
			writeBoolean(objOpStrObj, isInitiator);
		}
		catch(IOException ioEx)
		{
			ioEx.printStackTrace();
		}
		catch(ClassNotFoundException cnfEx)
		{
			cnfEx.printStackTrace();
		}
		finally
		{
			close();
		}
	}
	
	/**
	 * method writes two strings and  to the server socket an EmailAddress to 
	 * validate keys  and returns the integer written by the server socket. 
	 * Note that this method writes ACTION_KEYS_VALIDATION string constant
	 * for identification purposes of the server. 
	 * Server socket outputs an integer after validating the keys. 
	 * @param keyP1 String p1
	 * @param keyP2 String p2
	 * @param emailObj EmailAddress of the signer
	 * @return status integer written by the server socket
	 * @throws IOException
	 * @throws DatabaseException
	 * @throws ClassNotFoundException
	 */
	public void sendKeys(String keyP1, String keyP2, EmailAddress emailObj) 
	throws IOException, DatabaseException, ClassNotFoundException
	{
		try
		{
			boolean writing = true;
			socketObj = connect(writing);
			objOpStrObj = getObjectOutputStream(socketObj);
			
			writeString(objOpStrObj, ACTION_KEYS_VALIDATION);
			writeString(objOpStrObj, keyP1);
			writeString(objOpStrObj, keyP2);
			writeEmailAddress(objOpStrObj, emailObj);
		}
		catch(IOException ioEx)
		{
			ioEx.printStackTrace();
		}
		catch(ClassNotFoundException cnfEx)
		{
			cnfEx.printStackTrace();
		}
		finally
		{
			close();
		}
	}

	/**
	 * method writes the trust code to the socket.
     * Note that this method writes ACTION_TRUSTCODE_VERIFICATION string 
	 * constant for identification purposes of the server. 
	 * @param trCodeObj TrustCode entered by the user.
	 * @throws IOException
	 * @throws DatabaseException
	 * @throws ClassNotFoundException
	 */
	public void sendTrustCode(TrustCode trCodeObj) 
	throws IOException, ClassNotFoundException
	{
		try
		{
			boolean writing = true;
			socketObj = connect(writing);
			objOpStrObj = getObjectOutputStream(socketObj);
			
			writeString(objOpStrObj, ACTION_TRUSTCODE_VERIFICATION);
			writeTrustCode(objOpStrObj, trCodeObj);
		}
		catch(IOException ioEx)
		{
			ioEx.printStackTrace();
		}
		catch(ClassNotFoundException cnfEx)
		{
			cnfEx.printStackTrace();
		}
		finally
		{
			close();
		}
	}
	
	/**
	 * method receives the Set<Object> hash set sent by the server socket
	 * @param action String constant for authentication purposes
	 * @return set Set<Object> written to the socket by the server
	 * @throws IOException
	 */
	public Set<Object> receiveSet(String action) throws IOException 
	{
		try 
		{
			boolean writing = false;
			socketObj = connect(writing);
			
			objIpStrObj = this.getObjectInputStream(socketObj);
			if(action.equals(objIpStrObj.readUTF()))
			{
				Set<Object> set = (Set<Object>)objIpStrObj.readObject();
				return set;
			}
		}
		catch(ClassNotFoundException cnfEx)
		{
			cnfEx.printStackTrace();
		}
		catch(IOException e) 
		{
			e.printStackTrace();
		} 
		finally
		{
			close();
		}
		return null;
	}
	
	//this is for verification. Remove this in real release.
	public static void main(String [] args)
	{
		try 
		{
			DigSigMobClientSocketHelper helperObj = new 
			  DigSigMobClientSocketHelper();
			UserBean userBnObj = new UserBean();
			
			userBnObj.setUserId(new UserId(50));
			userBnObj.setName("Adams");
			userBnObj.setFamilyName("Carlisle");
			userBnObj.setHasCertificate(false);
			userBnObj.setPrimaryEmail(new EmailAddress("cadams@uottawa.ca"));
			userBnObj.setPrimaryNumber(new PhoneNumber("6136141508"));
			userBnObj.setAddress(new Address("canada","ON","Ottawa", "K2E6J5", 
					"Meadowlands DrEast"));
			
			helperObj.sendUser(userBnObj);
			int registryStatus = helperObj.receiveInt(ACTION_USER_REGISTRATION);
			System.out.println("Registered status: "+registryStatus);
		} 
		catch (InvalidInputException e) 
		{
			e.printStackTrace();
		}
		catch(IOException ioEx)
		{
			ioEx.printStackTrace();
		}
		
	}
	
	}
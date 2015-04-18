package com.digsigmobile.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Set;

import com.digsigmobile.beans.DigitalSignatureBean;
import com.digsigmobile.beans.DocumentBean;
import com.digsigmobile.beans.UserBean;
import com.digsigmobile.datatypes.EmailAddress;
import com.digsigmobile.datatypes.TrustCode;

public class DigSigMobClientSocket implements ServerSocketConnectionParameters
{
	
	protected Socket socketObj = null;
	protected InputStream ipStrObj = null;
	protected ObjectInputStream objIpStrObj = null;
	protected OutputStream opStrObj = null;
	protected ObjectOutputStream objOpStrObj = null;
	
	/**
	 * method connects to the server socket and returns a socket object
	 * @return socketObj socket opened by the server
	 * @param writing boolean determines the port # to connect
	 * @throws IOException
	 * @throws RuntimeException
	 */
	protected Socket connect(boolean writing) throws IOException 
	{
		try
		{
			if(writing)
				socketObj = new Socket(HOST_NAME, WRITING_PORT);
			else
				socketObj = new Socket(HOST_NAME, READING_PORT);
			
			return socketObj;
		}
		catch(IOException ioex)
		{
			ioex.printStackTrace();
			return null;
		}
		catch(RuntimeException ex)
		{
			ex.printStackTrace();
			return null;
		} 
	}

	/**
	 * method returns an object of ObjectInputstream
	 * @param sktObj socket object
	 * @return objIpStrObj ObjectInputStream object
	 * @throws Exception
	 */
	protected ObjectInputStream getObjectInputStream(Socket sktObj) 
	{
		try
		{
			ipStrObj = socketObj.getInputStream();
			objIpStrObj = new ObjectInputStream(ipStrObj);
			
			return objIpStrObj;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * method reads an integer in the socket
	 * @param sktObj socket object
	 * @return integer in the socket
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws RuntimeException
	 */
	protected int readInt(ObjectInputStream objIpStrObj) throws ClassNotFoundException, 
	IOException 
	{
		try
		{
			if(objIpStrObj != null)
				return objIpStrObj.readInt();
			else
				return -1;
			
		}
		catch(IOException ioEx)
		{
			ioEx.printStackTrace();
			return -2;
		}
		catch(RuntimeException ex)
		{
			ex.printStackTrace();
			return -3;
		} 
	}
	
	
	
	/**
	 * method reads the object in the socket
	 * @param sktObj socket object
	 * @return object in the socket
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws RuntimeException
	 */
	protected Object readObject(ObjectInputStream objIpStrObj) throws 
	ClassNotFoundException, IOException
	{
		try
		{
			
			if(objIpStrObj != null)
				return objIpStrObj.readObject();
			else
				return null;
		}
		catch(ClassNotFoundException cnfEx)
		{
			cnfEx.printStackTrace();
			return null;
		}
		catch(IOException ioEx)
		{
			ioEx.printStackTrace();
			return null;
		}
		catch(RuntimeException ex)
		{
			ex.printStackTrace();
			return null;
		} 
	}
	
	/**
	 * method reads the TrustCode in the socket
	 * @param socketObj socket object
	 * @return trCode TrustCode in the socket
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws RuntimeException
	 */
	protected TrustCode readTrustCode(ObjectInputStream objIpStrObj) 
	{
		try
		{
			TrustCode trCode = (TrustCode)this.readObject(objIpStrObj);
			
			if(trCode != null)
				return trCode;
			else
				return null;
			
		}
		catch(RuntimeException ex)
		{
			ex.printStackTrace();
			return null;
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
			return null;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * method reads the CertificateBean in the socket
	 * @param sktObj socket object
	 * @return certBnObj CertificateBean in the socket
	 * @throws RuntimeException
	 * @throws Exception
	 */
	/*protected CertificateBean readCertificate(ObjectInputStream objIpStrObj) 
	{
		try
		{
			CertificateBean certBnObj = (CertificateBean) this.readObject(objIpStrObj);
			
			if(certBnObj != null)
				return certBnObj;
			else
				return null;
		}
		catch(RuntimeException ex)
		{
			ex.printStackTrace();
			return null;
		} 
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}*/
	
	/**
	 * method reads the Set<Object> in the socket
	 * @param sktObj socket object
	 * @return setObj CertificateBean in the socket
	 * @throws RuntimeException
	 * @throws Exception
	 */
	protected Set<Object> readsetOfObjects(ObjectInputStream objIpStrObj) 
	{
		try
		{
			Set<Object> setObj = (Set<Object>) this.readObject(objIpStrObj);
			
			if(setObj != null)
				return setObj;
			else
				return null;
		}
		catch(RuntimeException ex)
		{
			ex.printStackTrace();
			return null;
		} 
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * method returns an object of ObjectOuputstream
	 * @param sktObj socket object
	 * @return objOpStrObj ObjectOuputStream object
	 * @throws Exception
	 */
	protected ObjectOutputStream getObjectOutputStream(Socket sktObj) 
	{
		try
		{
			opStrObj = sktObj.getOutputStream();
			objOpStrObj = new ObjectOutputStream(opStrObj);
			
			return objOpStrObj;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	
	
	
	/**
	 * method writes a boolean to the socket
	 * @param sktObj socket object
	 * @param boolean value to be written to the socket
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	protected void writeBoolean(ObjectOutputStream objOpStrObj, boolean bool) 
	throws ClassNotFoundException, IOException 
	{
		try
		{
			if(objOpStrObj != null)
				objOpStrObj.writeBoolean(bool);
			
		}
		catch(IOException ioEx)
		{
			ioEx.printStackTrace();
		}
		catch(RuntimeException ex)
		{
			ex.printStackTrace();
		} 
	}
	
	/**
	 * method writes a string to the socket
	 * @param sktObj socket object
	 * @param string value to be written to the socket
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	protected void writeString(ObjectOutputStream objOpStrObj, String string) 
	throws ClassNotFoundException, IOException 
	{
		try
		{
			//objOpStrObj = this.getObjectOutputStream(sktObj);
			
			if(objOpStrObj != null)
			{
				objOpStrObj.writeUTF(string);
				//objOpStrObj.reset();
			}
			
		}
		catch(IOException ioEx)
		{
			ioEx.printStackTrace();
		}
		catch(RuntimeException ex)
		{
			ex.printStackTrace();
		} 
	}
	
	/**
	 * method reads the object in the socket
	 * @param sktObj socket object
	 * @return object in the socket
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	protected void writeObject(ObjectOutputStream objOpStrObj, Object object) 
	throws IOException
	{
		try
		{
			if(objOpStrObj != null)
				objOpStrObj.writeObject(object);
		}
		catch(IOException ioEx)
		{
			ioEx.printStackTrace();
		}
		catch(RuntimeException ex)
		{
			ex.printStackTrace();
		} 
	}
	
	/**
	 * method writes EmailAddress to the socket
	 * @param sktObj socket object
	 * @param email EmailAddress to be written to the socket
	 * @throws Exception
	 */
	protected void writeEmailAddress(ObjectOutputStream objOpStrObj, EmailAddress email) 
	{
		try
		{
			if(objOpStrObj != null)
				objOpStrObj.writeObject(email);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * method writes Bytes[] to the socket
	 * @param sktObj socket object
	 * @param bytes Byte[] to be written to the socket
	 * @throws Exception
	 */
	protected void writeBytes(ObjectOutputStream objOpStrObj, byte[] bytes) 
	{
		try
		{
			if(objOpStrObj != null)
				objOpStrObj.writeObject(bytes);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}
	/**
	 * method writes TrustCode to the socket
	 * @param sktObj socket object
	 * @param trCodeObj value to be written to the socket
	 * @throws Exception
	 */
	protected void writeTrustCode(ObjectOutputStream objOpStrObj, TrustCode trCodeObj) 
	{
		try
		{
			if(objOpStrObj != null)
				objOpStrObj.writeObject(trCodeObj);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}
	
	/**
	 * method writes CertificateBean to the socket
	 * @param sktObj socket object
	 * @param certBnObj object to be written to the socket
	 * @throws Exception
	 *//*
	protected void writeCertificate(ObjectOutputStream objOpStrObj, 
			CertificateBean certBnObj) 
	{
		try
		{
			if(objOpStrObj != null)
				objOpStrObj.writeObject(certBnObj);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}*/
	
	/**
	 * method writes UserBean to the socket
	 * @param sktObj socket object
	 * @param userBnObj object to be written to the socket
	 * @throws Exception
	 */
	protected void writeUser(ObjectOutputStream objOpStrObj, UserBean userBnObj) 
	{
		try
		{
			//objOpStrObj = this.getObjectOutputStream(sktObj);
			
			if(objOpStrObj != null)
			{
				objOpStrObj.writeObject(userBnObj);
				//objOpStrObj.reset();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}
	
	/**
	 * method writes DocumentBean to the socket
	 * @param sktObj socket object
	 * @param docBnObj object to be written to the socket
	 * @throws Exception
	 */
	protected void writeDocument(ObjectOutputStream objOpStrObj, DocumentBean docBnObj) 
	{
		try
		{
			if(objOpStrObj != null)
				objOpStrObj.writeObject(docBnObj);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * method writes DocumentBean to the socket
	 * @param sktObj socket object
	 * @param digSigBnObj object to be written to the socket
	 * @throws Exception
	 */
	protected void writeDigitalSignature(ObjectOutputStream objOpStrObj, 
			DigitalSignatureBean digSigBnObj) 
	{
		try
		{
			if(objOpStrObj != null)
				objOpStrObj.writeObject(digSigBnObj);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * closes all streams and sockets
	 * @throws IOException
	 * @throws RuntimeException
	 */
	protected void close() throws IOException 
	{
		try
		{
			if(objOpStrObj != null)
				objOpStrObj.close();
			if(opStrObj != null)
				opStrObj.close();
			if(objIpStrObj != null)
				objIpStrObj.close();
			if(ipStrObj != null)
				ipStrObj.close();
			if(socketObj != null)
				socketObj.close();
		}
		catch(IOException ioEx)
		{
			ioEx.printStackTrace();
		}
		catch(RuntimeException ex)
		{
			ex.printStackTrace();
		} 
	}
}

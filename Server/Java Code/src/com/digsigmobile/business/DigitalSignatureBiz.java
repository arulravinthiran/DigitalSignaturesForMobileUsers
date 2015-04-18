package com.digsigmobile.business;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import com.digsigmobile.beans.CertificateBean;
import com.digsigmobile.beans.DigitalSignatureBean;
import com.digsigmobile.beans.DocumentBean;
import com.digsigmobile.beans.UserBean;
import com.digsigmobile.datatypes.EmailAddress;
import com.digsigmobile.datatypes.Row;
import com.digsigmobile.datatypes.TrustCode;
import com.digsigmobile.datatypes.UserId;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.persistence.Persistence;
import com.digsigmobile.persistence.digitalsignature.CmdCreateDigitalSignature;
import com.digsigmobile.persistence.digitalsignature.CmdRetrieveDigitalSignature;
import com.digsigmobile.persistence.digitalsignature.CmdUpdateSignatureParameters;
import com.digsigmobile.persistence.digitalsignature.CmdValidateCoSigner;
import com.digsigmobile.util.DigSigMobServerUtils;
import com.digsigmobile.util.Messages;

public class DigitalSignatureBiz implements Messages
{

	/**
	 * Persistence object
	 */
	private Persistence persistence = null;

	public DigitalSignatureBiz() throws DatabaseException 
	{
		this.persistence = Persistence.getInstance();
		if (!persistence.isConnected()) persistence.connect();
	}
	
	public Map<Integer,Row>retrieveSignature(TrustCode trustCode) 
	throws DatabaseException 
	{
		CmdRetrieveDigitalSignature retDigSig = new CmdRetrieveDigitalSignature(trustCode);
		
		persistence.run(retDigSig);
		persistence.disconnect();
		
		Map<Integer,Row> objectsOfBeanObjects = new HashMap<Integer,Row>();
		 
		objectsOfBeanObjects = retDigSig.getMap();
		return objectsOfBeanObjects;
	}

	/**
	 * method creates signature file for both initiator and co-signer.
	 * if isInitiator is true, then trust code is generated only if the initiator's 
	 * signature is valid.
	 * if isInitiator is false, then the existing co-signer's signature parameters are  
	 * updated according to the validity of the signature.
	 * @param email EmailAddress of initiator
	 * @param reqdCoSigner EmailAddress of co-signer
	 * @param signature DigitalSignatureBean
	 * @param document DocumentBean
	 * @param isInitiator boolean true if initiator
	 * @return status int 0 if valid signature and -5 for invalid signatures
	 * @throws DatabaseException
	 * /
	public int createSignature(EmailAddress email, EmailAddress reqdCoSigner, DigitalSignatureBean 
			signature, DocumentBean document, boolean isInitiator) 
	throws DatabaseException, CertificateException, IOException
	{
		DigSigMobServerUtils utilsObj = new DigSigMobServerUtils();
		UserBiz userBizObj = new UserBiz();
		
		//if initiator, generate trust code only for valid signatures
		if(isInitiator)
		{
			UserBean userBn = userBizObj.retrieveUser(email);
			UserId userId = userBn.getUserId();
			
			boolean isValid = this.validateSignature(userId, signature, document);
			 if(isValid)
			 {
				 DocumentBiz docBizObj = new DocumentBiz();
				 TrustCode trCode = docBizObj.createDocument(document); 
				 signature.setTrustCode(trCode);
				 signature.setHasSigned(true);
				 signature.setIsValid(true);
					
				 this.insertSigner(signature);
					
				 utilsObj.sendMail(trCode, email, reqdCoSigner, true, true);
				 return 0;		
			 }
			 else
			 {
				 utilsObj.sendMail(null, email, reqdCoSigner, true, false);
				 return -5;
			 }
		}
		
		//if co-signer, check the validity of the signature and update accordingly
		else
		{
			UserBean userBn = userBizObj.retrieveUser(reqdCoSigner);
			UserId userId = userBn.getUserId();
			
			TrustCode trCode = signature.getTrustCode();
			String signingReason = signature.getSigningReason();
			
			boolean isValid = this.validateSignature(userId, signature, document);
			
			if(isValid)
			{
				this.updateSignatureParameters(userId, trCode, true, true, signingReason);
				utilsObj.sendMail(trCode, null, reqdCoSigner, false, true);
				return 0;
			}
			else
			{
				this.updateSignatureParameters(userId, trCode, true, false, signingReason);
				utilsObj.sendMail(trCode, null, reqdCoSigner, false, false);
				return -5;
			}
		}
	}
	*/
	/**
	 * method to insert signature in the DB
	 * @param signature
	 * @throws DatabaseException 
	 */
	public void insertSigner(DigitalSignatureBean signature) throws DatabaseException 
	{
		CmdCreateDigitalSignature digSig = new CmdCreateDigitalSignature(signature);
		persistence.run(digSig);
		persistence.disconnect();
	}

	/**
	 * method says if the given trust code and user id matches for a co-signer
	 * @param trCode
	 * @param userId
	 * @return
	 * @throws DatabaseException
	 */
	public boolean validateCoSigner(TrustCode trCode, UserId userId) 
	   throws DatabaseException
	{
		CmdValidateCoSigner iSCoSigner = new CmdValidateCoSigner(trCode, userId);
		persistence.run(iSCoSigner);
		persistence.disconnect();
		return (iSCoSigner.validateCoSigner());
		
	}
	/**
	 * method updates hasSigned, isValid and reasonForSigning fields of a co-signer
	 * @param userid User ID
	 * @param trCode Trust Code of the document
	 * @param hasSigned boolean true if the user has signed the document
	 * @param isValid boolean true if the document is signed and the signature is valid 
	 * @param signingReason string signing reason
	 * @throws DatabaseException
	 */
	public void updateSignatureParameters(UserId userid, TrustCode trCode, 
			boolean hasSigned, boolean isValid, String signingReason, byte[] signedFile) 
	throws DatabaseException
			 
	{
		CmdUpdateSignatureParameters updateSign = new CmdUpdateSignatureParameters(userid,  
			trCode, hasSigned, isValid, signingReason, signedFile);
		persistence.run(updateSign);
		persistence.disconnect();

	}
	
	/**
	 * method validates the signature of the given digital signature belonging to a user id
	 * @param userId User ID
	 * @param digSigBn DigitalSigntureBean
	 * @param docBn DocumentBean
	 * @return isValid boolean true if signature is valid
	 * @throws CertificateException
	 * @throws IOException
	 * @throws DatabaseException
	 */
	public boolean validateSignature(UserId userId,DigitalSignatureBean digSigBn, DocumentBean docBn)
	throws CertificateException, IOException, DatabaseException
	{
		CertificateBiz certBizObj = new CertificateBiz();
		CertificateBean certBn = certBizObj.retrieveCertificate(userId);
		byte[] certificate = certBn.getCertificateFile();
		
		byte[] signatureDoc = digSigBn.getSignedFile();
		byte[] data = docBn.getDocumentFile();
		
		//convert certificate, which is in byte[], to X509Certificate
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		InputStream ipStrmObj = new ByteArrayInputStream(certificate);
		X509Certificate x509Certificate = (X509Certificate) certFactory.generateCertificate
		 (ipStrmObj);
		if(ipStrmObj != null)
			ipStrmObj.close();
		
		SignatureManager signManObj = new SignatureManager();
		boolean isValid = signManObj.verifySignature(x509Certificate, signatureDoc, data);
		
		return isValid;
		
	}
}

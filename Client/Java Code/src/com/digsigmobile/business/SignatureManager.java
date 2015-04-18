package com.digsigmobile.business;

import java.security.PrivateKey;
import java.security.Provider;
//import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Class creates a digital signature of the user and verifies it using the public key.
 * Note that signature creation involves the private key and this is possible in client only.
 * @author Bernado
 */
public class SignatureManager 
{

	Provider BC = null;
	
	/**
	 * Constructor. Sets up the bouncy castle provider
	 */
	public SignatureManager() {
		BC = new BouncyCastleProvider();
		Security.addProvider(BC);
	}
	
	/**
	 * Creates a new signature for a data using a certificate and a private key
	 * @param certificate
	 * @param privateKey
	 * @param data
	 * @return sigData the signature of the data
	 */
	public byte[] createSignature(PrivateKey privateKey, byte[] data) {
		try {
			Signature signature = Signature.getInstance("SHA256withRSA");
			signature.initSign(privateKey);
			signature.update(data);
			return signature.sign();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
	}
	
	/**
	 * Verifies a signature for a data using a public key
	 * commented out as this takes place only in the server and not in the client : Arul
	 * @param publicKey
	 * @param sigBytes
	 * @param data
	 * @return true if the signature if valid
	 */
	
	/*public boolean verifySignature(PublicKey publicKey, byte[] sigBytes, byte[] data) {
		try {
			Signature signature = Signature.getInstance("SHA256withRSA", "BC");
			signature.initVerify(publicKey);
	        signature.update(data);
	        return signature.verify(sigBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}*/
	
}

package com.digsigmobile.business;

import java.io.ByteArrayInputStream;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * class verifies a digital signature given its public key or its digital certificate using BC provider
 * @author Bernardo
 */
public class SignatureManager {

	Provider BC = null;
	
	/**
	 * Constructor. Sets up the bouncy castle provider
	 */
	public SignatureManager() {
		BC = new BouncyCastleProvider();
		Security.addProvider(BC);
	}
	
	public boolean verifySignature(byte[] cert, byte[] sigBytes, byte[] data) {
		X509Certificate certificate;
		try {
			certificate = (X509Certificate) CertificateFactory.getInstance("X.509")
			.generateCertificate(new ByteArrayInputStream(cert));
			return verifySignature(certificate.getPublicKey(), sigBytes, data);
		} catch (CertificateException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Verifies a signature for a data using a certificate
	 * @param certificate
	 * @param sigBytes
	 * @param data
	 * @return true if the signature is valid
	 */
	public boolean verifySignature(X509Certificate certificate, byte[] sigBytes, byte[] data) {
		return verifySignature(certificate.getPublicKey(), sigBytes, data);
	}
	
	/**
	 * Verifies a signature for a data using a public key
	 * @param publicKey
	 * @param sigBytes
	 * @param data
	 * @return true if the signature if valid
	 */
	public boolean verifySignature(PublicKey publicKey, byte[] sigBytes, byte[] data) {
		try {
			Signature signature = Signature.getInstance("SHA256withRSA", "BC");
			signature.initVerify(publicKey);
	        signature.update(data);
	        return signature.verify(sigBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}

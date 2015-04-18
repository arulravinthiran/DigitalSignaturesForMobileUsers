package com.digsigmobile.business;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.sql.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v1CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import com.digsigmobile.beans.UserBean;
import com.digsigmobile.util.KeyStoreProvider;

/**
 * class creates root certificate (certificate of the server), end user certificate, 
 * verifies the integrity of end user's certificate and prints a given X509Certificate.
 * @author Bernardo
 *
 */
public class CertificateManager 
{

	private Provider BC = null;

	/**
	 * Constructor. Sets up the bouncy castle provider
	 */
	public CertificateManager() {
		BC = new BouncyCastleProvider();
		Security.addProvider(BC);
	}

	/**
	 * Creates a Self Signed Certificate Authority root certificate
	 * valid for one year and stores the created certificate inside
	 * the KeyStore
	 * @return certificate
	 */
	public X509Certificate createRootCertificate() 
	{

		String rootProfile = "C=CA, L=Ottawa, ST=Ontario, O=DigSigMobile, " +
				"OU=DigSigMobile Development Unit,E=digsigmobile@gmail.com";

		try {
			KeyPair keyPair = KeyPairGenerator.getInstance("RSA", "BC").generateKeyPair();

			ContentSigner sigGen = new JcaContentSignerBuilder("SHA256withRSA").
			 setProvider("BC").build(keyPair.getPrivate());

			Date startDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
			Date endDate = new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000);

			/*
			 * Configure certificate builder
			 */
			X509v1CertificateBuilder v1CertGen = new JcaX509v1CertificateBuilder(
					new X500Name(rootProfile),
					BigInteger.ONE,
					startDate, endDate,
					new X500Name(rootProfile),
					keyPair.getPublic());

			// Build certificate
			X509CertificateHolder certHolder = v1CertGen.build(sigGen);

			X509Certificate certificate = new JcaX509CertificateConverter().
			setProvider( "BC" ).getCertificate( certHolder );

			// Verify certificate
			certificate.checkValidity(new Date(System.currentTimeMillis()));
			certificate.verify(keyPair.getPublic());

			// Store certificate in KeyStore
			KeyStoreProvider.store(certificate, keyPair.getPrivate());

			return certificate;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Creates a certificate for the user, signed by the Certificate Authority,
	 * using the private key from the Certificate Authority to sign and storing
	 * the user's public key
	 * @param userPublicKey
	 * @param userBn
	 * @return certificate
	 */

	public X509Certificate createUserCertificate(PublicKey userPublicKey, UserBean userBn) 
	{
		// Retrieve rootCertificate
		X509Certificate rootCertificate = KeyStoreProvider.retrieveRootCertificate();
		if (rootCertificate == null) rootCertificate = createRootCertificate();
		// Retrieve rootPrivateKey
		PrivateKey rootPrivateKey = KeyStoreProvider.retrieveRootPrivatekey();

		ContentSigner sigGen = null;
		try {
			sigGen = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").
			build(rootPrivateKey);
		} catch (OperatorCreationException e1) {
			e1.printStackTrace();
		}

		Date startDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
		Date endDate = new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000);

		/*
		 * Configure certificate builder
		 */
		X509v3CertificateBuilder v3CertGen = new JcaX509v3CertificateBuilder (
				new X500Name(rootCertificate.getIssuerX500Principal().getName()),
				BigInteger.ONE,
				startDate, endDate,
				new X500Name("CN=userBn.getName(),E=userBn.getPrimaryEmail().getText()"),
				//verify if the above line works perfectly,
				userPublicKey);

		/*
		 * Extensions
		 */
		
		// Add SubjectKeyIdentifier extension based on the hash of the public key
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			md.update(userPublicKey.getEncoded());
			byte[] digest = md.digest();
			SubjectKeyIdentifier sIdentifier = new SubjectKeyIdentifier(digest);
			v3CertGen.addExtension(Extension.subjectKeyIdentifier, false, sIdentifier);

		} catch (CertIOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		// Add AuthorityKeyIdentifier extension based on the hash of the CA public key
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			md.update(rootCertificate.getPublicKey().getEncoded());
			byte[] digest = md.digest();
			AuthorityKeyIdentifier aIdentifier = new AuthorityKeyIdentifier(digest);
			v3CertGen.addExtension(Extension.authorityKeyIdentifier, false, aIdentifier);
		} catch (CertIOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		// Build certificate
		X509CertificateHolder certHolder = v3CertGen.build(sigGen);

		try {
			X509Certificate certificate = new JcaX509CertificateConverter().
			setProvider( "BC" ).getCertificate( certHolder );

			// Verify certificate
			certificate.checkValidity(new Date(System.currentTimeMillis()));
			certificate.verify(rootCertificate.getPublicKey());

			return certificate;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Verifies validity and integrity of the certificate provided using
	 * the Public Key from the Certificate Authority.
	 * @param certificate
	 * @return true if certificate is valid
	 */
	public boolean verifyUserCertificate(X509Certificate certificate) {
		// Retrieve rootCertificate
		X509Certificate rootCertificate = KeyStoreProvider.retrieveRootCertificate();
		if (rootCertificate == null) rootCertificate = createRootCertificate();

		try {
			// Verify validity using root public key
			certificate.checkValidity(new Date(System.currentTimeMillis()));
			certificate.verify(rootCertificate.getPublicKey());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Print a certificate object to a file in PEM format
	 * @param cert
	 * @param filename
	 */
	public void printCertificate(X509Certificate cert, String filename) {
		try {
			PEMWriter pemWriter = new PEMWriter(new PrintWriter(filename, "UTF-8"));
			pemWriter.writeObject(cert);
			pemWriter.flush();
			pemWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

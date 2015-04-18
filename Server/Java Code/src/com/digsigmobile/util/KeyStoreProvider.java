package com.digsigmobile.util;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class KeyStoreProvider {

	/**
	 * KeyStore related variables. 
	 * In order to this program to run, there has to be a keyStore
	 * file under the directory pointed by KEYSTORE_FILE variable.
	 * To create a new KeyStore file, use the command:
	 * keytool -genkey -alias {desired alias certificate} 
	 * 	-keystore {path to keystore.pfx} -storepass {password} 
	 * 	-validity 365 -keyalg RSA -keysize 2048 -storetype pkcs12
	 */
	private static final String KEYSTORE_FILE = "keys/cert.p12";
	private static final String KEYSTORE_PWD = "digsigmobile";
	private static final String KEYSTORE_INSTANCE = "PKCS12";
	private static final String KEYSTORE_ALIAS = "Key1";
	
	/**
	 * Retrieve Certificate Authority root certificate
	 * from the KeyStore
	 * @return certificate
	 */
	public static X509Certificate retrieveRootCertificate() {
		try {
			KeyStore ks = loadKeyStore();
			return (X509Certificate) ks.getCertificate(KEYSTORE_ALIAS);
		} catch (KeyStoreException e) {
			return null;
		}
	}

	/**
	 * Retrieve the private key of the Certificate Authority
	 * from the KeyStore
	 * @return privateKey
	 */
	public static PrivateKey retrieveRootPrivatekey() {
		try {
			KeyStore ks = loadKeyStore();
			return (PrivateKey) ks.getKey(KEYSTORE_ALIAS, KEYSTORE_PWD.toCharArray());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Store a root certificate and a private key
	 * inside the KeyStore
	 * @param certificate
	 * @param key
	 */
	public static void store(X509Certificate certificate, PrivateKey key) {
		try {
			Certificate[] certChain = {certificate};
			KeyStore ks = loadKeyStore();
			ks.setKeyEntry(KEYSTORE_ALIAS, key, KEYSTORE_PWD.toCharArray(), certChain);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load a KeyStore object using these class parameters
	 * @return keyStore
	 */
	public static KeyStore loadKeyStore() {
		try {
			KeyStore ks = KeyStore.getInstance(KEYSTORE_INSTANCE);
			ks.load(new FileInputStream(KEYSTORE_FILE), KEYSTORE_PWD.toCharArray());
			return ks;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
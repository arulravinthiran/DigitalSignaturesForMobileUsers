package com.digsigmobile.util;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Util class that generates prime numbers
 */
public class DigSigMobClientUtils 
{
	public static final BigInteger R = new BigInteger("298895077120468797620027224312030"
			+ "56791124520443466841492575492085042978563057141655636145216127193482504910"
			+ "258235356540546009941");
	/**
	 * method generates a prime number of the size specified by the parameter bits
	 * @param bits number of bits of desired prime number
	 * @return c BigInteger prime number
	 * @author Bernardo
	 */
	public static BigInteger generatePrime(int bits) {
	    SecureRandom ran = new SecureRandom();
	    BigInteger c = new BigInteger(bits, ran);
	    for (; ; ) {
	        if (c.isProbablePrime(1) == true) break;
	        c = c.subtract(new BigInteger("1"));
	    }
	    return c;
	}

}
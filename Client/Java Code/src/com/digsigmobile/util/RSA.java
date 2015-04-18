package com.digsigmobile.util;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Random;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;

public class RSA {

	public static final BigInteger R1 = new BigInteger("603706535958583574142044615180498973275792"
			+ "29372171765262372195957732692125815878351402731565366899041040973795656942771268234"
			+ "57299655460482690745373593131");
	private static final int seed =  65537;
	private static final int STRENGTH = 1024;

	@SuppressWarnings("unused")
	public static double testgenerateKeys(int p1, int p2, String fingerprint, BigInteger R, 
			PrintWriter out) {
		long startTime = System.currentTimeMillis();

		BigInteger sInteger = new BigInteger(fingerprint, 16);
		BigInteger pFactor = sInteger.pow(p1).mod(R);
		BigInteger qFactor = sInteger.pow(p2).mod(R);
		BigInteger eFactor = new BigInteger(String.valueOf(p1) + String.valueOf(p2));

		Random random = new Random(seed);

		PrimeGenerator primeGenerator = new PrimeGenerator(
				512,					// number of bits of primes generated 
				20, 					// number of times it has to pass the prime test
				random);				// seed

		// If eFactor is odd, e = eFactor. Otherwise e = eFactor + 1.
		BigInteger e = eFactor.testBit(0) ? eFactor : eFactor.add(BigIntegerMath.ONE); 

		// Adjust the size of pFactor and qFactor
		int maxIndex = 0;
		if ((maxIndex = pFactor.bitLength()) < (STRENGTH/2) - 1 ) {
			pFactor = pFactor.shiftLeft(STRENGTH/2 - maxIndex - 1);
		}
		System.out.println("pFactor bits = " + pFactor.bitLength());
		if ((maxIndex = qFactor.bitLength()) < (STRENGTH/2) - 1) {
			qFactor = qFactor.shiftLeft(STRENGTH/2 - maxIndex - 1);
		}
		System.out.println("qFactor bits = " + qFactor.bitLength());


		BigInteger p, q, n = null;

		try {
			p = primeGenerator.getNextSafePrime(pFactor);
			q = primeGenerator.getNextSafePrime(qFactor);
			for(;;) {
				n = p.multiply(q);
				if (n.bitLength() >= STRENGTH) break;
				p = primeGenerator.getNextSafePrime(p);
				q = primeGenerator.getNextSafePrime(q);
			}
			BigInteger pSub1 = p.subtract(BigIntegerMath.ONE);
			BigInteger qSub1 = q.subtract(BigIntegerMath.ONE);
			BigInteger phy = pSub1.multiply(qSub1);
			for(;;) {
				if (phy.gcd(e).compareTo(BigIntegerMath.ONE) == 0 && n.bitLength() >= STRENGTH) break;
				p = primeGenerator.getNextSafePrime(p);
				q = primeGenerator.getNextSafePrime(q);
				n = p.multiply(q);
				pSub1 = p.subtract(BigIntegerMath.ONE);
				qSub1 = q.subtract(BigIntegerMath.ONE);
				phy = pSub1.multiply(qSub1);
			}

			/*
			 * d = (1/e) mod phy
			 * dP = (1/e) mod (p-1)
			 * dQ = (1/e) mod (q-1)
			 * qInv = (1/q) mod p
			 */
			BigInteger d = e.modInverse(phy);
			BigInteger dP = d.remainder(pSub1);
			BigInteger dQ = d.remainder(qSub1);
			BigInteger qInv = q.modInverse(p);

		} catch(RuntimeException error) {
			// Try again if any number isn't a real prime
			System.out.println("Got an error. Treating...");
			if (error instanceof java.lang.ArithmeticException)
				testgenerateKeys(p1, p2, fingerprint, eFactor, out);
		}

		long estimatedTime = System.currentTimeMillis() - startTime;
		double elapsedTime = (double)estimatedTime / 1000;

		final Object[] row = new String[] {
				String.valueOf(n.bitLength()),
				R.toString(), 
				String.valueOf(p1), 
				String.valueOf(p2),
				String.format("%.2f%n", elapsedTime)
		};
		out.format("%5s %130s %5s %5s %6s\n", row);
		out.flush();

		return elapsedTime;
	}


	public static KeyPair generateKeys(String p1, String p2, String secret) {
		try
		{
		
			long startTime = System.currentTimeMillis();
			
			// Implement RSA algorithm
			// PrivateKey(n, e, d, p, q, dP, dQ, qInv)
			// PublicKey(n, e)
			String fingerprint = String.format("%040x", new BigInteger(1, secret.getBytes()));
			BigInteger sInteger = new BigInteger(fingerprint, 16);
			//BigInteger R = new BigInteger(511, 100, new SecureRandom());
			System.out.println("R = " + R1);
			int p1Integer = Integer.parseInt(p1);
			int p2Integer = Integer.parseInt(p2);
			BigInteger pFactor = sInteger.pow(p1Integer).mod(R1);
			BigInteger qFactor = sInteger.pow(p2Integer).mod(R1);
			BigInteger eFactor = new BigInteger(p1 + p2);

			Random random = new Random(seed);

			PrimeGenerator primeGenerator = new PrimeGenerator(
					512,					// number of bits of primes generated 
					20, 					// number of times it has to pass the prime test
					random);				// seed

			// If eFactor is odd, e = eFactor. Otherwise e = eFactor + 1.
			BigInteger e = eFactor.testBit(0) ? eFactor : eFactor.add(BigIntegerMath.ONE);

			// Adjust the size of pFactor and qFactor
			int maxIndex = 0;
			if ((maxIndex = pFactor.bitLength()) < (STRENGTH/2) - 1 ) {
				pFactor = pFactor.shiftLeft(STRENGTH/2 - maxIndex - 1);
			}
			System.out.println("pFactor bits = " + pFactor.bitLength());
			if ((maxIndex = qFactor.bitLength()) < (STRENGTH/2) - 1) {
				qFactor = qFactor.shiftLeft(STRENGTH/2 - maxIndex - 1);
			}
			System.out.println("qFactor bits = " + qFactor.bitLength());

			BigInteger p = null, 
					q = null, n = null, 
					d = null, dP = null, 
					dQ = null, qInv = null;
			
			try {
			p = primeGenerator.getNextSafePrime(pFactor);
			q = primeGenerator.getNextSafePrime(qFactor);
			for(;;) {
				n = p.multiply(q);	
				System.out.println("n bits = " + n.bitLength());
				if (n.bitLength() >= STRENGTH) break;
				p = primeGenerator.getNextSafePrime(p);
				q = primeGenerator.getNextSafePrime(q);
			}
			BigInteger pSub1 = p.subtract(BigIntegerMath.ONE);
			BigInteger qSub1 = q.subtract(BigIntegerMath.ONE);
			BigInteger phy = pSub1.multiply(qSub1);
			for(;;) {
				if (phy.gcd(e).compareTo(BigIntegerMath.ONE) == 0 && n.bitLength() >= STRENGTH) break;
				p = primeGenerator.getNextSafePrime(p);
				q = primeGenerator.getNextSafePrime(q);
				n = p.multiply(q);
				pSub1 = p.subtract(BigIntegerMath.ONE);
				qSub1 = q.subtract(BigIntegerMath.ONE);
				phy = pSub1.multiply(qSub1);
			}

			/*
			 * d = (1/e) mod phy
			 * dP = (1/e) mod (p-1)
			 * dQ = (1/e) mod (q-1)
			 * qInv = (1/q) mod p
			 */
			d = e.modInverse(phy);
			dP = d.remainder(pSub1);
			dQ = d.remainder(qSub1);
			qInv = q.modInverse(p);

			} catch (RuntimeException error) {
				// Try again if any number isn't a real prime
							System.out.println("Got an error. Treating...");
							if (error instanceof java.lang.ArithmeticException)
								generateKeys(p1, p2, fingerprint);
			}
			
			AsymmetricCipherKeyPair pair = 
					new AsymmetricCipherKeyPair(
							new RSAKeyParameters(false, n, e), 
							new RSAPrivateCrtKeyParameters(n, e, d, p, q, dP, dQ, qInv));
			RSAKeyParameters pub = (RSAKeyParameters)pair.getPublic();
			RSAPrivateCrtKeyParameters priv = (RSAPrivateCrtKeyParameters)pair.getPrivate();

			PublicKey publicKey = new BCRSAPublicKey(pub);
			PrivateKey privateKey = new BCRSAPrivateCrtKey(priv); 

			long estimatedTime = System.currentTimeMillis() - startTime;
			System.out.println("Keys generated!");
			System.out.println("Time elapsed: " + (double)estimatedTime / 1000 + " seconds");
			return new KeyPair(publicKey, privateKey);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
}
package com.nimbusds.jose;


import junit.framework.TestCase;

/**
 * Tests the JWS Algorithm class.
 *
 * @author Vladimir Dzhuvinov
 * @version 2015-11-16
 */
public class JWEAlgorithmTest extends TestCase {


	public void testParse() {

		assertTrue(JWEAlgorithm.RSA1_5 == JWEAlgorithm.parse("RSA1_5"));
		assertTrue(JWEAlgorithm.RSA_OAEP == JWEAlgorithm.parse("RSA-OAEP"));
		assertTrue(JWEAlgorithm.RSA_OAEP_256 == JWEAlgorithm.parse("RSA-OAEP-256"));

		assertTrue(JWEAlgorithm.A128KW == JWEAlgorithm.parse("A128KW"));
		assertTrue(JWEAlgorithm.A192KW == JWEAlgorithm.parse("A192KW"));
		assertTrue(JWEAlgorithm.A256KW == JWEAlgorithm.parse("A256KW"));

		assertTrue(JWEAlgorithm.DIR == JWEAlgorithm.parse("dir"));

		assertTrue(JWEAlgorithm.ECDH_ES == JWEAlgorithm.parse("ECDH-ES"));

		assertTrue(JWEAlgorithm.ECDH_ES_A128KW == JWEAlgorithm.parse("ECDH-ES+A128KW"));
		assertTrue(JWEAlgorithm.ECDH_ES_A192KW == JWEAlgorithm.parse("ECDH-ES+A192KW"));
		assertTrue(JWEAlgorithm.ECDH_ES_A256KW == JWEAlgorithm.parse("ECDH-ES+A256KW"));

		assertTrue(JWEAlgorithm.A128GCMKW == JWEAlgorithm.parse("A128GCMKW"));
		assertTrue(JWEAlgorithm.A192GCMKW == JWEAlgorithm.parse("A192GCMKW"));
		assertTrue(JWEAlgorithm.A256GCMKW == JWEAlgorithm.parse("A256GCMKW"));

		assertTrue(JWEAlgorithm.PBES2_HS256_A128KW == JWEAlgorithm.parse("PBES2-HS256+A128KW"));
		assertTrue(JWEAlgorithm.PBES2_HS384_A192KW == JWEAlgorithm.parse("PBES2-HS384+A192KW"));
		assertTrue(JWEAlgorithm.PBES2_HS512_A256KW == JWEAlgorithm.parse("PBES2-HS512+A256KW"));
	}


	public void testRSAFamily() {

		assertTrue(JWEAlgorithm.Family.RSA.contains(JWEAlgorithm.RSA1_5));
		assertTrue(JWEAlgorithm.Family.RSA.contains(JWEAlgorithm.RSA_OAEP));
		assertTrue(JWEAlgorithm.Family.RSA.contains(JWEAlgorithm.RSA_OAEP_256));
		assertEquals(3, JWEAlgorithm.Family.RSA.size());
	}


	public void testAxxxKWFamily() {

		assertTrue(JWEAlgorithm.Family.AES_KW.contains(JWEAlgorithm.A128KW));
		assertTrue(JWEAlgorithm.Family.AES_KW.contains(JWEAlgorithm.A192KW));
		assertTrue(JWEAlgorithm.Family.AES_KW.contains(JWEAlgorithm.A256KW));
		assertEquals(3, JWEAlgorithm.Family.AES_KW.size());
	}


	public void testAxxxGCMKWFamily() {

		assertTrue(JWEAlgorithm.Family.AES_GCM_KW.contains(JWEAlgorithm.A256GCMKW));
		assertTrue(JWEAlgorithm.Family.AES_GCM_KW.contains(JWEAlgorithm.A256GCMKW));
		assertTrue(JWEAlgorithm.Family.AES_GCM_KW.contains(JWEAlgorithm.A256GCMKW));
		assertEquals(3, JWEAlgorithm.Family.AES_GCM_KW.size());
	}


	public void testPBES2Family() {

		assertTrue(JWEAlgorithm.Family.PBES2.contains(JWEAlgorithm.PBES2_HS256_A128KW));
		assertTrue(JWEAlgorithm.Family.PBES2.contains(JWEAlgorithm.PBES2_HS256_A128KW));
		assertTrue(JWEAlgorithm.Family.PBES2.contains(JWEAlgorithm.PBES2_HS256_A128KW));
		assertEquals(3, JWEAlgorithm.Family.PBES2.size());
	}


	public void testECDHFamily() {

		assertTrue(JWEAlgorithm.Family.ECDH_ES.contains(JWEAlgorithm.ECDH_ES));
		assertTrue(JWEAlgorithm.Family.ECDH_ES.contains(JWEAlgorithm.ECDH_ES_A128KW));
		assertTrue(JWEAlgorithm.Family.ECDH_ES.contains(JWEAlgorithm.ECDH_ES_A192KW));
		assertTrue(JWEAlgorithm.Family.ECDH_ES.contains(JWEAlgorithm.ECDH_ES_A256KW));
		assertEquals(4, JWEAlgorithm.Family.ECDH_ES.size());
	}
}

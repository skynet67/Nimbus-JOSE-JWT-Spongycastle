package com.nimbusds.jose.jwk;


import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.util.*;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.bc.BouncyCastleProviderSingleton;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import junit.framework.TestCase;
import net.minidev.json.JSONObject;


/**
 * Tests the EC JWK class.
 *
 * @author Vladimir Dzhuvinov
 * @version 2016-17-03
 */
public class ECKeyTest extends TestCase {


	// Test parameters are from JWK spec
	private static final class ExampleKeyP256 {


		public static final ECKey.Curve CRV = ECKey.Curve.P_256;


		public static final Base64URL X = new Base64URL("MKBCTNIcKUSDii11ySs3526iDZ8AiTo7Tu6KPAqv7D4");


		public static final Base64URL Y = new Base64URL("4Etl6SRW2YiLUrN5vfvVHuhp7x8PxltmWWlbbM4IFyM");


		public static final Base64URL D = new Base64URL("870MB6gfuTJ4HtUnUvYMyJpr5eUZNP4Bk43bVdj3eAE");
	}


	// Test parameters are from Anders Rundgren, public only
	private static final class ExampleKeyP256Alt {


		public static final ECKey.Curve CRV = ECKey.Curve.P_256;


		public static final Base64URL X = new Base64URL("3l2Da_flYc-AuUTm2QzxgyvJxYM_2TeB9DMlwz7j1PE");


		public static final Base64URL Y = new Base64URL("-kjT7Wrfhwsi9SG6H4UXiyUiVE9GHCLauslksZ3-_t0");
	}


	// Test parameters are from Anders Rundgren, public only
	private static final class ExampleKeyP384Alt {


		public static final ECKey.Curve CRV = ECKey.Curve.P_384;


		public static final Base64URL X = new Base64URL("Xy0mn0LmRyDBeHBjZrqH9z5Weu5pzCZYl1FJGHdoEj1utAoCpD4-Wn3VAIT-qgFF");


		public static final Base64URL Y = new Base64URL("mrZQ1aB1E7JksXe6LXmM3BiGzqtlwCtMN0cpJb5EU62JMSISSK8l7cXSFt84A25z");
	}


	// Test parameters are from Anders Rundgren, public only
	private static final class ExampleKeyP521Alt {


		public static final ECKey.Curve CRV = ECKey.Curve.P_521;


		public static final Base64URL X = new Base64URL("AfwEaSkqoPQynn4SdAXOqbyDuK6KsbI04i-6aWvh3GdvREZuHaWFyg791gcvJ4OqG13-gzfYxZxfblPMqfOtQrzk");


		public static final Base64URL Y = new Base64URL("AHgOZhhJb2ZiozkquiEa0Z9SfERJbWaaE7qEnCuk9VVZaWruKWKNzZadoIRPt8h305r14KRoxu8AfV20X-d_2Ups");
	}


	public void testKeySizes() {

		assertEquals(256, new ECKey.Builder(ExampleKeyP256.CRV, ExampleKeyP256.X, ExampleKeyP256.Y).build().size());
		assertEquals(256, new ECKey.Builder(ExampleKeyP256Alt.CRV, ExampleKeyP256Alt.X, ExampleKeyP256Alt.Y).build().size());
		assertEquals(384, new ECKey.Builder(ExampleKeyP384Alt.CRV, ExampleKeyP384Alt.X, ExampleKeyP384Alt.Y).build().size());
		assertEquals(521, new ECKey.Builder(ExampleKeyP521Alt.CRV, ExampleKeyP521Alt.X, ExampleKeyP521Alt.Y).build().size());
	}


	public void testKeySizeForUnknownCurve() {

		try {
			new ECKey.Builder(new ECKey.Curve("unknown"), ExampleKeyP256.X, ExampleKeyP256.Y).build().size();
			fail();
		} catch (UnsupportedOperationException e) {
			assertEquals("Couldn't determine field size for curve unknown", e.getMessage());
		}
	}


	public void testAltECKeyParamLengths() {

		assertEquals(32, ExampleKeyP256Alt.X.decode().length);
		assertEquals(32, ExampleKeyP256Alt.Y.decode().length);

		assertEquals(48, ExampleKeyP384Alt.X.decode().length);
		assertEquals(48, ExampleKeyP384Alt.Y.decode().length);

		assertEquals(66, ExampleKeyP521Alt.X.decode().length);
		assertEquals(66, ExampleKeyP521Alt.Y.decode().length);
	}


	public void testCoordinateEncoding() {

		byte[] unpadded = {1, 2, 3, 4, 5};
		BigInteger bigInteger = new BigInteger(1, unpadded);

		// With no padding required
		int fieldSize = unpadded.length * 8;
		assertEquals(Base64URL.encode(unpadded), ECKey.encodeCoordinate(fieldSize, bigInteger));

		// With two leading zeros padding required
		fieldSize = unpadded.length * 8 + 2 * 8;
		assertEquals(Base64URL.encode(new byte[]{0, 0, 1, 2, 3, 4, 5}), ECKey.encodeCoordinate(fieldSize, bigInteger));
		assertEquals(bigInteger.toString(), ECKey.encodeCoordinate(fieldSize, bigInteger).decodeToBigInteger().toString());
	}


	public void testFullConstructorAndSerialization()
		throws Exception {

		URI x5u = new URI("http://example.com/jwk.json");
		Base64URL x5t = new Base64URL("abc");
		List<Base64> x5c = new LinkedList<>();
		x5c.add(new Base64("def"));

		Set<KeyOperation> ops = null;

		ECKey key = new ECKey(ExampleKeyP256.CRV, ExampleKeyP256.X, ExampleKeyP256.Y, ExampleKeyP256.D,
			KeyUse.SIGNATURE, ops, JWSAlgorithm.ES256, "1", x5u, x5t, x5c);

		assertTrue(key instanceof AssymetricJWK);

		// Test getters
		assertEquals(KeyUse.SIGNATURE, key.getKeyUse());
		assertNull(key.getKeyOperations());
		assertEquals(JWSAlgorithm.ES256, key.getAlgorithm());
		assertEquals("1", key.getKeyID());
		assertEquals(x5u.toString(), key.getX509CertURL().toString());
		assertEquals(x5t.toString(), key.getX509CertThumbprint().toString());
		assertEquals(x5c.size(), key.getX509CertChain().size());

		assertEquals(ECKey.Curve.P_256, key.getCurve());
		assertEquals(ExampleKeyP256.X, key.getX());
		assertEquals(ExampleKeyP256.Y, key.getY());
		assertEquals(ExampleKeyP256.D, key.getD());

		assertTrue(key.isPrivate());


		String jwkString = key.toJSONObject().toString();

		key = ECKey.parse(jwkString);

		// Test getters
		assertEquals(KeyUse.SIGNATURE, key.getKeyUse());
		assertNull(key.getKeyOperations());
		assertEquals(JWSAlgorithm.ES256, key.getAlgorithm());
		assertEquals("1", key.getKeyID());

		assertEquals(ECKey.Curve.P_256, key.getCurve());
		assertEquals(ExampleKeyP256.X, key.getX());
		assertEquals(ExampleKeyP256.Y, key.getY());
		assertEquals(ExampleKeyP256.D, key.getD());

		assertTrue(key.isPrivate());


		// Test conversion to public JWK

		key = key.toPublicJWK();

		assertEquals(KeyUse.SIGNATURE, key.getKeyUse());
		assertNull(key.getKeyOperations());
		assertEquals(JWSAlgorithm.ES256, key.getAlgorithm());
		assertEquals("1", key.getKeyID());
		assertEquals(x5u.toString(), key.getX509CertURL().toString());
		assertEquals(x5t.toString(), key.getX509CertThumbprint().toString());
		assertEquals(x5c.size(), key.getX509CertChain().size());

		assertEquals(ECKey.Curve.P_256, key.getCurve());
		assertEquals(ExampleKeyP256.X, key.getX());
		assertEquals(ExampleKeyP256.Y, key.getY());
		assertNull(key.getD());

		assertFalse(key.isPrivate());
	}


	public void testFullConstructorAndSerializationWithOps()
		throws Exception {

		URI x5u = new URI("http://example.com/jwk.json");
		Base64URL x5t = new Base64URL("abc");
		List<Base64> x5c = new LinkedList<>();
		x5c.add(new Base64("def"));

		KeyUse use = null;
		Set<KeyOperation> ops = new LinkedHashSet<>(Arrays.asList(KeyOperation.SIGN, KeyOperation.VERIFY));

		ECKey key = new ECKey(ExampleKeyP256.CRV, ExampleKeyP256.X, ExampleKeyP256.Y, ExampleKeyP256.D,
			use, ops, JWSAlgorithm.ES256, "1", x5u, x5t, x5c);

		// Test getters
		assertNull(key.getKeyUse());
		assertTrue(key.getKeyOperations().contains(KeyOperation.SIGN));
		assertTrue(key.getKeyOperations().contains(KeyOperation.VERIFY));
		assertEquals(2, key.getKeyOperations().size());
		assertEquals(JWSAlgorithm.ES256, key.getAlgorithm());
		assertEquals("1", key.getKeyID());
		assertEquals(x5u.toString(), key.getX509CertURL().toString());
		assertEquals(x5t.toString(), key.getX509CertThumbprint().toString());
		assertEquals(x5c.size(), key.getX509CertChain().size());

		assertEquals(ECKey.Curve.P_256, key.getCurve());
		assertEquals(ExampleKeyP256.X, key.getX());
		assertEquals(ExampleKeyP256.Y, key.getY());
		assertEquals(ExampleKeyP256.D, key.getD());

		assertTrue(key.isPrivate());


		String jwkString = key.toJSONObject().toString();

		key = ECKey.parse(jwkString);

		// Test getters
		assertNull(key.getKeyUse());
		assertTrue(key.getKeyOperations().contains(KeyOperation.SIGN));
		assertTrue(key.getKeyOperations().contains(KeyOperation.VERIFY));
		assertEquals(2, key.getKeyOperations().size());
		assertEquals(JWSAlgorithm.ES256, key.getAlgorithm());
		assertEquals("1", key.getKeyID());

		assertEquals(ECKey.Curve.P_256, key.getCurve());
		assertEquals(ExampleKeyP256.X, key.getX());
		assertEquals(ExampleKeyP256.Y, key.getY());
		assertEquals(ExampleKeyP256.D, key.getD());

		assertTrue(key.isPrivate());


		// Test conversion to public JWK

		key = key.toPublicJWK();

		assertNull(key.getKeyUse());
		assertTrue(key.getKeyOperations().contains(KeyOperation.SIGN));
		assertTrue(key.getKeyOperations().contains(KeyOperation.VERIFY));
		assertEquals(2, key.getKeyOperations().size());
		assertEquals(JWSAlgorithm.ES256, key.getAlgorithm());
		assertEquals("1", key.getKeyID());
		assertEquals(x5u.toString(), key.getX509CertURL().toString());
		assertEquals(x5t.toString(), key.getX509CertThumbprint().toString());
		assertEquals(x5c.size(), key.getX509CertChain().size());

		assertEquals(ECKey.Curve.P_256, key.getCurve());
		assertEquals(ExampleKeyP256.X, key.getX());
		assertEquals(ExampleKeyP256.Y, key.getY());
		assertNull(key.getD());

		assertFalse(key.isPrivate());
	}


	public void testBuilder()
		throws Exception {

		URI x5u = new URI("http://example.com/jwk.json");
		Base64URL x5t = new Base64URL("abc");
		List<Base64> x5c = new LinkedList<>();
		x5c.add(new Base64("def"));

		ECKey key = new ECKey.Builder(ECKey.Curve.P_256, ExampleKeyP256.X, ExampleKeyP256.Y).
			d(ExampleKeyP256.D).
			keyUse(KeyUse.SIGNATURE).
			algorithm(JWSAlgorithm.ES256).
			keyID("1").
			x509CertURL(x5u).
			x509CertThumbprint(x5t).
			x509CertChain(x5c).
			build();

		// Test getters
		assertEquals(KeyUse.SIGNATURE, key.getKeyUse());
		assertEquals(JWSAlgorithm.ES256, key.getAlgorithm());
		assertEquals("1", key.getKeyID());
		assertEquals(x5u.toString(), key.getX509CertURL().toString());
		assertEquals(x5t.toString(), key.getX509CertThumbprint().toString());
		assertEquals(x5c.size(), key.getX509CertChain().size());

		assertEquals(ECKey.Curve.P_256, key.getCurve());
		assertEquals(ExampleKeyP256.X, key.getX());
		assertEquals(ExampleKeyP256.Y, key.getY());
		assertEquals(ExampleKeyP256.D, key.getD());

		assertTrue(key.isPrivate());


		String jwkString = key.toJSONObject().toString();

		key = ECKey.parse(jwkString);

		// Test getters
		assertEquals(KeyUse.SIGNATURE, key.getKeyUse());
		assertEquals(JWSAlgorithm.ES256, key.getAlgorithm());
		assertEquals("1", key.getKeyID());

		assertEquals(ECKey.Curve.P_256, key.getCurve());
		assertEquals(ExampleKeyP256.X, key.getX());
		assertEquals(ExampleKeyP256.Y, key.getY());
		assertEquals(ExampleKeyP256.D, key.getD());

		assertTrue(key.isPrivate());


		// Test conversion to public JWK

		key = key.toPublicJWK();

		assertEquals(KeyUse.SIGNATURE, key.getKeyUse());
		assertEquals(JWSAlgorithm.ES256, key.getAlgorithm());
		assertEquals("1", key.getKeyID());
		assertEquals(x5u.toString(), key.getX509CertURL().toString());
		assertEquals(x5t.toString(), key.getX509CertThumbprint().toString());
		assertEquals(x5c.size(), key.getX509CertChain().size());

		assertEquals(ECKey.Curve.P_256, key.getCurve());
		assertEquals(ExampleKeyP256.X, key.getX());
		assertEquals(ExampleKeyP256.Y, key.getY());
		assertNull(key.getD());

		assertFalse(key.isPrivate());

	}


	public void testP256ExportAndImport()
		throws Exception {

		// Public + private

		ECKey key = new ECKey.Builder(ExampleKeyP256.CRV, ExampleKeyP256.X, ExampleKeyP256.Y).d(ExampleKeyP256.D).build();

		// Export
		KeyPair pair = key.toKeyPair();

		ECPublicKey pub = (ECPublicKey) pair.getPublic();
		assertEquals(256, pub.getParams().getCurve().getField().getFieldSize());
		assertEquals(ExampleKeyP256.X.decodeToBigInteger(), pub.getW().getAffineX());
		assertEquals(ExampleKeyP256.Y.decodeToBigInteger(), pub.getW().getAffineY());

		ECPrivateKey priv = (ECPrivateKey) pair.getPrivate();
		assertEquals(256, priv.getParams().getCurve().getField().getFieldSize());
		assertEquals(ExampleKeyP256.D.decodeToBigInteger(), priv.getS());

		// Import
		key = new ECKey.Builder(ECKey.Curve.P_256, pub).privateKey(priv).build();
		assertEquals(ECKey.Curve.P_256, key.getCurve());
		assertEquals(ExampleKeyP256.X, key.getX());
		assertEquals(ExampleKeyP256.Y, key.getY());
		assertEquals(ExampleKeyP256.D, key.getD());
		assertEquals(32, ExampleKeyP256.D.decode().length);

		assertTrue(key.isPrivate());
	}


	public void testP256AltExportAndImport()
		throws Exception {

		ECKey key = new ECKey.Builder(ExampleKeyP256Alt.CRV, ExampleKeyP256Alt.X, ExampleKeyP256Alt.Y).build();

		// Export
		KeyPair pair = key.toKeyPair(BouncyCastleProviderSingleton.getInstance());

		ECPublicKey pub = (ECPublicKey) pair.getPublic();
		assertEquals(256, pub.getParams().getCurve().getField().getFieldSize());
		assertEquals(ExampleKeyP256Alt.X.decodeToBigInteger(), pub.getW().getAffineX());
		assertEquals(ExampleKeyP256Alt.Y.decodeToBigInteger(), pub.getW().getAffineY());

		// Import
		key = new ECKey.Builder(ExampleKeyP256Alt.CRV, pub).build();
		assertEquals(ECKey.Curve.P_256, key.getCurve());
		assertEquals(ExampleKeyP256Alt.X, key.getX());
		assertEquals(ExampleKeyP256Alt.Y, key.getY());

		assertFalse(key.isPrivate());
	}


	public void testP384AltExportAndImport()
		throws Exception {

		ECKey key = new ECKey.Builder(ExampleKeyP384Alt.CRV, ExampleKeyP384Alt.X, ExampleKeyP384Alt.Y).build();

		// Export
		KeyPair pair = key.toKeyPair();

		ECPublicKey pub = (ECPublicKey) pair.getPublic();
		assertEquals(384, pub.getParams().getCurve().getField().getFieldSize());
		assertEquals(ExampleKeyP384Alt.X.decodeToBigInteger(), pub.getW().getAffineX());
		assertEquals(ExampleKeyP384Alt.Y.decodeToBigInteger(), pub.getW().getAffineY());

		// Import
		key = new ECKey.Builder(ExampleKeyP384Alt.CRV, pub).build();
		assertEquals(ECKey.Curve.P_384, key.getCurve());
		assertEquals(ExampleKeyP384Alt.X, key.getX());
		assertEquals(ExampleKeyP384Alt.Y, key.getY());

		assertFalse(key.isPrivate());
	}


	public void testP521AltExportAndImport()
		throws Exception {

		ECKey key = new ECKey.Builder(ExampleKeyP521Alt.CRV, ExampleKeyP521Alt.X, ExampleKeyP521Alt.Y).build();

		// Export
		KeyPair pair = key.toKeyPair();

		ECPublicKey pub = (ECPublicKey) pair.getPublic();
		assertEquals(521, pub.getParams().getCurve().getField().getFieldSize());
		assertEquals(ExampleKeyP521Alt.X.decodeToBigInteger(), pub.getW().getAffineX());
		assertEquals(ExampleKeyP521Alt.Y.decodeToBigInteger(), pub.getW().getAffineY());

		// Import
		key = new ECKey.Builder(ExampleKeyP521Alt.CRV, pub).build();
		assertEquals(ECKey.Curve.P_521, key.getCurve());
		assertEquals(ExampleKeyP521Alt.X, key.getX());
		assertEquals(ExampleKeyP521Alt.Y, key.getY());

		assertFalse(key.isPrivate());
	}


	public void testRejectKeyUseWithOps() {

		KeyUse use = KeyUse.SIGNATURE;

		Set<KeyOperation> ops = new HashSet<>(Arrays.asList(KeyOperation.SIGN, KeyOperation.VERIFY));

		try {
			new ECKey(ExampleKeyP256.CRV, ExampleKeyP256.X, ExampleKeyP256.Y, use, ops, null, null, null, null, null);

			fail();
		} catch (IllegalArgumentException e) {
			// ok
		}

		try {
			new ECKey.Builder(ExampleKeyP256.CRV, ExampleKeyP256.X, ExampleKeyP256.Y).
				keyUse(use).keyOperations(ops).build();
			fail();
		} catch (IllegalStateException e) {
			// ok
		}
	}


	public void testCookbookExampleKey()
		throws Exception {

		// See http://tools.ietf.org/html/rfc7520#section-3.2

		String json = "{" +
			"\"kty\":\"EC\"," +
			"\"kid\":\"bilbo.baggins@hobbiton.example\"," +
			"\"use\":\"sig\"," +
			"\"crv\":\"P-521\"," +
			"\"x\":\"AHKZLLOsCOzz5cY97ewNUajB957y-C-U88c3v13nmGZx6sYl_oJXu9" +
			"A5RkTKqjqvjyekWF-7ytDyRXYgCF5cj0Kt\"," +
			"\"y\":\"AdymlHvOiLxXkEhayXQnNCvDX4h9htZaCJN34kfmC6pV5OhQHiraVy" +
			"SsUdaQkAgDPrwQrJmbnX9cwlGfP-HqHZR1\"," +
			"\"d\":\"AAhRON2r9cqXX1hg-RoI6R1tX5p2rUAYdmpHZoC1XNM56KtscrX6zb" +
			"KipQrCW9CGZH3T4ubpnoTKLDYJ_fF3_rJt\"" +
			"}";

		ECKey jwk = ECKey.parse(json);

		assertEquals(KeyType.EC, jwk.getKeyType());
		assertEquals("bilbo.baggins@hobbiton.example", jwk.getKeyID());
		assertEquals(KeyUse.SIGNATURE, jwk.getKeyUse());
		assertEquals(ECKey.Curve.P_521, jwk.getCurve());

		assertEquals("AHKZLLOsCOzz5cY97ewNUajB957y-C-U88c3v13nmGZx6sYl_oJXu9" +
			"A5RkTKqjqvjyekWF-7ytDyRXYgCF5cj0Kt", jwk.getX().toString());

		assertEquals("AdymlHvOiLxXkEhayXQnNCvDX4h9htZaCJN34kfmC6pV5OhQHiraVy" +
			"SsUdaQkAgDPrwQrJmbnX9cwlGfP-HqHZR1", jwk.getY().toString());

		assertEquals("AAhRON2r9cqXX1hg-RoI6R1tX5p2rUAYdmpHZoC1XNM56KtscrX6zb" +
			"KipQrCW9CGZH3T4ubpnoTKLDYJ_fF3_rJt", jwk.getD().toString());

		// Convert to Java EC key object
		ECPublicKey ecPublicKey = jwk.toECPublicKey();
		ECPrivateKey ecPrivateKey = jwk.toECPrivateKey();

		jwk = new ECKey.Builder(ECKey.Curve.P_521, ecPublicKey).privateKey(ecPrivateKey).build();

		assertEquals("AHKZLLOsCOzz5cY97ewNUajB957y-C-U88c3v13nmGZx6sYl_oJXu9" +
			"A5RkTKqjqvjyekWF-7ytDyRXYgCF5cj0Kt", jwk.getX().toString());

		assertEquals("AdymlHvOiLxXkEhayXQnNCvDX4h9htZaCJN34kfmC6pV5OhQHiraVy" +
			"SsUdaQkAgDPrwQrJmbnX9cwlGfP-HqHZR1", jwk.getY().toString());

		assertEquals("AAhRON2r9cqXX1hg-RoI6R1tX5p2rUAYdmpHZoC1XNM56KtscrX6zb" +
			"KipQrCW9CGZH3T4ubpnoTKLDYJ_fF3_rJt", jwk.getD().toString());
	}


	public void testUnsupportedCurveParams() {

		assertNull(new ECKey.Curve("unsupported").toECParameterSpec());
	}


	public void testCurveParams() {

		ECParameterSpec ecParameterSpec;

		ecParameterSpec = ECKey.Curve.P_256.toECParameterSpec();
		assertNotNull(ecParameterSpec);
		assertEquals(ECKey.Curve.P_256, ECKey.Curve.forECParameterSpec(ecParameterSpec));

		ecParameterSpec = ECKey.Curve.P_384.toECParameterSpec();
		assertNotNull(ecParameterSpec);
		assertEquals(ECKey.Curve.P_384, ECKey.Curve.forECParameterSpec(ecParameterSpec));

		ecParameterSpec = ECKey.Curve.P_521.toECParameterSpec();
		assertNotNull(ecParameterSpec);
		assertEquals(ECKey.Curve.P_521, ECKey.Curve.forECParameterSpec(ecParameterSpec));
	}


	public void testCurveForStdName() {

		assertEquals(ECKey.Curve.P_256, ECKey.Curve.forStdName("secp256r1"));
		assertEquals(ECKey.Curve.P_256, ECKey.Curve.forStdName("prime256v1"));

		assertEquals(ECKey.Curve.P_384, ECKey.Curve.forStdName("secp384r1"));

		assertEquals(ECKey.Curve.P_521, ECKey.Curve.forStdName("secp521r1"));
	}


	public void testThumbprint()
		throws Exception {

		ECKey ecKey = new ECKey.Builder(ExampleKeyP256.CRV, ExampleKeyP256.X, ExampleKeyP256.Y).build();

		Base64URL thumbprint = ecKey.computeThumbprint();

		assertEquals(256 / 8, thumbprint.decode().length);

		String orderedJSON = "{\"crv\":\"P-256\",\"kty\":\"EC\",\"x\":\"MKBCTNIcKUSDii11ySs3526iDZ8AiTo7Tu6KPAqv7D4\",\"y\":\"4Etl6SRW2YiLUrN5vfvVHuhp7x8PxltmWWlbbM4IFyM\"}";

		Base64URL expected = Base64URL.encode(MessageDigest.getInstance("SHA-256").digest(orderedJSON.getBytes(Charset.forName("UTF-8"))));

		assertEquals(expected, thumbprint);
	}


	public void testThumbprintSHA1()
		throws Exception {

		ECKey ecKey = new ECKey.Builder(ExampleKeyP256.CRV, ExampleKeyP256.X, ExampleKeyP256.Y).build();

		Base64URL thumbprint = ecKey.computeThumbprint("SHA-1");

		assertEquals(160 / 8, thumbprint.decode().length);
	}


	public void testThumbprintAsKeyID()
		throws Exception {

		ECKey ecKey = new ECKey.Builder(ExampleKeyP256.CRV, ExampleKeyP256.X, ExampleKeyP256.Y)
			.keyIDFromThumbprint()
			.build();

		Base64URL thumbprint = new Base64URL(ecKey.getKeyID());

		assertEquals(256 / 8, thumbprint.decode().length);

		String orderedJSON = JSONObject.toJSONString(ecKey.getRequiredParams());

		Base64URL expected = Base64URL.encode(MessageDigest.getInstance("SHA-256").digest(orderedJSON.getBytes(Charset.forName("UTF-8"))));

		assertEquals(expected, thumbprint);
	}


	public void testThumbprintSHA1AsKeyID()
		throws Exception {

		ECKey ecKey = new ECKey.Builder(ExampleKeyP256.CRV, ExampleKeyP256.X, ExampleKeyP256.Y)
			.keyIDFromThumbprint("SHA-1")
			.build();

		Base64URL thumbprint = new Base64URL(ecKey.getKeyID());

		assertEquals(160 / 8, thumbprint.decode().length);
	}


	// See https://mailarchive.ietf.org/arch/msg/jose/gS-nOfqgV1n17DFUd6w_yBEf0sU
	public void testJose4jVectorP256()
		throws Exception {

		String json = "{\"kty\":\"EC\"," +
			"\"x\":\"CEuRLUISufhcjrj-32N0Bvl3KPMiHH9iSw4ohN9jxrA\"," +
			"\"y\":\"EldWz_iXSK3l_S7n4w_t3baxos7o9yqX0IjzG959vHc\"," +
			"\"crv\":\"P-256\"}";

		ECKey ecKey = ECKey.parse(json);

		assertEquals("j4UYwo9wrtllSHaoLDJNh7MhVCL8t0t8cGPPzChpYDs", ecKey.computeThumbprint().toString());
	}


	// See https://mailarchive.ietf.org/arch/msg/jose/gS-nOfqgV1n17DFUd6w_yBEf0sU
	public void testJose4jVectorP384()
		throws Exception {

		String json = "{\"kty\":\"EC\"," +
			" \"x\":\"2jCG5DmKUql9YPn7F2C-0ljWEbj8O8-vn5Ih1k7Wzb-y3NpBLiG1BiRa392b1kcQ\"," +
			" \"y\":\"7Ragi9rT-5tSzaMbJlH_EIJl6rNFfj4V4RyFM5U2z4j1hesX5JXa8dWOsE-5wPIl\"," +
			" \"crv\":\"P-384\"}";

		ECKey ecKey = ECKey.parse(json);

		assertEquals("vZtaWIw-zw95JNzzURg1YB7mWNLlm44YZDZzhrPNetM", ecKey.computeThumbprint().toString());
	}


	// See https://mailarchive.ietf.org/arch/msg/jose/gS-nOfqgV1n17DFUd6w_yBEf0sU
	public void testJose4jVectorP521()
		throws Exception {

		String json = "{\"kty\":\"EC\"," +
			"\"x\":\"Aeq3uMrb3iCQEt0PzSeZMmrmYhsKP5DM1oMP6LQzTFQY9-F3Ab45xiK4AJxltXEI-87g3gRwId88hTyHgq180JDt\"," +
			"\"y\":\"ARA0lIlrZMEzaXyXE4hjEkc50y_JON3qL7HSae9VuWpOv_2kit8p3pyJBiRb468_U5ztLT7FvDvtimyS42trhDTu\"," +
			"\"crv\":\"P-521\"}";

		ECKey ecKey = ECKey.parse(json);

		assertEquals("rz4Ohmpxg-UOWIWqWKHlOe0bHSjNUFlHW5vwG_M7qYg", ecKey.computeThumbprint().toString());
	}
}
package com.nimbusds.jose.crypto;


import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.ByteUtils;


/**
 * Additional authenticated data (AAD).
 *
 * <p>See RFC 7518 (JWA), section 5.1, point 14.
 *
 * @author Vladimir Dzhuvinov
 * @version 2015-05-14
 */
class AAD {


	/**
	 * Computes the Additional Authenticated Data (AAD) for the specified
	 * JWE header.
	 *
	 * @param jweHeader The JWE header. Must not be {@code null}.
	 *
	 * @return The AAD.
	 */
	public static byte[] compute(final JWEHeader jweHeader) {

		return compute(jweHeader.toBase64URL());
	}


	/**
	 * Computes the Additional Authenticated Data (AAD) for the specified
	 * BASE64URL-encoded JWE header.
	 *
	 * @param encodedJWEHeader The BASE64URL-encoded JWE header. Must not
	 *                         be {@code null}.
	 *
	 * @return The AAD.
	 */
	public static byte[] compute(final Base64URL encodedJWEHeader) {

		return encodedJWEHeader.toString().getBytes(Charset.forName("ASCII"));
	}


	/**
	 * Computes the bit length of the specified Additional Authenticated
	 * Data (AAD). Used in AES/CBC/PKCS5Padding/HMAC-SHA2 encryption.
	 *
	 * @param aad The Additional Authenticated Data (AAD). Must not be
	 *            {@code null}.
	 *
	 * @return The computed AAD bit length, as a 64 bit big-endian
	 *         representation (8 byte array).
	 */
	public static byte[] computeLength(final byte[] aad) {

		final int bitLength = ByteUtils.bitLength(aad);
		return ByteBuffer.allocate(8).putLong(bitLength).array();
	}
}

package com.nimbusds.jose.util;


import java.math.BigInteger;

import net.jcip.annotations.Immutable;


/**
 * Base64URL-encoded object.
 *
 * <p>Related specifications:
 *
 * <ul>
 *     <li>RFC 4648.
 * </ul>
 *
 * @author Vladimir Dzhuvinov
 * @version 2013-05-16
 */
@Immutable
public class Base64URL extends Base64 {


	/**
	 * Creates a new Base64URL-encoded object.
	 *
	 * @param base64URL The Base64URL-encoded object value. The value is 
	 *                  not validated for having characters from the 
	 *                  Base64URL alphabet. Must not be {@code null}.
	 */
	public Base64URL(final String base64URL) {

		super(base64URL);
	}


	/**
	 * Overrides {@code Object.equals()}.
	 *
	 * @param object The object to compare to.
	 *
	 * @return {@code true} if the objects have the same value, otherwise
	 *         {@code false}.
	 */
	@Override
	public boolean equals(final Object object) {

		return object != null && 
		       object instanceof Base64URL && 
		       this.toString().equals(object.toString());
	}


	/**
	 * Base64URL-encodes the specified byte array.
	 *
	 * @param bytes The byte array to encode. Must not be {@code null}.
	 *
	 * @return The resulting Base64URL object.
	 */
	public static Base64URL encode(final byte[] bytes) {

		return new Base64URL(Base64Codec.encodeToString(bytes, true));
	}


	/**
	 * Base64URL-encodes the specified big integer, without the sign bit.
	 *
	 * @param bigInt The big integer to encode. Must not be {@code null}.
	 *
	 * @return The resulting Base64URL object.
	 */
	public static Base64URL encode(final BigInteger bigInt) {

		return encode(BigIntegerUtils.toBytesUnsigned(bigInt));
	}


	/**
	 * Base64URL-encodes the specified string.
	 *
	 * @param text The string to encode. Must be in the UTF-8 character set
	 *             and not {@code null}.
	 *
	 * @return The resulting Base64URL object.
	 */
	public static Base64URL encode(final String text) {

		return encode(text.getBytes(CHARSET));
	}
}

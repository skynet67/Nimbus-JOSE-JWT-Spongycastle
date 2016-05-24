package com.nimbusds.jose.util;


import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * Byte utilities.
 *
 * @author Vladimir Dzhuvinov
 * @version 2015-05-12
 */
public class ByteUtils {


	/**
	 * Concatenates the specified byte arrays.
	 *
	 * @param byteArrays The byte arrays to concatenate, may be
	 *                   {@code null}.
	 *
	 * @return The resulting byte array.
	 */
	public static byte[] concat(byte[]... byteArrays) {

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			for (byte[] bytes : byteArrays) {

				if (bytes == null) {
					continue; // skip
				}

				baos.write(bytes);
			}
			return baos.toByteArray();

		} catch (IOException e) {
			// Should never happen
			throw new IllegalStateException(e.getMessage(), e);
		}
	}


	/**
	 * Returns a portion of the specified byte array.
	 *
	 * @param byteArray  The byte array. Must not be {@code null}.
	 * @param beginIndex The beginning index, inclusive. Must be zero or
	 *                   positive.
	 * @param length     The length. Must be zero or positive.
	 *
	 * @return The byte array portion.
	 */
	public static byte[] subArray(byte[] byteArray, int beginIndex, int length) {

		byte[] subArray = new byte[length];
		System.arraycopy(byteArray, beginIndex, subArray, 0, subArray.length);
		return subArray;
	}


	/**
	 * Returns the bit length of the specified byte length.
	 *
	 * @param byteLength The byte length.
	 *
	 * @return The bit length.
	 */
	public static int bitLength(final int byteLength) {

		return byteLength * 8;
	}


	/**
	 * Returns the byte length of the specified byte array.
	 *
	 * @param byteArray The byte array. May be {@code null}.
	 *
	 * @return The bite length, zero if the array is {@code null}.
	 */
	public static int bitLength(final byte[] byteArray) {

		if (byteArray == null) {
			return 0;
		} else {
			return bitLength(byteArray.length);
		}
	}


	/**
	 * Returns the byte length of the specified bit length.
	 *
	 * @param bitLength The bit length.
	 *
	 * @return The byte byte length.
	 */
	public static int byteLength(final int bitLength) {

		return bitLength / 8;
	}
}

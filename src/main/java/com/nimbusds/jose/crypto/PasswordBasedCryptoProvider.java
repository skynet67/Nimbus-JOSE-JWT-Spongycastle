package com.nimbusds.jose.crypto;


import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;


/**
 * The base abstract class for password-based encrypters and decrypters of
 * {@link com.nimbusds.jose.JWEObject JWE objects}.
 *
 * <p>Supports the following key management algorithms:
 *
 * <ul>
 *     <li>{@link com.nimbusds.jose.JWEAlgorithm#PBES2_HS256_A128KW}
 *     <li>{@link com.nimbusds.jose.JWEAlgorithm#PBES2_HS384_A192KW}
 *     <li>{@link com.nimbusds.jose.JWEAlgorithm#PBES2_HS512_A256KW}
 * </ul>
 *
 * <p>Supports the following content encryption algorithms:
 *
 * <ul>
 *     <li>{@link com.nimbusds.jose.EncryptionMethod#A128CBC_HS256}
 *     <li>{@link com.nimbusds.jose.EncryptionMethod#A192CBC_HS384}
 *     <li>{@link com.nimbusds.jose.EncryptionMethod#A256CBC_HS512}
 *     <li>{@link com.nimbusds.jose.EncryptionMethod#A128GCM}
 *     <li>{@link com.nimbusds.jose.EncryptionMethod#A192GCM}
 *     <li>{@link com.nimbusds.jose.EncryptionMethod#A256GCM}
 *     <li>{@link com.nimbusds.jose.EncryptionMethod#A128CBC_HS256_DEPRECATED}
 *     <li>{@link com.nimbusds.jose.EncryptionMethod#A256CBC_HS512_DEPRECATED}
 * </ul>
 *
 * @author Vladimir Dzhuvinov
 * @version 2015-05-26
 */
abstract class PasswordBasedCryptoProvider extends BaseJWEProvider {


	/**
	 * The supported JWE algorithms by the password-based crypto provider
	 * class.
	 */
	public static final Set<JWEAlgorithm> SUPPORTED_ALGORITHMS;


	/**
	 * The supported encryption methods by the password-base crypto
	 * provider class.
	 */
	public static final Set<EncryptionMethod> SUPPORTED_ENCRYPTION_METHODS = ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS;


	static {
		Set<JWEAlgorithm> algs = new LinkedHashSet<>();
		algs.add(JWEAlgorithm.PBES2_HS256_A128KW);
		algs.add(JWEAlgorithm.PBES2_HS384_A192KW);
		algs.add(JWEAlgorithm.PBES2_HS512_A256KW);
		SUPPORTED_ALGORITHMS = Collections.unmodifiableSet(algs);
	}


	/**
	 * The password.
	 */
	private final byte[] password;


	/**
	 * Creates a new password-based encryption / decryption provider.
	 *
	 * @param password The password bytes. Must not be empty or
	 *                 {@code null}.
	 */
	protected PasswordBasedCryptoProvider(final byte[] password) {

		super(SUPPORTED_ALGORITHMS, ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS);

		if (password == null || password.length == 0) {
			throw new IllegalArgumentException("The password must not be null or empty");
		}

		this.password = password;
	}


	/**
	 * Returns the password.
	 *
	 * @return The password bytes.
	 */
	public byte[] getPassword() {

		return password;
	}


	/**
	 * Returns the password.
	 *
	 * @return The password as a UTF-8 encoded string.
	 */
	public String getPasswordString() {

		return new String(password, Charset.forName("UTF-8"));
	}
}

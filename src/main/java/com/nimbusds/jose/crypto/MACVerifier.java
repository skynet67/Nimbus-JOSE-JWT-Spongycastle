package com.nimbusds.jose.crypto;


import java.nio.charset.Charset;
import java.util.Set;

import javax.crypto.SecretKey;

import net.jcip.annotations.ThreadSafe;

import com.nimbusds.jose.*;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.util.Base64URL;


/**
 * Message Authentication Code (MAC) verifier of 
 * {@link com.nimbusds.jose.JWSObject JWS objects}. This class is thread-safe.
 *
 * <p>Supports the following algorithms:
 *
 * <ul>
 *     <li>{@link com.nimbusds.jose.JWSAlgorithm#HS256}
 *     <li>{@link com.nimbusds.jose.JWSAlgorithm#HS384}
 *     <li>{@link com.nimbusds.jose.JWSAlgorithm#HS512}
 * </ul>
 * 
 * @author Vladimir Dzhuvinov
 * @version 2015-06-02
 */
@ThreadSafe
public class MACVerifier extends MACProvider implements JWSVerifier, CriticalHeaderParamsAware {


	/**
	 * The critical header policy.
	 */
	private final CriticalHeaderParamsDeferral critPolicy = new CriticalHeaderParamsDeferral();


	/**
	 * Creates a new Message Authentication (MAC) verifier.
	 *
	 * @param secret The secret. Must be at least 256 bits long and not
	 *               {@code null}.
	 *
	 * @throws JOSEException If the secret length is shorter than the
	 *                       minimum 256-bit requirement.
	 */
	public MACVerifier(final byte[] secret)
		throws JOSEException {

		this(secret, null);
	}


	/**
	 * Creates a new Message Authentication (MAC) verifier.
	 *
	 * @param secretString The secret as a UTF-8 encoded string. Must be at
	 *                     least 256 bits long and not {@code null}.
	 *
	 * @throws JOSEException If the secret length is shorter than the
	 *                       minimum 256-bit requirement.
	 */
	public MACVerifier(final String secretString)
		throws JOSEException {

		this(secretString.getBytes(Charset.forName("UTF-8")));
	}


	/**
	 * Creates a new Message Authentication (MAC) verifier.
	 *
	 * @param secretKey The secret key. Must be at least 256 bits long and
	 *                  not {@code null}.
	 *
	 * @throws JOSEException If the secret length is shorter than the
	 *                       minimum 256-bit requirement.
	 */
	public MACVerifier(final SecretKey secretKey)
		throws JOSEException {

		this(secretKey.getEncoded());
	}


	/**
	 * Creates a new Message Authentication (MAC) verifier.
	 *
	 * @param jwk The secret as a JWK. Must be at least 256 bits long and
	 *            not {@code null}.
	 *
	 * @throws JOSEException If the secret length is shorter than the
	 *                       minimum 256-bit requirement.
	 */
	public MACVerifier(final OctetSequenceKey jwk)
		throws JOSEException {

		this(jwk.toByteArray());
	}


	/**
	 * Creates a new Message Authentication (MAC) verifier.
	 *
	 * @param secret         The secret. Must be at least 256 bits long
	 *                       and not {@code null}.
	 * @param defCritHeaders The names of the critical header parameters
	 *                       that are deferred to the application for
	 *                       processing, empty set or {@code null} if none.
	 *
	 * @throws JOSEException If the secret length is shorter than the
	 *                       minimum 256-bit requirement.
	 */
	public MACVerifier(final byte[] secret,
			   final Set<String> defCritHeaders)
		throws JOSEException {

		super(secret, SUPPORTED_ALGORITHMS);

		critPolicy.setDeferredCriticalHeaderParams(defCritHeaders);
	}


	@Override
	public Set<String> getProcessedCriticalHeaderParams() {

		return critPolicy.getProcessedCriticalHeaderParams();
	}


	@Override
	public Set<String> getDeferredCriticalHeaderParams() {

		return critPolicy.getProcessedCriticalHeaderParams();
	}


	@Override
	public boolean verify(final JWSHeader header,
		              final byte[] signedContent, 
		              final Base64URL signature)
		throws JOSEException {

		if (! critPolicy.headerPasses(header)) {
			return false;
		}

		String jcaAlg = getJCAAlgorithmName(header.getAlgorithm());
		byte[] expectedHMAC = HMAC.compute(jcaAlg, getSecret(), signedContent, getJCAContext().getProvider());
		return ConstantTimeUtils.areEqual(expectedHMAC, signature.decode());
	}
}

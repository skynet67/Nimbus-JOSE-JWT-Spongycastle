package com.nimbusds.jose.crypto;


import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Signature;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;


/**
 * RSA-SSA functions and utilities.
 *
 * @author Vladimir Dzhuvinov
 * @version 2015-05-31
 */
class RSASSA {


	/**
	 * Returns a signer and verifier for the specified RSASSA-based JSON
	 * Web Algorithm (JWA).
	 *
	 * @param alg The JSON Web Algorithm (JWA). Must be supported and not
	 *            {@code null}.
	 *
	 * @return A signer and verifier instance.
	 *
	 * @throws JOSEException If the algorithm is not supported.
	 */
	protected static Signature getSignerAndVerifier(final JWSAlgorithm alg,
							final Provider provider)
		throws JOSEException {

		// The JCE crypto provider uses different alg names

		final String jcaAlg;

		PSSParameterSpec pssSpec = null;

		if (alg.equals(JWSAlgorithm.RS256)) {
			jcaAlg = "SHA256withRSA";
		} else if (alg.equals(JWSAlgorithm.RS384)) {
			jcaAlg = "SHA384withRSA";
		} else if (alg.equals(JWSAlgorithm.RS512)) {
			jcaAlg = "SHA512withRSA";
		} else if (alg.equals(JWSAlgorithm.PS256)) {
			jcaAlg = "SHA256withRSAandMGF1";
			// JWA mandates salt length must equal hash
			pssSpec = new PSSParameterSpec("SHA256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1);
		} else if (alg.equals(JWSAlgorithm.PS384)) {
			jcaAlg = "SHA384withRSAandMGF1";
			// JWA mandates salt length must equal hash
			pssSpec = new PSSParameterSpec("SHA384", "MGF1", MGF1ParameterSpec.SHA384, 48, 1);
		} else if (alg.equals(JWSAlgorithm.PS512)) {
			jcaAlg = "SHA512withRSAandMGF1";
			// JWA mandates salt length must equal hash
			pssSpec = new PSSParameterSpec("SHA512", "MGF1", MGF1ParameterSpec.SHA512, 64, 1);
		} else {
			throw new JOSEException(AlgorithmSupportMessage.unsupportedJWSAlgorithm(alg, RSASSAProvider.SUPPORTED_ALGORITHMS));
		}

		final Signature signature;
		try {
			if (provider != null) {
				signature = Signature.getInstance(jcaAlg, provider);
			} else {
				signature = Signature.getInstance(jcaAlg);
			}
		} catch (NoSuchAlgorithmException e) {
			throw new JOSEException("Unsupported RSASSA algorithm: " + e.getMessage(), e);
		}


		if (pssSpec != null) {
			try {
				signature.setParameter(pssSpec);
			} catch (InvalidAlgorithmParameterException e) {
				throw new JOSEException("Invalid RSASSA-PSS salt length parameter: " + e.getMessage(), e);
			}
		}

		return signature;
	}


	/**
	 * Prevents public instantiation.
	 */
	private RSASSA() {

	}
}

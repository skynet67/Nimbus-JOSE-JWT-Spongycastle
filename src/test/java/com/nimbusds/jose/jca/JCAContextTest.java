package com.nimbusds.jose.jca;


import java.security.Provider;
import java.security.SecureRandom;

import junit.framework.TestCase;


/**
 * Tests the simple JCA context.
 *
 * @author Vladimir Dzhuvinov
 * @version 2015-06-08
 */
public class JCAContextTest extends TestCase {


	public void testDefaultConstructor() {

		JCAContext context = new JCAContext();
		assertNull(context.getProvider());
		assertNotNull(context.getSecureRandom());
	}


	public void testConstructor() {

		Provider provider = new Provider("general", 1.0, "test") {};
		SecureRandom sr = new SecureRandom();

		JCAContext context = new JCAContext(provider, sr);

		assertEquals(provider, context.getProvider());
		assertEquals(sr, context.getSecureRandom());
	}


	public void testSetters() {

		JCAContext context = new JCAContext();

		context.setProvider(new Provider("general", 1.0, "test") {
		});
		assertEquals("general", context.getProvider().getName());

		SecureRandom sr = new SecureRandom();
		context.setSecureRandom(sr);
		assertEquals(sr, context.getSecureRandom());
	}
}

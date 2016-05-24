package com.nimbusds.jose.util;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedInputStream;


/**
 * The default retriever of resources specified by URL. Provides setting of
 * HTTP connect and read timeouts as well as a size limit of the retrieved
 * entity. Caching header directives are not honoured.
 */
@ThreadSafe
public class DefaultResourceRetriever extends AbstractRestrictedResourceRetriever implements RestrictedResourceRetriever {
	
	
	/**
	 * Creates a new resource retriever. The HTTP timeouts and entity size
	 * limit are set to zero (infinite).
	 */
	public DefaultResourceRetriever() {
	
		this(0, 0);	
	}
	
	
	/**
	 * Creates a new resource retriever. The HTTP entity size limit is set
	 * to zero (infinite).
	 *
	 * @param connectTimeout The HTTP connects timeout, in milliseconds, 
	 *                       zero for infinite. Must not be negative.
	 * @param readTimeout    The HTTP read timeout, in milliseconds, zero 
	 *                       for infinite. Must not be negative.
	 */
	public DefaultResourceRetriever(final int connectTimeout, final int readTimeout) {

		this(connectTimeout, readTimeout, 0);
	}


	/**
	 * Creates a new resource retriever.
	 *
	 * @param connectTimeout The HTTP connects timeout, in milliseconds,
	 *                       zero for infinite. Must not be negative.
	 * @param readTimeout    The HTTP read timeout, in milliseconds, zero
	 *                       for infinite. Must not be negative.
	 * @param sizeLimit      The HTTP entity size limit, in bytes, zero for
	 *                       infinite. Must not be negative.
	 */
	public DefaultResourceRetriever(final int connectTimeout, final int readTimeout, final int sizeLimit) {
	
		super(connectTimeout, readTimeout, sizeLimit);
	}


	@Override
	public Resource retrieveResource(final URL url)
		throws IOException {
		
		HttpURLConnection con;
		try {
			con = (HttpURLConnection)url.openConnection();
		} catch (ClassCastException e) {
			throw new IOException("Couldn't open HTTP(S) connection: " + e.getMessage(), e);
		}

		con.setConnectTimeout(getConnectTimeout());
		con.setReadTimeout(getReadTimeout());

		InputStream inputStream = con.getInputStream();

		if (getSizeLimit() > 0) {
			inputStream = new BoundedInputStream(inputStream, getSizeLimit());
		}

		final String content = IOUtils.toString(inputStream);

		// Check HTTP code + message
		final int statusCode = con.getResponseCode();
		final String statusMessage = con.getResponseMessage();

		// Ensure 2xx status code
		if (statusCode > 299 || statusCode < 200) {
			throw new IOException("HTTP " + statusCode + ": " + statusMessage);
		}

		return new Resource(content, con.getContentType());
	}
}

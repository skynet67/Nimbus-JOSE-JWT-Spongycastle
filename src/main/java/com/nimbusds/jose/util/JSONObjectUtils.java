package com.nimbusds.jose.util;


import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;


/**
 * JSON object helper methods for parsing and typed retrieval of member values.
 *
 * @author Vladimir Dzhuvinov
 * @version 2015-04-15
 */
public class JSONObjectUtils {


	/**
	 * Parses a JSON object.
	 *
	 * <p>Specific JSON to Java entity mapping (as per JSON Smart):
	 *
	 * <ul>
	 *     <li>JSON true|false map to {@code java.lang.Boolean}.
	 *     <li>JSON numbers map to {@code java.lang.Number}.
	 *         <ul>
	 *             <li>JSON integer numbers map to {@code long}.
	 *             <li>JSON fraction numbers map to {@code double}.
	 *         </ul>
	 *     <li>JSON strings map to {@code java.lang.String}.
	 *     <li>JSON arrays map to {@code net.minidev.json.JSONArray}.
	 *     <li>JSON objects map to {@code net.minidev.json.JSONObject}.
	 * </ul>
	 *
	 * @param s The JSON object string to parse. Must not be {@code null}.
	 *
	 * @return The JSON object.
	 *
	 * @throws ParseException If the string cannot be parsed to a valid JSON 
	 *                        object.
	 */
	public static JSONObject parse(final String s)
		throws ParseException {

		Object o;

		try {
			o = new JSONParser(JSONParser.USE_HI_PRECISION_FLOAT | JSONParser.ACCEPT_TAILLING_SPACE).parse(s);

		} catch (net.minidev.json.parser.ParseException e) {

			throw new ParseException("Invalid JSON: " + e.getMessage(), 0);
		}

		if (o instanceof JSONObject) {
			return (JSONObject)o;
		} else {
			throw new ParseException("JSON entity is not an object", 0);
		}
	}


	/**
	 * Use {@link #parse(String)} instead.
	 *
	 * @param s The JSON object string to parse. Must not be {@code null}.
	 *
	 * @return The JSON object.
	 *
	 * @throws ParseException If the string cannot be parsed to a valid JSON
	 *                        object.
	 */
	@Deprecated
	public static JSONObject parseJSONObject(final String s)
		throws ParseException {

		return parse(s);
	}


	/**
	 * Gets a generic member of a JSON object.
	 *
	 * @param o     The JSON object. Must not be {@code null}.
	 * @param key   The JSON object member key. Must not be {@code null}.
	 * @param clazz The expected class of the JSON object member value. Must
	 *              not be {@code null}.
	 *
	 * @return The JSON object member value.
	 *
	 * @throws ParseException If the value is missing, {@code null} or not
	 *                        of the expected type.
	 */
	@SuppressWarnings("unchecked")
	private static <T> T getGeneric(final JSONObject o, final String key, final Class<T> clazz)
			throws ParseException {

		if (! o.containsKey(key)) {
			throw new ParseException("Missing JSON object member with key \"" + key + "\"", 0);
		}

		if (o.get(key) == null) {
			throw new ParseException("JSON object member with key \"" + key + "\" has null value", 0);
		}

		Object value = o.get(key);

		if (! clazz.isAssignableFrom(value.getClass())) {
			throw new ParseException("Unexpected type of JSON object member with key \"" + key + "\"", 0);
		}

		return (T)value;
	}


	/**
	 * Gets a boolean member of a JSON object.
	 *
	 * @param o   The JSON object. Must not be {@code null}.
	 * @param key The JSON object member key. Must not be {@code null}.
	 *
	 * @return The member value.
	 *
	 * @throws ParseException If the value is missing, {@code null} or not
	 *                        of the expected type.
	 */
	public static boolean getBoolean(final JSONObject o, final String key)
			throws ParseException {

		return getGeneric(o, key, Boolean.class);
	}


	/**
	 * Gets an number member of a JSON object as {@code int}.
	 *
	 * @param o   The JSON object. Must not be {@code null}.
	 * @param key The JSON object member key. Must not be {@code null}.
	 *
	 * @return The member value.
	 *
	 * @throws ParseException If the value is missing, {@code null} or not
	 *                        of the expected type.
	 */
	public static int getInt(final JSONObject o, final String key)
			throws ParseException {

		return getGeneric(o, key, Number.class).intValue();	
	}


	/**
	 * Gets a number member of a JSON object as {@code long}.
	 *
	 * @param o   The JSON object. Must not be {@code null}.
	 * @param key The JSON object member key. Must not be {@code null}.
	 *
	 * @return The member value.
	 *
	 * @throws ParseException If the value is missing, {@code null} or not
	 *                        of the expected type.
	 */
	public static long getLong(final JSONObject o, final String key)
			throws ParseException {

		return getGeneric(o, key, Number.class).longValue();
	}


	/**
	 * Gets a number member of a JSON object {@code float}.
	 *
	 * @param o   The JSON object. Must not be {@code null}.
	 * @param key The JSON object member key. Must not be {@code null}.
	 *
	 * @return The member value.
	 *
	 * @throws ParseException If the value is missing, {@code null} or not
	 *                        of the expected type.
	 */
	public static float getFloat(final JSONObject o, final String key)
			throws ParseException {

		return getGeneric(o, key, Number.class).floatValue();
	}


	/**
	 * Gets a number member of a JSON object as {@code double}.
	 *
	 * @param o   The JSON object. Must not be {@code null}.
	 * @param key The JSON object member key. Must not be {@code null}.
	 *
	 * @return The member value.
	 *
	 * @throws ParseException If the value is missing, {@code null} or not
	 *                        of the expected type.
	 */
	public static double getDouble(final JSONObject o, final String key)
			throws ParseException {

		return getGeneric(o, key, Number.class).doubleValue();
	}


	/**
	 * Gets a string member of a JSON object.
	 *
	 * @param o   The JSON object. Must not be {@code null}.
	 * @param key The JSON object member key. Must not be {@code null}.
	 *
	 * @return The member value.
	 *
	 * @throws ParseException If the value is missing, {@code null} or not
	 *                        of the expected type.
	 */
	public static String getString(final JSONObject o, final String key)
			throws ParseException {

		return getGeneric(o, key, String.class);
	}


	/**
	 * Gets a string member of a JSON object as {@code java.net.URI}.
	 *
	 * @param o   The JSON object. Must not be {@code null}.
	 * @param key The JSON object member key. Must not be {@code null}.
	 *
	 * @return The member value.
	 *
	 * @throws ParseException If the value is missing, {@code null} or not
	 *                        of the expected type.
	 */
	public static URI getURI(final JSONObject o, final String key)
			throws ParseException {

		try {
			return new URI(getGeneric(o, key, String.class));

		} catch (URISyntaxException e) {

			throw new ParseException(e.getMessage(), 0);
		}
	}


	/**
	 * Gets a JSON array member of a JSON object.
	 *
	 * @param o   The JSON object. Must not be {@code null}.
	 * @param key The JSON object member key. Must not be {@code null}.
	 *
	 * @return The member value.
	 *
	 * @throws ParseException If the value is missing, {@code null} or not
	 *                        of the expected type.
	 */
	public static JSONArray getJSONArray(final JSONObject o, final String key)
			throws ParseException {

		return getGeneric(o, key, JSONArray.class);
	}


	/**
	 * Gets a string array member of a JSON object.
	 *
	 * @param o   The JSON object. Must not be {@code null}.
	 * @param key The JSON object member key. Must not be {@code null}.
	 *
	 * @return The member value.
	 *
	 * @throws ParseException If the value is missing, {@code null} or not
	 *                        of the expected type.
	 */
	public static String[] getStringArray(final JSONObject o, final String key)
			throws ParseException {

		JSONArray jsonArray = getJSONArray(o, key);

		try {
			return jsonArray.toArray(new String[0]);

		} catch (ArrayStoreException e) {

			throw new ParseException("JSON object member with key \"" + key + "\" is not an array of strings", 0);
		}
	}

	/**
	 * Gets a string list member of a JSON object
	 * 
	 * @param o   The JSON object. Must not be {@code null}.
	 * @param key The JSON object member key. Must not be {@code null}.
	 *
	 * @return The member value.
	 *
	 * @throws ParseException If the value is missing, {@code null} or not
	 *                        of the expected type.
	 */
	public static List<String> getStringList(final JSONObject o, final String key) throws ParseException {

		String[] array = getStringArray(o, key);

		return Arrays.asList(array);

	}

	/**
	 * Gets a JSON object member of a JSON object.
	 *
	 * @param o   The JSON object. Must not be {@code null}.
	 * @param key The JSON object member key. Must not be {@code null}.
	 *
	 * @return The member value.
	 *
	 * @throws ParseException If the value is missing, {@code null} or not
	 *                        of the expected type.
	 */
	public static JSONObject getJSONObject(final JSONObject o, final String key)
			throws ParseException {

		return getGeneric(o, key, JSONObject.class);
	}


	/**
	 * Prevents public instantiation.
	 */
	private JSONObjectUtils() {

		// Nothing to do
	}
}


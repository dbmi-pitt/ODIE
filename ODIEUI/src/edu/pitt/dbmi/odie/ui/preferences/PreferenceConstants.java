package edu.pitt.dbmi.odie.ui.preferences;

import java.util.HashMap;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

	public static final String VERSION_PREFIX = "odie.";

	public static final String PROPERTY_KEY_GATE_HOME = VERSION_PREFIX
			+ "gate.home";

	public static final String PROPERTY_KEY_LOG4J_CONFIG_FILE = "log4j.properties.file";
	public static final String PROPERTY_KEY_HIBERNATE_DIALECT = "hibernate.dialect";
	public static final String PROPERTY_KEY_HIBERNATE_DRIVER_CLASS = "hibernate.connection.driver_class";
	public static final String PROPERTY_KEY_HIBERNATE_CONNECTION_URL = "hibernate.connection.url";
	public static final String PROPERTY_KEY_HIBERNATE_CONNECTION_USERNAME = "hibernate.connection.username";
	public static final String PROPERTY_KEY_HIBERNATE_CONNECTION_PASSWORD = "hibernate.connection.password";
	public static final String PROPERTY_KEY_HIBERNATE_HBM2DDL = "hibernate.hbm2ddl.auto";
	public static final String PROPERTY_KEY_HIBERNATE_CONNECTION_POOLSIZE = "hibernate.connection.pool_size";

	public static final String PROPERTY_KEY_REPOSITORY_TABLE_NAME = "repository.table_name";

	public static final String PROPERTY_KEY_TEMP_DIR = "temporary.directory";

	public static final String PROPERTY_KEY_LUCENE_DIR = "luceneindex.directory";

	public static final String PROPERTY_KEY_REDIRECT_STDSTREAMS = "redirect.standardstreams";

	public static final String PROPERTY_KEY_SUGGESTION_THRESHOLD = "suggestion.threshold";

	public static final String PROPERTY_KEY_NO_CHARTS = "ui.nocharts";
	private static HashMap<String, String> prettyNameMap = new HashMap<String, String>();

	static {
		prettyNameMap
				.put(PROPERTY_KEY_GATE_HOME, "GATE Installation Directory");

		prettyNameMap.put(PROPERTY_KEY_HIBERNATE_DIALECT, "Hibernate Dialect");
		prettyNameMap.put(PROPERTY_KEY_HIBERNATE_DRIVER_CLASS,
				"Database Driver");
		prettyNameMap.put(PROPERTY_KEY_HIBERNATE_CONNECTION_URL,
				"Connection URL");
		prettyNameMap.put(PROPERTY_KEY_HIBERNATE_CONNECTION_USERNAME,
				"Username");
		prettyNameMap.put(PROPERTY_KEY_HIBERNATE_CONNECTION_PASSWORD,
				"Password");
		prettyNameMap.put(PROPERTY_KEY_HIBERNATE_CONNECTION_POOLSIZE,
				"Connection Pool Size");
		prettyNameMap.put(PROPERTY_KEY_HIBERNATE_HBM2DDL, "HBM2DDL Policy");
		prettyNameMap.put(PROPERTY_KEY_LOG4J_CONFIG_FILE,
				"Log4J Properties File");
		prettyNameMap.put(PROPERTY_KEY_REDIRECT_STDSTREAMS,
				"Redirect Standard Streams");
		prettyNameMap.put(PROPERTY_KEY_SUGGESTION_THRESHOLD,
				"Suggestion Score Threshold");
		prettyNameMap.put(PROPERTY_KEY_NO_CHARTS,
		"Disable Charts");

	}

	public static String getPrettyName(String key) {
		return (prettyNameMap.get(key) != null ? prettyNameMap.get(key) : key);
	}

}

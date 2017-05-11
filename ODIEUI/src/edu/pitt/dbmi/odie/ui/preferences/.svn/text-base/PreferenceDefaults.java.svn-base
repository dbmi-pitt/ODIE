package edu.pitt.dbmi.odie.ui.preferences;

import java.util.HashMap;

import javax.persistence.Table;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;

import edu.pitt.dbmi.odie.model.LanguageResource;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceDefaults {
	private static HashMap<String, String> valueMap = new HashMap<String, String>();

	static {
		valueMap.put(PreferenceConstants.PROPERTY_KEY_GATE_HOME, Platform
				.getInstallLocation().getURL().getPath().substring(1)
				+ "gate");
		valueMap.put(PreferenceConstants.PROPERTY_KEY_HIBERNATE_DRIVER_CLASS,
				"com.mysql.jdbc.Driver");
		valueMap.put(PreferenceConstants.PROPERTY_KEY_HIBERNATE_CONNECTION_URL,
				"jdbc:mysql://localhost:3306/odie");
		valueMap.put(
				PreferenceConstants.PROPERTY_KEY_HIBERNATE_CONNECTION_POOLSIZE,
				"1");
		valueMap.put(PreferenceConstants.PROPERTY_KEY_REPOSITORY_TABLE_NAME,
				generateRepTableNameDefault());
		valueMap.put(PreferenceConstants.PROPERTY_KEY_TEMP_DIR,
				generateTempDirDefault());
		valueMap.put(PreferenceConstants.PROPERTY_KEY_LUCENE_DIR,
				generateLuceneDirDefault());
		valueMap.put(PreferenceConstants.PROPERTY_KEY_HIBERNATE_HBM2DDL,
				"update");
		valueMap.put(PreferenceConstants.PROPERTY_KEY_LOG4J_CONFIG_FILE,
				generateLog4jFileDefault());
		valueMap.put(PreferenceConstants.PROPERTY_KEY_REDIRECT_STDSTREAMS,
				"true");
		valueMap.put(PreferenceConstants.PROPERTY_KEY_SUGGESTION_THRESHOLD,
				"0.8");
		valueMap.put(PreferenceConstants.PROPERTY_KEY_NO_CHARTS,
		"false");
	}

	public static String getDefaultForProperty(String propertyName) {
		return valueMap.get(propertyName);
	}

	public static HashMap<String, String> getDefaults() {
		return valueMap;
	}

	private static String generateRepTableNameDefault() {
		return LanguageResource.class.getAnnotation(Table.class).name();
	}

	private static String generateTempDirDefault() {
		IPath path = GeneralUtils.getPluginFolderPath();
		path = path.append("/temp");

		return path.toOSString();
	}

	private static String generateLuceneDirDefault() {
		IPath path = GeneralUtils.getPluginFolderPath();
		path = path.append("/indices");

		return path.toOSString();
	}

	private static String generateLog4jFileDefault() {
		IPath path = GeneralUtils.getPluginFolderPath();
		path = path.append("/log4j.properties");

		return path.toOSString();
	}

}

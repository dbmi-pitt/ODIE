package edu.pitt.dbmi.odie.ui.preferences;

import java.util.HashMap;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.pitt.dbmi.odie.ui.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		HashMap<String, String> defaults = PreferenceDefaults.getDefaults();

		for (String key : defaults.keySet()) {
			store.setDefault(key, defaults.get(key));
		}
	}

}

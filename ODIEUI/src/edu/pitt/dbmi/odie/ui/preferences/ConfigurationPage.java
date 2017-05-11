package edu.pitt.dbmi.odie.ui.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import edu.pitt.dbmi.odie.ui.Activator;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class ConfigurationPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public ConfigurationPage() {
		super(GRID);
		setTitle("Configuration");
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		// setDescription("Configuration");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		String key = PreferenceConstants.PROPERTY_KEY_HIBERNATE_DRIVER_CLASS;
		addField(new StringFieldEditor(key, PreferenceConstants
				.getPrettyName(key)
				+ ":", getFieldEditorParent()));

		key = PreferenceConstants.PROPERTY_KEY_HIBERNATE_CONNECTION_URL;
		addField(new StringFieldEditor(key, PreferenceConstants
				.getPrettyName(key)
				+ ":", getFieldEditorParent()));

		key = PreferenceConstants.PROPERTY_KEY_HIBERNATE_CONNECTION_USERNAME;
		addField(new StringFieldEditor(key, PreferenceConstants
				.getPrettyName(key)
				+ ":", getFieldEditorParent()));

		key = PreferenceConstants.PROPERTY_KEY_HIBERNATE_CONNECTION_PASSWORD;
		addField(new StringFieldEditor(key, PreferenceConstants
				.getPrettyName(key)
				+ ":", getFieldEditorParent()));

		// key = PreferenceConstants.PROPERTY_KEY_GATE_HOME;
		// addField(new StringFieldEditor(key,
		// PreferenceConstants.getPrettyName(key) + ":",
		// getFieldEditorParent()));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}
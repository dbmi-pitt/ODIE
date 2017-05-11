package edu.pitt.dbmi.odie.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.pitt.dbmi.odie.ui.Activator;

public class AdvancedPreferencesPage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public AdvancedPreferencesPage() {
		super(GRID);
		setTitle("Advanced Configuration");
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Set advanced preferences");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		// String key = PreferenceConstants.PROPERTY_KEY_HIBERNATE_DIALECT;
		// addField(new StringFieldEditor(key,
		// PreferenceConstants.getPrettyName(key) + ":",
		// getFieldEditorParent()));

		String key = PreferenceConstants.PROPERTY_KEY_HIBERNATE_HBM2DDL;
		addField(new StringFieldEditor(key, PreferenceConstants
				.getPrettyName(key)
				+ ":", getFieldEditorParent()));

		key = PreferenceConstants.PROPERTY_KEY_SUGGESTION_THRESHOLD;
		addField(new StringFieldEditor(key, PreferenceConstants
				.getPrettyName(key)
				+ ":", getFieldEditorParent()));

		key = PreferenceConstants.PROPERTY_KEY_REDIRECT_STDSTREAMS;
		addField(new BooleanFieldEditor(key, PreferenceConstants
				.getPrettyName(key)
				+ ":", getFieldEditorParent()));
		
		key = PreferenceConstants.PROPERTY_KEY_NO_CHARTS;
		addField(new BooleanFieldEditor(key, PreferenceConstants
				.getPrettyName(key)
				+ ":", getFieldEditorParent()));

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
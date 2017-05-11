package edu.pitt.dbmi.odie.ui.editors;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.Form;

public class ConfigurationParameterPage extends FormPage{

	public static final String ID = "edu.pitt.dbmi.odie.ui.editors.ConfigurationParameterPage";
	
	private AnalysisEngineDelegatesMDB block;
	public ConfigurationParameterPage(FormEditor editor) {
		super(editor, ID, "Configuration Parameters" );
		block = new AnalysisEngineDelegatesMDB(this);
	}
	protected void createFormContent(final IManagedForm managedForm) {
		getEditor().getToolkit().decorateFormHeading((Form) managedForm.getForm().getContent());
		
		managedForm.getForm().setText("Configuration Parameters");
		
		block.createContent(managedForm);
	}
	
}
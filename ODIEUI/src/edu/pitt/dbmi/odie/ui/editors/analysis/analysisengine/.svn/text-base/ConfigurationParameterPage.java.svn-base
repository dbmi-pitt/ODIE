package edu.pitt.dbmi.odie.ui.editors.analysis.analysisengine;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.Section;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.ui.editors.DualPanelForm;
import edu.pitt.dbmi.odie.utils.FormUtils;

public class ConfigurationParameterPage extends FormPage {

	public static final String ID = "edu.pitt.dbmi.odie.ui.editors.ConfigurationParameterPage";
	private ConfigParametersSection configSection;

	public ConfigurationParameterPage(FormEditor editor) {
		super(editor, ID, "Analysis Engine");
	}

	protected void createFormContent(final IManagedForm managedForm) {
		getEditor().getToolkit().decorateFormHeading(
				(Form) managedForm.getForm().getContent());

		final DualPanelForm form = FormUtils.setupDividedLayout(getEditor()
				.getToolkit(), managedForm, 50, 50, SWT.VERTICAL);

		managedForm.addPart(new AnalysisEngineSection(getEditor(), form.left,
				Section.DESCRIPTION | Section.TITLE_BAR | Section.EXPANDED));
		
		configSection = new ConfigParametersSection(getEditor(), form.right,
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
						| Section.EXPANDED);
		managedForm.addPart(configSection);
	}

	public void refresh() {
		// if the config page has not been opened, then the form
		// content may not be initialized yet.
		if (configSection != null)
			configSection.refresh();

	}

	// @Override
	// public void setActive(boolean active) {
	// if(configSection.getAnalysisEngine() == null){
	// final Analysis a = ((AnalysisEditorInput)getEditorInput()).getAnalysis();
	//			
	// AnalysisEngine analysisEngine = a.getAnalysisEngine();
	// if(analysisEngine == null){
	// ProgressMonitorDialog pd = GeneralUtils.getProgressMonitorDialog();
	// try {
	// pd.run(false, false, new IRunnableWithProgress(){
	//	
	// @Override
	// public void run(IProgressMonitor monitor)
	// throws InvocationTargetException, InterruptedException {
	// monitor.beginTask("Loading Analysis Engine", IProgressMonitor.UNKNOWN);
	//							
	// try {
	// GeneralUtils.loadAndConfigureAnalysisEngine(a);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// monitor.done();
	// }
	// });
	// } catch (InvocationTargetException e) {
	// e.printStackTrace();
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	// configSection.setAnalysisEngine(a.getAnalysisEngine());
	// configSection.refresh();
	// }
	// super.setActive(active);
	// }

	// @Override
	// public void doSave(IProgressMonitor monitor) {
	// monitor.beginTask("Saving Configuration Parameters", 1);
	// super.doSave(null);
	// monitor.worked(1);
	// monitor.done();
	// }

}
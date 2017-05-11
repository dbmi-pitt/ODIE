/**
 * 
 */
package edu.pitt.dbmi.odie.ui.wizards;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.model.AnalysisEngineMetadata;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.preferences.PreferenceConstants;
import edu.pitt.dbmi.odie.ui.wizards.pages.SelectAEDescriptorPage;
import edu.pitt.dbmi.odie.uima.utils.UIMAUtils;

/**
 * @author Girish Chavan
 * 
 */
public class ImportAnalysisEnginesWizard extends Wizard {
	Logger logger = Logger.getLogger(ImportAnalysisEnginesWizard.class);

	SelectAEDescriptorPage aePage;

	private String aeDescFilePath = null;

	public ImportAnalysisEnginesWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	public ImportAnalysisEnginesWizard(String aeDescFilePath) {
		this();
		this.aeDescFilePath = aeDescFilePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		try {
			AnalysisEngineMetadata aem = aePage.getAnalysisEngineMetadata();
			if (aem == null) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(),
						"Registration Failed",
						"Could not generate metadata for analysis engine.");
				return false;
			}
			Activator.getDefault().getMiddleTier().persist(aem);

			MessageDialog.openInformation(
					Display.getCurrent().getActiveShell(),
					"Registration Successful", "The analysis engine ("
							+ aem.getName()
							+ ") was successfully registered with ODIE.");

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	@Override
	public void addPages() {
		setWindowTitle("Register Analysis Engine");

		aePage = new SelectAEDescriptorPage(aeDescFilePath);
		addPage(aePage);

	}

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		pageContainer.getShell().setSize(485, 600);
	}

}

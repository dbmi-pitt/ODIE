package edu.pitt.dbmi.odie.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.ODIEUtils;
import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.model.Document;
import edu.pitt.dbmi.odie.ui.editors.analysis.AnalysisEditorInput;
import edu.pitt.dbmi.odie.ui.editors.analysis.ChartEditor;
import edu.pitt.dbmi.odie.ui.wizards.pages.AnalysisPropertiesPage;
import edu.pitt.dbmi.odie.ui.wizards.pages.SelectAnalysisEngineWizardPage;
import edu.pitt.dbmi.odie.ui.wizards.pages.SelectAnalysisTypePage;
import edu.pitt.dbmi.odie.ui.wizards.pages.SelectDirectoryWizardPage;
import edu.pitt.dbmi.odie.ui.wizards.pages.SelectLexicalSetWizardPage;
import edu.pitt.dbmi.odie.ui.workers.AnalysisCreator;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class NewAnalysisWizard extends Wizard {

	private SelectAnalysisEngineWizardPage aePage;
	private SelectDirectoryWizardPage folderPage;
	private SelectLexicalSetWizardPage lsPage;
	private AnalysisPropertiesPage propPage;

	Logger logger = Logger.getLogger(this.getClass());
	private SelectAnalysisTypePage aetypePage;
	private String selectedAEType = ODIEConstants.AE_TYPE_NER;

	public NewAnalysisWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public boolean performFinish() {
		Analysis analysis;
		try {
			analysis = createAnalysisObject();
			AnalysisCreator worker = new AnalysisCreator(analysis);

			getContainer().run(true, true, worker);

			if (!worker.isSuccessful())
				return false;

		} catch (Exception e1) {
			e1.printStackTrace();
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					"Analysis Creation Failed", e1.getMessage());
			return false;
		}

		GeneralUtils.refreshViews();

		if (MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
				"Analysis Created", "Do you want to run the analysis now?")) {

			// the analysis is run in separate thread. it blocks until it is
			// complete or cancelled.
			try {
				GeneralUtils.runAnalysis(analysis);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		return true;
	}

	private Analysis createAnalysisObject() throws Exception {
		Analysis analysis = new Analysis();
		analysis.setName(propPage.getName());

		String dbname = ODIEUtils
				.convertToValidDatabaseName(ODIEConstants.ANALYSIS_SPACE_PREFIX
						+ analysis.getName());
		analysis.setDatabaseName(dbname);

		analysis.setDescription(propPage.getDescription());

		List<AnalysisDocument> dalist = new ArrayList<AnalysisDocument>();
		for (Document doc : folderPage.getDocuments()) {
			dalist.add(new AnalysisDocument(analysis, doc));
		}
		analysis.setAnalysisDocuments(dalist);
		analysis.setAnalysisEngineMetadata(aePage.getAnalysisEngineMetadata());
		String type = aePage.getAnalysisEngineMetadata().getType();
		if (type.equals(ODIEConstants.AE_TYPE_NER)
				|| type.equals(ODIEConstants.AE_TYPE_OE)) {
			analysis.setLexicalSet(lsPage.getLexicalSet());
		}

		return analysis;
	}

	// /**
	// *
	// */
	// private void updateUI(Analysis analysis) {
	//		
	// IWorkbenchPage page = PlatformUI.getWorkbench()
	// .getActiveWorkbenchWindow().getActivePage();
	//		
	// IViewPart analysisView = page.findView(AnalysesView.ID);
	//		
	// if(analysisView!=null){
	// ((AnalysesView)analysisView).refresh();
	// } else
	// try {
	// analysisView = page.showView(AnalysesView.ID);
	// } catch (PartInitException e) {
	// e.printStackTrace();
	// }
	//
	// ((AnalysesView)analysisView).setSelection(analysis);
	// // openEditor(analysis);
	//		
	// }

	@Override
	public void addPages() {
		setWindowTitle("New Analysis");

		aetypePage = new SelectAnalysisTypePage();
		addPage(aetypePage);

		aePage = new SelectAnalysisEngineWizardPage();
		addPage(aePage);

		lsPage = new SelectLexicalSetWizardPage();
		addPage(lsPage);

		folderPage = new SelectDirectoryWizardPage();
		addPage(folderPage);

		propPage = new AnalysisPropertiesPage();
		addPage(propPage);

	}

	@Override
	public boolean canFinish() {
		if (selectedAEType.equals(ODIEConstants.AE_TYPE_OTHER)
				&& propPage.isPageComplete())
			return true;
		else
			return super.canFinish();
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		pageContainer.getShell().setSize(485, 600);
	}

	//
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page instanceof SelectAnalysisTypePage) {
			selectedAEType = ((SelectAnalysisTypePage) page).getSelectedType();

			Display.getCurrent().asyncExec(new Runnable() {
				@Override
				public void run() {
					aePage.setAnalysisEngineType(selectedAEType);
				}
			});

		} else if (page instanceof SelectAnalysisEngineWizardPage) {
			if (selectedAEType.equals(ODIEConstants.AE_TYPE_OTHER))
				return getPage(SelectDirectoryWizardPage.PAGE_NAME);
		}

		return super.getNextPage(page);

	}

	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {
		if (page instanceof SelectDirectoryWizardPage) {
			if (selectedAEType.equals(ODIEConstants.AE_TYPE_OTHER))
				return getPage(SelectAnalysisEngineWizardPage.PAGE_NAME);
		}
		return super.getPreviousPage(page);
	}

}

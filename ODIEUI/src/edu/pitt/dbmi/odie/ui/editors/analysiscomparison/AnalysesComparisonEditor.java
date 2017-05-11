package edu.pitt.dbmi.odie.ui.editors.analysiscomparison;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import edu.pitt.dbmi.odie.ui.editors.analysis.summary.SummaryPage;

public class AnalysesComparisonEditor extends FormEditor {

	public static final String ID = "edu.pitt.dbmi.odie.ui.editors.AnalysesComparisonEditor";
	private SummaryPage summaryPage;
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		setPartName(input.getName());

	}

	@Override
	protected void addPages() {
		try {
			summaryPage = new SummaryPage(this);
			addPage(summaryPage);
			
		} catch (PartInitException e) {
			e.printStackTrace();
		}

	}

	public void refresh() {
	}
	
	protected void createPages() {
		super.createPages();
	        if (getPageCount() == 1 &&
			getContainer() instanceof CTabFolder) {
	            ((CTabFolder) getContainer()).setTabHeight(0);
	        }
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}
}

package edu.pitt.dbmi.odie.ui.editors.ontology;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import edu.pitt.ontology.IClass;

public class OntologyEditor extends FormEditor {

	public static final String ID = "edu.pitt.dbmi.odie.ui.editors.OntologyEditor";
	private OntologyPage ontologyPage;

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		setPartName(input.getName());

	}

	@Override
	protected void addPages() {
		ontologyPage = new OntologyPage(this);
		try {
			addPage(ontologyPage);
		} catch (PartInitException e) {
			e.printStackTrace();
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
		return false;
	}

	protected void createPages() {
		super.createPages();
		if (getPageCount() == 1 && getContainer() instanceof CTabFolder) {
			((CTabFolder) getContainer()).setTabHeight(0);
		}
	}

	public void select(IClass c) {
		ontologyPage.select(c);

	}
}

/**
 * 
 */
package edu.pitt.dbmi.odie.ui.wizards;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.ODIEUtils;
import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.LanguageResource;
import edu.pitt.dbmi.odie.model.LexicalSet;
import edu.pitt.dbmi.odie.ui.wizards.pages.NewLexicalSetPage;
import edu.pitt.dbmi.odie.ui.workers.LexicalSetWorker;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.IResource;

/**
 * @author Girish Chavan
 * 
 */
public class NewLexicalSetWizard extends Wizard {
	Logger logger = Logger.getLogger(NewLexicalSetWizard.class);

	NewLexicalSetPage lexicalSetPage;

	private LexicalSet newLexicalSet;

	public NewLexicalSetWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	public LexicalSet getNewLexicalSet() {
		return newLexicalSet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		LexicalSet lexicalSet = createLexicalSetObject(
				lexicalSetPage.getName(), lexicalSetPage.getDescription(),
				lexicalSetPage.getSelectedResources());

		try {
			LexicalSetWorker worker = new LexicalSetWorker(lexicalSet);
			getContainer().run(true, true, worker);

			if (worker.isSuccessful()) {
				MessageDialog.openInformation(Display.getCurrent()
						.getActiveShell(), "Vocabulary created",
						"Successfully created a new vocabulary named '"
								+ lexicalSet.getName() + "'");
				return true;
			} else {
				MessageDialog.openError(Display.getCurrent().getActiveShell(),
						"Vocabulary creation failed",
						"An error occured when trying to create the '"
								+ lexicalSet.getName() + "' vocabulary");
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					"Vocabulary creation failed",
					"An error occured when trying to create the '"
							+ lexicalSet.getName() + "' vocabulary\n"
							+ e.getMessage());
			return false;
		}

	}

	public static LexicalSet createLexicalSetObject(String name,
			String description, List<IResource> resources) {
		LexicalSet lexicalSet = new LexicalSet();
		lexicalSet.setName(name);
		lexicalSet.setDescription(description);

		MiddleTier mt = edu.pitt.dbmi.odie.ui.Activator.getDefault()
				.getMiddleTier();
		for (IResource r : resources) {
			if (r instanceof IOntology) {
				LanguageResource lr = mt.getLanguageResourceForURI(r.getURI());
				if(lr == null)
					return null;
				lexicalSet.addLanguageResource(lr);
				
			} else if (r instanceof IClass) {
				lexicalSet.addLanguageResource(mt.getLanguageResourceForURI(r
						.getOntology().getURI()), r.getURI());
			}
		}
		try {
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return lexicalSet;
	}

	@Override
	public void addPages() {
		setWindowTitle("New Vocabulary");

		lexicalSetPage = new NewLexicalSetPage();
		addPage(lexicalSetPage);

	}

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		pageContainer.getShell().setSize(485, 600);
	}

}

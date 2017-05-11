/**
 * 
 */
package edu.pitt.dbmi.odie.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import edu.pitt.dbmi.odie.model.LexicalSet;
import edu.pitt.dbmi.odie.ui.handlers.NewLexicalSetHandler;
import edu.pitt.dbmi.odie.ui.views.OntologiesView;
import edu.pitt.dbmi.odie.ui.wizards.pages.SelectBioportalOntologyPage;
import edu.pitt.dbmi.odie.ui.wizards.pages.SelectOntologyFilePage;
import edu.pitt.dbmi.odie.ui.wizards.pages.SelectOntologySourcePage;
import edu.pitt.dbmi.odie.ui.workers.BioportalOntologyImporter;
import edu.pitt.dbmi.odie.ui.workers.LexicalSetWorker;
import edu.pitt.dbmi.odie.ui.workers.OntologyImporter;
import edu.pitt.dbmi.odie.utils.GeneralUtils;
import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.IResource;

/**
 * @author Girish Chavan
 * 
 */
public class ImportOntologiesWizard extends Wizard {

	SelectOntologySourcePage sourcePage;
	private SelectOntologyFilePage filePage;
	private SelectBioportalOntologyPage bioportalPage;
	private boolean promptForIndexfinderSet = false;

	public ImportOntologiesWizard(boolean promptForIndexfinderSet) {
		super();
		this.promptForIndexfinderSet = promptForIndexfinderSet;
		setNeedsProgressMonitor(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {

		IWizardPage p = getContainer().getCurrentPage();

		try {
			List<IOntology> iolist = null;
			if (p instanceof SelectOntologyFilePage) {
				List<URI> uriList = new ArrayList<URI>();
				uriList.add(((SelectOntologyFilePage) p).getSelectedFileURI());
				OntologyImporter oi = new OntologyImporter(uriList);
				getContainer().run(true, true, oi);

				iolist = oi.getImportedOntologies();
			} else if (p instanceof SelectBioportalOntologyPage) {
				BioportalOntologyImporter boi = new BioportalOntologyImporter(
						((SelectBioportalOntologyPage) p)
								.getSelectedOntologies());
				getContainer().run(true, true, boi);
				iolist = boi.getImportedOntologies();
			}

			if (iolist != null && iolist.size() > 0) {
				for(IOntology o:iolist){
					List<IResource> l = new ArrayList<IResource>();
					l.add(o);
					LexicalSet lexicalSet = NewLexicalSetWizard.createLexicalSetObject(
							o.getName(),o.getDescription(),l);
	
					//TODO There seems to be a problem with owl file based ontologies.
					//The middletier does not find the languageResource entry for the 
					//ontology even though it exists in the database. It finds the entry
					//correctly when the ontology has been imported from bioportal
					if(lexicalSet != null){
						try {
							LexicalSetWorker worker = new LexicalSetWorker(lexicalSet);
							getContainer().run(true, true, worker);
		
							if (worker.isSuccessful()) {
								MessageDialog.openInformation(Display.getCurrent()
										.getActiveShell(), "Vocabulary created",
										"Successfully created a new vocabulary named '"
												+ lexicalSet.getName() + "'");
							} else {
								MessageDialog.openError(Display.getCurrent().getActiveShell(),
										"Vocabulary creation failed",
										"An error occured when trying to create the '"
												+ lexicalSet.getName() + "' vocabulary");
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
				}

				boolean doit = MessageDialog.openQuestion(Display.getCurrent()
						.getActiveShell(), "Import Successful",
						"The ontology search feature will not function until ODIE is restarted.\n"
								+ "Restart now?");
				if (doit) {
					GeneralUtils.restartODIE();
					return true;
				} else {
					GeneralUtils.refreshOntologiesView();
					return true;
				}
			} else {
				GeneralUtils
						.showErrorDialog(
								"Failed to import ontology",
								"There was an error while importing the selected ontology. See the log files for details.");
				return false;
			}

		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public void addPages() {
		setWindowTitle("Import Ontologies");

		sourcePage = new SelectOntologySourcePage();
		addPage(sourcePage);

		filePage = new SelectOntologyFilePage();
		addPage(filePage);

		bioportalPage = new SelectBioportalOntologyPage();
		addPage(bioportalPage);

	}

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		pageContainer.getShell().setSize(465, 600);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page instanceof SelectOntologySourcePage) {
			String selectedOption = ((SelectOntologySourcePage) page)
					.getSelectedOption();
			if (selectedOption == null)
				;
			else if (selectedOption
					.equals(SelectOntologySourcePage.FILE_IMPORT_OPTION))
				return getPage(SelectOntologyFilePage.PAGE_NAME);
			else if (selectedOption
					.equals(SelectOntologySourcePage.BIOPORTAL_IMPORT_OPTION)) {
				SelectBioportalOntologyPage p = (SelectBioportalOntologyPage) getPage(SelectBioportalOntologyPage.PAGE_NAME);
				p.prepareForDisplay();
				return p;
			}

			return getPage(SelectOntologyFilePage.PAGE_NAME);
		}
		else if(page instanceof SelectOntologyFilePage)
			 return null;
		else {
			return super.getNextPage(page);
		}
	}

	@Override
	public boolean canFinish() {

		IWizardPage p = getContainer().getCurrentPage();
		if (p instanceof SelectOntologySourcePage)
			return false;

		else
			return p.isPageComplete();
	}

}

package edu.pitt.dbmi.odie.ui.wizards.pages;

import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.ontology.IOntology;

public class SelectEnrichmentOntologyPage extends WizardPage {

	public static final String DESC = "Pick the ontology you want to suggest a new concept for";
	private List<IOntology> ontologies;

	public SelectEnrichmentOntologyPage(List<IOntology> olist) {
		super("SelectEnrichmentOntologyPage");
		this.ontologies = olist;

		setTitle("Select an ontology");
		setDescription(DESC);
	}

	SelectionListener radioSelectionListener = new SelectionListener() {

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			handleEvent(e);
		}

		private void handleEvent(SelectionEvent e) {
			selectedOntology = (IOntology) ((Button) e.getSource()).getData();
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			handleEvent(e);
		}

	};
	private IOntology selectedOntology = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);

		GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = Aesthetics.INTRAGROUP_SPACING;
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);
		setControl(container);

		for (IOntology o : ontologies) {
			Button button = new Button(container, SWT.RADIO);
			button.setText(o.getName());
			button.setData(o);
			button.addSelectionListener(radioSelectionListener);
		}
	}

	public IOntology getSelectedOntology() {
		return selectedOntology;
	}

}

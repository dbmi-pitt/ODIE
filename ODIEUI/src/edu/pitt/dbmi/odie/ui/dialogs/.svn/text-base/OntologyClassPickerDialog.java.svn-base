/**
 * @author Girish Chavan
 *
 * Sep 10, 2008
 */
package edu.pitt.dbmi.odie.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import edu.pitt.dbmi.odie.ui.editors.ontology.OntologyClassPickerPanel;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;

public class OntologyClassPickerDialog extends Dialog {
	private OntologyClassPickerPanel ontologyClassPickerPanel;
	private IOntology[] ontologies;
	protected IClass selectedClass;

	public OntologyClassPickerDialog(Shell activeShell, IOntology[] ontologies) {
		super(activeShell);
		this.ontologies = ontologies;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Select parent concept");
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);

		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, true);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		container.setLayout(new GridLayout(1, false));
		ontologyClassPickerPanel = new OntologyClassPickerPanel(container,
				ontologies);
		ontologyClassPickerPanel
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						selectedClass = (IClass) ((StructuredSelection) event
								.getSelection()).getFirstElement();
					}
				});
		return container;
	}

	public IClass getSelectedClass() {
		return selectedClass;
	}
}

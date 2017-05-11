package edu.pitt.dbmi.odie.ui.editors.ontology;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;

public class OntologyClassPickerPanel implements ISelectionProvider {

	private static final String SEARCH_ONTOLOGY_MSG = "Search ontology";
	private OntologyTree ontologyTree;
	private OntologyContentProposalProvider ontologyContentProposalProvider;
	private IOntology[] ontologies;
	private Text searchTextbox;

	public OntologyClassPickerPanel(Composite container, IOntology[] ontologies) {
		this.ontologies = ontologies;
		if (this.ontologies == null || ontologies.length == 0)
			throw new IllegalArgumentException("Needs a non-empty IOntology[]");
		buildGUI(container);
	}

	private void buildGUI(Composite container) {
		if (ontologies.length > 1) {
			addOntologyPickerCombo(container);
		}

		searchTextbox = new Text(container, SWT.BORDER);
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		searchTextbox.setLayoutData(gd);

		addContentAssist(searchTextbox);
		ontologyTree = new OntologyTree(container);

		changeOntologyTo(ontologies[0]);
	}

	private void addOntologyPickerCombo(Composite container) {
		Combo ontologyPickerCombo = new Combo(container, SWT.READ_ONLY
				| SWT.DROP_DOWN);

		for (IOntology o : ontologies) {
			ontologyPickerCombo.add(o.getName());
		}

		ontologyPickerCombo.select(0);

		ontologyPickerCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleSelection(e);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				handleSelection(e);
			}

			private void handleSelection(SelectionEvent e) {
				Combo c = ((Combo) e.getSource());
				String s = c.getItem(c.getSelectionIndex());

				IOntology o = getOntologyForName(s);
				changeOntologyTo(o);
			}

			private IOntology getOntologyForName(String s) {
				for (IOntology o : ontologies)
					if (o.getName().equals(s))
						return o;

				// should never fail to find an ontology
				assert false;
				return null;
			}
		});
	}

	protected void changeOntologyTo(IOntology o) {
		ontologyTree.setInput(o);
		ontologyContentProposalProvider.setOntology(o);
	}

	public ISelectionProvider getOntologyTree() {
		return ontologyTree;
	}

	private void addContentAssist(final Text searchTextbox) {
		searchTextbox.setText(SEARCH_ONTOLOGY_MSG);

		searchTextbox.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				handleSearchBoxFocusGained();
			}

			@Override
			public void focusLost(FocusEvent e) {
				handleSearchBoxFocusLost();
			}

		});

		ontologyContentProposalProvider = new OntologyContentProposalProvider(
				ontologies[0]);

		ContentProposalAdapter adapter = new ContentProposalAdapter(
				searchTextbox, new TextContentAdapter(),
				ontologyContentProposalProvider, null, null);
		adapter
				.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		adapter.addContentProposalListener(new IContentProposalListener() {

			@Override
			public void proposalAccepted(IContentProposal proposal) {
				IClass c = ((ClassContentProposal) proposal).getProposalClass();
				ontologyTree.setSelection(new StructuredSelection(c));
			}

		});
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		ontologyTree.addSelectionChangedListener(listener);

	}

	@Override
	public ISelection getSelection() {
		return ontologyTree.getSelection();
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		ontologyTree.removeSelectionChangedListener(listener);

	}

	@Override
	public void setSelection(ISelection selection) {
		IClass c = (IClass) ((StructuredSelection) selection).getFirstElement();
		
//		TreePath tp = constructTreePath(c);
//		ontologyTree.setSelection(new StructuredSelection(tp));
		ontologyTree.setSelection(selection);
		handleSearchBoxFocusGained();
		searchTextbox.setText(c.getName());

	}

	private TreePath constructTreePath(IClass c) {
		Stack<IClass> ancestors = new Stack<IClass>();

		IClass cc = c;
		ancestors.push(c);
		
		while(cc.getDirectSuperClasses().length>0){
			cc = cc.getDirectSuperClasses()[0];
			ancestors.push(cc);
		}
		
		List<IClass> reverseAncestry = new ArrayList<IClass>();
		
		//pop OWLThing
		ancestors.pop();
		while(ancestors.size()>0){
			reverseAncestry.add(ancestors.pop());
		}
			
		TreePath tp = new TreePath(reverseAncestry.toArray());
		return tp;
	}

	private void handleSearchBoxFocusGained() {
		if (searchTextbox.getText().equals(SEARCH_ONTOLOGY_MSG)) {
			searchTextbox.setText("");
			searchTextbox.setForeground(Aesthetics.getForegroundColor());
		}
	}

	private void handleSearchBoxFocusLost() {
		if (searchTextbox.getText().trim().length() == 0) {
			searchTextbox.setText(SEARCH_ONTOLOGY_MSG);
			searchTextbox.setForeground(Aesthetics.getFilterTextColor());
		}
	}
}

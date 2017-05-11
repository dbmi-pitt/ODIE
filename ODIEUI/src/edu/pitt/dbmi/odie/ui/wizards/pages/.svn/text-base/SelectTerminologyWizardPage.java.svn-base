package edu.pitt.dbmi.odie.ui.wizards.pages;

import java.util.List;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.terminology.Terminology;

public class SelectTerminologyWizardPage extends WizardPage {

	private StyledText descTextBox;
	private CheckboxTreeViewer ontologyTree;

	public SelectTerminologyWizardPage() {
		super("selectSynonymSource");
		setTitle("Synonym Source");
		setDescription("Select a synonym source");

	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		container.setLayout(gl);

		// Ontology List Group
		Group synlistGrp = new Group(container, SWT.NONE);
		synlistGrp.setText("Synonym Sources");
		GridData gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = false;
		gd.grabExcessHorizontalSpace = true;
		// gd.minimumHeight = 100;
		synlistGrp.setLayoutData(gd);

		RowLayout rl = new RowLayout();
		rl.type = SWT.VERTICAL;
		synlistGrp.setLayout(rl);

		// syn list
		SelectionListener l = new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (e.widget.getData() instanceof Terminology) {
					descTextBox.setText(((Terminology) e.widget.getData())
							.getDescription());
				}

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.widget.getData() instanceof Terminology) {
					descTextBox.setText(((Terminology) e.widget.getData())
							.getDescription());
				}
			}

		};

		List<Terminology> list = Activator.getDefault().getMiddleTier()
				.getTerminologies();

		if (list.isEmpty()) {
			Label empty = new Label(synlistGrp, SWT.NULL);
			empty.setText("No terminologies found");

		} else {
			for (Terminology t : list) {
				Button tButton = new Button(synlistGrp, SWT.RADIO);
				tButton.setText(t.getName());
				tButton.setData(t);
				tButton.addSelectionListener(l);
			}
		}

		// /////// Details group
		Group descGrp = new Group(container, SWT.NONE);
		descGrp.setLayout(new FillLayout());
		descGrp.setText("Description");

		gd = new GridData();
		gd.verticalAlignment = SWT.TOP;
		gd.horizontalAlignment = GridData.FILL;
		gd.heightHint = 150;
		gd.minimumHeight = 150;

		gd.grabExcessVerticalSpace = false;
		gd.grabExcessHorizontalSpace = true;
		descGrp.setLayoutData(gd);

		descTextBox = new StyledText(descGrp, SWT.WRAP | SWT.READ_ONLY
				| SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		descTextBox
				.setText("Select a synonym source above to see its description");

		setControl(container);
	}

	public Terminology getSelectedTerminology() {
		// TODO Auto-generated method stub
		return null;
	}
}

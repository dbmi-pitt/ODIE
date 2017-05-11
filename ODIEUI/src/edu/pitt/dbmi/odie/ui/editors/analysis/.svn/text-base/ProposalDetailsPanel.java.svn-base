package edu.pitt.dbmi.odie.ui.editors.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.dialogs.OntologyClassPickerDialog;
import edu.pitt.dbmi.odie.utils.GeneralUtils;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;

public class ProposalDetailsPanel {
	private static final String NO_PARENT_SET = "No parent set";
	public static final int NAME_CHANGED_EVENT = 1;
	public static final int SYNONYMS_CHANGED_EVENT = 2;
	public static final int PARENT_CHANGED_EVENT = 3;
	Composite container;
	private Text nameText;
	private Text synText;
	List<String> synonymList = new ArrayList<String>();
	private ListViewer listViewer;
	protected String name;
	private Button addButton;
	private Button remButton;

	ListenerList listeners = new ListenerList();
	private boolean nameReadOnly;
	private Label parentLabel;
	private Button changeParentButton;
	private boolean isLoadingClass;
	protected IOntology[] ontologies;
	protected IClass parentClass;
	private IClass currentClass;

	public ProposalDetailsPanel(Composite parent, IOntology[] ontologies,
			boolean nameReadOnly) {
		super();
		this.nameReadOnly = nameReadOnly;
		this.container = parent;
		this.ontologies = ontologies;
		buildGUI();
		hookListeners();
	}

	private void buildGUI() {
		Label nameLabel = new Label(container, SWT.NONE);
		nameLabel.setText("Name");
		nameLabel
				.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		nameText = new Text(container, SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		nameText.setEditable(!nameReadOnly);

		createSynonymsGroup();
		createParentGroup();

	}

	private void createParentGroup() {
		Group parentGrp = new Group(container, SWT.SHADOW_ETCHED_IN);
		parentGrp.setText("Parent");
		parentGrp.setLayout(new GridLayout(2, false));

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		parentGrp.setLayoutData(gd);

		parentLabel = new Label(parentGrp, SWT.NONE);
		parentLabel.setText(NO_PARENT_SET);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		parentLabel.setLayoutData(gd);

		changeParentButton = new Button(parentGrp, SWT.PUSH);
		changeParentButton.setText("Change...");

	}

	private void createSynonymsGroup() {
		Group synGrp = new Group(container, SWT.SHADOW_ETCHED_IN);
		synGrp.setText("Synonyms");
		synGrp.setLayout(new GridLayout(2, false));

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		synGrp.setLayoutData(gd);

		synText = new Text(synGrp, SWT.BORDER);
		synText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		addButton = new Button(synGrp, SWT.PUSH);
		addButton.setText("Add");
		addButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		addButton.setEnabled(false);

		listViewer = new ListViewer(synGrp, SWT.V_SCROLL | SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 100;
		gd.widthHint = 150;
		listViewer.getList().setLayoutData(gd);

		remButton = new Button(synGrp, SWT.PUSH);
		remButton.setText("Remove");
		remButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		remButton.setEnabled(false);

		Composite spacer = new Composite(container, SWT.NULL);
		gd = new GridData();
		gd.minimumHeight = 10;
		gd.heightHint = 10;
		spacer.setLayoutData(gd);

		listViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public Object[] getElements(Object inputElement) {
				return synonymList.toArray();
			}

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

		});

		listViewer.setLabelProvider(new LabelProvider() {

			@Override
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getText(Object element) {
				return element.toString();
			}
		});

		listViewer.setInput(synonymList);
	}

	private void hookListeners() {
		addButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				handleSelection();
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleSelection();
			}

			private void handleSelection() {
				if (synText.getText().length() > 0) {
					synonymList.add(synText.getText());
					synText.setText("");
					listViewer.refresh();

					Event ev = new Event();
					ev.type = SYNONYMS_CHANGED_EVENT;
					fireDetailsChangedEvent(ev);
				}

			}

		});

		remButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				handleSelection();
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleSelection();
			}

			private void handleSelection() {
				if (!listViewer.getSelection().isEmpty()) {
					String s = (String) ((StructuredSelection) listViewer
							.getSelection()).getFirstElement();
					synonymList.remove(s);
					listViewer.refresh();

					Event ev = new Event();
					ev.type = SYNONYMS_CHANGED_EVENT;
					fireDetailsChangedEvent(ev);
				}
			}

		});

		listViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				remButton.setEnabled(!listViewer.getSelection().isEmpty());
			}

		});

		nameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (isLoadingClass)
					return;

				name = nameText.getText();
				Event ev = new Event();
				ev.type = NAME_CHANGED_EVENT;
				fireDetailsChangedEvent(ev);
			}
		});

		synText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				addButton.setEnabled(synText.getText().length() > 0);
			}
		});

		changeParentButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				OntologyClassPickerDialog d = new OntologyClassPickerDialog(
						Activator.getDefault().getWorkbench().getDisplay()
								.getActiveShell(), ontologies);
				d.setBlockOnOpen(true);

				// Open the main window
				if (d.open() == IDialogConstants.OK_ID) {
					if(d.getSelectedClass().equals(currentClass)){
						GeneralUtils.showErrorDialog("Cyclic relationship", 
								"A child concept cannot be it's own parent. Please select another class.");
						widgetSelected(e);
					}
					else{
						parentClass = d.getSelectedClass();
						parentLabel.setText(parentClass.getName());
	
						Event ev = new Event();
						ev.type = PARENT_CHANGED_EVENT;
						fireDetailsChangedEvent(ev);
					}
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	public String getName() {
		return name;
	}

	public List<String> getSynonyms() {
		return synonymList;
	}

	public void loadClass(IClass proposal) {
		this.currentClass = proposal;
		isLoadingClass = true;
		nameText.setText(proposal.getName());

		if (proposal.getSuperClasses().length > 0) {
			parentClass = proposal.getSuperClasses()[0];
			parentLabel.setText(parentClass.getName());
		} else {
			parentClass = null;
			parentLabel.setText(NO_PARENT_SET);
		}
		synonymList.clear();
		synonymList.addAll(Arrays.asList(proposal.getLabels()));
		listViewer.refresh();
		isLoadingClass = false;
	}

	protected void fireDetailsChangedEvent(final Event e) {
		Object[] ls = listeners.getListeners();
		for (int i = 0; i < ls.length; ++i) {
			final Listener l = (Listener) ls[i];
			SafeRunnable.run(new SafeRunnable() {
				public void run() {
					l.handleEvent(e);
				}
			});
		}
	}

	public void addDetailsChangedListener(Listener listener) {
		listeners.add(listener);

	}

	public void removeDetailsChangedListener(Listener listener) {
		listeners.remove(listener);

	}

	// TODO Auto-generated method stub
	public IClass getParentClass() {
		return parentClass;
	}

	public void clear() {
		isLoadingClass = true;
		nameText.setText("");

		parentClass = null;
		parentLabel.setText(NO_PARENT_SET);

		synonymList.clear();
		listViewer.refresh();
		isLoadingClass = false;
	}
}

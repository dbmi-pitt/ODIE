package edu.pitt.dbmi.odie.ui.editors;

import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import edu.pitt.dbmi.odie.ui.editors.providers.ConfigParamsTableContentProvider;
import edu.pitt.dbmi.odie.ui.editors.providers.ConfigParamsTableLabelProvider;
import edu.pitt.dbmi.odie.ui.views.DocumentsMessages;
import edu.pitt.dbmi.odie.utils.FormUtils;

public class AnalysisEngineConfigParamsPage implements IDetailsPage {

	private IManagedForm form;
	private TableViewer tableViewer;

	@Override
	public void createContents(Composite parent) {
		Section section = new Section(parent,Section.DESCRIPTION|Section.TITLE_BAR);
		section.setText("Configuration Parameters");
		section.setDescription("All configuration parameters for the selected Analysis Engine.");
		section.marginHeight = 10;
		section.marginWidth = 5;
		
		FormToolkit toolkit = form.getToolkit();
		
		Composite sectionClient = toolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		sectionClient.setLayout(layout);
		FormUtils.setMargins(sectionClient, 5, 0);
		
		Table table = createTable(sectionClient);
		createColumns(table);
		
		tableViewer = new TableViewer(table);
		
		tableViewer.setContentProvider(new ConfigParamsTableContentProvider());
		tableViewer.setLabelProvider(new ConfigParamsTableLabelProvider());
		
	}
	
	private  Table createTable(Composite parent) {
		Table table = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI
				| SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		
		return table;
		// table.setLayout(new TableLayout());
	}

	private String columnHeaders[] = { "Name", "Value"};

	private ColumnLayoutData columnLayouts[] = { new ColumnWeightData(75), new ColumnWeightData(75)};

	
	void createColumns(Table table) {
		TableLayout layout = new TableLayout();
		table.setLayout(layout);
		table.setHeaderVisible(true);

		for (int i = 0; i < columnHeaders.length; i++) {
			TableColumn tc = new TableColumn(table, SWT.NONE, i);
			tc.setText(columnHeaders[i]);

			tc.setResizable(columnLayouts[i].resizable);
			layout.addColumnData(columnLayouts[i]);
		}
	}
	
	@Override
	public void commit(boolean onSave) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void initialize(IManagedForm form) {
		this.form = form;

	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isStale() {
		return false;
	}

	@Override
	public void refresh() {
	}

	@Override
	public void setFocus() {
	}

	@Override
	public boolean setFormInput(Object input) {
		return true;
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		Object o;
	}

}

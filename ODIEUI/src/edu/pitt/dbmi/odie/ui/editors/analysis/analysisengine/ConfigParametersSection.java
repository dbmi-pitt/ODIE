package edu.pitt.dbmi.odie.ui.editors.analysis.analysisengine;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.resource.ResourceConfigurationException;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.ui.editors.analysis.AnalysisEditorInput;
import edu.pitt.dbmi.odie.ui.editors.providers.ConfigParamsTableContentProvider;
import edu.pitt.dbmi.odie.ui.editors.providers.ConfigParamsTableLabelProvider;
import edu.pitt.dbmi.odie.ui.editors.providers.ConfigParamsTableContentProvider.ConfigParameterWithValue;
import edu.pitt.dbmi.odie.utils.FormUtils;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class ConfigParametersSection extends SectionPart {
	Logger logger = Logger.getLogger(this.getClass());
	private TableViewer tableViewer;

	private FormEditor editor;
	AnalysisEngine analysisEngine;

	List<ConfigParameterWithValue> dirtyParamsList = new ArrayList<ConfigParameterWithValue>();

	private class ValueEditing extends EditingSupport {
		private TextCellEditor cellEditor;

		public ValueEditing(TableViewer viewer) {
			super(viewer);
			cellEditor = new TextCellEditor(viewer.getTable());
		}

		protected boolean canEdit(Object element) {
			ConfigParameterWithValue cp = (ConfigParameterWithValue) element;
			return (cp.cp.getType().equals("String"));
		}

		protected CellEditor getCellEditor(Object element) {
			return cellEditor;
		}

		protected Object getValue(Object element) {
			return ConfigParamsTableLabelProvider
					.getValue((ConfigParameterWithValue) element);
		}

		protected void setValue(Object element, Object value) {
			ConfigParameterWithValue cp = (ConfigParameterWithValue) element;
			if (cp.cg != null) {
				logger.error("Config params within groups not supported yet");
				return;
			}
			cp.value = value;

			dirtyParamsList.add(cp);
			ConfigParametersSection.this.markDirty();
			getViewer().update(element, null);

		}
	}

	public List<ConfigParameterWithValue> getDirtyParamsList() {
		return dirtyParamsList;
	}

	@Override
	public void commit(boolean onSave) {
		if (onSave) {
			if (analysisEngine == null)
				return;

			try {
				for (ConfigParameterWithValue cp : dirtyParamsList) {
					analysisEngine.setConfigParameterValue(cp.cp.getName(),
							cp.value);
				}
				analysisEngine.reconfigure();
			} catch (ResourceConfigurationException e) {
				GeneralUtils.showErrorDialog("Save Configuration Parameters",
						"There was an error saving the new configuration parameters:"
								+ "\n" + e.getMessage());
				return;
			}
			dirtyParamsList.clear();
			super.commit(onSave);
		}
	}

	public ConfigParametersSection(FormEditor editor, Composite parent,
			int style) {
		super(parent, editor.getToolkit(), style);
		this.editor = editor;
	}

	public AnalysisEngine getAnalysisEngine() {
		return analysisEngine;
	}



	public void initialize(final IManagedForm form) {
		super.initialize(form);

		FormToolkit toolkit = form.getToolkit();
		Section section = getSection();
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		section.setLayoutData(gd);
		section.addExpansionListener(new ExpansionAdapter() {

			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});
		section.setText("Configuration Parameters");

		Composite sectionClient = toolkit.createComposite(section);
		section.setClient(sectionClient);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		sectionClient.setLayout(layout);
		FormUtils.setMargins(sectionClient, 5, 0);

		tableViewer = new TableViewer(sectionClient, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns();

		gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;

		tableViewer.getTable().setLayoutData(gd);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);

		tableViewer.setContentProvider(new ConfigParamsTableContentProvider());
		tableViewer.setLabelProvider(new ConfigParamsTableLabelProvider());

		refresh();
	}

	// private void setCellEditors() {
	// CellEditor[] editors = new CellEditor[3];
	//		
	// editors[1] = new TextCellEditor(tableViewer.getTable());
	// tableViewer.setCellEditors(editors);

	// }

	// private Table createTable(Composite parent) {
	// Table table = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI
	// | SWT.FULL_SELECTION | SWT.BORDER);
	//		
	// table.setLinesVisible(true);
	// GridData gd = new GridData(GridData.FILL_BOTH);
	// gd.grabExcessHorizontalSpace = true;
	// gd.grabExcessVerticalSpace = true;
	//		
	// table.setLayoutData(gd);
	// return table;
	// // table.setLayout(new TableLayout());
	// }

	public static String columnHeaders[] = { "Name", "Value", "Type" };

	private ColumnLayoutData columnLayouts[] = { new ColumnWeightData(30),
			new ColumnWeightData(80), new ColumnWeightData(20) };

	// void createColumns(Table table) {
	// TableLayout layout = new TableLayout();
	// table.setLayout(layout);
	// table.setHeaderVisible(true);
	//
	// for (int i = 0; i < columnHeaders.length; i++) {
	// TableColumn tc = new TableColumn(table, SWT.NONE, i);
	// tc.setText(columnHeaders[i]);
	//			
	//
	// tc.setResizable(columnLayouts[i].resizable);
	// layout.addColumnData(columnLayouts[i]);
	// }
	// }

	void createColumns() {
		Table table = tableViewer.getTable();
		TableLayout layout = new TableLayout();
		table.setLayout(layout);

		for (int i = 0; i < columnHeaders.length; i++) {
			TableViewerColumn tvc = new TableViewerColumn(tableViewer, SWT.LEFT);
			TableColumn tc = tvc.getColumn();

			tc.setText(columnHeaders[i]);
			tc.setResizable(columnLayouts[i].resizable);
			layout.addColumnData(columnLayouts[i]);

			// if(i == 1)
			// tvc.setEditingSupport(new ValueEditing(tableViewer));
		}

	}

	@Override
	public void refresh() {
		Analysis a = ((AnalysisEditorInput) editor.getEditorInput())
				.getAnalysis();
		analysisEngine = a.getAnalysisEngine();

		if (analysisEngine != null) {
			getSection().setDescription(
					"Configuration parameters for the Analysis Engine.");

			tableViewer.setInput(analysisEngine);
			super.refresh();
		} else
			getSection()
					.setDescription(
							"Analysis not run yet. This section will show the configuration parameters used for the analysis engine.");

	}
}

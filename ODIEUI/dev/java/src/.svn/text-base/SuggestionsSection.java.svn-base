package edu.pitt.dbmi.odie.ui.editors;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.Suggestion;
import edu.pitt.dbmi.odie.ui.editors.providers.SuggestionsContentProvider;
import edu.pitt.dbmi.odie.ui.editors.providers.SuggestionsLabelProvider;
import edu.pitt.dbmi.odie.ui.sorters.HeaderSortListener;
import edu.pitt.dbmi.odie.ui.sorters.MultiColumnSorter;
import edu.pitt.dbmi.odie.ui.sorters.SuggestionSorter;
import edu.pitt.dbmi.odie.ui.workers.AnalysisReprocessor;
import edu.pitt.dbmi.odie.utils.FormUtils;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class SuggestionsSection extends SectionPart {
	Logger logger = Logger.getLogger(this.getClass());
	private TableViewer tableViewer;

	private FormEditor editor;
	private Analysis analysis;
	
	private List<Suggestion> goodSuggestions = new ArrayList<Suggestion>();
	private MultiColumnSorter<Suggestion> suggestionsComparator = new SuggestionSorter();
	
	private class ValueEditing extends EditingSupport {
		private CellEditor cellEditor;
		private int columnIndex = 0;
		public ValueEditing(TableViewer viewer, int columnIndex) {
			super(viewer);
			
			this.columnIndex = columnIndex;
			
			switch(columnIndex){
			case 0:
				cellEditor = new  CheckboxCellEditor(viewer.getTable(), SWT.BORDER);
				
				return;
			case 1:
				cellEditor = new TextCellEditor(viewer.getTable());
				return;
			}
		}
		
		protected boolean canEdit(Object element) {
			if(columnIndex == 1)
				return ((Suggestion)element).isGoodSuggestion();

			return true;
		}
		
		protected CellEditor getCellEditor(Object element) {
			return cellEditor;
		}

		protected Object getValue(Object element) {
			if(columnIndex == 0)
				return ((Suggestion)element).isGoodSuggestion();
			else if(columnIndex == 1)
				return ((Suggestion)element).getNerNegative();
			else
				return "invalid column";
		}

		protected void setValue(Object element, Object value) {
			if(columnIndex == 0){
				((Suggestion)element).setGoodSuggestion((Boolean) value);
				if(((Suggestion)element).isGoodSuggestion())
					goodSuggestions.add((Suggestion) element);
				else
					goodSuggestions.remove(element);
					
			}
			else if(columnIndex == 1)
				((Suggestion)element).setNerNegative((String) value);
				
			getViewer().update(element, null);
		}
		
		
	}
	
	public SuggestionsSection(FormEditor editor, Composite parent, int style) {
		super(parent, editor.getToolkit(), style);
		this.editor = editor;
	}

	public void initialize(final IManagedForm form) {
		super.initialize(form);
		
		analysis = ((AnalysisEditorInput)editor.getEditorInput()).getAnalysis();
		
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
		section.setText("Suggestions");
		section.setDescription("Suggestions for new concepts, synonyms or relationships");
		
		
		Composite sectionClient = toolkit.createComposite(section);
		section.setClient(sectionClient);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		sectionClient.setLayout(layout);
		FormUtils.setMargins(sectionClient, 5, 0);
		
		tableViewer = new TableViewer(sectionClient,SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI
				| SWT.FULL_SELECTION | SWT.BORDER);
		createColumns();
		
		
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		
		tableViewer.getTable().setLayoutData(gd);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.setComparator(suggestionsComparator);
		tableViewer.setContentProvider(new SuggestionsContentProvider());
		tableViewer.setLabelProvider(new SuggestionsLabelProvider());
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener(){

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Suggestion sugg = (Suggestion)((StructuredSelection)event.getSelection()).getFirstElement();
				if(sugg.isGoodSuggestion()){
					tableViewer.getTable().getColumns()[1].setText("Suggestion (click to edit)");
				}
				else
					tableViewer.getTable().getColumns()[1].setText("Suggestion");
				
			}
			
		});
		
		for(TableColumn tc:tableViewer.getTable().getColumns())
			tc.addSelectionListener(new HeaderSortListener(tableViewer,suggestionsComparator));
		
		Button repeatAnalysis  = new Button(sectionClient, SWT.PUSH);
		repeatAnalysis.setText("Repeat Analysis");
		repeatAnalysis.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				repeatAnalysis();
				
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				repeatAnalysis();
				
			}
			
		});
		
		gd = new GridData();
		gd.horizontalAlignment = SWT.RIGHT;
		gd.horizontalSpan = 2;
		repeatAnalysis.setLayoutData(gd);
		
	}
	
	protected void repeatAnalysis() {
		ProgressMonitorDialog pd = new ProgressMonitorDialog(PlatformUI
				.getWorkbench().getDisplay().getActiveShell());
		try {
			pd.run(true, true, new AnalysisReprocessor(analysis,goodSuggestions));
			
			GeneralUtils.showPerformanceDialog(analysis);
			GeneralUtils.openEditor(analysis);
			
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}

	public static String columnHeaders[] = { "Suggestion", "Score", "Proposed Concept", "Method", "Evidence", "Concept" };

	private ColumnLayoutData columnLayouts[] = { new ColumnWeightData(40), new ColumnWeightData(20), new ColumnWeightData(30), new ColumnWeightData(30), new ColumnWeightData(30), new ColumnWeightData(30)};

	void createColumns(){
		Table table = tableViewer.getTable();
		TableLayout layout = new TableLayout();
		table.setLayout(layout);
		
		for (int i = 0; i < columnHeaders.length; i++) {
			int style = SWT.LEFT;
			
			TableViewerColumn tvc = new TableViewerColumn(tableViewer,style);
			TableColumn tc = tvc.getColumn();
			
			tc.setText(columnHeaders[i]);
			tc.setResizable(columnLayouts[i].resizable);
			layout.addColumnData(columnLayouts[i]);
			
			if(i<2){
				tvc.setEditingSupport(new ValueEditing(tableViewer,i));
			}
		}
		
		
		
	}

	@Override
	public void refresh() {
		if(analysis!=null){
			tableViewer.setInput(analysis);
			super.refresh();
		}
	}	
}

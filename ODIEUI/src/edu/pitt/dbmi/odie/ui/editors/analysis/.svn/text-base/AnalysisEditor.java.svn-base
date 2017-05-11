package edu.pitt.dbmi.odie.ui.editors.analysis;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;

import com.mountainminds.eclipse.selectionsample.SelectionProviderIntermediate;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.ui.editors.analysis.analysisengine.ConfigurationParameterPage;
import edu.pitt.dbmi.odie.ui.editors.analysis.summary.SummaryPage;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class AnalysisEditor extends FormEditor {

	public static final String ID = "edu.pitt.dbmi.odie.ui.editors.AnalysisEditor";
//	private AnalysisOverviewPage analysisOverviewPage;
	private ChartEditor chartEditor;
	private ConfigurationParameterPage configParamsPage;
	private EnrichOntologiesPage enrichOntologiesPage;

	private SummaryPage summaryPage;
	
	private int summaryPageIndex = -1;
	private int configParamsPageIndex = -1;
	private int chartEditorPageIndex = -1;
	private int enrichOntologiesPageIndex = -1;
	private SelectionProviderIntermediate selectionProviderIntermediate = new SelectionProviderIntermediate();

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		setPartName(input.getName());

		Analysis analysis = ((AnalysisEditorInput) input).getAnalysis();
		if (GeneralUtils.isOE(analysis))
			setTitleImage(Aesthetics.getOeAEIcon());
		else if (GeneralUtils.isOther(analysis))
			setTitleImage(Aesthetics.getOtherAEIcon());
		else
			setTitleImage(Aesthetics.getNerAEIcon());
	}

	@Override
	protected void addPages() {
		try {
			summaryPage = new SummaryPage(this);
			summaryPageIndex = addPage(summaryPage);
			
//			analysisOverviewPage = new AnalysisOverviewPage(this);
//			overviewPageIndex = addPage(analysisOverviewPage);

			configParamsPage = new ConfigurationParameterPage(this);
			configParamsPageIndex = addPage(configParamsPage);

			if (((AnalysisEditorInput) getEditorInput()).getAnalysis()
					.getType().equals(ODIEConstants.AE_TYPE_OTHER))
				return;

			if(!Activator.getDefault().getConfiguration().isNoCharts()){
				chartEditor = new ChartEditor();
				chartEditorPageIndex = addPage(chartEditor, getEditorInput());
				setPageText(chartEditorPageIndex, "Interactive Chart");
			}
			
			if (((AnalysisEditorInput) getEditorInput()).getAnalysis()
					.getType().equals(ODIEConstants.AE_TYPE_OE)) {
				enrichOntologiesPage = new EnrichOntologiesPage(this);
				enrichOntologiesPageIndex = addPage(enrichOntologiesPage);
//				setActivePage(enrichOntologiesPageIndex);
			} else
//				setActivePage(chartEditorPageIndex);
				
			setActivePage(summaryPageIndex);

			getSite().setSelectionProvider(selectionProviderIntermediate);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

	}

	IRunnableWithProgress doSave = new IRunnableWithProgress() {

		@Override
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			monitor.beginTask("Saving Changes", IProgressMonitor.UNKNOWN);

			if (monitor.isCanceled())
				return;

//			if (analysisOverviewPage.isDirty())
//				analysisOverviewPage.doSave(null);
			if (configParamsPage.isDirty())
				configParamsPage.doSave(null);

			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					if (!AnalysisEditor.this.isDirty())
						AnalysisEditor.this.editorDirtyStateChanged();

				}

			});

			monitor.done();

		}

	};

	@Override
	public void doSave(IProgressMonitor monitor) {

		try {
			if (monitor == null) {
				ProgressMonitorDialog dialog = GeneralUtils
						.getProgressMonitorDialog();

				dialog.run(true, true, doSave);
			} else {
				doSave.run(monitor);
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	public ChartEditor getChartEditor() {
		return chartEditor;
	}

	@Override
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);

		if (newPageIndex == enrichOntologiesPageIndex)
			selectionProviderIntermediate
					.setSelectionProviderDelegate(enrichOntologiesPage
							.getSelectionProvider());
		else if (newPageIndex == chartEditorPageIndex)
			selectionProviderIntermediate
					.setSelectionProviderDelegate(chartEditor);
	}

	public void refresh() {
		chartEditor.refresh();
		configParamsPage.refresh();
		if (enrichOntologiesPage != null)
			enrichOntologiesPage.refresh();

	}
	
	protected void createPages() {
		super.createPages();
	        if (getPageCount() == 1 &&
			getContainer() instanceof CTabFolder) {
	            ((CTabFolder) getContainer()).setTabHeight(0);
	        }
	}
}

package edu.pitt.dbmi.odie.ui.editors.analysis.summary;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class OverviewTreeTable extends TreeViewer {

	;
	
	private Clipboard clipboard;

	public OverviewTreeTable(Composite parent) {
		super(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI
				| SWT.FULL_SELECTION | SWT.BORDER);

		
		createColumns(new ArrayList<Analysis>());
		addSortingSupport();
		enableToolTipSupport();
		attachProviders();

		applyLayout(getDefaultLayout());
		doMiscSetup();
	}


	private void enableToolTipSupport() {
//		ColumnViewerToolTipSupport.enableFor(this,ToolTip.NO_RECREATE);
		
//		final Listener labelListener = new Listener () {
//			public void handleEvent (Event event) {
//				Label label = (Label)event.widget;
//				Shell shell = label.getShell ();
//				switch (event.type) {
//					case SWT.MouseDown:
//						Event e = new Event ();
//						e.item = (TableItem) label.getData ("_TABLEITEM");
//						// Assuming table is single select, set the selection as if
//						// the mouse down event went through to the table
//						table.setSelection (new TableItem [] {(TableItem) e.item});
//						table.notifyListeners (SWT.Selection, e);
//						shell.dispose ();
//						table.setFocus();
//						break;
//					case SWT.MouseExit:
//						shell.dispose ();
//						break;
//				}
//			}
//		};
		
		Listener tableListener = new Listener () {
			Shell tip = null;
			Label label = null;
			public void handleEvent (Event event) {
				switch (event.type) {
					case SWT.Dispose:
					case SWT.KeyDown:
					case SWT.MouseMove: {
						if (tip == null) break;
						tip.dispose ();
						tip = null;
						label = null;
						break;
					}
					case SWT.MouseHover: {
						ViewerCell item = OverviewTreeTable.this.getCell(new Point (event.x, event.y));
						Object o = item.getElement();
						if (o != null && o instanceof ComparisonValuesData) {
							ComparisonValuesData cvd = (ComparisonValuesData)o;
							if (tip != null  && !tip.isDisposed ()) tip.dispose ();
							tip = new Shell (GeneralUtils.getShell(), SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
							tip.setBackground (GeneralUtils.getShell().getDisplay().getSystemColor (SWT.COLOR_INFO_BACKGROUND));
							FillLayout layout = new FillLayout ();
							layout.marginWidth = 2;
							tip.setLayout (layout);
							label = new Label (tip, SWT.NONE);
							label.setForeground (GeneralUtils.getShell().getDisplay().getSystemColor (SWT.COLOR_INFO_FOREGROUND));
							label.setBackground (GeneralUtils.getShell().getDisplay().getSystemColor (SWT.COLOR_INFO_BACKGROUND));
							label.setData ("_TABLEITEM", item);
							label.setText (cvd.getTooltip());
//							label.addListener (SWT.MouseExit, labelListener);
//							label.addListener (SWT.MouseDown, labelListener);
							Point size = tip.computeSize (SWT.DEFAULT, SWT.DEFAULT);
							Rectangle rect = item.getBounds();
							Point pt = OverviewTreeTable.this.getTree().toDisplay (rect.x, rect.y);
							tip.setBounds (pt.x, pt.y, size.x, size.y);
							tip.setVisible (true);
						}
					}
				}
			}
		};
		this.getTree().addListener (SWT.Dispose, tableListener);
		this.getTree().addListener (SWT.KeyDown, tableListener);
		this.getTree().addListener (SWT.MouseMove, tableListener);
		this.getTree().addListener (SWT.MouseHover, tableListener);
	}

	void createColumns(List<Analysis> analyses) {
		removeExistingColumns();
		
		//column with all labels
		TreeColumn tc = new TreeColumn(getTree(), SWT.LEFT);
		tc.setText("Overview");
		tc.setResizable(true);
		tc.setWidth(200);
		
		CellLabelProvider labelProvider = new CellLabelProvider() {

			public String getToolTipText(Object element) {
				if(element instanceof ComparisonValuesData)
					return ((ComparisonValuesData)element).getTooltip();
				return element.toString();
			}

			public Point getToolTipShift(Object object) {
				return new Point(5, 5);
			}

			public int getToolTipDisplayDelayTime(Object object) {
				return 2000;
			}

			public int getToolTipTimeDisplayed(Object object) {
				return 5000;
			}

			public void update(ViewerCell cell) {
				cell.setText(cell.getElement().toString());

			}
		};
		
		
		
		for(Analysis a:analyses){
			tc = new TreeColumn(getTree(), SWT.LEFT);
			tc.setText(a.getName());
			tc.setResizable(true);
		}
	}

	private void removeExistingColumns() {
		for(TreeColumn tc:getTree().getColumns()){
			tc.dispose();
		}
	}

	private void addSortingSupport() {
//		setComparator(suggestionsComparator);
//		for (TreeColumn tc : getTree().getColumns())
//			tc.addSelectionListener(new HeaderSortListener(this,
//					suggestionsComparator));
	}

	protected void attachProviders() {
		setContentProvider(new OverviewContentProvider());
		setLabelProvider(new ComparisonValuesDataLabelProvider());
	}

	protected void applyLayout() {
		GridData gd = getDefaultLayout();
		getTree().setLayoutData(gd);
	}

	private GridData getDefaultLayout() {
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		return gd;
	}

	protected void applyLayout(GridData gd) {
		getTree().setLayoutData(gd);
	}

	private void doMiscSetup() {
		getTree().setLinesVisible(true);
		getTree().setHeaderVisible(true);

	}

	
	public void setInput(List<Analysis> analyses){
		createColumns(analyses);
		super.setInput(analyses);
		packColumns();
	}

	private void packColumns() {
		for(TreeColumn tc:getTree().getColumns()){
			tc.pack();
		}
	}
	
}

package edu.pitt.dbmi.odie.ui.views.providers;

import java.util.Iterator;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;

import edu.pitt.dbmi.odie.model.AnalysisDocument;

public class AnnotationInstancesContentProvider implements
		IStructuredContentProvider {

	AnalysisDocument analysisDocument;
	Object firstInstance = null;

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Type) {
			AnnotationIndex ai = analysisDocument.getCas().getAnnotationIndex(
					(Type) inputElement);
			AnnotationFS[] arr = new AnnotationFS[ai.size()];

			int i = 0;
			for (Iterator it = ai.iterator(); it.hasNext();) {
				arr[i] = (AnnotationFS) it.next();
				i++;
			}
			if (i > 0)
				firstInstance = arr[0];
			else
				firstInstance = null;

			return arr;
		}
		firstInstance = null;
		return new Object[] {};
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	public Object getFirstInstance() {
		return firstInstance;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput != null && !newInput.equals(oldInput)) {

			try {
				// don't want the user to see updates that will be made to
				// the table
				// we are setting redraw(false) on the composite to avoid
				// dancing scrollbar
				viewer.getControl().setRedraw(false);
				removeOldColumns(viewer);
				addNewColumns(viewer, newInput);
			} finally {
				viewer.getControl().setRedraw(true);
			}
		}

	}

	private void removeOldColumns(Viewer viewer) {
		if (viewer instanceof TableViewer) {
			TableViewer tviewer = (TableViewer) viewer;

			for (TableColumn tc : tviewer.getTable().getColumns()) {
				tc.dispose();
			}
		}
	}

	private void addNewColumns(Viewer viewer, Object newInput) {
		if (newInput instanceof Type && viewer instanceof TableViewer) {
			Type type = (Type) newInput;
			TableViewer tviewer = (TableViewer) viewer;

			// add first column that has covered text
			TableColumn tc = new TableColumn(tviewer.getTable(), SWT.NONE);
			tc.setToolTipText("Covered Text");
			tc.setText("Text");
			tc.pack();
			// add columns for all features.
			for (Object o : type.getFeatures()) {
				tc = new TableColumn(tviewer.getTable(), SWT.NONE);
				tc.setToolTipText(((Feature) o).getName());
				tc.setText(((Feature) o).getShortName());
				tc.pack();
			}
		}
	}

	public void setAnalysisDocument(AnalysisDocument ad) {
		analysisDocument = ad;

	}

}

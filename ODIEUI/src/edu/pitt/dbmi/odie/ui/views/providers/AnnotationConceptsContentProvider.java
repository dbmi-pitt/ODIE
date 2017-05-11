package edu.pitt.dbmi.odie.ui.views.providers;

import org.apache.uima.cas.ArrayFS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.uima.utils.UIMAUtils;

public class AnnotationConceptsContentProvider implements
		IStructuredContentProvider {

	AnalysisDocument analysisDocument;

	@Override
	public Object[] getElements(Object inputElement) {
		if (UIMAUtils.isNamedEntity(inputElement)) {
			AnnotationFS ann = ((AnnotationFS) inputElement);
			Feature f = ann.getType()
					.getFeatureByBaseName("ontologyConceptArr");
			FeatureStructure fs = ann.getFeatureValue(f);

			if (fs.getType().isArray()) {
				ArrayFS arr = (ArrayFS) fs;
				FeatureStructure[] fsarr = new FeatureStructure[arr.size()];

				arr.copyToArray(0, fsarr, 0, fsarr.length);
				return fsarr;
			}
		}

		return new Object[] {};
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput != null && !newInput.equals(oldInput)
				&& UIMAUtils.isNamedEntity(newInput)) {
			removeOldColumns(viewer);
			addNewColumns(viewer, newInput);
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
		if (newInput instanceof AnnotationFS && viewer instanceof TableViewer) {
			Type namedEntityType = ((AnnotationFS) newInput).getType();
			AnnotationFS ann = ((AnnotationFS) newInput);

			Feature f = namedEntityType
					.getFeatureByBaseName("ontologyConceptArr");
			FeatureStructure fs = ann.getFeatureValue(f);
			ArrayFS arr = (ArrayFS) fs;

			Type type = arr.get(0).getType();

			TableViewer tviewer = (TableViewer) viewer;

			// add columns for all features.
			for (Object o : type.getFeatures()) {
				TableColumn tc = new TableColumn(tviewer.getTable(), SWT.NONE);
				tc.setToolTipText(((Feature) o).getName());
				tc.setText(((Feature) o).getShortName());
				tc.pack();
			}
		}
	}

}

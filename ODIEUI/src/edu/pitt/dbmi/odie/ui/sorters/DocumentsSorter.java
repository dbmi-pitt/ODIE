package edu.pitt.dbmi.odie.ui.sorters;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;

import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.uima.util.ODIE_IFConstants;

public class DocumentsSorter extends MultiColumnSorter<AnalysisDocument> {

	public DocumentsSorter(int[] columnOrder) {
		super(columnOrder);
		// TODO Auto-generated constructor stub
	}

	public static final int NAME = 0;
	public static final int ANNOTATIONS = 1;
	public static final int STATUS = 2;

	final static int[] DEFAULT_PRIORITIES = { ANNOTATIONS, NAME, STATUS };

	final static int[] DEFAULT_DIRECTIONS = { DESCENDING, ASCENDING, DESCENDING };

	/**
	 * Compares the concept names of two datapoints
	 */
	private int getNameOrder(AnalysisDocument m1, AnalysisDocument m2) {
		return m1.getDocument().getName().compareTo(m2.getDocument().getName());
	}

	private long getStatusOrder(AnalysisDocument m1, AnalysisDocument m2) {
		return m1.getStatus().compareTo(m2.getStatus());
	}

	private long getAnnotationsOrder(AnalysisDocument m1, AnalysisDocument m2) {
		CAS cas = m1.getCas();
		if (cas != null) {
			TypeSystem ts = m1.getAnalysis().getAnalysisEngineMetadata()
					.getTypeSystem();
			Type namedEntityType = ts
					.getType(ODIE_IFConstants.NAMED_ENTITY_TYPE_FULLNAME);

			if (m2.getCas() == null)
				return 0;

			if (namedEntityType == null)
				return cas.getAnnotationIndex().size()
						- m2.getCas().getAnnotationIndex().size();
			else
				return cas.getAnnotationIndex(namedEntityType).size()
						- m2.getCas().getAnnotationIndex(namedEntityType)
								.size();
		} else
			return 0;
	}

	@Override
	protected int compare(AnalysisDocument m1, AnalysisDocument m2,
			int columnNumber) {
		switch (columnNumber) {
		case NAME: {
			return getNameOrder(m1, m2);
		}
		case STATUS: {
			return (int) getStatusOrder(m1, m2);
		}
		case ANNOTATIONS: {
			return (int) getAnnotationsOrder(m1, m2);

		}
		default:
			return 0;
		}
	}

	@Override
	protected int[] getDefaultDirections() {
		return DEFAULT_DIRECTIONS;
	}

	@Override
	protected int[] getDefaultPriorities() {
		return DEFAULT_PRIORITIES;
	}
}

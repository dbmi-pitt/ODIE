package edu.pitt.dbmi.odie.ui.editors.providers;

import java.net.URI;
import java.util.HashMap;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.Statistics;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class OntologyLegendLabelProvider implements ITableLabelProvider {

	HashMap<Object, Image> colorMap = new HashMap<Object, Image>();

	public OntologyLegendLabelProvider() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang
	 * .Object, int)
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex != 0)
			return null;

		if (element instanceof Statistics
				&& ((Statistics) element).context instanceof URI) {
			return Aesthetics
					.getColorImageForObject(((URI)((Statistics) element).context).toASCIIString());
		}
		// if(element instanceof Statistics){
		// return
		// Aesthetics.getColorImageForObject(((Statistics)element).context);
		// }
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang
	 * .Object, int)
	 */
	@Override
	public String getColumnText(Object element, int columnIndex) {
		// if(element instanceof Object[]){
		// Object[] arr = (Object[])element;
		// if(arr.length==0)
		// return "No hits";
		//			
		// switch(columnIndex){
		// case 0:return ((IOntology)arr[0]).getName();
		// case 1:return arr[1].toString();
		// // case 2:return ""+
		// stats.getFrequency(lr.getLanguageResource().getResource());
		// default:return "not defined for:" + columnIndex;
		// }
		// }

		if (element instanceof Statistics) {
			Statistics s = (Statistics) element;
			switch (columnIndex) {
			case 0:
				if (s.context instanceof URI) {
					URI ouri = (URI) s.context;
					return ouri.toASCIIString().substring(ouri.toASCIIString().lastIndexOf("/") + 1);
				} else if (s.context instanceof Analysis)
					return "TOTAL";
				else
					return "Unknown Object";
			case 1:
				return "" + s.uniqueConceptsCount;
			case 2:
				return GeneralUtils
						.getPrettyPercentageFormat(s.getCoverage());
			default:
				return "not defined for:" + columnIndex;
			}
		}

		return "Unexpected object:" + element.getClass().getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.
	 * jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang
	 * .Object, java.lang.String)
	 */
	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse
	 * .jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}
}

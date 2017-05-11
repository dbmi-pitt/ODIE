package edu.pitt.dbmi.odie.ui.editors.analysis.summary;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import edu.pitt.dbmi.odie.ui.Aesthetics;

public class ComparisonValuesDataLabelProvider implements ITableLabelProvider, ITableFontProvider, ITableColorProvider{
	static Logger logger = Logger.getLogger(ComparisonValuesDataLabelProvider.class);


	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof ComparisonValuesData) {
			ComparisonValuesData cvd = (ComparisonValuesData) element;

			if(columnIndex ==0)
				return cvd.getLabel();
			else
				return cvd.getValueLabel(columnIndex-1);
		}
		return "object not ComparisonValuesData";
	}



	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Font getFont(Object element, int columnIndex) {
		if (element instanceof ComparisonValuesData) {
			ComparisonValuesData cvd = (ComparisonValuesData) element;
			if(columnIndex ==0){
				if(cvd.noDifferences())
					return Aesthetics.getRegularFont();
				else
					return Aesthetics.getBoldFont();
			}
			else
				return Aesthetics.getRegularFont();
		}
		return Aesthetics.getRegularFont();
	}

	@Override
	public Color getBackground(Object element, int columnIndex) {

		if (element instanceof ComparisonValuesData) {
			ComparisonValuesData cvd = (ComparisonValuesData) element;
			if(cvd.isColumnHighlighted(columnIndex-1))
				return Aesthetics.getStickyNoteBackground();
			else
				return null;
		}
		return null;
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {
		return null;
	}



}

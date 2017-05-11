/**
 * @author Girish Chavan
 *
 * Nov 6, 2008
 */
package edu.pitt.dbmi.odie.ui.editors;

import java.util.Comparator;

import org.eclipse.swt.custom.StyleRange;

class StyleRangeSorter implements Comparator<StyleRange>{

	@Override
	public int compare(StyleRange styleRange1, StyleRange styleRange2) {
		int startdiff = styleRange1.start - styleRange2.start;
		
		if(startdiff==0)
			return styleRange1.length - styleRange2.length;
		else
			return startdiff;
	}
	
}
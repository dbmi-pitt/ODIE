package edu.pitt.dbmi.odie.uima.util;

import java.io.Serializable;
import java.util.Comparator;

import edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateAnnotation;

/**
 * Allows ascending or descending annotation sort.
 * 
 * @author mitchellkj@upmc.edu
 * @version $Id$
 * @since 1.6.15
 */
public class ODIE_Comparator implements Comparator<ODIE_GateAnnotation>, Serializable {

	/**
	 * Field serialVersionUID. (value is "-1863322709982419803L ;")
	 */
	private static final long serialVersionUID = -1863322709982419803L;
	
	/**
	 * Field isAscendingMultiplier.
	 */
	private int isAscendingMultiplier = 1;
	
	/**
	 * Constructor for ODIE_Comparator.
	 */
	public ODIE_Comparator() {
	}

	/**
	 * Constructor for ODIE_Comparator.
	 * 
	 * @param isAscending
	 *            boolean
	 */
	public ODIE_Comparator(boolean isAscending) {
		setIsAscending(isAscending);
	}

	/**
	 * Method compare.
	 * 
	 * @param o2
	 *            Object
	 * @param o1
	 *            Object
	 * 
	 * @return int
	 * 
	 * @see java.util.Comparator#compare(Object, Object)
	 */
	public int compare(ODIE_GateAnnotation o1, ODIE_GateAnnotation o2) {
		int retValue = -1 * isAscendingMultiplier;
		try {
			long start1 = o1.getBegin() ;
			long end1 = o1.getEnd();
			long len1 = end1 - start1;
			long start2 = o2.getBegin();
			long end2 = o2.getEnd();
			long len2 = end2 - start2;
			if (start1 < start2) {
				retValue = -1 * isAscendingMultiplier;
			} else if (start2 < start1) {
				retValue = 1 * isAscendingMultiplier;
			} else {
				if (len1 > len2) {
					retValue = -1 * isAscendingMultiplier;
				} else if (len2 > len1) {
					retValue = 1 * isAscendingMultiplier;
				} else {
					retValue = -1;
				}
			}
		} catch (Exception x) {
			retValue = -1;
			x.printStackTrace();
		}

		return retValue;

	}

	/**
	 * Method equals.
	 * 
	 * @param obj
	 *            Object
	 * 
	 * @return boolean
	 * 
	 * @see java.util.Comparator#equals(Object)
	 */
	public boolean equals(Object obj) {
		return false;
	}

	/**
	 * Method setIsAscending.
	 * 
	 * @param isAscending
	 *            boolean
	 */
	public void setIsAscending(boolean isAscending) {
		if (isAscending) {
			this.isAscendingMultiplier = 1;
		} else {
			this.isAscendingMultiplier = -1;
		}
	}

}

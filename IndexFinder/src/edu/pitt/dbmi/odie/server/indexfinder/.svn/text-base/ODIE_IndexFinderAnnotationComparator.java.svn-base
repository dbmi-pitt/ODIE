package edu.pitt.dbmi.odie.server.indexfinder;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Allows ascending or descending annotation sort.
 * 
 * @author mitchellkj@upmc.edu
 * @version $Id
 * @since 1.6.0_14
 * 
 */
public class ODIE_IndexFinderAnnotationComparator implements Comparator<ODIE_IndexFinderAnnotation>, Serializable {

	/**
	 * Field serialVersionUID. (value is "-1863322709982419803L ;")
	 */
	private static final long serialVersionUID = -1863322709982419803L;
	
	/**
	 * Field isAscendingMultiplier.
	 */
	private int isAscendingMultiplier = 1;
	
	/**
	 * Constructor for CaTIES_Comparator.
	 */
	public ODIE_IndexFinderAnnotationComparator() {
	}

	/**
	 * Constructor for CaTIES_Comparator.
	 * 
	 * @param isAscending
	 *            boolean
	 */
	public ODIE_IndexFinderAnnotationComparator(boolean isAscending) {
		setIsAscending(isAscending);
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

	@Override
	public int compare(ODIE_IndexFinderAnnotation o1,
			ODIE_IndexFinderAnnotation o2) {
		int retValue = -1 * isAscendingMultiplier;
		try {
			long start1 = o1.getStartNode().getOffset()
					.longValue();
			long end1 = o1.getEndNode().getOffset().longValue();
			long len1 = end1 - start1;
			long start2 = o2.getStartNode().getOffset()
					.longValue();
			long end2 = o2.getEndNode().getOffset().longValue();
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

}

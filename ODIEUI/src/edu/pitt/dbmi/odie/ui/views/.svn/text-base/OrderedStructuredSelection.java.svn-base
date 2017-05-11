/**
 * 
 */
package edu.pitt.dbmi.odie.ui.views;

import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;

/**
 * @author Girish Chavan
 * 
 */
public class OrderedStructuredSelection extends StructuredSelection {

	int type;

	private List<Object> displayOrder;

	public static final int TYPE_SELECTION = 1;
	public static final int TYPE_CHECK = 2;

	/**
	 * @param selectedItems
	 */
	public OrderedStructuredSelection(List<Object> selectedItems) {
		super(selectedItems);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @param displayOrder
	 */
	public void setDisplayOrder(List<Object> displayOrder) {
		this.displayOrder = displayOrder;

	}

	public List<Object> getDisplayOrder() {
		return displayOrder;
	}

}

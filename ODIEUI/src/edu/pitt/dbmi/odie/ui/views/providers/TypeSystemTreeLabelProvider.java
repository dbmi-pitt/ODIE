package edu.pitt.dbmi.odie.ui.views.providers;

import java.util.HashMap;
import java.util.Random;

import org.apache.uima.cas.Type;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import edu.pitt.dbmi.odie.ui.Aesthetics;

public class TypeSystemTreeLabelProvider implements ILabelProvider,
		IFontProvider {

	HashMap<Object, Image> colorMap = new HashMap<Object, Image>();
	Display display;
	Random generator = new Random();

	private FilteredTree filterTree;
	private PatternFilter filterForBoldElements = new PatternFilter();

	public TypeSystemTreeLabelProvider(FilteredTree filterTree, Display display) {
		super();
		this.filterTree = filterTree;
		this.display = display;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		return Aesthetics.getColorImageForObject(element);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof Type)
			return ((Type) element).getShortName();
		else
			return "Not A Type";
	}

	public Font getFont(Object element) {
		return FilteredTree.getBoldFont(element, filterTree,
				filterForBoldElements);
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
}

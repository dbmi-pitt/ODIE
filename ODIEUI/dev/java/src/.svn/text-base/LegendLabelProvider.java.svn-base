package edu.pitt.dbmi.odie.ui.editors.providers;

import java.util.HashMap;
import java.util.Random;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import edu.pitt.dbmi.odie.model.AnalysisLanguageResource;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.ontology.IResource;

public class LegendLabelProvider implements ITableLabelProvider {
	
	HashMap<Object,Image> colorMap = new HashMap<Object,Image>();
	Display display;
	Random generator = new Random();
	public LegendLabelProvider(Display display) {
		super();
		this.display = display;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if(columnIndex != 0 || !(element instanceof AnalysisLanguageResource))
			return null;
		
		Image m = colorMap.get(element);
		IResource r = ((AnalysisLanguageResource)element).getLanguageResource().getResource();
		
		//TODO Make aesthetics store the color images and implement a getColorImageForAnnotationType
		//this will make sure that same image is not recreated here and in AnnotationtypeView
		if(m==null){
			PaletteData paletteData = new PaletteData(
		        new RGB[] {Aesthetics.getColorForAnnotationType(r).getRGB()});
		    ImageData imageData = new ImageData(12,12,1,paletteData);
		    Image image = new Image(display,imageData);
		    m = image;
		    colorMap.put(element,m);
		}
		return m;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	@Override
	public String getColumnText(Object element, int columnIndex) {
		if(!(element instanceof AnalysisLanguageResource))
			return "Element is not AnalysisLanguageResource";
		AnalysisLanguageResource lr =  (AnalysisLanguageResource)element;
		
		return "dummy";
//		Statistics  stats = lr.getAnalysis().getStatistics();
//		switch(columnIndex){
//			case 0:return lr.getLanguageResource().getName();
//			case 1:return ""+ stats.getUniqueConceptCount((IOntology) lr.getLanguageResource().getResource());
//			case 2:return ""+ stats.getFrequency(lr.getLanguageResource().getResource());
//			default:return "not defined for:" + columnIndex;
//		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}
}

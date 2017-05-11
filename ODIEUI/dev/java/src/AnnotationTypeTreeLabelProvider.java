package edu.pitt.dbmi.odie.ui.views.providers;

import java.util.HashMap;
import java.util.Random;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.Suggestion;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.ontology.IClass;

public class AnnotationTypeTreeLabelProvider implements ILabelProvider {

	HashMap<Object,Image> colorMap = new HashMap<Object,Image>();
	Display display;
	Random generator = new Random();
	public AnnotationTypeTreeLabelProvider(Display display) {
		super();
		this.display = display;
		
	}

	boolean isOE = false;
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		
		Image m = colorMap.get(element);
		
		if(m==null){
			PaletteData paletteData = null;
			
			if(element instanceof Suggestion){
				paletteData = new PaletteData(
			        new RGB[] {Aesthetics.getColorForAnnotationType(AnnotationTypeTreeContentProvider.SUGGESTIONS_LABEL).getRGB()});
			}
			else if(element instanceof IClass && isOE){
				if(((IClass)element).getOntology().getURI().toString().equals(MiddleTier.CONCEPT_DISCOVERY_ONTOLOGY_URI))
					paletteData = new PaletteData(
					        new RGB[] {Aesthetics.getColorForAnnotationType(AnnotationTypeTreeContentProvider.LSP_LABEL).getRGB()});
				else
					paletteData = new PaletteData(
			        new RGB[] {Aesthetics.getColorForAnnotationType(AnnotationTypeTreeContentProvider.NER_LABEL).getRGB()});
			}
			else{
				paletteData = new PaletteData(
				        new RGB[] {Aesthetics.getColorForAnnotationType(element).getRGB()});
			}
			
			ImageData imageData = new ImageData(12,12,1,paletteData);
		    Image image = new Image(display,imageData);
		    m = image;
		    colorMap.put(element,m);
		}
		return m;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if(element!=null){
			if(element instanceof Suggestion){
				return element.toString() + " (weight=" +((Suggestion)element).getWeight() + ")";
			}
			return element.toString();
		}
		else
			return "null";
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

	public boolean isOE() {
		return isOE;
	}

	public void setOE(boolean isOE) {
		this.isOE = isOE;
		colorMap.clear();
	}


}

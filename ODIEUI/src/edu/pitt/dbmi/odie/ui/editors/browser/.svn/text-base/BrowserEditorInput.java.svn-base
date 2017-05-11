package edu.pitt.dbmi.odie.ui.editors.browser;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class BrowserEditorInput implements IEditorInput {

	private URL url;

	public URL getURL() {
		return url;
	}

	@Override
	public boolean exists() {
		return !(url == null);
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
		// if(GeneralUtils.isOE(analysis))
		// return Aesthetics.getOeAEIconDescriptor();
		// else if(GeneralUtils.isOther(analysis))
		// return Aesthetics.getOtherAEIconDescriptor();
		// else
		// return Aesthetics.getNerAEIconDescriptor();
	}

	@Override
	public String getName() {
		return url.toString();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return getName();
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	public BrowserEditorInput(URL url) {
		super();
		this.url = url;

	}
}

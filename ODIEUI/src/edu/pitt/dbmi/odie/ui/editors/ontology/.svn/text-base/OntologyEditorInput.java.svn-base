package edu.pitt.dbmi.odie.ui.editors.ontology;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import edu.pitt.ontology.IOntology;

public class OntologyEditorInput implements IEditorInput {

	IOntology ontology;

	public IOntology getOntology() {
		return ontology;
	}

	@Override
	public boolean exists() {
		return !(ontology == null);
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
		return ontology.getName();
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

	public OntologyEditorInput(IOntology ontology) {
		super();
		this.ontology = ontology;

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj instanceof OntologyEditorInput && this.ontology != null
				&& ((OntologyEditorInput) obj).ontology != null)
			return ((OntologyEditorInput) obj).ontology.equals(this.ontology);
		else
			return super.equals(obj);
	}

}

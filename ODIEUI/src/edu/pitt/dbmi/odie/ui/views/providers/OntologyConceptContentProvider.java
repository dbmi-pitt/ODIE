package edu.pitt.dbmi.odie.ui.views.providers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.IResource;

public class OntologyConceptContentProvider implements ITreeContentProvider {

	ResourceTreeNode[] ontologyTreeNodeArr = new ResourceTreeNode[] {};

	public class ResourceTreeNode {
		ResourceTreeNode parent;
		public IResource obj;

		public ResourceTreeNode getParent() {
			return parent;
		}

		public void setParent(ResourceTreeNode parent) {
			this.parent = parent;
		}

		public ResourceTreeNode(ResourceTreeNode parent, IResource obj) {
			super();
			this.parent = parent;
			this.obj = obj;
		}

	}

	Logger logger = Logger.getLogger(OntologyConceptContentProvider.class);
	private TreeViewer viewer;

	public OntologyConceptContentProvider(TreeViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof ResourceTreeNode) {
			Object parent = ((ResourceTreeNode) element).getParent();
			if (parent == null)
				parent = ontologyTreeNodeArr;
			return parent;
		} else
			return ontologyTreeNodeArr;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		assert newInput instanceof ResourceTreeNode[];

		dispose();

		ontologyTreeNodeArr = (ResourceTreeNode[]) newInput;

	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ResourceTreeNode) {
			return getChildrenTreeNodes((ResourceTreeNode) parentElement);
		}
		return new Object[] {};
	}

	private ResourceTreeNode[] getChildrenTreeNodes(ResourceTreeNode parent) {
		IClass[] children = new IClass[] {};
		if (parent.obj instanceof IOntology) {
			children = ((IOntology) parent.obj).getRootClasses();

		} else if (parent.obj instanceof IClass) {
			children = ((IClass) parent.obj).getDirectSubClasses();
		}

		return buildResourceTreeNodes(parent, children);
	}

	private ResourceTreeNode[] buildResourceTreeNodes(ResourceTreeNode parent,
			IClass[] children) {

		List<ResourceTreeNode> l = new ArrayList<ResourceTreeNode>();
		for (IClass c : children) {
			if (c.getName() != null)
				l.add(new ResourceTreeNode(parent, c));
		}
		return l.toArray(new ResourceTreeNode[] {});
	}

	@Override
	public boolean hasChildren(Object element) {
		return true;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return ontologyTreeNodeArr;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
}

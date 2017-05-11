package edu.pitt.dbmi.odie.ui.editors.providers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class AnalysisEngineDelegatesTreeContentProvider implements
		ITreeContentProvider {

	HashMap<AnalysisEngineMetaData, AnalysisEngineMetaData> childToParentMap = new HashMap<AnalysisEngineMetaData, AnalysisEngineMetaData>();

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof AnalysisEngineMetaData) {
			AnalysisEngineMetaData aem = (AnalysisEngineMetaData) parentElement;

			XMLInputSource in;
			try {
				in = new XMLInputSource(aem.getSourceUrl().getFile());
				ResourceSpecifier specifier = UIMAFramework.getXMLParser()
						.parseResourceSpecifier(in);

				if (specifier instanceof AnalysisEngineDescription) {
					Map m = ((AnalysisEngineDescription) specifier)
							.getDelegateAnalysisEngineSpecifiersWithImports();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidXMLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			AnalysisEngineMetaData[] daem = aem
					.getDelegateAnalysisEngineMetaData();

			updateChildParentMap(aem, daem);

			if (daem != null)
				return daem;
		}
		return new Object[] {};
	}

	private void updateChildParentMap(AnalysisEngineMetaData parent,
			AnalysisEngineMetaData[] daem) {
		if (daem == null || daem.length == 0)
			return;

		for (AnalysisEngineMetaData aem : daem)
			childToParentMap.put(aem, parent);
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof AnalysisEngineMetaData) {
			return childToParentMap.get(element);
		}
		return null;

	}

	@Override
	public boolean hasChildren(Object element) {
		// if(element instanceof AnalysisEngineMetaData){
		// return ((AnalysisEngineMetaData)
		// element).getDelegateAnalysisEngineMetaData()!=null;
		// }
		return true;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof AnalysisEngineMetaData) {
			return new Object[] { inputElement };
		}

		return new Object[] {};
	}

	@Override
	public void dispose() {
		childToParentMap.clear();

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}

package edu.pitt.dbmi.odie.ui.views.providers;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.NonEmptyFSList;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.mayo.bmi.uima.coref.type.Chain;
import edu.mayo.bmi.uima.coref.type.Markable;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class CoreferenceContentProvider implements ITreeContentProvider {

	public CoreferenceContentProvider() {
		super();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement.toString().equals(CHAINS))
			return getChains();
		else if (parentElement instanceof Chain) {
			return getMembers((Chain) parentElement);
		} else
			return new Object[] {};
	}

	public static Object[] getMembers(Chain c) {
		List<Markable> outList = new ArrayList<Markable>();
		List<Integer> mkidList = new ArrayList<Integer>();
		try {
			FSList m = c.getMembers();

			while (m instanceof NonEmptyFSList) {
				Markable mk = (Markable) ((NonEmptyFSList) m).getHead();

				if (!mkidList.contains(mk.getId())) {
					outList.add(mk);
					mkidList.add(mk.getId());
				}

				m = ((NonEmptyFSList) m).getTail();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return outList.toArray();
	}

	private Object[] getChains() {
		if (ad == null)
			return new Object[] {};
		else {
			List<Chain> outList = new ArrayList<Chain>();
			try {
				GeneralUtils.initCASIfRequired(ad);

				CAS cas = ad.getCas();
				FSIterator fsit = cas.getJCas().getJFSIndexRepository()
						.getAllIndexedFS(Chain.type);

				while (fsit.hasNext()) {
					Chain c = (Chain) fsit.next();

					if (getMembers(c).length > 1)
						outList.add(c);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return outList.toArray();
		}
	}

	@Override
	public Object getParent(Object element) {
		if (element.equals(CHAINS))
			return null;
		else
			return CHAINS;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element.equals(CHAINS))
			return true;
		else if (element instanceof Chain)
			return getMembers((Chain) element).length > 0;
		else
			return false;
	}

	final String CHAINS = "Chains";

	final String[] rootElements = new String[] { CHAINS };

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof AnalysisDocument)
			return rootElements;
		else
			return new String[] { "Was not expecting:"
					+ inputElement.getClass().getName() };
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	AnalysisDocument ad;

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof AnalysisDocument)
			ad = (AnalysisDocument) newInput;

	}

}

package edu.pitt.dbmi.odie.ui.editors.analysis.summary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.middletier.AnalysisSpaceMiddleTier;
import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.model.LanguageResource;
import edu.pitt.dbmi.odie.model.Statistics;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class OverviewContentProvider implements ITreeContentProvider
		 {

	Logger logger = Logger.getLogger(this.getClass());
	List<Analysis> analyses;
	
	
	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof List){
			analyses = (List)inputElement;
			
			List<ComparisonValuesData> outlist = new ArrayList<ComparisonValuesData>();
			
			ComparisonValuesData atcvd = new ComparisonValuesData();
			atcvd.setLabel("Analysis Type");
			atcvd.setTooltip("Whether the analysis engine used is a Named Entity Recoginition(NER), Ontology Enrichment(OE) or Other type");
			
			ComparisonValuesData aecvd = new ComparisonValuesData();
			aecvd.setLabel("Analysis Engine");
			aecvd.setTooltip("The UIMA Analysis Engine that was used for this analysis.");
			
			ComparisonValuesData ocvd = new ComparisonValuesData();
			ocvd.setLabel(ODIEConstants.ONTOLOGIES_LABEL);
			
			ComparisonValuesData pcvd = new ComparisonValuesData();
			pcvd.setLabel(ODIEConstants.PERFORMANCE_LABEL);
			
			ComparisonValuesData dccvd = new ComparisonValuesData();
			dccvd.setLabel("Concepts Found");
			dccvd.setTooltip("No. unique occurences of concepts found in the document set.");
			
			ComparisonValuesData accvd = new ComparisonValuesData();
			accvd.setLabel("Named Entities Found");
			
			ComparisonValuesData dcvd = new ComparisonValuesData();
			dcvd.setLabel("Documents Processed");
			
			ComparisonValuesData npcvd = new ComparisonValuesData();
			npcvd.setLabel("Noun Phrases Processed");
			
			ComparisonValuesData sucvd = new ComparisonValuesData();
			sucvd.setLabel("Suggestions");
			sucvd.setTooltip("No. of suggestions for new concepts");
//			ComparisonValuesData scvd = new ComparisonValuesData();
//			scvd.setLabel("Sentences Processed");
			
			ComparisonValuesData ccvd = new ComparisonValuesData();
			ccvd.setLabel("Ontology Coverage");
			ccvd.setTooltip("Percentage of total characters in the document set that are part of named entities");
			List<ComparisonValuesData> pchildren = new ArrayList<ComparisonValuesData>();
			pchildren.add(dcvd);
			pchildren.add(npcvd);
			pchildren.add(accvd);
			pchildren.add(dccvd);
			pchildren.add(ccvd);
			pchildren.add(sucvd);
			pcvd.setChildren(pchildren);
			
			
			for(Analysis a:analyses){
				if(a.getType().equals(ODIEConstants.AE_TYPE_OE)){
					atcvd.addValue("Ontology Enrichment");
					AnalysisSpaceMiddleTier amt = edu.pitt.dbmi.odie.ui.Activator.getDefault().getAnalysisMiddleTier(a);
					sucvd.addValue(new Long(amt.getSuggestions(0.8f).size()));
				}
				else if(a.getType().equals(ODIEConstants.AE_TYPE_NER)){
					atcvd.addValue("Named Entity Recognition");
					sucvd.addValue("Not Applicable");
				}
				else{
					atcvd.addValue("Other");
					sucvd.addValue("Not Applicable");
				}
				aecvd.addValue(a.getAnalysisEngineMetadata().getName());
				dcvd.addValue(new Integer(a.getAnalysisDocuments(AnalysisDocument.STATUS_DONE).size()));
				
				Statistics s = a.getStatistics();
				accvd.addValue(new Long(s.namedEntityCount));
				dccvd.addValue(new Long(s.uniqueConceptsCount));
				npcvd.addValue(new Long(s.nounPhraseCount));
				
//				scvd.addValue(new Long(s.sentenceCount));
				float f = (float) s.coveredCharCount / (float) s.totalCharCount;
				ccvd.addValue(new Float(f),GeneralUtils.getPrettyPercentageFormat(f));
			}
			outlist.add(atcvd);
			outlist.add(aecvd);
			outlist.add(ocvd);
			outlist.add(pcvd);
			
			
			return outlist.toArray();
		}
		return new Object[]{};
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object inputElement) {
	}


	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ComparisonValuesData) {
			ComparisonValuesData cvd = (ComparisonValuesData) parentElement;
			if(cvd.getChildren()==null){
				if(cvd.getLabel().equals(ODIEConstants.ONTOLOGIES_LABEL)){
					populateOntologyCVDs(cvd);
				}
				else
					return  new Object[] {};
			}
			return cvd.getChildren().toArray();
		}
		return new Object[] {};
	}


	/**
	 * Creates a list of CVDs for ontologies across all the analyses.
	 * CVDs are labelled from 1 to n. It tries to have common ontologies all 
	 * in the same CVD. If an ontology exists in an analysis but not in others,
	 * it will leave the space empty.
	 * @param cvd
	 */
	private void populateOntologyCVDs(ComparisonValuesData cvd) {
		List<ComparisonValuesData> ontologyCVDs = new ArrayList<ComparisonValuesData>();
		
		HashMap<String,ComparisonValuesData> map = new HashMap<String,ComparisonValuesData>();
		
		for(int i=0;i<analyses.size();i++){
			Analysis a = analyses.get(i);
			
			List<LanguageResource> lrlist =  a.getLanguageResources();
			
			for(LanguageResource lr:lrlist){
				ComparisonValuesData ocvd = map.get(lr.getName());
				
				if(ocvd == null){
					ocvd = new ComparisonValuesData();
					ontologyCVDs.add(ocvd);
					ocvd.setLabel("Ontology "+(ontologyCVDs.size()));
					map.put(lr.getName(), ocvd);
				}
				ocvd.addValueAtIndex(i, lr.getName());
			}
		}
		cvd.setChildren(ontologyCVDs);
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof ComparisonValuesData) {
			ComparisonValuesData s = (ComparisonValuesData) element;
			return s.getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof ComparisonValuesData) {
			ComparisonValuesData s = (ComparisonValuesData) element;
			if(s.getChildren() == null){
				return ODIEConstants.ONTOLOGIES_LABEL.equals(s.getLabel());
			}
			return !s.getChildren().isEmpty();
		}
		return false;
	}
}

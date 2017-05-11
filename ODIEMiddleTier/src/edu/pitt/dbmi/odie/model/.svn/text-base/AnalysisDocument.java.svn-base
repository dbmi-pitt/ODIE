/**
 * 
 */
package edu.pitt.dbmi.odie.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.uima.cas.CAS;

import edu.pitt.dbmi.odie.ODIEException;

/**
 * @author Girish Chavan
 * 
 */

@Entity
@Table(name="odie_analysis_document")
public class AnalysisDocument{
	public static final String STATUS_DONE = "Done";
	public static final String STATUS_PROCESSING = "Processing";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable=false, nullable=false)
	private Long id;
	
	@ManyToOne(optional=false)
	private Analysis analysis = null;

	@ManyToOne(optional=false, cascade=CascadeType.PERSIST)
	private Document document = null;
	
	@ManyToMany(mappedBy="analysisDocuments")
//	@ManyToMany(fetch=FetchType.LAZY)
//    @JoinTable(name = "odie_dp_analysis_doc",
//            joinColumns = @JoinColumn(name="analysis_document_id"),
//            inverseJoinColumns = @JoinColumn(name="datapoint_id"))
	private List<Datapoint> datapoints = new ArrayList<Datapoint>();
	
	private String status;
	
	public static final int METADATA_COLUMN_LENGTH = 50000000;
	@Column(length=METADATA_COLUMN_LENGTH)
	private String metadata;
	
	public static final int CAS_COLUMN_LENGTH = 50000000;
	@Column(length=CAS_COLUMN_LENGTH)
	private String serializedCas;
	
	@Transient
	private CAS cas;

	@Transient
	private List<Suggestion> suggestions;
	
	
	public List<Suggestion> getSuggestions() {
		return suggestions;
	}

	public void setSuggestions(List<Suggestion> suggestions) {
		this.suggestions = suggestions;
	}

	public List<Datapoint> getDatapoints() {
		return datapoints;
	}

	public void setDatapoints(List<Datapoint> datapointList) {
		this.datapoints = datapointList;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CAS getCas() {
		return cas;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public void setCas(CAS cas) {
		this.cas = cas;
	}

	/*
	 * Required for Hibernate
	 */
	private AnalysisDocument(){
		
	}
	
	public AnalysisDocument(Analysis analysis, Document document) {
		super();
		this.analysis = analysis;
		this.document = document;
		this.status = AnalysisDocument.STATUS_PROCESSING;
		
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AnalysisDocument) {
			return (getAnalysis()
					.equals(((AnalysisDocument) obj).getAnalysis()) && getDocument()
					.equals(((AnalysisDocument) obj).getDocument()));
		}

		return super.equals(obj);
	}

	public Analysis getAnalysis() {

		return analysis;
	}

	public String getSerializedCAS() {
		return serializedCas;
	}

	/*
	 * Throws exception if the length of serialized CAS is greater than the column
	 * length.
	 */
	public void setSerializedCAS(String serializedCAS) throws ODIEException {
		this.serializedCas = serializedCAS;
//		if(serializedCas!=null)
//			if(serializedCAS.length()> CAS_COLUMN_LENGTH)
//				throw new ODIEException("Serialized CAS Length is larger than database column length. " +
//						"Data loss might occur when saved to the database.");
	}

//	public Set<AnnotationFS> getAnnotations() {
//		return getCas().getAnnotationIndex()
//	}

	public Document getDocument() {
		return document;
	}


	public String getStatus() {
		return status;
	}

	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}

//	public void setAnnotations(Set<AnnotationFS> annotations) {
//		this.annotations = annotations;
//	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void addDatapoint(Datapoint datapoint) {
		if(!datapoints.contains(datapoint))
			datapoints.add(datapoint);
		
	}

	public HashMap<String, Suggestion> getUniqueSuggestions() {
		if(getSuggestions() == null)
			return new HashMap();
			
		HashMap<String,Suggestion> map = new HashMap<String, Suggestion>();
		
		List<Suggestion> slist = getSuggestions();
		for(Suggestion s:slist){
			Suggestion ss = map.get(s.getNerNegative());
			
			if(ss==null){
				ss = new Suggestion();
				ss.setAnalysis(analysis);
				ss.setAggregate(true);
				ss.setNerNegative(s.getNerNegative());
				map.put(ss.getNerNegative(), ss);
				
			}
			if(ss.getScore()<s.getScore())
				ss.setScore(s.getScore());
		}
		
		return map;
			
	}
	
	public List<Suggestion> getSuggestionsForAggregate(Suggestion as){
		List<Suggestion> slist = getSuggestions();
		List<Suggestion> outlist = new ArrayList<Suggestion>();
		for(Suggestion s:slist){
			if(s.getNerNegative().equals(as.getNerNegative()))
				outlist.add(s);
		}
		return outlist;
	}
	
}

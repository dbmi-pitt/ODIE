package edu.pitt.dbmi.odie.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;

import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;

@Entity
@Table(name="odie_datapoint")
public class Datapoint implements Comparable<Datapoint> {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable=false, nullable=false)
	private Long id;
	
	@Column(name="ontologyURI") 
	String ontologyURIString;
	
	@Column(name="conceptURI")
	String conceptURIString;
	
	@Column(nullable=false)
	String type;
	
	int documentFrequency = 0;
	int occurences = 0;
	
	@ManyToOne(optional=false)
	Analysis analysis;

	@ManyToMany(cascade=CascadeType.PERSIST,fetch=FetchType.LAZY)
    @JoinTable(name = "odie_dp_analysis_doc",
            joinColumns = @JoinColumn(name="datapoint_id"),
            inverseJoinColumns = @JoinColumn(name="analysis_document_id"))
    
    List<AnalysisDocument> analysisDocuments = new ArrayList<AnalysisDocument>();
	
	@Transient
	protected IClass conceptClass;
	
	@Transient
	Comparator comparator;


	@Transient
	int sort;

	public Long getId() {
		return id;
	}

	private void setId(Long id) {
		this.id = id;
	}

	public Comparator getComparator() {
		return comparator;
	}

	public void setComparator(Comparator comparator) {
		this.comparator = comparator;
	}

	private Datapoint(){
		
	}

	public Datapoint(String type, String conceptURIString, AnalysisDocument ad){
		
		setType(type);
		setConceptURIString(conceptURIString);
		this.analysis = ad.getAnalysis();
		analysis.addDatapoint(this);
		
		addAnalysisDocument(ad);
	}


	
	

	public String getType() {
		return type;
	}

	public String getOntologyURIString() {
		return ontologyURIString;
	}

	private void setOntologyURIString(String ontologyURIString) {
		this.ontologyURIString = ontologyURIString;
	}

	public String getConceptURIString() {
		return conceptURIString;
	}

	public void setConceptURIString(String conceptURIString) {
		assert conceptURIString.contains("#");

		this.conceptURIString = conceptURIString;
		this.ontologyURIString = conceptURIString.substring(0,conceptURIString.lastIndexOf("#"));
	}

	public void setType(String type) {
		this.type = type;
	}

	public Datapoint(String type, AnalysisDocument ad){
		this(type,null,ad);
	}

	public void addAnalysisDocument(AnalysisDocument ad) throws IllegalArgumentException {
		occurences++;
		if(!this.getAnalysisDocuments().contains(ad)){
			if(getAnalysis() != ad.getAnalysis())
				throw new IllegalArgumentException("The AnalysisDocument's Analysis does not match this Datapoint's Analysis.");
			
			getAnalysisDocuments().add(ad);
			ad.addDatapoint(this);
			
			documentFrequency++;
		}
	}

	public Analysis getAnalysis() {
		return analysis;
	}

	private void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}

	public List<AnalysisDocument> getAnalysisDocuments() {
		return analysisDocuments;
	}

	private void setAnalysisDocuments(List<AnalysisDocument> analysisDocuments) {
		this.analysisDocuments = analysisDocuments;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Datapoint dp) {
		if(comparator!=null)
			return comparator.compare(this, dp);
		else
			return getConceptURIString().compareTo(dp.getConceptURIString());
	}

//	@Override
//	public boolean equals(Object o) {
//		if (o instanceof Datapoint) {
//			return ((Datapoint) o).getConceptClassURI().equals(
//					this.getConceptClassURI());
//		} else
//			return super.equals(o);
//	}

	public IClass getConceptClass() {
		return conceptClass;
	}


	
	public IOntology getOntology() {
		IClass c = getConceptClass();
		if (c != null)
			return c.getOntology();
		else
			return null;
	}

	public void setConceptClass(IClass conceptClass) {
		this.conceptClass = conceptClass;
	}


	public String getConceptId() {
		if(conceptURIString !=null)
			return conceptURIString.substring(conceptURIString.indexOf("#")+1);
		else
			return null;
	}

	public int getDocumentFrequency() {
		return documentFrequency;
	}

	public void setDocumentFrequency(int documentFrequency) {
		this.documentFrequency = documentFrequency;
	}

	public int getOccurences() {
		return occurences;
	}

	public void setOccurences(int occurences) {
		this.occurences = occurences;
	}
	
	
}

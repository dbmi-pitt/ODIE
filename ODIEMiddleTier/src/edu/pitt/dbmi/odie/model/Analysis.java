package edu.pitt.dbmi.odie.model;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.util.ProcessTrace;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.terminology.Terminology;
import edu.pitt.terminology.lexicon.Concept;


@Entity
@Table(name="odie_analysis")
public class Analysis{

	//////// Persisted Fields
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable=false, nullable=false)
	private Long id;

	@Column(nullable=false, unique=true)
	private String name = "New Analysis";
	
	private String databaseName;
	
	private String description;
	
	public static final int METADATA_COLUMN_LENGTH = 5000000;
	@Column(length=METADATA_COLUMN_LENGTH)
	private String metadata;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="analysis_engine_id")
	private AnalysisEngineMetadata analysisEngineMetadata;
	
	@ManyToOne(optional=true)
	@JoinColumn(name="lexset_id")
	private LexicalSet lexicalSet;
	
	@ManyToOne(optional=true)
	@JoinColumn(name="proposal_lexset_id")
	private LexicalSet proposalLexicalSet;
	
	@OneToMany(cascade={CascadeType.REMOVE}, mappedBy="analysis", fetch=FetchType.LAZY)
	private List<AnalysisDocument> analysisDocuments = new ArrayList<AnalysisDocument>() ;;
	
	@OneToMany(mappedBy="analysis", cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
	private List<Datapoint> datapoints = new ArrayList<Datapoint>();
	
	/////// Transient Fields 
	@Transient
	boolean hasNewProposals = false;

	@Transient
	private Terminology terminology;
	
	@Transient
	Statistics stats = null;

	
	public Analysis() {}
	
	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
	
	public LexicalSet getLexicalSet() {
		return lexicalSet;
	}

	public void setLexicalSet(LexicalSet lexicalSet) {
		this.lexicalSet = lexicalSet;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Analysis) {
			return getName().equals(((Analysis) obj).getName());
		}
		return super.equals(obj);
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	/*
	 * Convenience method for getAnalysisEngineMetadata().getAnalysisEngine()
	 */
	public AnalysisEngine getAnalysisEngine() {
		return analysisEngineMetadata.getAnalysisEngine();
	}
	

	public String getDescription() {
		return description;
	}


	public List<AnalysisDocument> getAnalysisDocuments() {
		return this.analysisDocuments;
	}

	public List<Datapoint> getDatapoints() {
		return datapoints;
	}

	public void setDatapoints(List<Datapoint> datapoints) {
		this.datapoints = datapoints;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pitt.dbmi.odie.model.IAnalysis#getDocumentAnalyses(java.lang.String)
	 */
	public List<AnalysisDocument> getAnalysisDocuments(String status) {
		List<AnalysisDocument> outlist = new ArrayList<AnalysisDocument>();
		for (AnalysisDocument da : getAnalysisDocuments()) {
			if (da.getStatus().equals(status))
				outlist.add(da);
		}
		return outlist;
	}

	public Document[] getDocumentsForConcept(Concept concept) {
		return null;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Concept[] getNewConcepts() {
		return null;
	}

	public Terminology getTerminology() {
		return terminology;
	}


	public boolean hasNewProposals() {
		return hasNewProposals;
	}

	


	public void setDescription(String description) {
		this.description = description;
	}

	public void setAnalysisDocuments(List<AnalysisDocument> documents) {
		this.analysisDocuments = documents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pitt.dbmi.odie.model.IAnalysis#setHasNewProposals(boolean)
	 */
	public void setHasNewProposals(boolean hasProposals) {
		hasNewProposals = hasProposals;
	}

	private void setId(Long id) {
		this.id = id;
	}



	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pitt.dbmi.odie.model.IAnalysis#createNewProposal(java.lang.String)
	 */

	public void setTerminology(Terminology terminology) {
		this.terminology = terminology;
	}

	public void addAnalysisDocument(AnalysisDocument ad) {
		analysisDocuments.add(ad);
		ad.setAnalysis(this);
	}

	public AnalysisEngineMetadata getAnalysisEngineMetadata() {
		return analysisEngineMetadata;
	}

	public void setAnalysisEngineMetadata(
			AnalysisEngineMetadata analysisEngineMetadata) {
		this.analysisEngineMetadata = analysisEngineMetadata;
	}

	public void addDatapoint(Datapoint datapoint) {
		if(!getDatapoints().contains(datapoint))
			getDatapoints().add(datapoint);
		
	}

	public LexicalSet getProposalLexicalSet() {
		return proposalLexicalSet;
	}

	public void setProposalLexicalSet(LexicalSet proposalLexicalSet) {
		this.proposalLexicalSet = proposalLexicalSet;
	}


	@Transient
	List<ProcessTrace> processTraceList = new ArrayList<ProcessTrace>();
	public void addPerformanceReport(ProcessTrace performanceReport) {
		processTraceList.add(performanceReport);
	}
	
	public List<ProcessTrace> getPerformanceReports(){
		return processTraceList;
	}

	public List<LanguageResource> getLanguageResources() {
		List<LanguageResource> lrList = new ArrayList<LanguageResource>();
		
		if(lexicalSet!=null)
			for(LexicalSetLanguageResource lslr:lexicalSet.getLexicalSetLanguageResources())
				lrList.add(lslr.getLanguageResource());
		
		if(proposalLexicalSet!=null)
			for(LexicalSetLanguageResource lslr:proposalLexicalSet.getLexicalSetLanguageResources())
				lrList.add(lslr.getLanguageResource());
		
		return lrList;
	}

	public String getType() {
		return getAnalysisEngineMetadata().getType();
	}

	public Statistics getStatistics() {
		if(stats==null){
			initStatisticsObj();
		}
		return stats;
	}

	public void refreshMetadataFromStatistics(){
		setMetadata(generateMetadataXML(stats));
	}
	
	private String generateMetadataXML(Statistics s) {
		Document doc = buildMetadataShellDocument();
		
		addAnalysisStatisticsElements(s, doc);
		addOntologyStatisticsElements(s, doc);

		XMLOutputter o = new XMLOutputter(Format.getPrettyFormat());
		StringWriter w = new StringWriter();
		try {
			o.output(doc, w);
			return w.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	private void addOntologyStatisticsElements(Statistics s, Document doc) {
		Element statistics = doc.getRootElement().getChild(ODIEConstants.XMLELEMENT_STATISTICS);
		Element anElement = statistics.getChild(ODIEConstants.XMLELEMENT_ANALYSIS);
		for(Statistics os:s.ontologyStatistics){
			Element ontology = new Element(ODIEConstants.XMLELEMENT_ONTOLOGY);
			ontology.setAttribute(new Attribute("uri", os.context.toString()));
			addStatisticsElements(os, ontology);
			
			anElement.addContent(ontology);
		}
	}

	private void addAnalysisStatisticsElements(Statistics s, Document doc) {
		Element statistics = doc.getRootElement().getChild(ODIEConstants.XMLELEMENT_STATISTICS);

		Element	anElement = new Element(ODIEConstants.XMLELEMENT_ANALYSIS);
		anElement.setAttribute(ODIEConstants.XMLATTRIBUTE_ID, ""+getId());
		statistics.addContent(anElement);
		
		addStatisticsElements(s, anElement);
	}

	

	private Document buildMetadataShellDocument() {
		Element root = new Element(ODIEConstants.XMLELEMENT_ROOT);
		Document doc = new Document(root);
		Element statistics = new Element(ODIEConstants.XMLELEMENT_STATISTICS);
		root.addContent(statistics);
		
		return doc;
		
	}
	
	private void initStatisticsObj() {
		org.jdom.Document doc;
		stats = new Statistics();
		stats.context = this;
		if(metadata!=null){
			SAXBuilder builder = new SAXBuilder();
			try {
				doc = builder.build(new StringReader(metadata));
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			
			initAnalysisStatistics(doc);
			initOntologyStatistics(doc);
		}
	}

	private void initOntologyStatistics(Document doc) {
		Element statistics = doc.getRootElement().getChild(ODIEConstants.XMLELEMENT_STATISTICS);
		Element anElement = statistics.getChild(ODIEConstants.XMLELEMENT_ANALYSIS);
		
		List<Element> ontologyElements = anElement.getChildren(ODIEConstants.XMLELEMENT_ONTOLOGY);
		
		List<Statistics> ontologyStats = new ArrayList<Statistics>();
		for(Element oE:ontologyElements){
			Statistics s = new Statistics();
			try {
				s.context = new URI(oE.getAttributeValue("uri"));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			
			initStatisticsFromElements(oE,s);
			ontologyStats.add(s);
		}
			
		stats.ontologyStatistics = ontologyStats;		
	}

	private void initAnalysisStatistics(org.jdom.Document doc) {
		Element statistics = doc.getRootElement().getChild(ODIEConstants.XMLELEMENT_STATISTICS);
		Element anElement = statistics.getChild(ODIEConstants.XMLELEMENT_ANALYSIS);
		
		stats.context = this;
		initStatisticsFromElements(anElement,stats);
	}

	private void addStatisticsElements(Statistics s, Element anElement) {
		Element nounPhraseCount = new Element(ODIEConstants.XMLELEMENT_NP_COUNT);
		nounPhraseCount.setText(""+s.nounPhraseCount);
		Element namedEntityCount = new Element(ODIEConstants.XMLELEMENT_NAMED_ENTITY_COUNT);
		namedEntityCount.setText(""+s.uniqueConceptsCount);
		Element charCount = new Element(ODIEConstants.XMLELEMENT_CHARACTER_COUNT);
		charCount.setText(""+s.totalCharCount);
		Element coveredCharCount = new Element(ODIEConstants.XMLELEMENT_COVERED_CHARACTER_COUNT);
		coveredCharCount.setText(""+s.coveredCharCount);
		Element uniqueConceptsCount = new Element(ODIEConstants.XMLELEMENT_UNIQUE_CONCEPTS_COUNT);
		uniqueConceptsCount.setText(""+s.uniqueConceptsCount);
		
		
		anElement.addContent(nounPhraseCount);
		anElement.addContent(namedEntityCount);
		anElement.addContent(charCount);
		anElement.addContent(coveredCharCount);
		anElement.addContent(uniqueConceptsCount);
	}
	
	private void initStatisticsFromElements(Element anElement, Statistics s) {
		Element nounPhraseCount = anElement.getChild(ODIEConstants.XMLELEMENT_NP_COUNT);
		Element namedEntityCount = anElement.getChild(ODIEConstants.XMLELEMENT_NAMED_ENTITY_COUNT);
		Element charCount = anElement.getChild(ODIEConstants.XMLELEMENT_CHARACTER_COUNT);
		Element coveredCharCount = anElement.getChild(ODIEConstants.XMLELEMENT_COVERED_CHARACTER_COUNT);
		Element uniqueConceptsCount = anElement.getChild(ODIEConstants.XMLELEMENT_UNIQUE_CONCEPTS_COUNT);
		
		s.nounPhraseCount = Long.parseLong(nounPhraseCount.getText());
		s.namedEntityCount = Long.parseLong(namedEntityCount.getText());
		s.totalCharCount = Long.parseLong(charCount.getText());
		s.coveredCharCount = Long.parseLong(coveredCharCount.getText());
		s.uniqueConceptsCount = Long.parseLong(uniqueConceptsCount.getText());
	}

	public void resetStatistics() {
		stats = null;
	}
}

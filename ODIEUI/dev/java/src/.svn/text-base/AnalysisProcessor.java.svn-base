package edu.pitt.dbmi.odie.ui.workers;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import edu.pitt.dbmi.odie.middletier.Configuration;
import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.AnalysisLanguageResource;
import edu.pitt.dbmi.odie.model.DatapointComparator;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.model.LanguageResource;
import edu.pitt.dbmi.odie.model.Statistics;
import edu.pitt.dbmi.odie.model.DatapointComparator.SortOrder;
import edu.pitt.dbmi.odie.model.utils.StatisticsUpdater;
import edu.pitt.dbmi.odie.model.utils.SuggestionTracker;
import edu.pitt.dbmi.odie.server.discovery.ODIE_OntologyEnrichmentConceptDiscoverer;
import edu.pitt.dbmi.odie.server.indexfinder.ODIE_IndexFinderNamedEntityRecognizer;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.IOntologyException;
import edu.pitt.ontology.IRepository;

public class AnalysisProcessor implements IRunnableWithProgress {
	Logger logger = Logger.getLogger(this.getClass());

	Analysis analysis;
	ODIE_IndexFinderNamedEntityRecognizer nerRecognizer = null;
	ODIE_IndexFinderNamedEntityRecognizer proposalNERRecognizer = null;
	ODIE_OntologyEnrichmentConceptDiscoverer conceptDiscoverer = null;
	
	SuggestionTracker tracker = new SuggestionTracker();
	
	boolean isOE = false;
	private boolean proposalOnly;
	private boolean freshRun;
	
	public AnalysisProcessor(Analysis analysis, boolean freshRun, boolean repeatProposalOnly) {
		this.analysis = analysis;
		this.freshRun = freshRun;
		this.proposalOnly = repeatProposalOnly;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		
		isOE = analysis.getType().equals(Analysis.TYPE_OE);
		boolean noPO = analysis.getProposalOntology()==null;
		
		
		List<AnalysisDocument> docs;
		
		if(freshRun || proposalOnly){
			docs = analysis.getAnalysisDocuments();
			
			for(AnalysisDocument da:docs){
				da.setStatus(AnalysisDocument.STATUS_PENDING);
			}
		}
		else{
			docs = analysis.getAnalysisDocuments(AnalysisDocument.STATUS_PENDING);
		}
		
		int jobCount = 1; //saving analysis
		jobCount+=docs.size(); //processing documents
		jobCount += docs.size(); //saving documents
		
		if(isOE){
			jobCount += docs.size(); //processing documents for new concepts
		}
		
		if(!proposalOnly && nerRecognizer==null)
			jobCount++;
		
		if(isOE && proposalNERRecognizer==null)
			jobCount++;
		
		if(isOE && !proposalOnly &&  conceptDiscoverer==null)
			jobCount++;
		
		if(isOE)
			jobCount++; //create proposal ontology
		
		
		monitor.beginTask("Running Analysis: ", jobCount);

		if(!proposalOnly){
			if(!initNERPipeline(monitor)){
				monitor.done();
				return;
			}
			monitor.worked(1);
			if(monitor.isCanceled())
				return;
		}
		
		if(isOE && proposalNERRecognizer==null){
			if(!initProposalPipeline(monitor)){
				monitor.done();
				return;
			}
			monitor.worked(1);
			if(monitor.isCanceled())
				return;
		}
		
		if(isOE && !proposalOnly &&  conceptDiscoverer==null){
			if(!initDiscovererPipeline(monitor)){
				monitor.done();
				return;
			}
			monitor.worked(1);
			if(monitor.isCanceled())
				return;
		}
		
		if(isOE){
			if(noPO){
				if(!createProposalOntology(monitor)){
					monitor.done();
					return;
				}
				
			}
			else{
				try {
					((IOntology)analysis.getProposalOntology().getLanguageResource().getResource()).save();
				} catch (IOntologyException e) {
					e.printStackTrace();
					monitor.done();
					return;
				}
			}
			monitor.worked(1);
			if(monitor.isCanceled())
				return;
		}
		else{
			
		}
		
		if(!saveAnalysis(monitor)){
			monitor.done();
			return;
		}
		monitor.worked(1);
		if(monitor.isCanceled())
			return;
		
		
		if(!runAnalysis(monitor)){
			monitor.done();
			return;
		}
		monitor.worked(1);
		if(monitor.isCanceled())
			return;
		
    	analysis.setHasNewProposals(false);
        monitor.done();
	}

	/**
	 * @param monitor
	 */
	private boolean initDiscovererPipeline(IProgressMonitor monitor) {
		monitor.subTask("Initializing concept discovery pipeline...");
		boolean success = false;
		
		Configuration config = Activator.getDefault().getConfiguration();
		conceptDiscoverer = new ODIE_OntologyEnrichmentConceptDiscoverer();
		for (int rdx = 0 ; rdx < ODIE_OntologyEnrichmentConceptDiscoverer.definitionTable.length ; rdx++) {
			conceptDiscoverer.addIncludedClass(ODIE_OntologyEnrichmentConceptDiscoverer.definitionTable[rdx][0]);
		}
		conceptDiscoverer.addIncludedClass("NP");
//		conceptDiscoverer.addIncludedClass("Sentence");
		conceptDiscoverer.setGateHome(config.getGATEHome());
		success = conceptDiscoverer.initialize();
		
		return success;
	}

	/**
	 * @param monitor
	 */
	private boolean initProposalPipeline(IProgressMonitor monitor) {
		monitor.subTask("Initializing proposal pipeline...");
		boolean success = false;
		
		Configuration config = Activator.getDefault().getConfiguration();
		proposalNERRecognizer = new ODIE_IndexFinderNamedEntityRecognizer();
		proposalNERRecognizer.setGateHome(config.getGATEHome());
		proposalNERRecognizer.setDynamic(true);
		proposalNERRecognizer.setCurrentAnalysis(analysis) ;
		success = proposalNERRecognizer.initialize();

		return success;
	}

	/**
	 * @param monitor
	 */
	private boolean initNERPipeline(IProgressMonitor monitor) {
		monitor.subTask("Initializing NER pipeline...");
		boolean success = false;
		
		Configuration config = Activator.getDefault().getConfiguration();
		nerRecognizer = new ODIE_IndexFinderNamedEntityRecognizer();
		nerRecognizer.setCurrentAnalysis(analysis);
		nerRecognizer.setDataBaseDriver(config.getDatabaseDriver());
		nerRecognizer
				.setDataBaseUrl(config.getDatabaseURL());
		nerRecognizer.setDatabaseIfTablePrefix("if_");
		nerRecognizer.setDatabaseOntologyTablePrefix("ontology_");
		nerRecognizer.setDatabaseUserName(config.getUsername());
		nerRecognizer.setDatabasePassword(config.getPassword());
		nerRecognizer.setGateHome(config.getGATEHome());
		nerRecognizer.setDynamic(false);
		success = nerRecognizer.initialize();
		
		return success;
	}

	/**
	 * @param monitor
	 */
	private boolean createProposalOntology(IProgressMonitor monitor) {
		monitor.subTask("Creating proposal ontology...");
		boolean success = false;
		URI uri;
		try {
			IRepository rep = Activator.getDefault().getMiddleTier().getRepository();
			
			uri = new URI("http://odie.dbmi.pitt.edu/" + analysis.getName().replaceAll(" ", "_") + "_Proposal");
			IOntology o = rep.createOntology(uri);
			rep.importOntology(o);
			
			LanguageResource lr = new LanguageResource();
			lr.setResource(o);
			
			AnalysisLanguageResource alr = new AnalysisLanguageResource();
			alr.setAnalysis(analysis);
			alr.setLanguageResource(lr);
			alr.setIsProposalOntology(true);
			
			this.analysis.setProposalOntology(alr);
			
			success = true;
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
			success = false;
		} catch (IOntologyException e) {
			e.printStackTrace();
			success = false;
		}
		
		return success;
		
	}

	/**
	 * @param monitor
	 */
	private boolean saveAnalysis(IProgressMonitor monitor) {
		MiddleTier mt = Activator.getDefault().getMiddleTier();
		boolean success = false;
		
		//////////////////// Save Analysis
		

		if(analysis.getID()>0){
			monitor.subTask("Updating analysis...");
			success = mt.updateAnalysis(analysis);
		}
		else{
			monitor.subTask("Saving analysis...");
			Statistics stats = new Statistics();
	    	analysis.setStatistics(stats);
	    	
			success = mt.addAnalysis(analysis);
		}
			
		return success;
		
	}

	private boolean runAnalysis(IProgressMonitor monitor) {
    	
		logger.debug("Running analysis");
		MiddleTier mt = Activator.getDefault().getMiddleTier();

		/////////////////// Initialize pipeline for processing
		if(isOE){
			monitor.subTask("Updating Proposal pipeline with proposals...");
			updateNERInMemory();
		}
		
		/////////////////// Processing documents

    	List<AnalysisDocument> docs = analysis.getAnalysisDocuments(AnalysisDocument.STATUS_PENDING);
    	StatisticsUpdater updater = new StatisticsUpdater(analysis.getStatistics());
    	
    	//track suggestions only if analysis type is OE
    	updater.setTrackSuggestions(isOE);
    	
		for(AnalysisDocument da : docs){
			
			if(!proposalOnly){
				monitor.subTask("Running NER pipeline on " + da.getDocument().getName() + "...");
				nerRecognizer.processDocument(da);
			}
			
			if(isOE){
				monitor.subTask("Running Proposal pipeline on " + da.getDocument().getName() + "...");
				proposalNERRecognizer.processDocument(da);
			}

			monitor.worked(1);
			if(monitor.isCanceled())
				return false;

			if(isOE && !proposalOnly){
				monitor.subTask("Running ConceptDiscovery pipeline on " + da.getDocument().getName() + "...");
				conceptDiscoverer.executeDocument(da);
				monitor.worked(1);
				if(monitor.isCanceled())
					return false;
			}
			
			monitor.subTask("Saving " + da.getDocument().getName() + "...");
			updater.updateDatapoints(da.getAnnotations());
			da.setStatus(AnalysisDocument.STATUS_DONE);
			mt.updateDocumentAnalysis(da);
			monitor.worked(1);
			if(monitor.isCanceled())
				return false;
    	}
		
		Collections.sort(
				analysis.getStatistics().getDatapoints(),
				new DatapointComparator(SortOrder.ONTOLOGY_FREQUENCY,analysis.getStatistics())
				);
		
//		if(isOE && (freshRun || proposalOnly)){
//			updater.redoSuggestionsRanking();
//		}
		if(isOE && proposalOnly){
			updater.redoSuggestionsRanking();
		}
		
		if(!proposalOnly)
			nerRecognizer.closeDatabaseConnection();
		
		
		return true;
	}

	/**
	 * 
	 */
	private boolean updateNERInMemory() {
		//clear the in memory indexfinder if it is already initialized
		proposalNERRecognizer.getInMemoryIndexFinder().clear();

		IOntology o = (IOntology) analysis.getProposalOntology().getLanguageResource().getResource();
		for(Iterator it=o.getAllClasses();it.hasNext();){
			IClass c = (IClass) it.next();
			if(c==null)
				break;
			String uriStr = c.getURI().toString();
			logger.debug("Adding " + uriStr + " to InMemoryIF");
			proposalNERRecognizer.getInMemoryIndexFinder().addClass(uriStr);
		}
		return true;
	}
}

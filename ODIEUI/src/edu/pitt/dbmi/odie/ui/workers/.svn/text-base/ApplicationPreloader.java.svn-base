package edu.pitt.dbmi.odie.ui.workers;

import java.io.File;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.ASCIIFoldingFilter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter.Side;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.LanguageResource;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.utils.GeneralUtils;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.IRepository;
import edu.pitt.terminology.lexicon.Concept;

public class ApplicationPreloader extends Thread implements
		IRunnableWithProgress {

	Logger logger = Logger.getLogger(this.getClass());
	IProgressMonitor monitor;
	private boolean success;

	public ApplicationPreloader(IProgressMonitor monitor) {
		super();
		this.monitor = monitor;
	}

	@Override
	public void run() {
		try {
			run(monitor);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		monitor.beginTask("Loading ODIE...", 10);

		monitor.worked(1);
		loadMiddleTier(new SubProgressMonitor(monitor, 2));
		if (!isSuccessful()) {
			monitor.done();
			return;
		}

		preloadOntologies(new SubProgressMonitor(monitor, 7));
		if (!isSuccessful()) {
			monitor.done();
			return;
		}
		logger.info(Platform.getLocation().toOSString());
	}

	private void loadMiddleTier(IProgressMonitor monitor) {
		monitor.beginTask("Connecting to database", 1);
		monitor.setTaskName("Connecting to database");
		try {
			MiddleTier mt = edu.pitt.dbmi.odie.ui.Activator.getDefault()
					.getMiddleTier();
			success = mt != null;
			monitor.worked(1);
		} catch (Exception e) {
			logger.error("Could not initialize MiddleTier.");
			success = false;
			return;
		} finally {
			monitor.done();
		}
	}

	private void preloadOntologies(IProgressMonitor monitor) {
		MiddleTier mt = edu.pitt.dbmi.odie.ui.Activator.getDefault()
				.getMiddleTier();
		List<LanguageResource> l = mt
				.getLanguageResources(IRepository.TYPE_ONTOLOGY);
		if (l.isEmpty()) {
			success = true;
			monitor.done();
		}
		monitor.beginTask("Preloading ontologies", l.size());
		monitor.setTaskName("Preloading ontologies");
		monitor.worked(1);
		for (LanguageResource lr : l) {
			monitor.setTaskName("Loading " + lr.getName() + "...");
//			try {
				GeneralUtils.initLanguageResourceIfRequired(lr);

//				((IOntology) lr.getResource()).load();

				monitor.subTask("checking index");
				if (mt.getIndex(lr) == null) {
					monitor.subTask("creating index");
					createIndex(lr,new SubProgressMonitor(monitor, 1));
				}
				monitor.subTask("");

//			} catch (IOntologyException e) {
//				e.printStackTrace();
//			}
			monitor.worked(1);
		}
		success = true;
		monitor.done();

	}

public boolean createIndex(LanguageResource lr, SubProgressMonitor monitor) {
		
		monitor.beginTask("creating index",IProgressMonitor.UNKNOWN);
		String indexDir = GeneralUtils.getOntologySearchIndexLocation(lr);
		File f = new File(indexDir);
		boolean status = false;
		
		if(!f.exists())
			if(!f.mkdir())
				return false;
		
		IndexWriter iwriter = null;
		try {
			FSDirectory directory = FSDirectory.open(f);
			Analyzer a = new Analyzer() {

				public TokenStream tokenStream(String fieldName,
			                                Reader reader) {
                        TokenStream result = new StandardTokenizer(Version.LUCENE_29,reader);

                        Set englishStopWords = new HashSet(Arrays.asList(ODIEConstants.ENGLISH_STOP_WORDS));
                        
                        result = new StandardFilter(result);
                        result = new LowerCaseFilter(result);
                        result = new ASCIIFoldingFilter(result);
                        result = new StopFilter(StopFilter.getEnablePositionIncrementsVersionDefault(Version.LUCENE_29),result,
                        		englishStopWords);
                        result = new EdgeNGramTokenFilter(
                                result, Side.FRONT,1, 20);

                        return result;
                }
	        };

			iwriter = new IndexWriter(directory, a, MaxFieldLength.UNLIMITED);
			
			IOntology o = ((IOntology)lr.getResource());
			Iterator it = o.getAllClasses();
			
			int count = 0;
			while(it.hasNext()){
				IClass c = (IClass) it.next();
				monitor.subTask("Processing(" + count + "):" + c.getName());
				org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
				
				String content = c.getName();
				Concept concept = c.getConcept();
				
				content += " " + concept.getName();
				for(String s: concept.getSynonyms())
					content += " " + s;
				
				doc.add(new Field(ODIEConstants.LUCENE_ONTOLOGY_FIELD_URI, c.getURI().toASCIIString(), Store.YES,Index.NO));
				doc.add(new Field(ODIEConstants.LUCENE_ONTOLOGY_FIELD_CONTENT, content, Field.Store.NO,Field.Index.ANALYZED));
				
				iwriter.addDocument(doc);
				
				count++;
				
			}
			logger.info("Indexing finished. Indexed " + count + " concepts");
			iwriter.optimize();
			
		} catch (Exception e) {
			e.printStackTrace();
			status = false;
		}
		finally{
			monitor.done();
			if(iwriter!=null){
				try {
					iwriter.close();
					status = true;
				} catch (Exception e) {
					e.printStackTrace();
					status = false;
				}
			}
			status = false;
		}			status = true;

		return status;
		
		
	}
	public boolean isSuccessful() {
		return success;
	}

	Object semaphore = new Object();

}

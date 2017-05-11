package edu.pitt.dbmi.odie.uima.lucenefinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.FSDirectory;

import edu.pitt.dbmi.odie.server.indexfinder.ODIE_IndexFinderAnnotation;
import edu.pitt.dbmi.odie.server.indexfinder.ODIE_IndexFinderNode;
import edu.pitt.dbmi.odie.uima.lucenefinder.ae.stategy.ODIE_LuceneNerStrategyInterface;
import edu.pitt.dbmi.odie.uima.util.ODIE_FormatUtils;
import edu.pitt.text.tools.Stemmer;

public class ODIE_LuceneFinderStandaloneCoder {
	
	// LOG4J logger based on class name
	private Logger logger = Logger.getLogger(getClass().getName());

	private static final int CONST_MAX_NUMBER_CODERS = 1;
	
	protected String inputDeckPath = "C:/workspace/ws-odie/ODIEResearch/documents" ;

	protected String fsDirectoryPath = null;

	protected FSDirectory fsDirectory = null;

	protected Searcher searcher = null;
	
	protected ODIE_LuceneNerStrategyInterface strategyEngine;

	protected Stemmer stemmer = null;
	
	protected String isContiguousAsString = null ;
	
	protected String isOverlappingAsString = null ;
	
	protected int numberOfSubsumingHits = 0 ;
	
	protected int numberOfPartialHits = 0 ;
	
	protected int numberOfTotalDocuments = 0 ;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int coderIdx = 1 ; coderIdx <= CONST_MAX_NUMBER_CODERS ; coderIdx++) {
			String coderIdxAsString = ODIE_FormatUtils.formatIntegerAsDigitString(coderIdx, "000") ;
			String coderIndexPath = "C:/index_ncit";
			coderIndexPath = "T:/tmp/NCITLuceneFinderIndex" ;
			String inputDeckPath = "C:/DOCUME~1/mitchellkj/Desktop/odie_docs" ;
			ODIE_LuceneFinderStandaloneCoder coder = new ODIE_LuceneFinderStandaloneCoder() ;
			coder.setFsDirectoryPath(coderIndexPath) ;
			coder.setInputDeckPath(inputDeckPath) ;
			coder.setIsContiguousAsString("true") ;
			coder.setIsOverlappingAsString("false") ;
			coder.initialize() ;
			System.out.println(coderIndexPath + " ==> ") ;
			coder.process() ;
		}
	}
	
	
	
	public ODIE_LuceneFinderStandaloneCoder() {
	}
	
	public void initialize() {
	
		this.numberOfSubsumingHits = 0 ;
		this.numberOfPartialHits = 0 ;
		this.numberOfSubsumingHits = 0 ;
		
		openIndex();

		openSearcher();

		this.stemmer = new Stemmer();
		
		Boolean isContiguous = new Boolean(isContiguousAsString != null
				&& isContiguousAsString.toLowerCase().equals("true"));
		Boolean isOverlapping = new Boolean(isOverlappingAsString != null
				&& isOverlappingAsString.toLowerCase().equals("true"));
		
		if (!isOverlapping && isContiguous) {
			this.strategyEngine = new edu.pitt.dbmi.odie.uima.lucenefinder.ae.stategy.nonoverlapping.contiguous.ODIE_LuceneNerStategy(
					searcher);
		} else if (!isOverlapping && !isContiguous) {
			this.strategyEngine = new edu.pitt.dbmi.odie.uima.lucenefinder.ae.stategy.nonoverlapping.noncontiguous.ODIE_LuceneNerStategy(
					searcher);
		} else if (isOverlapping && isContiguous) {
			this.strategyEngine = new edu.pitt.dbmi.odie.uima.lucenefinder.ae.stategy.overlapping.contiguous.ODIE_LuceneNerStategy(
					searcher);
		} else if (isOverlapping && !isContiguous) {
			this.strategyEngine = new edu.pitt.dbmi.odie.uima.lucenefinder.ae.stategy.overlapping.noncontiguous.ODIE_LuceneNerStategy(
					searcher);
		}
	}
	
	protected void openIndex() {
		try {
			this.fsDirectory = FSDirectory.open(new File(this.fsDirectoryPath));
			logger.debug("Opened the index at " + this.fsDirectoryPath);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	protected void closeIndex() {
		try {
			this.fsDirectory.close();
		} catch (Exception x) {
			;
		}
	}

	protected void openSearcher() {
		logger.debug("Try to open the searcher with FSDirectory ==> "
				+ this.fsDirectory.getFile().getAbsolutePath());
		try {
			boolean readOnly = true ;
			this.searcher = new IndexSearcher(IndexReader.open(this.fsDirectory, readOnly));
			logger
					.debug("Succeeded in opening the searcher with FSDirectory ==> "
							+ this.fsDirectory.getFile().getAbsolutePath());
		} catch (Exception x) {
			logger.error(x.getMessage());
			x.printStackTrace();
			logger.error("Failed opening the searcher with FSDirectory ==> "
					+ this.fsDirectory.getFile().getAbsolutePath());
		}
	}

	
	/**
	 * Entry point for processing.
	 */
	public void process() {
		try {
			
			File inputDeckDirectory = new File(this.inputDeckPath) ;
			File[] directoryFiles = inputDeckDirectory.listFiles();
			
			for (File directoryFile : directoryFiles) {
				
				logger.debug("Processing input file " + directoryFile.getCanonicalPath()) ;
				
				FileReader inputDeckReader = new FileReader(directoryFile) ;
				
				BufferedReader bufferedInputDeckReader = new BufferedReader(inputDeckReader) ;
				String line = null ;
				while ((line=bufferedInputDeckReader.readLine()) != null) {
				
					Pattern tokenPattern = Pattern.compile("[a-zA-Z0-9_']+") ;
					Matcher matcher = tokenPattern.matcher(line) ;
					ArrayList<ODIE_IndexFinderAnnotation> annotations = new ArrayList<ODIE_IndexFinderAnnotation>() ;
					
					int inputCoveredLetterCount = 0 ;
					
					while (matcher.find()) {
						
						ODIE_IndexFinderAnnotation annotation = new ODIE_IndexFinderAnnotation() ;
						String tokenKind = "word";
						String tokenString = matcher.group() ;
						inputCoveredLetterCount += tokenString.length();
						tokenString = (tokenString != null) ? tokenString.toLowerCase()
								: null;
						if (tokenString == null) {
							continue;
						}
						
						int sPos = matcher.start();
						int ePos = matcher.end();
						ODIE_IndexFinderNode sNode = new ODIE_IndexFinderNode() ;
						sNode.setOffset(Long.valueOf(sPos)) ;
						ODIE_IndexFinderNode eNode = new ODIE_IndexFinderNode() ;
						eNode.setOffset(Long.valueOf(ePos)) ;
						annotation.setStartNode(sNode) ;
						annotation.setEndNode(eNode) ;
						
						annotation.setAnnotationSetName("Default");
						annotation.setAnnotationTypeName("Token");
						annotation.getFeatures().put("kind", tokenKind);
						annotation.getFeatures().put("string", tokenString);
						if (this.stemmer != null) {
							this.stemmer.add(tokenString);
							this.stemmer.stem();
							String normalizedForm = this.stemmer.getResultString();
							annotation.getFeatures().put("normalizedForm", normalizedForm);
						} 
						
						annotations.add(annotation) ;
					}
					
					int conceptCoveredLetterCount = 0 ;
					if (annotations.size() > 0) {
						this.strategyEngine.setSortedTokens(annotations);
						this.strategyEngine.execute();
						ArrayList<ODIE_IndexFinderAnnotation> resultingConcepts = this.strategyEngine
						.getResultingConcepts();
						for (ODIE_IndexFinderAnnotation concept : resultingConcepts) {
							System.out.println(concept);
							conceptCoveredLetterCount += concept.getEndNode().getOffset() - concept.getStartNode().getOffset();
						}
					}
					
					numberOfTotalDocuments++ ;
					if (conceptCoveredLetterCount == inputCoveredLetterCount) {
						numberOfSubsumingHits++ ;
						numberOfPartialHits++ ;
					}
					else if (conceptCoveredLetterCount > 0) {
						numberOfPartialHits++ ;
					}
					
				}
			}
			
			logger.info("Full hits: " + numberOfSubsumingHits) ;
			logger.info("Partial hits:" + numberOfPartialHits) ;
			logger.info("Total # gap terms:" + numberOfTotalDocuments) ;
			
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public String getInputDeckPath() {
		return inputDeckPath;
	}

	public void setInputDeckPath(String inputDeckPath) {
		this.inputDeckPath = inputDeckPath;
	}

	public String getFsDirectoryPath() {
		return fsDirectoryPath;
	}

	public void setFsDirectoryPath(String fsDirectoryPath) {
		this.fsDirectoryPath = fsDirectoryPath;
	}
	
	public String getIsContiguousAsString() {
		return isContiguousAsString;
	}

	public void setIsContiguousAsString(String isContiguousAsString) {
		this.isContiguousAsString = isContiguousAsString;
	}

	public String getIsOverlappingAsString() {
		return isOverlappingAsString;
	}

	public void setIsOverlappingAsString(String isOverlappingAsString) {
		this.isOverlappingAsString = isOverlappingAsString;
	}

}

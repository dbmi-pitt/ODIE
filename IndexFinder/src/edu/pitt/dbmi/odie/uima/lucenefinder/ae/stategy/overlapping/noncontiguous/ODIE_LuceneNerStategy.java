package edu.pitt.dbmi.odie.uima.lucenefinder.ae.stategy.overlapping.noncontiguous;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;

import edu.pitt.dbmi.odie.server.indexfinder.ODIE_IndexFinderAnnotation;
import edu.pitt.dbmi.odie.uima.lucenefinder.ae.stategy.ODIE_LuceneNerCandidate;
import edu.pitt.dbmi.odie.uima.lucenefinder.ae.stategy.ODIE_LuceneNerConceptTable;
import edu.pitt.dbmi.odie.uima.lucenefinder.ae.stategy.ODIE_LuceneNerStrategyInterface;
import edu.pitt.dbmi.odie.uima.lucenefinder.ae.utils.ODIE_StopWords;

public class ODIE_LuceneNerStategy implements ODIE_LuceneNerStrategyInterface {
	
	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_LuceneNerStategy.class);

	public static Comparator<ODIE_LuceneNerCandidate> candidateComparator = new Comparator<ODIE_LuceneNerCandidate>() {
		public int compare(ODIE_LuceneNerCandidate c1,
				ODIE_LuceneNerCandidate c2) {
			int result = c1.sPos - c2.sPos;
			result = (result == 0) ? c2.targetNumberOfWords
					- c1.targetNumberOfWords : result;
			result = (result == 0) ? c2.cName.length() - c1.cName.length()
					: result;
			result = (result == 0) ? c1.getStorageKey().compareTo(c2.getStorageKey()) : result;
			return result;
		}
	};
	
	private ODIE_LuceneNerConceptTable[] conceptsByStartPos = null ;

	private final ArrayList<ODIE_LuceneNerCandidate> candidates = new ArrayList<ODIE_LuceneNerCandidate>();

	private final TreeSet<ODIE_LuceneNerCandidate> finalists = new TreeSet<ODIE_LuceneNerCandidate>(
			candidateComparator);

	private ArrayList<ODIE_IndexFinderAnnotation> sentenceTokensAnnots = null;

	private Searcher searcher = null;

	private final ArrayList<ODIE_IndexFinderAnnotation> resultingConcepts = new ArrayList<ODIE_IndexFinderAnnotation>();

	public ODIE_LuceneNerStategy(Searcher searcher) {
		this.searcher = searcher;
	}

	public void setSortedTokens(
			ArrayList<ODIE_IndexFinderAnnotation> sentenceTokensAnnots) {
		this.sentenceTokensAnnots = sentenceTokensAnnots;
	}

	public void execute() {
		candidates.clear();
		finalists.clear();
		resultingConcepts.clear();
		allocateConceptTable();
		determineFinalists();
		determineLeftToRightMappingFromFinalists();
	}
	
	private void allocateConceptTable() {
		int viableTermCount = 0 ;
		for (ODIE_IndexFinderAnnotation annotation : this.sentenceTokensAnnots) {
			String word = (String) annotation.getFeatures().get("string");
			if (isAlphaNonStopword(word)) {
				viableTermCount++ ;
			}
		}
		this.conceptsByStartPos = new ODIE_LuceneNerConceptTable[viableTermCount] ;
		for (int idx = 0 ; idx < viableTermCount ; idx++) {
			this.conceptsByStartPos[idx] = new ODIE_LuceneNerConceptTable() ;
		}
	}

	private void determineFinalists() {
		try {

			int caret = 0; // caret is the relative position of a meaningful
			// word
			// in the sentence
			for (ODIE_IndexFinderAnnotation annotation : this.sentenceTokensAnnots) {
				if (isAlphaNonStopword(annotation.getCname())) {
					//
					// Search against the concept index for pertinent concept
					// for the current word
					//
					QueryParser queryParser = new QueryParser(Version.LUCENE_29,"delineatedTerms", new StandardAnalyzer(Version.LUCENE_29));
					Query query = queryParser.parse(annotation.getNormalizedForm());
					int maxHits = 10;
					
					TopDocs results = searcher.search(query,null,maxHits);
			    	 
				    if(results.totalHits>0){
				        ScoreDoc[] hits = results.scoreDocs;
				        for (ScoreDoc hit : hits) {
				        	org.apache.lucene.document.Document hitDoc = searcher.doc(hit.doc);
				        	ODIE_LuceneNerCandidate candidateTally = new edu.pitt.dbmi.odie.uima.lucenefinder.ae.stategy.overlapping.noncontiguous.ODIE_LuceneNerCandidate() ;
							candidateTally.loadCandidateFromIndexedDocument(hitDoc, annotation, caret);
							this.conceptsByStartPos[caret].put(candidateTally.getStorageKey(), candidateTally) ;
							for (int jdx = 0 ; jdx < caret ; jdx++) {
								ODIE_LuceneNerCandidate accumulatingCandidate = (ODIE_LuceneNerCandidate) this.conceptsByStartPos[jdx].get(candidateTally.getStorageKey()) ;
								if (accumulatingCandidate != null) {
									accumulatingCandidate.tally(annotation, caret) ;
								}
							}
				        }
				    }
				    caret++;
					
					
				}

			}
			
			// Partition candidates by their ultimate status at this
			// caret
			// position
			this.finalists.clear();
			for (int idx = 0 ; idx < this.conceptsByStartPos.length ; idx++) {
				for (ODIE_LuceneNerCandidate candidate : this.conceptsByStartPos[idx].values()) {
					if (candidate.isAttained()) {
						this.finalists.add(candidate) ;
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void determineLeftToRightMappingFromFinalists() {
		//
		// Annotate left to right
		//
		for (Iterator<ODIE_LuceneNerCandidate> candidateIterator = this.finalists
				.iterator(); candidateIterator.hasNext();) {
			ODIE_LuceneNerCandidate candidate = candidateIterator.next();
			this.resultingConcepts.add(candidate.generateCoveringAnnotation());
		}
	}

	private boolean isAlphaNonStopword(String word) {
		return (word != null) && (word.length() > 0) && (word.matches("\\w+"))
				&& (!ODIE_StopWords.getInstance().isStopWord(word));
	}

	public ArrayList<ODIE_IndexFinderAnnotation> getResultingConcepts() {
		for (ODIE_IndexFinderAnnotation annotation : this.resultingConcepts) {
			String diagnostic = "";
			diagnostic += "(" + annotation.getStartNode().getOffset();
			diagnostic += ", ";
			diagnostic += annotation.getEndNode().getOffset() + ") ";
			diagnostic += annotation.getAnnotationTypeName();
			logger.debug(diagnostic);
		}
		return this.resultingConcepts;
	}

}
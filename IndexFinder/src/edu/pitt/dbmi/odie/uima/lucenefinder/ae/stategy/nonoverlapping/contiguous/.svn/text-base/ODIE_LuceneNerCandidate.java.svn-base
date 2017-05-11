package edu.pitt.dbmi.odie.uima.lucenefinder.ae.stategy.nonoverlapping.contiguous;

import edu.pitt.dbmi.odie.server.indexfinder.ODIE_IndexFinderAnnotation;

public class ODIE_LuceneNerCandidate extends edu.pitt.dbmi.odie.uima.lucenefinder.ae.stategy.ODIE_LuceneNerCandidate {

		public void tally(ODIE_IndexFinderAnnotation annotation, int caret) {
			String normalizedWord = annotation.getNormalizedForm();
			if (!wordsMatched.contains(normalizedWord)) {
				if (caret < (sPos + targetNumberOfWords)) {
					wordsMatched.add(annotation.getNormalizedForm()) ;
					annotations.add(annotation) ;
					acquiredNumberOfWords++ ;
				}
			}
		}
		
		public boolean isAttained() {
			return acquiredNumberOfWords >= targetNumberOfWords ;
		}
		
		public CandidateStatus getStatus(int caret) {
			CandidateStatus result = CandidateStatus.ACCUMULATING ;
			if (acquiredNumberOfWords >= targetNumberOfWords) {
				result = CandidateStatus.ATTAINED ;
			}
			else {
				int hitLapse = caret - sPos ;
				if (hitLapse > targetNumberOfWords) {
					// Continguous constraint implemented here
					result = CandidateStatus.REJECTED ;
				}
				else {
					result = CandidateStatus.ACCUMULATING ;
				}		
			}
			return result ;
		}
}

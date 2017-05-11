package edu.pitt.dbmi.odie.uima.lucenefinder.ae.stategy.overlapping.noncontiguous;

import edu.pitt.dbmi.odie.server.indexfinder.ODIE_IndexFinderAnnotation;

public class ODIE_LuceneNerCandidate extends edu.pitt.dbmi.odie.uima.lucenefinder.ae.stategy.ODIE_LuceneNerCandidate {

	public void tally(ODIE_IndexFinderAnnotation annotation, int caret) {
		String normalizedForm = annotation.getNormalizedForm();
		if (!wordsMatched.contains(normalizedForm)) {
			wordsMatched.add(normalizedForm);
			annotations.add(annotation);
			acquiredNumberOfWords++;
		}
	}

	public boolean isAttained() {
		return acquiredNumberOfWords >= targetNumberOfWords;
	}

	public CandidateStatus getStatus(int caret) {
		CandidateStatus result = CandidateStatus.ACCUMULATING;
		if (acquiredNumberOfWords >= targetNumberOfWords) {
			result = CandidateStatus.ATTAINED;
		} else {
			result = CandidateStatus.ACCUMULATING;
		}
		return result;
	}
}

package edu.pitt.dbmi.odie.lexicalizer.lucenefinder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ODIE_StopWords {

	private static final String[] stopWordsArray = { "the", "of", "and", "to",
			"a", "in", "that", "is", "was", "he", "for", "it", "with", "as",
			"his", "on", "be", "at", "by", "i", "this", "had", "not", "are",
			"but", "from", "or", "have", "an", "they", "which", "one", "were",
			"you", "all", "her", "she", "there", "would", "their", "we", "him",
			"been", "has", "when", "who", "will", "no", "more", "if", "out",
			"its", "so", "up", "said", "what", "about", "than", "into", "them",
			"can", "only", "other", "time", "new", "some", "could", "these",
			"two", "may", "first", "then", "do", "any", "like", "my", "now",
			"over", "such", "our", "man", "me", "even", "most", "made",
			"after", "also", "well", "did", "many", "before", "must", "years",
			"through", "much", "where", "your", "way", "down",
			"should", "because", "long", "each", "just", "state", "those",
			"too", "how", "little", "good", "make", "very", "still", "see",
			"own", "work", "day", "get", "here", "between", "both", "being",
			"under", "never", "know", "same", "last", "another", "while", "us",
			"off", "might", "great", "go", "come", "since", "against", "right",
			"came", "take", "used", "himself", "few", "use", "place", "during",
			"without", "again", "around", "however", "found", "thought",
			"went", "say", "every", "don't", "does", "got", "until", "always",
			"away", "something", "less", "through", "put", "think", "called",
			"set", "almost", "enough", "end", "took", "yet", "better",
			"nothing", "told", "going", "why", "didn't", "look", "find",
			"asked", "later", "next", "knew", "give", "toward", "let", "given",
			"per", "several", "possible", "rather", "among", "often", "early",
			"things", "looked", "ever", "become", "best", "need", "within",
			"felt", "along", "saw", "least", "family", "others", "thing",
			"seemed", "want", "done", "although", "whole", "certain",
			"different", "kind", "began", "perhaps", "times", "itself",
			"whether", "either", "gave", "across", "taken", "anything",
			"having", "seen", "i'm", "really", "tell", "making", "sure",
			"themselves", "together", "full", "shall", "held", "known", "keep",
			"probably", "seems", "behind", "cannot", "brought", "whose",
			"self", "heard", "ago", "became", "available", "am", "ill", "seem",
			"wanted", "necessary", "following", "sometimes", "feel", "provide",
			"therefore", "evidence", "believe", "says", "nor", "that's",
			"everything", "except", "particular", "recent", "data", "person",
			"beyond", "coming", "else", "couldn't", "can't", "including",
			"actually", "shown", "especially", "wasn't", "likely",
			"particularly", "whom", "below", "certainly", "maybe", "continued",
			"generally", "wouldn't", "clearly", "somewhat", "apparently",
			"he's" };

	private static List<String> stopWordsList = Arrays.asList(stopWordsArray);
	
	private final static HashSet<String> stopWordMap = new HashSet<String>() ;
	
	private static ODIE_StopWords singleton = null ;
	
	public static ODIE_StopWords getInstance() {
		if (singleton == null) {
			singleton = new ODIE_StopWords() ;
		}
		return singleton ;
	}
	
	private ODIE_StopWords() {
		stopWordMap.addAll(stopWordsList) ;
	}
	
	public boolean isStopWord(String word) {
		return stopWordMap.contains(word) ;
	}
}

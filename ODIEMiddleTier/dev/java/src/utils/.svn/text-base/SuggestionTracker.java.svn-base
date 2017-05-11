package edu.pitt.dbmi.odie.model.utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.Annotation;
import edu.pitt.dbmi.odie.model.Suggestion;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IProperty;
import edu.pitt.text.tools.TextTools;

/**
 * keeps track of unused noun-phrases and returns a list of suggestions
 * 
 * @author tseytlin
 * 
 */
public class SuggestionTracker {
	private static final String PROPERTY_PATTERN_WEIGHT = "hasPatternWeight";
	public static URI DISCOVERY_URI = URI.create("http://caties.cabig.upmc.edu/ODIE/ontologies/2008/1/1/DiscoveryPatterns.owl");
	public static URI NP_CLASS_URI = URI.create(DISCOVERY_URI + "#NP");
	public static URI SENTENCE_CLASS_URI = URI.create(DISCOVERY_URI+ "#Sentence");

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// if(args.length == 0){
		// System.err.println("Usage: java ODIESuggestionTracker <report
		// filenames>");
		// return;
		// }
		// TextTools tools = new TextTools(new
		// URL("http://pathtutor.upmc.edu/term/servlet/TextToolsServlet"));
		// SuggestionTracker tracker = new SuggestionTracker();
		// long time = System.currentTimeMillis();
		// //iterate over files
		// for(int i=0;i<args.length;i++){
		// File f = new File(args[i]);
		// String text = TextTools.getText(new FileInputStream(f));
		// Document doc = tools.parseDocument(new Document(f));
		// IDocument d = new DocumentImpl();
		// /////////////////////////////////////////////////////////////
		// // When sentence is parsed on WINDOWS and CODE RUNS ON UNIX
		// // DO THIS:
		// d.setText(text.replaceAll("\n","\n\r"));
		//			
		// // iterate over noun-phrases
		// for(Object x:doc.getSentences()){
		// Sentence sentence = (Sentence)x;
		// for(Object y: sentence.getPhrases()){
		// Phrase p = ((Phrase)y).getNounPhrase();
		// Span span = p.getSpan();
		// //got nount phrase, create annotation
		// if(span.getNumberOfCharacters() > 0){
		// //System.out.println(p.getOriginalString()+"|"+p.getSpan());
		// Annotation a = new AnnotationImpl();
		// a.setStartOffset(span.getBeginCharacter());
		// a.setEndOffset(span.getEndCharacter()+1);
		// a.setDocument(d);
		// a.setType(Annotation.NOUN_TYPE);
		// // track
		// tracker.addAnnotation(a);
		// }
		// }
		// }
		// }
		// System.out.println("processing documents
		// "+(System.currentTimeMillis()-time)+" ms");
		// time = System.currentTimeMillis();
		// // analyze results
		// System.out.println("---- phrase suggestions ----");
		// for(Suggestion s: tracker.getPhraseSuggestions()){
		// System.out.println(s.getText()+"|"+s.getFrequency());
		// }
		// System.out.println("---- word suggestions ----");
		// for(Suggestion s: tracker.getWordSuggestions()){
		// System.out.println(s.getText()+"|"+s.getFrequency());
		// }
		// System.out.println("get suggestions
		// "+(System.currentTimeMillis()-time)+" ms");
	}

	private Map<String, Suggestion> phraseTable, wordTable;

	/**
	 * create new instance of suggestion tracker
	 */
	public SuggestionTracker() {
		phraseTable = new HashMap<String, Suggestion>();
		wordTable = new HashMap<String, Suggestion>();
	}

	/**
	 * add new annotation to the tracker
	 * 
	 * @param npChildAnnotation
	 */
	private void addAnnotation(String text, String ntext, ChildAnnotation npChildAnnotation,	Map<String, Suggestion> map) {
		// try to lookup suggestion object
		// System.out.println(ntext);
		Suggestion s = map.get(ntext);

		// if not in table, put it there
		if (s == null) {
			s = new Suggestion(text);
			s.setNormalizedText(ntext);
			map.put(ntext, s);
		}
		// add annotation
		s.addAnnotation(npChildAnnotation);
		// determine weight
		assignWeight(s, npChildAnnotation);
	}

	/**
	 * set appripriate weight
	 * 
	 * @param s
	 * @param npAnnotation
	 */
	private void assignWeight(Suggestion s, ChildAnnotation npAnnotation) {
//		// ignore words
//		if (a instanceof ChildAnnotation && ChildAnnotation.TYPE_WORD.equals(((ChildAnnotation)a).getType())){
//			return;
//		}
		List<Annotation> list = getCoveredAnnotations(npAnnotation);
		// if list empty, then we have non-covered phrase
		// NO NER NO LSP
		if (list.isEmpty()) {
			if (s.getWeight() < 4.0)
				s.setWeight(4.0);
		} else {
			boolean hasLSP = false;
			boolean hasPartialNER = false;
			boolean hasFullNER = false;

			int st = Integer.MAX_VALUE;
			int en = -1;
			
			IClass lspClass = null;
			// if list contains LSPs or NER then
			for (Annotation an : list) {
				if (MiddleTier.isLSPAnnotation(an)) {
					lspClass = an.getAnnotationClass();
					hasLSP = true;
				} else {
					if (st > an.getStartOffset()) {
						st = an.getStartOffset();
					}
					if (en < an.getEndOffset()) {
						en = an.getEndOffset();
					}
				}
			}

			// TODO Full NER has a bug when there are annotations that donot
			// cover the entire NP
			// together but are located at the start and end of the NP
			// e.g. NP=dysplastic border nevus ann1=dysplastic ann2=nevus, hence
			// NP will return FullNER
			hasFullNER = (npAnnotation.getStartOffset() >= st && npAnnotation.getEndOffset() <= en);
			hasPartialNER = (!hasFullNER && en > -1 && npAnnotation.getStartOffset() <= st && npAnnotation.getEndOffset() >= en);

			double existingWeight = s.getWeight();
			double lspWeight = 0;	
			if(lspClass!=null){
				IProperty lspProperty = lspClass.getOntology().getProperty(PROPERTY_PATTERN_WEIGHT); 
				Object obj = lspClass.getPropertyValue(lspProperty);
				lspWeight =  (new Double(""+obj)).doubleValue();
				 
			}
				
			
			// LSP, NO NER
			if (hasLSP && !hasFullNER && !hasPartialNER && existingWeight > -1 && existingWeight < 5.0)
				s.setWeight(5.0 + lspWeight);
			// LSP, PARTIAL NER
			else if (hasLSP && hasPartialNER && existingWeight > -1 && existingWeight < 3.0) 
				s.setWeight(3.0 + lspWeight);
			// NO LSP, PARTIAL NER
			else if (!hasLSP && hasPartialNER && existingWeight > -1 && existingWeight < 2.0) 
				s.setWeight(2.0 + lspWeight);
			// LSP, FULL NER
			else if (hasLSP && hasFullNER && existingWeight > -1 &&  existingWeight < 1.0) 
				s.setWeight(1.0 + lspWeight);
			// NO LSP, FULL NER  (0 overwrides everything)
			else if (!hasLSP && hasFullNER)
				s.setWeight(-1);

		}
	}

	/**
	 * is annotation covered by other NER annotation
	 * 
	 * @param a
	 * @return
	 */
	private List<Annotation> getCoveredAnnotations(ChildAnnotation a) {
		List<Annotation> list = new ArrayList<Annotation>();
		// get all annotations for a document
//		for (Annotation an : a.getDocumentAnalysis().getAnnotations()) {
//			// if not self
//			if (!an.equals(a.getParentAnnotation())) {
//				// if intersects, but not sentence
//				if (a.intersects(an)
//						&& !an.getAnnotationClassURI().equals(NP_CLASS_URI)
//						&& !an.getAnnotationClassURI().equals(
//								SENTENCE_CLASS_URI)) {
//					// System.out.println(an.getAnnotationClass().getName() +
//					// "intersects " + a.getText());
//					list.add(an);
//				}
//			}
//			// since list is sorted
//			if (an.getStartOffset() > a.getEndOffset())
//				break;
//		}
		return list;
	}

	/**
	 * derive annotation that is part of o original annotation
	 * 
	 * @param orig
	 * @param text
	 * @return
	 */
	private ChildAnnotation getSubAnnotation(Annotation orig, String word,String type) {
		String text = orig.getText().toLowerCase();
		int i = text.indexOf(word.toLowerCase());
		ChildAnnotation a = new ChildAnnotation(orig);
		a.setDocumentAnalysis(orig.getDocumentAnalysis());
		a.setAnnotationClass(orig.getAnnotationClass());
		a.setType(type);
		
		if (i > -1) {
			a.setStartOffset(orig.getStartOffset() + i);
			a.setEndOffset(a.getStartOffset() + word.length());
		}
		return a;
	}
	
	/**
	 * strip digits from the begining and end of phrase
	 * @param text
	 * @return
	 */
	private String stripDigits(String text){
		//if no digits return original
		if(text.matches("[a-zA-Z\\s]+"))
			return text;
		//	 if only digits
		if(text.matches("[^a-zA-Z]+"))
			return "";
		// else strip digits only in the begining and the end
		String [] w =  text.split("\\s+");
		int i=0,j=w.length-1;
		for(i=0;i < w.length;i++)
			if(!w[i].matches("[^a-zA-Z]+"))
				break;
		for(j=w.length-1;j >= 0;j--)
			if(!w[j].matches("[^a-zA-Z]+"))
				break;
		return text.substring(text.indexOf(w[i]),text.lastIndexOf(w[j])+w[j].length());
	}
	
	
	/**
	 * is annotation for noun-phrase
	 * 
	 * @param a
	 * @return
	 */
	private boolean isNounPhrase(Annotation a) {
		return NP_CLASS_URI.equals(a.getAnnotationClassURI());
	}

	/**
	 * add new annotation to the tracker
	 * 
	 * @param a
	 */
	public void addAnnotation(Annotation a) {
		// if noun phrase annotation
		// if(isNounPhrase(a) && getCoveredAnnotations(a).size()==0){
		if (isNounPhrase(a)) {
			// get concept text and normalized text
			String text = a.getText();
			if (text == null)
				return;

			String ntext = TextTools.normalize(text.trim());
			// || ntext.matches("[0-9 ]+") should be taken care by normalizer
			if (ntext.length()==0 || ntext.length() < 3)
				return;
			
			// add phrase annotation
			//addAnnotation(text, ntext,a, phraseTable);
			String stext = stripDigits(text);
			addAnnotation(stext, ntext, getSubAnnotation(a,stext,ChildAnnotation.TYPE_PHRASE),phraseTable);

			// split into words
			String[] words = ntext.split(" ");

			// now add individual words to word table
			for (int i = 0; i < words.length; i++) {
				addAnnotation(words[i], words[i],getSubAnnotation(a,words[i],ChildAnnotation.TYPE_WORD), wordTable);
			}

		}
	}

	/**
	 * get phrase suggestions
	 * 
	 * @return
	 */
	public SortedSet<Suggestion> getPhraseSuggestions() {
		TreeSet<Suggestion> set = new TreeSet<Suggestion>();
		// filter out 0 wieght
		for(Suggestion s: phraseTable.values()){
			if(s.getWeight() > 0)
				set.add(s);
		}
		//return new TreeSet<Suggestion>(phraseTable.values());
		return set;
	}

	/**
	 * get word suggestions
	 * 
	 * @return
	 */
	public SortedSet<Suggestion> getWordSuggestions() {
		// List<Suggestion> list = new
		// ArrayList<Suggestion>(wordTable.values());
		// Collections.sort(list);
		return new TreeSet<Suggestion>(wordTable.values());
	}

	/**
	 * iterate through list of suggestions are re-assign weights (call when
	 * there is pottentially new data present)
	 */
	public void recalculateWeights() {
		for (Suggestion s : phraseTable.values()) {
			for (ChildAnnotation a : s.getAnnotations()) {
				assignWeight(s, a);
			}
		}
	}

}

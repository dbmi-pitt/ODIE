/*
 *  Minipar.java
 *
 *  Copyright (c) 1998-2004, The University of Sheffield.
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Niraj Aswani
 *
 *  $Id: Minipar.java 10104 2008-12-23 11:48:42Z ian_roberts $
 *  
 *  This file has been ported from GATE 5.0 to a UIMA TAE kevin.j.mitchell@upmc.edu
 *  
 */

package edu.pitt.dbmi.odie.uima.minipar.ae;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.ResultSpecification;
import org.apache.uima.analysis_engine.annotator.AnnotatorConfigurationException;
import org.apache.uima.analysis_engine.annotator.AnnotatorContext;
import org.apache.uima.analysis_engine.annotator.AnnotatorContextException;
import org.apache.uima.analysis_engine.annotator.AnnotatorInitializationException;
import org.apache.uima.analysis_engine.annotator.AnnotatorProcessException;
import org.apache.uima.analysis_engine.annotator.JTextAnnotator_ImplBase;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;

import edu.mayo.bmi.uima.core.type.Segment;
import edu.mayo.bmi.uima.core.type.Sentence;
import edu.pitt.dbmi.odie.uima.minipar.type.DepTreeNode;
import edu.pitt.dbmi.odie.uima.minipar.type.POSType;

/**
 * This class is the implementation of the resource Minipar
 */
@SuppressWarnings("deprecation")
public class Minipar extends JTextAnnotator_ImplBase {
	
	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(Minipar.class);

	private String segmentId = null;

	/**
	 * Name of the temporary file, which is populated with the text of UIMA
	 * document in order to process it with Minipar
	 */
	public static final String FILE_NAME_MINIPAR_SENTENCES = "MiniparSentences";

	/**
	 * The Minipar executable limits the length of a sentence to 1024
	 * characters. If a sentence is longer than the maximal length it is not
	 * sent to MINIPAR7
	 */
	public static final long maxSentenceLength = 1024;

	/**
	 * Get the MiniparDataDir value.
	 */
	public URL getMiniparDataDir() {
		URL result = null ;
		try {
			result = getContext().getResourceURL("miniparDataDir") ;
		} catch (AnnotatorContextException e) {
			e.printStackTrace();
		}
		return result ;
	}

	/**
	 * This is the url of MiniparBinary It should be somewhere located on the
	 * drive where the user has execution rights
	 */
	public URL getMiniparBinary() {
		URL result = null ;
		try {
			result = getContext().getResourceURL("miniparBinary") ;
		} catch (AnnotatorContextException e) {
			e.printStackTrace();
		}
		return result ;
	}

	/**
	 * Init method is called when the resource is instantiated in UIMA. This
	 * checks for the supported operating systems and the mandotary init-time
	 * parameters.
	 */
	public void initialize(AnnotatorContext aContext)
			throws AnnotatorConfigurationException,
			AnnotatorInitializationException {
		super.initialize(aContext);

		try {
			configInit();
		} catch (AnnotatorContextException ace) {
			throw new AnnotatorConfigurationException(ace);
		}
	}

	/**
	 * Reads configuration parameters.
	 */
	private void configInit() throws AnnotatorContextException,
			AnnotatorConfigurationException {
		// we need to check the operating system
		// And to detect the underlying Operating system
		String osName = System.getProperty("os.name").toLowerCase();
		// Detecting Linux and Windows
		if (osName.toLowerCase().indexOf("linux") == -1
				&& osName.toLowerCase().indexOf("windows") == -1) {
			throw new AnnotatorConfigurationException(
					"This PR can only be instantiated on Windows/Linux Machine",
					null);
		}
	}

	/**
	 * UIMA TextAnnotator entry point. Here the method first checks for all
	 * mandatory runtime parameters, if not provided prompts user by throwing an
	 * AnnotatorProcessException. For optional parameters it considers the
	 * default values. this method initially given a document, converts the
	 * document text into a text file. This text file is then sent to runMinipar
	 * function in order to parse it with Minipar.
	 */
	public void process(JCas jcas, ResultSpecification resultSpec)
			throws AnnotatorProcessException {
		if (jcas == null)
			throw new AnnotatorProcessException("No document to process!", null);
		if (getMiniparBinary() == null)
			throw new AnnotatorProcessException(
					"Please provide the URL for Minipar Binary", null);
		if (getMiniparDataDir() == null)
			throw new AnnotatorProcessException(
					"Minipar requires the location of its data directory "
							+ "(By default it is %Minipar_Home%/data", null);

		// generate a temporary file for the current document
		File gateTempFile = null;
		try {
			gateTempFile = File.createTempFile(FILE_NAME_MINIPAR_SENTENCES,
					".txt");
			logger.debug("Creating minipar file called " + gateTempFile.getAbsolutePath()) ;
//			gateTempFile.deleteOnExit();
		} catch (java.io.IOException e) {
			throw new AnnotatorProcessException(
					"Impossible to generate temp file!", null);
		}
		// obtain UIMA sentence annotations as a list
		ArrayList<Sentence> allSentences = saveGateSentences(jcas, gateTempFile);
		// finally runMinipar
		runMinipar(jcas, allSentences, gateTempFile);
		
		// 
		// Explicitly delete the temporary file to avoid file system overflows
		//
		gateTempFile.delete();
	}

	/**
	 * Minipar Binary file takes a file as an argument, which has one sentence
	 * written on one line. It takes one sentence at a time and parses them one
	 * by one.
	 * 
	 * @return The list containing annotations of type *Sentence*
	 * @throws AnnotatorProcessException
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<Sentence> saveGateSentences(JCas jcas, File gateTextFile)
			throws AnnotatorProcessException {

		if (segmentId == null) {
			fetchSegment(jcas);
		}

		// 
		// Get all "sentences"
		FSIndex sentenceIndex = jcas.getAnnotationIndex(Sentence.type);
		Iterator<Sentence> sentenceIterator = sentenceIndex.iterator();
		ArrayList<Sentence> allSentences = new ArrayList<Sentence>();
		while (sentenceIterator.hasNext()) {
			Sentence sentenceAnnot = (Sentence) sentenceIterator.next();
			allSentences.add(sentenceAnnot);
		}

		// sort all sentences by start offset
		Collections.sort(allSentences, new Comparator<Sentence>() {
			public int compare(Sentence o1, Sentence o2) {
				int result;

				// compare start offsets
				result = Integer.valueOf(o1.getBegin()).compareTo(
						Integer.valueOf(o2.getBegin()));

				// if start offsets are equal compare end offsets
				if (result == 0) {
					result = Integer.valueOf(o1.getEnd()).compareTo(
							Integer.valueOf(o2.getEnd()));
				} // if

				return result;
			}

		});

		// only the sentences shorter than maxSentenceLength are sent to Minipar
		ArrayList<Sentence> legalLengthSentences = new ArrayList<Sentence>(
				allSentences.size());

		// save sentence strings to file for Minipar
		sentenceIterator = allSentences.listIterator();
		try {
			String sentenceString = null;
			FileWriter fw = new FileWriter(gateTextFile);
			PrintWriter pw = new PrintWriter(fw, true);
			while (sentenceIterator.hasNext()) {
				Sentence sentence = (Sentence) sentenceIterator.next();
				sentenceString = null;
				Long startOffset = new Long(sentence.getBegin());
				Long endOffset = new Long(sentence.getEnd());
				// check that the length is inferior to the limit
				long length = endOffset.longValue() - startOffset.longValue();
				if (length >= maxSentenceLength)
					continue;
				else {
					sentenceString = sentence.getCoveredText();
					sentenceString = sentenceString.replaceAll("\r", " ");
					sentenceString = sentenceString.replaceAll("\n", " ");
					pw.println(sentenceString);
					legalLengthSentences.add(sentence);
				}
			}
			fw.close();
		} catch (java.io.IOException except) {
			throw new AnnotatorProcessException(except);
		}

		return legalLengthSentences;
	}

	/**
	 * Core of the wrapper. FILE_NAME_MINIPAR_SENTENCES is processed with Minipar. Minipar
	 * given a text file, binary executable and the location of data directory,
	 * returns a parse, which is then mapped over the UIMA document.
	 * 
	 * @param allSentences
	 *            - UIMA sentence annotations
	 * @throws AnnotatorProcessException
	 */
	private void runMinipar(JCas jcas, ArrayList<Sentence> allSentences, File gateTextFile)
			throws AnnotatorProcessException {

		// this should be the miniparBinary + "-p " + getMiniparDataDir +
		// FILE_NAME_MINIPAR_SENTENCES
		File binary = fileFromURL(getMiniparBinary());
		File dataFile = fileFromURL(getMiniparDataDir());
		 String cmdlineAsString = binary.getAbsolutePath() + " -p "
		 + dataFile.getAbsolutePath() + " -file "
		 + gateTextFile.getAbsolutePath();
		 logger.debug("Executing command line ==> " + cmdlineAsString) ;
		String[] cmdline = new String[5];
		cmdline[0] = binary.getAbsolutePath();
		cmdline[1] = "-p";
		cmdline[2] = dataFile.getAbsolutePath();
		cmdline[3] = "-file";
		cmdline[4] = gateTextFile.getAbsolutePath();
		// run minipar and save output
		try {
			String line;
			Process p = Runtime.getRuntime().exec(cmdline);
			BufferedReader input = new BufferedReader(new InputStreamReader(p
					.getInputStream()));

			// this has ArrayList as its each element
			// this element consists of all annotations for that particular
			// sentence
			ArrayList<ArrayList<WordToken>> sentenceTokens = new ArrayList<ArrayList<WordToken>>();

			// this will have an annotation for each line beginning with a number
			ArrayList<WordToken> tokens = new ArrayList<WordToken>();
			outer: while ((line = input.readLine()) != null) {
				logger.debug(line) ;
				WordToken wt = new WordToken();
				// so here whatever we get in line
				// is of our interest only if it begins with any number
				// each line is deliminated with a tab sign
				String[] output = line.split("\t");
				if (output.length < 5)
					continue;
				for (int i = 0; i < output.length; i++) {
					// we ignore case 2 and 3 and 6 and after.. because we don't
					// want
					// that information
					switch (i) {
					case 0:
						// this is a word number
						try {
							wt.miniparId = Integer.parseInt(output[i].trim());
							// yes this is correct line
							// we need to check if the line number is 1
							// it may be the begining of new sentence
							if (wt.miniparId == 1 && tokens.size() > 0) {
								// we need to add tokens to the sentenceTokens
								sentenceTokens.add(tokens);
								tokens = new ArrayList<WordToken>() ;
							}
						} catch (NumberFormatException infe) {
							// if we are here, there is something wrong with
							// number
							// ignore this line and continue with next line
							continue outer;
						}
						break;
					case 1:
						// this is the actual word (Token.string)
						wt.word = output[i];
						break;
					case 4:
						// this should be the number and if it is not
						// then we leave it and do not add any head
						try {
							int head = Integer.parseInt(output[i].trim());
							// yes this is the correct head number
							wt.headNumber = head;
						} catch (NumberFormatException nfe) {
							// if we are here, there is something wrong with
							// number
							// ignore this and make headNumber -1 letter on to
							// remember that we don't want headnumber to be
							// inserted as a
							// feature
							wt.headNumber = -1;
						}
						break;
					case 5:
						// this is the relation between head and the current
						// node
						wt.relationWithHead = output[i];
						break;
					default:
						break;
					}
				}

				// here we have parsed the one line and thus now we should add
				// it to the
				// tokens for letter use
				tokens.add(wt);
			}
			if (tokens.size() > 0) {
				sentenceTokens.add(tokens);
			}
			input.close();

			// ok so here we have all the information we need from the minipar
			// in
			// local variables
			// ok so first we would create annotation for each word Token

			// size of the sentenceTokens and the allSentences would be always
			// same
			for (int i = 0; i < sentenceTokens.size(); i++) {
				tokens = (ArrayList<WordToken>) sentenceTokens.get(i);

				// we need this to generate the offsets
				Sentence sentence = (Sentence) allSentences.get(i);
				String sentenceString = sentence.getCoveredText();
				sentenceString = sentenceString.replaceAll("\r", " ");
				sentenceString = sentenceString.replaceAll("\n", " ");

				// this will hold the position from where it should start
				// searching for
				// the token text
				int startOffset = sentence.getBegin();

				int index = -1;
				for (int j = 0; j < tokens.size(); j++) {
					// each item here is a separate word token
					WordToken wt = (WordToken) tokens.get(j);
					// ok so find out the offsets
					int stOffset = sentenceString.toLowerCase().indexOf(
							wt.word.toLowerCase(), index)
							+ startOffset;
					int enOffset = stOffset + wt.word.length();
					// Here we want to add a DepTreeNode annotation
					DepTreeNode depTreeNode = new DepTreeNode(jcas);
					depTreeNode.setBegin(stOffset);
					depTreeNode.setEnd(enOffset);
					depTreeNode.setMiniparId(wt.miniparId);
					depTreeNode.setWord(wt.word);
					wt.annotation = depTreeNode;
					index = enOffset - startOffset;
					depTreeNode.addToIndexes() ;
				}
			}

			// now we need to create the children nodes
			for (int i = 0; i < sentenceTokens.size(); i++) {
				tokens = (ArrayList<WordToken>) sentenceTokens.get(i);

				for (int j = 0; j < tokens.size(); j++) {
					WordToken wt = (WordToken) tokens.get(j);
					// read the head node
					// find out the respective word token for that head node
					// and add the current node as its child
					if (wt.headNumber > 0 && wt.headNumber <= tokens.size()) {
						WordToken headToken = (WordToken) tokens
								.get(wt.headNumber - 1);
						headToken.children.add(wt.annotation.getMiniparId());
					}
				}
			}

			// and finally we need to add features to the annotations
			// now we need to create the children nodes
			for (int i = 0; i < sentenceTokens.size(); i++) {
				tokens = (ArrayList<WordToken>) sentenceTokens.get(i);
				for (int j = 0; j < tokens.size(); j++) {
					// for every wordtoken,
					// we look for its head
					// and create annotations
					// the annotation will have the type of relation string
					// and as a features
					// it will have one head ID and one child ID
					// head ID is the id of head
					// and child ID is the id of wt
					WordToken wt = (WordToken) tokens.get(j);
					if (wt.headNumber > 0 && wt.headNumber <= tokens.size()) {
						DepTreeNode headAnn = ((WordToken) tokens
								.get(wt.headNumber - 1)).annotation;
						POSType posType = new POSType(jcas);
						posType.setHeadId(headAnn.getMiniparId());
						posType.setChildId(wt.annotation.getMiniparId());
						posType.setHeadWord(((WordToken) tokens
								.get(wt.headNumber - 1)).word);
						posType.setChildWord(wt.word);
						// so create the new annotation
						int stOffset1 = headAnn.getBegin();
						int stOffset2 = wt.annotation.getBegin();
						int enOffset1 = headAnn.getEnd();
						int enOffset2 = wt.annotation.getEnd();
						stOffset1 = stOffset1 < stOffset2 ? stOffset1
								: stOffset2;
						enOffset1 = enOffset1 > enOffset2 ? enOffset1
								: enOffset2;
						posType.setTagName(wt.relationWithHead);
						posType.setBegin(stOffset1);
						posType.setEnd(enOffset1);
						posType.addToIndexes();
					}
				}
			}
			// and finally make the sentenceTokens and tokens to null
			tokens = null;
			sentenceTokens = null;
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new AnnotatorProcessException(exception);
		}
	} // end of runMinipar()

	/**
	 * WorkToken subclass is used as an internal data structure to hold
	 * temporary information returned by the minipar parser. This information is
	 * then mapped over the UIMA annotations.
	 */
	private class WordToken {
		
		/**
		 * Minipar Id is the minipar output sequence of this Token
		 */
		int miniparId = -1 ;
		
		/**
		 * Word is the string, that represents the token in minipar
		 */
		String word;

		/**
		 * Each token in minipar is given a number. This contains the number of
		 * the headToken
		 */
		int headNumber;

		/**
		 * This stores the relation of head word with its children
		 */
		String relationWithHead;

		/**
		 * Contains other instances of WordTokens which have been identified as
		 * children of the headword
		 */
		ArrayList<Integer> children = new ArrayList<Integer>();

		/**
		 * UIMA Minipar annotation that represents the WordToken in UIMA
		 */
		DepTreeNode annotation;
	}

	/**
	 * Fetch Segment
	 */
	@SuppressWarnings("unchecked")
	public void fetchSegment(JCas jcas) throws AnnotatorProcessException {
		try {
			FSIndex segmentIndex = jcas.getAnnotationIndex(Segment.type);
			Iterator<Segment> segmentIterator = segmentIndex.iterator();
			while (segmentIterator.hasNext()) {
				Segment segmentAnnot = (Segment) segmentIterator.next();
				this.segmentId = segmentAnnot.getId();
				break;
			}
		} catch (Exception e) {
			throw new AnnotatorProcessException(e);
		}
	}

	/**
	 * Convert a file: URL to a <code>java.io.File</code>. First tries to parse
	 * the URL's toExternalForm as a URI and create the File object from that
	 * URI. If this fails, just uses the path part of the URL. This handles URLs
	 * that contain spaces or other unusual characters, both as literals and
	 * when encoded as (e.g.) %20.
	 * 
	 * @exception IllegalArgumentException
	 *                if the URL is not convertable into a File.
	 */
	public static File fileFromURL(URL theURL) throws IllegalArgumentException {
		try {
//			theURL = ODIE_UimaUtils.convertToNativeURL(theURL) ;
			URI uri = new URI(theURL.toExternalForm());
			return new File(uri);
		} catch (URISyntaxException use) {
			try {
				URI uri = new URI(theURL.getProtocol(), null, theURL.getPath(),
						null, null);
				return new File(uri);
			} catch (URISyntaxException use2) {
				throw new IllegalArgumentException("Cannot convert " + theURL
						+ " to a file path");
			}
		}
	}

}
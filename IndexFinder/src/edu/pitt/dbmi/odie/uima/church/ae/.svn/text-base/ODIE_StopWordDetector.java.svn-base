/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package edu.pitt.dbmi.odie.uima.church.ae;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;

import edu.pitt.dbmi.odie.uima.church.resource.ODIE_Gazetteer;
import edu.pitt.dbmi.odie.uima.church.types.ODIE_StopWord;
import edu.pitt.dbmi.odie.uima.church.types.ODIE_Word;

/**
 * Annotates UIMA acronyms and provides their expanded forms. When combined in
 * an aggregate TAE with the UimaMeetingAnnotator, demonstrates the use of the
 * ResourceManager to share data between annotators.
 * 
 * 
 */
public class ODIE_StopWordDetector extends JCasAnnotator_ImplBase {

	private ODIE_Gazetteer stopWordGazetteer;

	/**
	 * @see AnalysisComponent#initialize(UimaContext)
	 */
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);
		// get a reference to the ODIE_Gazetteer Resource
		try {
			stopWordGazetteer = (ODIE_Gazetteer) getContext()
					.getResourceObject("ODIE_StopWordGazetteer");
		} catch (ResourceAccessException e) {
			throw new ResourceInitializationException(e);
		}
	}

	
	/**
	 * @see JCasAnnotator_ImplBase#process(JCas)
	 */
	public void process(JCas aJCas) {
		FSIndex wordIndex = aJCas.getAnnotationIndex(ODIE_Word.type);
		// iterate over all combinations
		ArrayList<ODIE_StopWord> stopWordsToAdd = new ArrayList<ODIE_StopWord>();
		ArrayList<ODIE_Word> underLyingWordsToRemove = new ArrayList<ODIE_Word>();
		Iterator wordIter = wordIndex.iterator();
		while (wordIter.hasNext()) {
			ODIE_Word word = (ODIE_Word) wordIter.next();
			if (stopWordGazetteer.contains(word.getWordText())) {
				ODIE_StopWord stopWord = new ODIE_StopWord(aJCas);
				stopWord.setBegin(word.getBegin());
				stopWord.setEnd(word.getEnd());
				stopWord.setWordText(word.getWordText());
				stopWordsToAdd.add(stopWord) ;
				underLyingWordsToRemove.add(word) ;
			}
		}

		for (ODIE_StopWord stopWord : stopWordsToAdd) {
			getContext().getLogger().log(Level.FINE, "Adding: " + stopWord);
			stopWord.addToIndexes();
		}
		
		for (ODIE_Word word : underLyingWordsToRemove) {
			word.removeFromIndexes() ;
		}
	}

}

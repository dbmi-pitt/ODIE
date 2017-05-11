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

import java.util.StringTokenizer;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;

import edu.pitt.dbmi.odie.uima.church.types.ODIE_Word;

/**
 * Annotates UIMA acronyms and provides their expanded forms. When combined in
 * an aggregate TAE with the UimaMeetingAnnotator, demonstrates the use of the
 * ResourceManager to share data between annotators.
 * 
 * 
 */
public class ODIE_Tokeniser extends JCasAnnotator_ImplBase {

	/**
	 * @see AnalysisComponent#initialize(UimaContext)
	 */
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);

	}

	/**
	 * @see JCasAnnotator_ImplBase#process(JCas)
	 */
	public void process(JCas aJCas) {
		// go through document word-by-word
		String text = aJCas.getDocumentText();
		int pos = 0;
		StringTokenizer tokenizer = new StringTokenizer(text,
				" \t\n\r.<.>/?\";:[{]}\\|=+()!", true);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			ODIE_Word annot = new ODIE_Word(aJCas);
			annot.setBegin(pos) ;
			annot.setEnd(pos + token.length()) ;
			annot.setWordText(token) ;
			getContext().getLogger().log(Level.FINE, "ODIE_Tokeniser Adding: " + annot);
			annot.addToIndexes();
			pos += token.length();
		}
	}

}

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

package edu.pitt.dbmi.odie.uima.gazetteer.ae;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.openai.util.fsm.Machine;
import net.openai.util.fsm.State;
import net.openai.util.fsm.UnhandledConditionException;

import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

import edu.mayo.bmi.fsm.token.BaseToken;
import edu.pitt.dbmi.odie.uima.gazetteer.type.Lookup;

/**
 * ODIE_GazetteerResourceImpl
 * 
 * Implements ODIE_GazetteerResource
 * 
 */
public class ODIE_GazetteerResourceImpl implements ODIE_GazetteerResource,
		SharedResourceObject {
	
	private static final String KEY_SEPERATOR = ":" ;
	
	private final HashMap<String, ODIE_GazetteerCategory> gazetteerCategories = new HashMap<String, ODIE_GazetteerCategory>() ; 
	
	public ODIE_GazetteerCategory getGazetteerCategory(String majorCategory, String minorCategory) {
		String key = buildKeyFromCategories(majorCategory, minorCategory) ;
		return gazetteerCategories.get(key) ;
	}
	
	public String buildKeyFromCategories(String majorCategory, String minorCategory) {
		return majorCategory + KEY_SEPERATOR + minorCategory ;
	}

	public void load(DataResource aData) throws ResourceInitializationException {
		InputStream inStr = null;
		try {
			// open input stream to data
			inStr = aData.getInputStream();
			// read each line
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inStr));
			String line;
			while ((line = reader.readLine()) != null) {
				//
				// Gazetteer definition files are formated as such
				// <termFileName>:<majorCategory>:<minorCategory>:
				//
				String[] lineFields = line.split(":") ;
				
				String termFileName = lineFields[0] ;
				String majorCategory = lineFields[1] ;
				String minorCategory = lineFields[2] ;
				
				ODIE_GazetteerCategory gazetteerCategory = new ODIE_GazetteerCategory() ;
				gazetteerCategory.setMajorCategory(majorCategory) ;
				gazetteerCategory.setMinorCategory(minorCategory) ;
				gazetteerCategory.setDirectoryUrl(aData.getUrl()) ;
				gazetteerCategory.setTermFileName(termFileName) ;
				gazetteerCategory.initialize() ;
				
				System.out.println(gazetteerCategory) ;
				
				//
				// Map this Gazetteer category
				//
				gazetteerCategories.put(buildKeyFromCategories(majorCategory, minorCategory), gazetteerCategory) ;
				
			}
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		} finally {
			if (inStr != null) {
				try {
					inStr.close();
				} catch (IOException e) {
				}
			}
		}

	}
	
	public Set<Lookup> process(JCas jcas, List<BaseToken> tokens) {
	
		Set<Lookup> lookupSet = new HashSet<Lookup>();

		// maps a fsm to a token start index
		// key = fsm , value = token start index
		Map<Machine, Integer> tokenStartMap = new HashMap<Machine, Integer>();
		
		for (int i = 0; i < tokens.size(); i++) {
		
			BaseToken token = tokens.get(i);

			Iterator<ODIE_GazetteerCategory> gazetteerCategoryItr = gazetteerCategories.values().iterator();
			
			while (gazetteerCategoryItr.hasNext()) {
				
				ODIE_GazetteerCategory gazetteerCategory = gazetteerCategoryItr.next();
				Machine fsm = gazetteerCategory.getFsm() ;

				try {
					
					fsm.input(token);
					
					State currentState = fsm.getCurrentState();
					
					if (currentState.getStartStateFlag()) {
						tokenStartMap.put(fsm, new Integer(i));
					}
					if (currentState.getEndStateFlag()) {
						Object o = tokenStartMap.get(fsm);
						int tokenStartIndex = 0 ;
						if (o != null) {
							tokenStartIndex = ((Integer) o).intValue();
							tokenStartIndex++;
						}
						BaseToken startToken = tokens
								.get(tokenStartIndex);
						BaseToken endToken = token ;
						Lookup lookupToken = new Lookup(jcas, startToken.getStartOffset(), endToken.getEndOffset());
						lookupToken.setMajorType(gazetteerCategory.getMajorCategory()) ;
						lookupToken.setMinorType(gazetteerCategory.getMinorCategory()) ;
						lookupSet.add(lookupToken);
						
						fsm.reset();
					}
					
				} catch (UnhandledConditionException e) {
					e.printStackTrace();
				}

			}
		}

		// cleanup
		tokenStartMap.clear();

		// reset machines
		Iterator<ODIE_GazetteerCategory> itr = gazetteerCategories.values().iterator();
		while (itr.hasNext()) {
			itr.next().getFsm().reset() ;
		}

		return lookupSet;
	}

}

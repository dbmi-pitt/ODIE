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

package edu.pitt.dbmi.odie.uima.gate.resource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

import edu.pitt.dbmi.odie.uima.util.ODIE_UimaUtils;
import gate.Corpus;
import gate.CreoleRegister;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.Resource;
import gate.creole.ResourceInstantiationException;
import gate.util.Err;
import gate.util.GateException;
import gate.util.Out;

/**
 * ODIE_GazetteerResourceImpl
 * 
 * Implements ODIE_GazetteerResource
 * 
 */
public class ODIE_GateResourceImpl implements ODIE_GateResource,
		SharedResourceObject {
	
	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_GateResourceImpl.class);

	
	private String gateHome = "./gate";
	private String gateUserHome = gateHome + "/gate.xml";
	private String gateSessionHome = gateHome + "/gate.session";
	private String gatePlugInsHome = gateHome + "/plugins" ;

	private CreoleRegister creoleRegister;

	public void load(DataResource aData) throws ResourceInitializationException {
		
		try {
			URL gateHomeUrl = aData.getUrl();
//			gateHomeUrl = new URL("file:///C:/workspace/ws-odie/IndexFinder/resources/gate/odiehome") ;
		
			logger.debug("Got gateHomeUrl of " + gateHomeUrl) ;
			
			
			gateHome = gateHomeUrl.getFile();
			
//			if(gateHome.startsWith("/"))
//				gateHome = gateHome.substring(1);
			
			gateHome = gateHome.replaceAll("%20", " ");
			gateUserHome = gateHome + "/gate.xml";
			gateSessionHome = gateHome + "/gate.session";
			gatePlugInsHome = gateHome + "/plugins" ;
						
			logger.debug("Setting gate.home to " + gateHome) ;
			logger.debug("Setting gate.user.config to " + gateUserHome) ;
			logger.debug("Setting gate.user.session to " + gateSessionHome) ;
			logger.debug("Setting gate.plugins.home to " + gatePlugInsHome) ;
			
			
			System.setProperty("gate.home", gateHome) ;
			System.setProperty("gate.user.config", gateUserHome) ;
			System.setProperty("gate.user.session", gateSessionHome) ;
			System.setProperty("gate.plugins.home", gatePlugInsHome) ;
			 
			//
			// Suppress Gate diagnostics by configuring Out with
			// a dummy PrintWriter
			//
			boolean isDisplayingDiagnostics = false ;
			isDisplayingDiagnostics = isDisplayingDiagnostics || (logger.getLevel() == Level.DEBUG) ;
			
			PrintStream savedOut = null ;
			if (!isDisplayingDiagnostics) {
				savedOut = System.out ;
				System.setOut(new PrintStream(new ByteArrayOutputStream())) ;
				Err.setPrintWriter(new PrintWriter(
		                  new StringWriter())) ;
				Out.setPrintWriter(new PrintWriter(
		                  new StringWriter())) ;
			}

			
			//
			// Initialize GATE
			//
			Gate.init();

			//
			// Load the creole register with local resources.
			//
			creoleRegister = Gate.getCreoleRegister();
			
			//
			// Explicitly register the creole directory
			//
			URL creoleUrl = new File(gateHome+"/plugins/ANNIE").toURI().toURL();
			creoleRegister.registerDirectories(creoleUrl);

			//
			// Programmatically register the noun phrase chunker
			//
			creoleUrl = new File(gateHome+"/plugins/NP_Chunking").toURI().toURL();
			creoleRegister.registerDirectories(creoleUrl);

			//
			// See what's in the creole register
			//
			Set<String> prTypes = creoleRegister.getPrTypes();
			Iterator<String> it = prTypes.iterator();
			while (it.hasNext()) {
				String prType = (String) it.next();
				logger.debug("prType is " + prType);
			}
			
			//
			// Restore Out to System.out
			//
			if (!isDisplayingDiagnostics) {
				Out.setPrintWriter(new PrintWriter(System.out)) ;
				Err.setPrintWriter(new PrintWriter(System.err)) ;
				System.setOut(savedOut) ;
			}
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		} catch (GateException e) {
			e.printStackTrace();
		} 

	}
	
	@SuppressWarnings("unused")
	private void setGatePropertiesFromExternalForm(URL gateHomeUrl) {
		gateHome = gateHomeUrl.toExternalForm() ;
		gateHome = gateHome.replaceAll("^[a-z]*:[\\/]+", "file:/") ;
		gateHome = replaceColonWithDollar(gateHome) ;
		gateUserHome = gateHome + "/gate.xml";
		gateSessionHome = gateHome + "/gate.session";
		gatePlugInsHome = gateHome + "/plugins" ;
	}
	
	private String replaceColonWithDollar(String input) {
		return input.replaceAll("^(.*)([A-Z]):(.*)$", "$1$2\\$$3") ;
	}

	public Resource createResource(String resourceClassName)
			throws ResourceInstantiationException {
		return Factory.createResource(resourceClassName);
	}

	public Resource createResource(String resourceClassName,
			FeatureMap parameterValues) throws ResourceInstantiationException {
		return Factory.createResource(resourceClassName, parameterValues) ;
	}

	public Resource createResource(String resourceClassName,
			FeatureMap parameterValues, FeatureMap features)
			throws ResourceInstantiationException {
		return Factory.createResource(resourceClassName, parameterValues, features) ;
	}

	public Resource createResource(String resourceClassName,
			FeatureMap parameterValues, FeatureMap features, String resourceName)
			throws ResourceInstantiationException {
		return Factory.createResource(resourceClassName, parameterValues, features, resourceName) ;
	}

	public void deleteResource(Resource resource) {
		Factory.deleteResource(resource) ;
	}

	public Corpus newCorpus(String name) throws ResourceInstantiationException {
		return newCorpus(name) ;
	}

	public Document newDocument(URL sourceUrl)
			throws ResourceInstantiationException {
		return Factory.newDocument(sourceUrl) ;
	}

	public Document newDocument(URL sourceUrl, String encoding)
			throws ResourceInstantiationException {
		return Factory.newDocument(sourceUrl, encoding) ;
	}

	public Document newDocument(String content)
			throws ResourceInstantiationException {
		return Factory.newDocument(content) ;
	}

	public FeatureMap newFeatureMap() {
		return Factory.newFeatureMap() ;
	}


}

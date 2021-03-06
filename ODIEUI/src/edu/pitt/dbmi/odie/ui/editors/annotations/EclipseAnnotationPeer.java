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

package edu.pitt.dbmi.odie.ui.editors.annotations;

import org.apache.log4j.Logger;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.text.AnnotationFS;

import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.ui.UIConstants;

/**
 * This class is used to provide additional information about the
 * {@link AnnotationFS} object to the custom drawing strategies. It has been
 * modified from the original by adding a constructor that takes AnnotationFS as
 * the only argument and initializes the type and text of the annotation object.
 * Original constructor removed.
 */
public class EclipseAnnotationPeer extends
		org.eclipse.jface.text.source.Annotation {

	Logger logger = Logger.getLogger(EclipseAnnotationPeer.class);
	private AnalysisDocument analysisDocument;

	/**
	 * The uima annotation.
	 */
	private AnnotationFS annotation;

	/**
	 * Initializes a new instance.
	 */
	public EclipseAnnotationPeer(AnnotationFS annFS,
			AnalysisDocument analysisDocument) {
		super(annFS.getType().getName(), false, annFS.getCoveredText());
		this.analysisDocument = analysisDocument;
		this.annotation = annFS;
	}

	public AnalysisDocument getAnalysisDocument() {
		return analysisDocument;
	}

	public void setAnalysisDocument(AnalysisDocument analysisDocument) {
		this.analysisDocument = analysisDocument;
	}

	/**
	 * Sets the annotation.
	 * 
	 * @param annotation
	 */
	public void setAnnotation(AnnotationFS annotation) {
		this.annotation = annotation;
		setType(annotation.getType().getName());
		setText(annotation.getCoveredText());
	}

	/**
	 * Retrieves the annotation.
	 * 
	 * @return the annotation
	 */
	public AnnotationFS getAnnotationFS() {
		return annotation;
	}

	@Override
	public String getType() {
		if (annotation != null) {
			IAnnotationSubType subtype = AnnotationSubTypeRegistry
					.getInstance().getSubType(annotation);
			if (subtype != null)
				return subtype.getName();
			else
				return annotation.getType().getName();
		}
		return super.getType();
	}

}
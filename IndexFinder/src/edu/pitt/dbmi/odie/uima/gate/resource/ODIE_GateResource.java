package edu.pitt.dbmi.odie.uima.gate.resource;

import gate.Corpus;
import gate.Document;
import gate.FeatureMap;
import gate.Resource;
import gate.creole.ResourceInstantiationException;

import java.net.URL;

public interface ODIE_GateResource {

	public Resource createResource(String resourceClassName)
			throws ResourceInstantiationException;

	public Resource createResource(String resourceClassName,
			FeatureMap parameterValues) throws ResourceInstantiationException;

	public Resource createResource(String resourceClassName,
			FeatureMap parameterValues, FeatureMap features)
			throws ResourceInstantiationException;

	public Resource createResource(String resourceClassName,
			FeatureMap parameterValues, FeatureMap features, String resourceName)
			throws ResourceInstantiationException;

	public void deleteResource(Resource resource);

	public Corpus newCorpus(String name) throws ResourceInstantiationException;

	public Document newDocument(URL sourceUrl)
			throws ResourceInstantiationException;

	public Document newDocument(URL sourceUrl, String encoding)
			throws ResourceInstantiationException;

	public Document newDocument(String content)
			throws ResourceInstantiationException;

	public FeatureMap newFeatureMap();

}

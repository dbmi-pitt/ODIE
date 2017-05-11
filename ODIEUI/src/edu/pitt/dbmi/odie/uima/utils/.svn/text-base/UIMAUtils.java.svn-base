package edu.pitt.dbmi.odie.uima.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.ArrayFS;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.metadata.Import;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.metadata.impl.Import_impl;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.TypeSystemUtil;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XMLSerializer;
import org.xml.sax.SAXException;

import edu.pitt.dbmi.odie.uima.util.ODIE_IFConstants;

public class UIMAUtils {

	public static void deSerializeCAS(String serializedCAS, CAS aCAS)
			throws SAXException, IOException {
		boolean lenientToTypeSystemErrors = false;
		ByteArrayInputStream bais = new ByteArrayInputStream(serializedCAS
				.getBytes());
		XmiCasDeserializer.deserialize(bais, aCAS, lenientToTypeSystemErrors);
	}

	public static String serializeCAS(CAS acas) throws SAXException,
			IOException {

		XmiCasSerializer ser = new XmiCasSerializer(acas.getTypeSystem());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		XMLSerializer xmlSer = new XMLSerializer(baos, false);
		ser.serialize(acas, xmlSer.getContentHandler());
		baos.close();
		return baos.toString();
	}

	public static String serializeTypeSystemDescriptor(
			TypeSystemDescription tsDesc) throws CASRuntimeException,
			ResourceInitializationException, SAXException, IOException,
			InvalidXMLException {

		TypeSystem ts = createCAS(tsDesc).getTypeSystem();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		TypeSystemDescription tsd = TypeSystemUtil
				.typeSystem2TypeSystemDescription(ts);
		addODIETypesToTypeSystem(tsd);
		tsd.resolveImports();
		tsd.toXML(baos);

		baos.close();
		return baos.toString();
	}

	private static void addODIETypesToTypeSystem(TypeSystemDescription tsd) {
		Import[] imports = tsd.getImports();
		Import odieImport = new Import_impl();
		odieImport.setName("edu.pitt.dbmi.odie.uima.types.ODIETypeSystem");

		List<Import> newImports = new ArrayList<Import>();
		newImports.addAll(Arrays.asList(imports));
		newImports.add(odieImport);

		tsd.setImports(newImports.toArray(new Import[] {}));

		// Alternatively we could use
		// CasCreationUtils.mergeTypeSystems(aTypeSystems)
	}

	public static TypeSystemDescription deSerializeTypeSystemDescriptor(
			String serializedTSDescriptor) throws InvalidXMLException,
			IOException, ResourceInitializationException {
		ByteArrayInputStream bais = new ByteArrayInputStream(
				serializedTSDescriptor.getBytes());
		// TODO assumes that this type system does not import other type
		// systems. Shouldnt be a problem since
		// when we store the serialized type system all imports have already
		// been resolved.
		TypeSystemDescription tsDesc = UIMAFramework.getXMLParser()
				.parseTypeSystemDescription(new XMLInputSource(bais, null));
		addODIETypesToTypeSystem(tsDesc);
		tsDesc.resolveImports();
		return tsDesc;
	}

	public static CAS createCAS(TypeSystemDescription tsDesc)
			throws ResourceInitializationException {
		return CasCreationUtils.createCas(tsDesc, null, null);
	}

	public static TypeSystem serializedTypeSystemDescriptorToTypeSystem(
			String serializedTSDescriptor) throws CASRuntimeException,
			ResourceInitializationException, InvalidXMLException, IOException {
		return createCAS(
				deSerializeTypeSystemDescriptor(serializedTSDescriptor))
				.getTypeSystem();

	}

	public static Object[] getAncestors(Type annotationType,
			TypeSystem typeSystem) {
		List ancestors = new ArrayList();
		Type t = annotationType;
		Type parent = typeSystem.getParent(t);
		while (parent != null) {
			ancestors.add(parent);
			parent = typeSystem.getParent(parent);
		}
		return ancestors.toArray();
	}

	public static AnalysisEngine loadAnalysisEngine(File aeDescriptorFile)
			throws Exception {
		XMLInputSource in = new XMLInputSource(aeDescriptorFile);
		ResourceSpecifier specifier = UIMAFramework.getXMLParser()
				.parseResourceSpecifier(in);

		AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(specifier);

		return ae;
	}

	public static AnalysisEngine loadAnalysisEngine(URL url) throws Exception {
		String filePath = url.getFile();
		File file = new File(filePath);
		return loadAnalysisEngine(file);
	}

	public static boolean isNamedEntity(Object inputElement) {
		if (inputElement instanceof AnnotationFS) {
			Type t = ((AnnotationFS) inputElement).getType();
			String s = t.getShortName();
			return (s.equals(ODIE_IFConstants.NAMED_ENTITY_TYPE_NAME));
		}
		return false;
	}

	public static Type getNamedEntityType(CAS cas) {
		return cas.getTypeSystem().getType(
				ODIE_IFConstants.NAMED_ENTITY_TYPE_FULLNAME);
	}

	public static Type getNewlineType(CAS cas) {
		return cas.getTypeSystem().getType(ODIE_IFConstants.NEWLINE_TYPE_FULLNAME);
	}

	public static Type getNPType(CAS cas) {
		return cas.getTypeSystem().getType(ODIE_IFConstants.NP_TYPE_FULLNAME);
	}

	public static ArrayFS getOntologyConceptArray(AnnotationFS ann) {
		Type namedEntityType = ann.getType();

		Feature ontologyConceptArrFeature = namedEntityType
				.getFeatureByBaseName(ODIE_IFConstants.ONTOLOGY_CONCEPT_ARRAY_FEATURE_NAME);
		FeatureStructure fs = ann.getFeatureValue(ontologyConceptArrFeature);
		ArrayFS arr = (ArrayFS) fs;
		return arr;
	}

	public static AnnotationFS[] getInstances(CAS cas, Type type) {
		AnnotationIndex ai = cas.getAnnotationIndex(type);
		AnnotationFS[] arr = new AnnotationFS[ai.size()];

		int i = 0;
		for (Iterator it = ai.iterator(); it.hasNext();) {
			arr[i] = (AnnotationFS) it.next();
			i++;
		}
		return arr;
	}

}

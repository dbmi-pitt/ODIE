/**
 * 
 */
package edu.pitt.dbmi.odie.ui.workers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import morphster.ontology.datamodel.OBOCacheImpl;
import morphster.ontology.oboparse.OBOSimpleParser;
import morphster.ontology.oboparse.ParseException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.ontology.IOntology;

/**
 * @author Girish Chavan
 * 
 */
public class OntologyImporter implements IRunnableWithProgress {

	List<URI> uriList = null;

	/**
	 * @param uriList
	 */
	public OntologyImporter(List<URI> uriList) {
		this.uriList = uriList;
	}

	public OntologyImporter(URI uri) {
		this.uriList = new ArrayList<URI>();
		uriList.add(uri);
	}

	String MSG_READ = "Reading ontology from...";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core
	 * .runtime.IProgressMonitor)
	 */
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		int work = 0;
		for (URI uri : uriList) {
			work++;
			if (uri.toASCIIString().endsWith("obo")) {
				work += 2;
			}
		}

		monitor.beginTask(MSG_READ, work);

		MiddleTier mt = Activator.getDefault().getMiddleTier();

		for (URI uri : uriList) {
			if (uri.toASCIIString().endsWith("obo")) {

				// TODO Get the correct ontology name
				String ontologyName = "SomeOntology";

				monitor.setTaskName("Converting OBO to OWL...");
				monitor.subTask("reading OBO file");
				monitor.worked(1);

				try {

					BufferedReader in = new BufferedReader(
							new InputStreamReader(uri.toURL().openStream()));

					OBOCacheImpl c = new OBOCacheImpl(ontologyName);
					OBOSimpleParser p = new OBOSimpleParser(in);
					OBOSimpleParser.setCache(c);
					OBOSimpleParser.debug = false;
					OBOSimpleParser.oboDocument();

					monitor.subTask("converting to OWL");
					monitor.worked(1);

					String owlOutName = ontologyName + ".owl";

					String stateLocation = Activator.getDefault()
							.getStateLocation().toOSString();
					FileWriter fwOWL = new FileWriter(stateLocation
							+ File.pathSeparator + owlOutName);
					c.toOWLFile(fwOWL);

					monitor.setTaskName("Importing " + owlOutName);
					monitor
							.subTask("This might take a while, please be patient...");
					monitor.worked(1);

					IOntology o = mt.importOntology(uri);

					if (o != null)
						importedOntologies.add(o);

				} catch (MalformedURLException e) {
					e.printStackTrace();
					monitor.worked(2);
				} catch (IOException e) {
					e.printStackTrace();
					monitor.worked(2);
				} catch (ParseException e) {
					e.printStackTrace();
					monitor.worked(2);
				} catch (Exception e) {
					e.printStackTrace();
					monitor.worked(2);
				} finally {
					monitor.setTaskName(MSG_READ);
				}

			} else {
				String name = uri.getPath();

				name = (new File(uri.getPath())).getName();

				monitor.setTaskName("Importing " + name);
				monitor
						.subTask("This might take a while, please be patient...");
				monitor.worked(1);
				IOntology o = mt.importOntology(uri);

				if (o != null)
					importedOntologies.add(o);
			}

		}

		monitor.done();
	}

	public boolean wasSuccessful() {
		return importedOntologies.size() == uriList.size();
	}

	List<IOntology> importedOntologies = new ArrayList<IOntology>();

	public List<IOntology> getImportedOntologies() {
		return importedOntologies;
	}

}

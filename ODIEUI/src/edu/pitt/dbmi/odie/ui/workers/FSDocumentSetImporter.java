package edu.pitt.dbmi.odie.ui.workers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.operation.IRunnableWithProgress;

import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.Document;
import edu.pitt.dbmi.odie.ui.Activator;

public class FSDocumentSetImporter implements IRunnableWithProgress,
		IMonitoredWorker {

	IPath path;
	private List<Document> doclist;
	private int workUnits;

	public FSDocumentSetImporter(IPath path, List<Document> documentSet, int workUnits) {
		this.path = path;
		doclist = documentSet;
		this.workUnits = workUnits;
	}

	@Override
	public int getWorkUnits() {
		return workUnits;
	}

	@Override
	public boolean startWork(IProgressMonitor monitor) {
		monitor.beginTask("Reading Documents", getWorkUnits());

		processDirectory(path.toFile(), monitor);
		monitor.subTask("");
		monitor.done();
		return true;
	}

	private boolean processDirectory(File file, IProgressMonitor monitor) {
		MiddleTier mt = Activator.getDefault().getMiddleTier();
	
		for(File f:file.listFiles()){
		monitor.subTask(" Importing " + f.getName());

			if (f.isFile()) {
				Document d = mt
						.getDocument(f.toURI().toASCIIString());
				if (d == null) {
					d = new Document();
					try {
						d.setURI(f.toURI());
					} catch (Exception e1) {
						e1.printStackTrace();
						continue;
					}
					d.setName(f.getName());
					try {
						mt.persist(d);
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
				doclist.add(d);
				monitor.worked(1);
			}
			else if(f.isDirectory()){
				return processDirectory(f,monitor);
			}
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
		}
		return true;
		
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		this.startWork(monitor);

	}

}

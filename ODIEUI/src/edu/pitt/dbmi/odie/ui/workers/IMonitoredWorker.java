package edu.pitt.dbmi.odie.ui.workers;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IMonitoredWorker {

	public int getWorkUnits();

	public boolean startWork(IProgressMonitor monitor);
}

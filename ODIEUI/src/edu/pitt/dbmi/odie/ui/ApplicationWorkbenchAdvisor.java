package edu.pitt.dbmi.odie.ui;

import java.io.File;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import edu.pitt.dbmi.odie.ui.handlers.DropInHandler;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "edu.pitt.dbmi.odie.perspective";

	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);

		// save last window state
		// configurer.setSaveAndRestore(true);
	}

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

	@Override
	public void postStartup() {
		super.postStartup();
		createSystemDirectories();
		handleDropIns();
	}

	private void createSystemDirectories() {
		File f = GeneralUtils.getDropinDir();
		if(!f.exists())
			f.mkdirs();
		
	}

	private void handleDropIns() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				DropInHandler dh = new DropInHandler();
				try {
					dh.executeIfRequired();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		});
	}

}

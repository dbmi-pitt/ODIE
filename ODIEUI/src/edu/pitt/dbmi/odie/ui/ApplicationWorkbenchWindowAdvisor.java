package edu.pitt.dbmi.odie.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	Logger logger = Logger.getLogger(ApplicationWorkbenchWindowAdvisor.class);

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();

		configurer.setInitialSize(new Point(1024, 768));
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(false);
		hookPerspectiveListener();
	}

	private void hookPerspectiveListener() {
		getWindowConfigurer().getWindow().addPerspectiveListener(
				new IPerspectiveListener() {

					@Override
					public void perspectiveActivated(IWorkbenchPage page,
							IPerspectiveDescriptor perspective) {
						System.out.println("perspective activated");

					}

					@Override
					public void perspectiveChanged(IWorkbenchPage page,
							IPerspectiveDescriptor perspective, String changeId) {
					}
				});
	}

	public void postWindowCreate() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
				.setMaximized(true);

		// ICoolBarManager cbm =
		// getWindowConfigurer().getActionBarConfigurer().getCoolBarManager();
		// for(IContributionItem ci:cbm.getItems()){
		// logger.debug(ci.getId());
		// }

		removeUnwantedPreferencePages();
		hookPreferenceListener();
		hookView2EditorLinker();
	}

	private void hookPreferenceListener() {
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(
				new IPropertyChangeListener() {
						@Override
					public void propertyChange(
						org.eclipse.jface.util.PropertyChangeEvent event) {
						
						boolean doit = MessageDialog.openQuestion(Display.getCurrent()
								.getActiveShell(), "Preferences Changed",
								"ODIE needs to be restarted for the changes to take effect.\n"
										+ "Restart now?");
						if (doit) {
							GeneralUtils.restartODIE();
						}
					}
				});
	}

	private void removeUnwantedPreferencePages() {
		PreferenceManager pm = PlatformUI.getWorkbench().getPreferenceManager();
		pm.remove("org.eclipse.ui.preferencePages.Workbench");
	}

	private void hookView2EditorLinker() {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		page.addPartListener(new View2EditorLinker());
	}

}

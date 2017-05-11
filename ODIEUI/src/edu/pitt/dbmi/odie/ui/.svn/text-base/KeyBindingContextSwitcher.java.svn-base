package edu.pitt.dbmi.odie.ui;

import org.apache.log4j.Logger;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;

public class KeyBindingContextSwitcher implements IPartListener {

	Logger logger = Logger.getLogger(this.getClass());
	String defaultContext = "org.eclipse.ui.contexts.window";
	private IContextActivation currentActivation;
	private IContextService currentContextService;

	@Override
	public void partActivated(IWorkbenchPart part) {
		logger.debug(part.getTitle() + " activated");
		if (currentContextService == null) {
			getContextService();
		}

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				currentActivation = currentContextService
						.activateContext("edu.pitt.dbmi.odie.ui.editors.AnalysisEditor");
			}
		});
	}

	private void getContextService() {
		currentContextService = ((IContextService) PlatformUI.getWorkbench()
				.getService(IContextService.class));

	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		logger.debug(part.getTitle() + " brought to top");

	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		logger.debug(part.getTitle() + " closed");

	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		logger.debug(part.getTitle() + " deactivated");

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				currentContextService.deactivateContext(currentActivation);
			}
		});
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		logger.debug(part.getTitle() + " opened");

	}

}

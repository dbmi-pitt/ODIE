/**
 * 
 */
package edu.pitt.dbmi.odie.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.apache.uima.pear.util.MessageRouter.StdChannelListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import edu.pitt.dbmi.odie.ui.wizards.pages.PEARInstallationPage;
import edu.pitt.dbmi.odie.ui.workers.PEARInstaller;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

/**
 * @author Girish Chavan
 * 
 */
public class PEARInstallerWizard extends Wizard {
	Logger logger = Logger.getLogger(PEARInstallerWizard.class);

	PEARInstallationPage pearPage;

	public PEARInstallerWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {

		try {
			pearPage.showConsole();

			final PEARInstaller pi = new PEARInstaller(pearPage.getPEARFile(),
					pearPage.getInstallationDir());

			pi.setListener(listener);

			getContainer().run(true, true, pi);

			if (pi.wasSuccessful()) {
				if (MessageDialog
						.openQuestion(
								Display.getCurrent().getActiveShell(),
								"Installation Successful",
								"Do you want to import the installed component into ODIE? \n This is required for it to be "
										+ "used within an ODIE analysis")) {

					Display.getCurrent().asyncExec(new Runnable() {

						@Override
						public void run() {
							ImportAnalysisEnginesWizard wizard = new ImportAnalysisEnginesWizard(
									pi.getMainComponentDescFilePath());
							WizardDialog dialog = new WizardDialog(Display
									.getCurrent().getActiveShell(), wizard);

							dialog.open();
						}

					});
				}
				return true;
			} else {
				GeneralUtils.showScrolledErrorDialog("Installation Error", pi
						.getErrorMessage());
				return false;
			}

		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void addPages() {
		setWindowTitle("Install PEAR");

		pearPage = new PEARInstallationPage();
		addPage(pearPage);
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		pageContainer.getShell().setSize(485, 600);
	}

	StdChannelListener listener = new StdChannelListener() {

		@Override
		public void errMsgPosted(final String errMsg) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					pearPage.appendToConsole(PEARInstallationPage.ERROR_HEADER
							+ errMsg + "\n");

				}

			});
		}

		@Override
		public void outMsgPosted(final String outMsg) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					pearPage.appendToConsole(outMsg + "\n");
				}
			});
		}
	};

}

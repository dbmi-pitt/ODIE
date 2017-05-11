package edu.pitt.dbmi.odie.ui.workers;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;
import org.apache.uima.pear.tools.InstallationController;
import org.apache.uima.pear.tools.InstallationDescriptor;
import org.apache.uima.pear.tools.InstallationDescriptorHandler;
import org.apache.uima.pear.util.MessageRouter.StdChannelListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class PEARInstaller implements IRunnableWithProgress {
	Logger logger = Logger.getLogger(this.getClass());
	private File localPearFile;
	private File installationDir;

	private StdChannelListener listener;

	private String mainComponentDescFilePath;

	public PEARInstaller(File localPearFile, File installationDir) {
		this.localPearFile = localPearFile;
		this.installationDir = installationDir;
	}

	public StdChannelListener getListener() {
		return listener;
	}

	public void setListener(StdChannelListener listener) {
		this.listener = listener;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public void run(final IProgressMonitor monitor)
			throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask(
					"Installing PEAR file:" + localPearFile.getName(), 3);
			InstallationController.setLocalMode(true);
			InstallationDescriptorHandler installationDescriptorHandler = new InstallationDescriptorHandler();

			JarFile jarFile = new JarFile(localPearFile);
			installationDescriptorHandler.parseInstallationDescriptor(jarFile);
			InstallationDescriptor instDescriptor = installationDescriptorHandler
					.getInstallationDescriptor();

			InstallationController installationController = new InstallationController(
					instDescriptor.getMainComponentId(), localPearFile,
					installationDir);

			if (listener != null)
				installationController.addMsgListener(listener);

			monitor.worked(1);
			monitor.setTaskName("Installing " + localPearFile.getName()
					+ " (ID:" + instDescriptor.getMainComponentId() + ")");
			InstallationDescriptor insdObject = installationController
					.installComponent();
			if (monitor.isCanceled())
				return;

			monitor.worked(1);
			if (insdObject == null) {
				setErrorMessage(installationController.getInstallationMsg());
			} else {
				monitor.setTaskName("Verifying Installation...");
				installationController.saveInstallationDescriptorFile();
				if (installationController.verifyComponent()) {
					File pearDescFile = new File(insdObject
							.getMainComponentRoot(), insdObject
							.getMainComponentId()
							+ InstallationController.PEAR_DESC_FILE_POSTFIX);
					mainComponentDescFilePath = pearDescFile.getAbsolutePath();
					success = true;
				} else {
					setErrorMessage(installationController.getVerificationMsg());
					success = false;
				}
				monitor.worked(1);
			}
		} catch (Exception e) {
			success = false;
			setErrorMessage(e.getMessage());
		} finally {
			monitor.done();
		}
	}

	boolean success = false;
	String errorMessage;

	public boolean wasSuccessful() {
		return success;
	}

	public String getMainComponentDescFilePath() {
		return mainComponentDescFilePath;
	}

}

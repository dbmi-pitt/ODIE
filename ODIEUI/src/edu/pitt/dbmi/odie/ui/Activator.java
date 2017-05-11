package edu.pitt.dbmi.odie.ui;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.middletier.AnalysisSpaceMiddleTier;
import edu.pitt.dbmi.odie.middletier.AnalysisSpaceMiddleTierFactory;
import edu.pitt.dbmi.odie.middletier.Configuration;
import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.utils.GeneralUtils;
import edu.pitt.dbmi.odie.utils.MySQLUtils;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ODIEUI";

	// The shared instance
	private static Activator plugin;

	private MiddleTier middleTier;
	private AnalysisSpaceMiddleTierFactory amFactory;

	private Configuration configuration;

	Logger logger = Logger.getLogger(this.getClass());

	public MiddleTier getMiddleTier() {
		if (middleTier == null && configuration != null) {
			initMiddleTier();
		}

		return middleTier;
	}

	public AnalysisSpaceMiddleTier getAnalysisMiddleTier(Analysis a) {
		if (amFactory == null)
			initAMFactory();

		return amFactory.getInstance(a);
	}

	private void initAMFactory() {
		amFactory = new AnalysisSpaceMiddleTierFactory(configuration);
	}

	/**
	 * 
	 */
	private void initMiddleTier() {
		middleTier = MiddleTier.getInstance(configuration);
		initAMFactory();
	}

	public Configuration getConfiguration() {

		return configuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		logger.info("Activator start");

		plugin = this;
		// IProduct product = Platform.getProduct();
		// AbstractSplashHandler splash = null;
		// if (product != null)
		// splash = SplashHandlerFactory.findSplashHandlerFor(product);
		// if(splash != null){
		// System.out.println("calling init");
		// splash.init(new Shell(Display.getCurrent()));
		// loadODIE(splash.getBundleProgressMonitor());
		// }

		super.start(context);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		middleTier.dispose();

		if(GeneralUtils.hasMySQLBundled() && MySQLUtils.isMySQLRunning(GeneralUtils.getBundledMySQLPath()))
			MySQLUtils.stopMySQL(GeneralUtils.getBundledMySQLPath());

		plugin = null;
		super.stop(context);

	}

	
	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;

	}

	public void resetAnalysisMiddleTier(Analysis analysis) {
		amFactory.refreshInstance(analysis);
	}

}

package edu.pitt.dbmi.odie.ui.splashHandlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;

import edu.pitt.dbmi.odie.middletier.Configuration;
import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.middletier.MiddleTierJDBC;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.preferences.ConfigurationPage;
import edu.pitt.dbmi.odie.ui.preferences.PreferenceConstants;
import edu.pitt.dbmi.odie.ui.preferences.PreferenceDefaults;
import edu.pitt.dbmi.odie.utils.GeneralUtils;
import edu.pitt.dbmi.odie.utils.MySQLUtils;

public class ApplicationConfigurer {

	public boolean success;

	public static boolean configureApplication(Shell shell) {
		configureLogging();

		boolean paramMissing = !checkReqdParametersPresent();

		boolean configError = false;
		if(GeneralUtils.hasMySQLBundled() && !MySQLUtils.isMySQLRunning(GeneralUtils.getBundledMySQLPath()))
			MySQLUtils.startupMySQL(GeneralUtils.getBundledMySQLPath());

		if (!paramMissing)
			configError = !checkConfiguration(getConfiguration(), shell);

		if (configError) {
			MessageDialog
					.openError(shell, "Configuration Error",
							"There was an error connecting to database.\n See odie.log for details.");
		}

		while (paramMissing || configError) {
			int returnCode = showConfigurationDialog(shell);
			if (returnCode == Dialog.OK) {
				if (!checkReqdParametersPresent()) {
					MessageDialog.openError(shell, "Missing Parameters",
							"Some of the required parameters are blank.");
					continue;
				} else if (!checkConfiguration(getConfiguration(), shell)) {
					MessageDialog
							.openError(shell, "Configuration Error",
									"There was an error connecting to database.\n See log file for details.");
					configError = true;
					continue;
				} else
					break;

			} else {
				return false;
			}
		}
		initConfigurationInActivator();
		changeHBM2DDLPolicyPreference();
		return true;
	}

	

	private static void configureLogging() {
		ILog logger = Activator.getDefault().getLog();

		String log4jfile = GeneralUtils.getPreferenceStore().getString(
				PreferenceConstants.PROPERTY_KEY_LOG4J_CONFIG_FILE);

		IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID,
				"Using LOG4J Configuration file:" + log4jfile);
		logger.log(status);
		File f = new File(log4jfile);
		if (f.exists()) {
			try {
				PropertyConfigurator.configure(f.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		Boolean redirect = new Boolean(
				GeneralUtils.getPreferenceStore().getString(
						PreferenceConstants.PROPERTY_KEY_REDIRECT_STDSTREAMS));
		if (redirect)
			redirectStandardStreamsToFiles();
	}

	private static void redirectStandardStreamsToFiles() {
		Logger log4jlogger = Logger.getLogger(ApplicationConfigurer.class);

		ILog logger = Activator.getDefault().getLog();
		IPath path = GeneralUtils.getPluginFolderPath();

		final String outfile = path.append("/stdout.log").toOSString();
		final String errfile = path.append("/stderr.log").toOSString();

		try {
			File f = new File(outfile);
			if (!f.exists())
				f.createNewFile();

			f = new File(errfile);
			if (!f.exists())
				f.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}

		// Create a new output stream for the standard output.
		PrintStream stdout = null;

		try {

			stdout = new PrintStream(new FileOutputStream(outfile));
		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Error redirecting standard output");
			logger.log(status);
			stdout = null;
		}

		// Create new output stream for the standard error output.
		PrintStream stderr = null;
		try {

			stderr = new PrintStream(new FileOutputStream(errfile));
		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Error redirecting standard error");
			logger.log(status);
			stderr = null;
		}

		// Set the System out and err streams to use our replacements.
		if (stdout != null) {
			log4jlogger.info("Redirecting standard out to " + outfile);
			System.setOut(stdout);
		}
		if (stderr != null) {
			log4jlogger.info("Redirecting standard error to " + errfile);
			System.setErr(stderr);
		}
	}

	Logger logger = Logger.getLogger(ApplicationConfigurer.class);
	private Connection conn;

	private void initDBConnection(String dbDriver, String dbURL,
			String username, String password) throws Exception {
		logger.info("Initializing database connection");
		Class.forName(dbDriver).newInstance();
		conn = DriverManager.getConnection(dbURL, username, password);
	}

	public void closeConnection() {
		try {
			if (conn != null)
				conn.close();
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
	}

	private static boolean checkConfiguration(Configuration config, Shell shell) {
		MiddleTierJDBC mtjdbc;
		mtjdbc = getJDBCConnection(config);
		if (mtjdbc == null) {
			mtjdbc = getMysqlJDBCConnection(config);
			if (mtjdbc == null)
				return false;
			else {
				// boolean createSchema = MessageDialog.openQuestion(shell,
				// "Database does not exist",
				// "The database '" + getConfiguration().getDatabaseName() +
				// "' does not exist. " +
				// "Would you like ODIE to create it for you?" );
				boolean createSchema = true;

				if (createSchema) {
					try {
						mtjdbc.createSchema(config.getDatabaseName());
					} catch (SQLException e) {
						e.printStackTrace();
						MessageDialog.openError(shell,
								"Database Creation Error",
								"There was an error creating the database.\n"
										+ e.getMessage());
						mtjdbc.dispose();
						return false;
					}
				}
			}
		}
		mtjdbc.dispose();
		MiddleTier mt = null;
		mt = MiddleTier.getInstance(config);
		return mt != null;
	}

	private static MiddleTierJDBC getMysqlJDBCConnection(Configuration config) {
		String oldUrl = config.getDatabaseURL();
		config.setDatabaseURL(config.getDatabaseURLWithoutSchema() + "mysql");
		MiddleTierJDBC mtjdbc = getJDBCConnection(config);
		config.setDatabaseURL(oldUrl);
		return mtjdbc;
	}

	private static MiddleTierJDBC getJDBCConnection(Configuration config) {
		MiddleTierJDBC mtjdbc = null;
		try {
			mtjdbc = new MiddleTierJDBC(config);
		} catch (Exception e) {
			e.printStackTrace();
			if (mtjdbc != null)
				mtjdbc.dispose();

			mtjdbc = null;
		}
		return mtjdbc;
	}

	private static int showConfigurationDialog(Shell shell) {
		PreferenceManager prefManager = createPreferenceManager();
		PreferenceDialog prefDialog = new PreferenceDialog(shell, prefManager);

		ScopedPreferenceStore ps = (ScopedPreferenceStore) GeneralUtils
				.getPreferenceStore();
		prefDialog.setPreferenceStore(ps);

		return prefDialog.open();
	}

	private static PreferenceManager createPreferenceManager() {

		PreferenceManager prefManager = new PreferenceManager();

		PreferenceNode node = new PreferenceNode(
				"edu.pitt.dbmi.odie.ui.preferences.ConfigurationPage",
				new ConfigurationPage());
		prefManager.addToRoot(node);

		// node = new
		// PreferenceNode("edu.pitt.dbmi.odie.ui.preferences.AdvancedPreferencesPage"
		// , new AdvancedPreferencesPage());
		// prefManager.addToRoot(node);
		return prefManager;
	}

	private static void changeHBM2DDLPolicyPreference() {
		String hbm2ddl = GeneralUtils.getPreferenceStore().getString(
				PreferenceConstants.PROPERTY_KEY_HIBERNATE_HBM2DDL);
		if (hbm2ddl != null && hbm2ddl.equals("create")) {
			GeneralUtils.getPreferenceStore().setValue(
					PreferenceConstants.PROPERTY_KEY_HIBERNATE_HBM2DDL,
					"update");
			try {
				(new ConfigurationScope()).getNode("ODIEUI").flush();
			} catch (BackingStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	private static void initConfigurationInActivator() {
		Configuration config = getConfiguration();
		Activator.getDefault().setConfiguration(config);
	}

	private static Configuration getConfiguration() {
		String dbDriver = GeneralUtils.getPreferenceStore().getString(
				PreferenceConstants.PROPERTY_KEY_HIBERNATE_DRIVER_CLASS);
		String dbURL = GeneralUtils.getPreferenceStore().getString(
				PreferenceConstants.PROPERTY_KEY_HIBERNATE_CONNECTION_URL);
		String dbUsername = GeneralUtils.getPreferenceStore().getString(
				PreferenceConstants.PROPERTY_KEY_HIBERNATE_CONNECTION_USERNAME);
		String dbPassword = GeneralUtils.getPreferenceStore().getString(
				PreferenceConstants.PROPERTY_KEY_HIBERNATE_CONNECTION_PASSWORD);
		String gateHome = GeneralUtils.getPreferenceStore().getString(
				PreferenceConstants.PROPERTY_KEY_GATE_HOME);
		String noCharts = GeneralUtils.getPreferenceStore().getString(
				PreferenceConstants.PROPERTY_KEY_NO_CHARTS);
		
		Configuration config = new Configuration();
		config.setDatabaseURL(dbURL);
		config.setDatabaseDriver(dbDriver);
		config.setGATEHome(gateHome);
		config.setUsername(dbUsername);
		config.setPassword(dbPassword);
		
		
		config.setNoCharts((new Boolean(noCharts)).booleanValue());
		
		config.setHBM2DDLPolicy(GeneralUtils.getPreferenceStore().getString(
				PreferenceConstants.PROPERTY_KEY_HIBERNATE_HBM2DDL));
		config
				.setRepositoryTableName(PreferenceDefaults
						.getDefaultForProperty(PreferenceConstants.PROPERTY_KEY_REPOSITORY_TABLE_NAME));
		config
				.setTemporaryDirectory(PreferenceDefaults
						.getDefaultForProperty(PreferenceConstants.PROPERTY_KEY_TEMP_DIR));
		config
				.setLuceneIndexDirectory(PreferenceDefaults
						.getDefaultForProperty(PreferenceConstants.PROPERTY_KEY_LUCENE_DIR));
		config
				.setRedirectStandardStreams(GeneralUtils
						.getPreferenceStore()
						.getString(
								PreferenceConstants.PROPERTY_KEY_REDIRECT_STDSTREAMS));
		return config;
	}

	private static boolean checkReqdParametersPresent() {
		List<String> valueList = new ArrayList<String>();

		valueList.add(GeneralUtils.getPreferenceStore().getString(
				PreferenceConstants.PROPERTY_KEY_HIBERNATE_DRIVER_CLASS));
		valueList.add(GeneralUtils.getPreferenceStore().getString(
				PreferenceConstants.PROPERTY_KEY_HIBERNATE_CONNECTION_URL));
		valueList
				.add(GeneralUtils
						.getPreferenceStore()
						.getString(
								PreferenceConstants.PROPERTY_KEY_HIBERNATE_CONNECTION_USERNAME));
		valueList
				.add(GeneralUtils
						.getPreferenceStore()
						.getString(
								PreferenceConstants.PROPERTY_KEY_HIBERNATE_CONNECTION_PASSWORD));
		valueList.add(GeneralUtils.getPreferenceStore().getString(
				PreferenceConstants.PROPERTY_KEY_GATE_HOME));
		// valueList.add(getPreferenceStore().getString(PreferenceConstants.PROPERTY_KEY_HIBERNATE_DIALECT));

		return checkForNotNullOrEmpty(valueList);

	}

	private static boolean checkForNotNullOrEmpty(List<String> valueList) {
		for (String value : valueList) {
			if (value == null || value.trim().length() == 0)
				return false;
		}

		return true;
	}

}

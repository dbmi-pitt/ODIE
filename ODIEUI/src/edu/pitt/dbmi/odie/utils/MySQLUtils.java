package edu.pitt.dbmi.odie.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.ui.Activator;

public class MySQLUtils {
	
	//Log4J may not be initialized yet so use default logging.
	static ILog logger = Activator.getDefault().getLog();
	
	
	public static void stopMySQL(String mysqlPath) {
		String command = mysqlPath + File.separator + "bin" + File.separator + "mysqladmin";
		
		if(mysqlPath!=null){
			try {
				IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID,
				"Stopping MySQL");
				logger.log(status);
				Runtime rt = Runtime.getRuntime();
				String[] commands = new String[] {
						command,
						"-u"+ODIEConstants.MYSQL_USERNAME, "-p" + ODIEConstants.MYSQL_PASSWORD,
						"-P" + ODIEConstants.MYSQL_PORT, "shutdown" };
				Process process = rt.exec(commands);
				
				InputStreamReader isr = new InputStreamReader(process
						.getErrorStream());
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					System.err.println(line);
				}
				br.close();
	
				
			} catch (Exception e) {
				IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						e.getMessage());
				logger.log(status);
				e.printStackTrace();
			}
		}
		
	}

	public static void startupMySQL(String mysqlPath) {
		String command = mysqlPath + File.separator + "bin"	+ File.separator + "mysqld --verbose";
		
		if(mysqlPath!=null){
			try {
				IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID,
						"Starting MySQL");
				logger.log(status);
				Runtime rt = Runtime.getRuntime();
				Process process = rt.exec(command);
				InputStreamReader isr = new InputStreamReader(process
						.getErrorStream());
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					System.err.println(line);
				}
				br.close();
	
				for (int i = 0; i < 3; i++) {
					if (isMySQLRunning(mysqlPath))
						break;
				}
	
			} catch (Exception e) {
				IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						e.getMessage());
				logger.log(status);
				e.printStackTrace();
			}
		}
	}

	public static boolean isMySQLRunning(String mysqlPath) {
		String command = mysqlPath + File.separator + "bin"	+ File.separator + "mysqladmin";
		try {
			Runtime rt = Runtime.getRuntime();
			String[] commands = new String[] { command, "-u"+ODIEConstants.MYSQL_USERNAME,
					"-p"+ODIEConstants.MYSQL_PASSWORD, "-P"+ODIEConstants.MYSQL_PORT, "status" };
			Process process = rt.exec(commands);
			InputStreamReader isr = new InputStreamReader(process
					.getErrorStream());
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				System.err.println(line);
			}
			br.close();
			
			if (process.waitFor() != 0)
				return false;
		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e
					.getMessage());
			logger.log(status);
			e.printStackTrace();
			return false;
		}
		return true;
	}
}

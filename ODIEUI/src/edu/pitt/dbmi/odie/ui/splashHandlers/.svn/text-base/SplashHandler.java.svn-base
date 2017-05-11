package edu.pitt.dbmi.odie.ui.splashHandlers;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.branding.IProductConstants;
import org.eclipse.ui.splash.BasicSplashHandler;

import edu.pitt.dbmi.odie.ui.workers.ApplicationPreloader;

/**
 * @since 3.3
 * 
 */
public class SplashHandler extends BasicSplashHandler {
	Logger logger = Logger.getLogger(SplashHandler.class);
	public static boolean firstcall = true;
	private Rectangle progressRect;
	private Rectangle messageRect;
	private int foregroundColorInteger;

	public void init(Shell splash) {
		super.init(splash);

		readProductConfig();

		setProgressRect(progressRect);
		setMessageRect(messageRect);
		setForeground(new RGB((foregroundColorInteger & 0xFF0000) >> 16,
				(foregroundColorInteger & 0xFF00) >> 8,
				foregroundColorInteger & 0xFF));

		if (!ApplicationConfigurer.configureApplication(splash)) {
			MessageBox message = new MessageBox(splash, SWT.ICON_ERROR);
			message.setText("ODIE");
			message
					.setMessage("Cannot continue without a valid configuration. ODIE will exit.");
			message.open();
			// TODO Find a better way to exit the application using some Eclipse
			// framework exit command
			System.exit(-1);
		}

		initApplicationPreloader(splash);
	}

	private void readProductConfig() {
		IProduct product = Platform.getProduct();
		if (product != null) {
			String progressRectString = product
					.getProperty(IProductConstants.STARTUP_PROGRESS_RECT);
			String messageRectString = product
					.getProperty(IProductConstants.STARTUP_MESSAGE_RECT);
			String foregroundColorString = product
					.getProperty(IProductConstants.STARTUP_FOREGROUND_COLOR);

			progressRect = StringConverter.asRectangle(progressRectString,
					new Rectangle(80, 200, 325, 15));
			messageRect = StringConverter.asRectangle(messageRectString,
					new Rectangle(80, 185, 325, 20));

			try {
				foregroundColorInteger = Integer.parseInt(
						foregroundColorString, 16);
			} catch (Exception ex) {
				foregroundColorInteger = 0x000000; // off white
			}
		} else {
			logger
					.error("Product Configuration not readable. Using defaults for splash screen display.");
			messageRect = new Rectangle(0, 0, 100, 20);
			progressRect = new Rectangle(0, 20, 100, 20);
			foregroundColorInteger = 0xD2D7FF;
		}
	}

	private void initApplicationPreloader(final Shell splash) {
		IProgressMonitor monitor = getBundleProgressMonitor();
		ApplicationPreloader preloader = new ApplicationPreloader(monitor);
		preloader.start();
		while (preloader.isAlive()) {
			if (splash.getDisplay().readAndDispatch() == false) {
				splash.getDisplay().sleep();
			}
		}
		if (!preloader.isSuccessful()) {

			MessageBox message = new MessageBox(splash, SWT.ICON_ERROR);
			message.setText("ODIE");
			message
					.setMessage("Application initialization failed. See log files for details.");
			message.open();
			// TODO Find a better way to exit the application using some Eclipse
			// framework exit command
			System.exit(-1);
			// while(true){
			// if (splash.getDisplay().readAndDispatch() == false) {
			// splash.getDisplay().sleep();
			// }
			// }
		}

	}

}

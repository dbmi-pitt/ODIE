package edu.pitt.dbmi.odie.ui.views;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import edu.pitt.dbmi.odie.ui.Aesthetics;

public class LightWeightConsoleView extends ViewPart {
	private Text text;

	public static final String ID = "edu.pitt.dbmi.odie.ui.views.LightWeightConsoleView";

	public void createPartControl(Composite parent) {
		text = new Text(parent, SWT.READ_ONLY | SWT.MULTI);
		text.setForeground(Aesthetics.getBlackColor());
		text.setBackground(Aesthetics.getWhiteColor());
		OutputStream out = new OutputStream() {
			@Override
			public void write(final int b) throws IOException {
				if (text.isDisposed())
					return;

				Display d = Display.getCurrent();
				if (d == null)
					return;

				d.asyncExec(new Runnable() {
					@Override
					public void run() {
						text.append(String.valueOf((char) b));
					}
				});
			}
		};
		final PrintStream oldOut = System.out;
		System.setOut(new PrintStream(out));
		text.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				System.setOut(oldOut);
			}
		});
	}

	public void setFocus() {
		text.setFocus();
	}
}
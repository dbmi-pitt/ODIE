/**
 * @author Girish Chavan
 *
 * Sep 10, 2008
 */
package test;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.pitt.dbmi.odie.ui.dialogs.AddToProposalOntologyDialog;
import edu.pitt.dbmi.odie.ui.views.StickyNotePanel;

public class SWTDialogTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display, SWT.DIALOG_TRIM | SWT.RESIZE);
		shell.setText("SWT Dialog Test");
		// testAddtoOntologyDialog(shell);
		testLayouts(shell);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private static void testLayouts(Shell shell) {
		FillLayout fl = new FillLayout();
		fl.marginHeight = 10;
		fl.marginWidth = 10;
		shell.setLayout(fl);
		Composite container = new Composite(shell, SWT.BORDER);
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		container.setLayout(gl);
		GridData gd = new GridData(GridData.FILL_BOTH);
		// gd.horizontalAlignment = SWT.LEFT;
		StickyNotePanel sticky = new StickyNotePanel(container, SWT.BORDER);
		sticky
				.setText("This is a test of a long piece of text. "
						+ "Some of our users really just want to use Ontologies "
						+ "to get the best results for their IE tasks. They aren't "
						+ "going to put alot of effort into extending the vocabulary. "
						+ "They are looking for something that will work well enough for "
						+ "whatever they want to do later in the pipeline. So for NER, the "
						+ "IE users want to get maximum coverage and accuracy. I also do think "
						+ "that they want to see the degree of overlap of the ontologies in "
						+ "terms of coverage because they may in fact need to use multiple "
						+ "ontologies and they want to make these choices carefully.");
		sticky.setLayoutData(gd);
	}

	/**
	 * @param shell
	 */
	private static void testAddtoOntologyDialog(Shell shell) {
		AddToProposalOntologyDialog d = new AddToProposalOntologyDialog(shell,
				null);

		// Don't return from open() until window closes
		d.setBlockOnOpen(true);

		// Open the main window
		if (d.open() == IDialogConstants.OK_ID) {

		}

		// Dispose the display
		Display.getCurrent().dispose();
	}

}

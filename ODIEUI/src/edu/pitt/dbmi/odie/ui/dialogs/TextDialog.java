/**
 * @author Girish Chavan
 *
 * Sep 10, 2008
 */
package edu.pitt.dbmi.odie.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TextDialog extends Dialog {
	private Text text;

	private String message;

	private String title;

	/**
	 * @param parentShell
	 */
	public TextDialog(Shell parentShell, String title, String message) {
		super(parentShell);
		this
				.setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE
						| SWT.APPLICATION_MODAL);
		this.title = title;
		this.message = message;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText(title);

		Composite container = (Composite) super.createDialogArea(parent);

		GridLayout gl = new GridLayout(1, false);
		container.setLayout(gl);

		text = new Text(container, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		text.setEditable(false);

		GridData gd = new GridData();
		gd.heightHint = 300;
		gd.widthHint = 500;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = false;
		gd.grabExcessVerticalSpace = true;
		text.setLayoutData(gd);

		text.setText(message);

		return container;

	}
}

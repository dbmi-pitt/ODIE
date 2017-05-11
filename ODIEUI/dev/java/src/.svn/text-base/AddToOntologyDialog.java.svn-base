/**
 * @author Girish Chavan
 *
 * Sep 10, 2008
 */
package edu.pitt.dbmi.odie.ui.dialogs;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddToOntologyDialog extends Dialog {
	private String preferredName = "";
	private Text nameText;

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Add to proposal ontology");
	}

	/**
	 * @param parentShell
	 */
	public AddToOntologyDialog(Shell parentShell) {
		super(parentShell);	
		setBlockOnOpen(true);
		
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
	            IDialogConstants.OK_LABEL, true);
	        createButton(parent, IDialogConstants.CANCEL_ID,
	            IDialogConstants.CANCEL_LABEL, false);
		
	}


	@Override
	protected Control createDialogArea(Composite parent) {

		Composite container = (Composite) super.createDialogArea(parent);
        
		GridLayout gl = new GridLayout(2,false);
		container.setLayout(gl);
		
		Label nameLabel = new Label(container,SWT.NONE);
		nameLabel.setText("Preferred Name:");

		
		nameText = new Text(container, SWT.BORDER);
		nameText.setText(preferredName);
		
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		nameText.setLayoutData(gd);
		
		
		
		Label parentLabel = new Label(container, SWT.NONE);
		parentLabel.setText("Parent: (optional)");
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.LEFT;
		gd.grabExcessHorizontalSpace = true;
		parentLabel.setLayoutData(gd);
		
		TreeViewer viewer = new TreeViewer(container);
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		viewer.getTree().setLayoutData(gd);
		
		
		nameText.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				preferredName = nameText.getText();
				
			}
			
		});
		return container;
		
	}

	public String getPreferredName(){
		return preferredName;
	}
	/**
	 * @param selectionText
	 */
	public void setPreferredName(String selectionText) {
		preferredName  = selectionText;
		
	}
	
}

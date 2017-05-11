/**
 * @author Girish Chavan
 *
 * Oct 21, 2008
 */
package edu.pitt.dbmi.odie.ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import edu.pitt.dbmi.odie.ui.Aesthetics;

public class HelpTextPanel extends Composite {

	private StyledText helpTextWidget;
	private Button hideHelpButton;

	public HelpTextPanel(Composite parent, int style) {
		super(parent, style);
		doSelfLayout();
		addHelpTitle();
		addStyledText();
		addHideHelpButton();

	}

	private void doSelfLayout() {
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 4;
		gridLayout.marginWidth = 4;
		setLayout(gridLayout);
		this.setBackgroundMode(SWT.INHERIT_DEFAULT);
		this.setBackground(Aesthetics.getEnabledBackground());
	}

	private void addHideHelpButton() {
		hideHelpButton = new Button(this, SWT.NONE);
		hideHelpButton.setText("Hide Help");
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = false;
		gd.grabExcessVerticalSpace = false;
		gd.horizontalAlignment = SWT.BOTTOM;
		gd.verticalAlignment = SWT.LEFT;
		gd.verticalIndent = 10;
		hideHelpButton.setLayoutData(gd);
	}

	private void addStyledText() {
		helpTextWidget = new StyledText(this, SWT.WRAP);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		helpTextWidget.setLayoutData(gd);
		increaseFontSize();

	}

	private void increaseFontSize() {
		Font initialFont = helpTextWidget.getFont();
		FontData[] fontData = initialFont.getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setHeight(10);
		}
		Font newFont = new Font(Display.getCurrent(), fontData);
		helpTextWidget.setFont(newFont);
	}

	private void addHelpTitle() {
		Label helpLabel = new Label(this, SWT.NONE);
		helpLabel.setText(" Help");
		helpLabel.setFont(new Font(Display.getCurrent(), new FontData(
				"SanSerif", 9, SWT.BOLD)));
		helpLabel.setBackground(Aesthetics.getHelpTitleColor());
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.heightHint = 16;
		helpLabel.setLayoutData(gd);
	}

	public String getHelpText() {
		return helpTextWidget.getText();
	}

	public void setText(String helpText) {
		helpTextWidget.setText(helpText);
	}

	public String getText() {
		return helpTextWidget.getText();
	}

	public void addHideHelpButtonListener(SelectionListener listener) {
		hideHelpButton.addSelectionListener(listener);

	}

	public void removeHideHelpButtonListener(SelectionListener listener) {
		hideHelpButton.removeSelectionListener(listener);

	}

}

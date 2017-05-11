/**
 * @author Girish Chavan
 *
 * Oct 21, 2008
 */
package edu.pitt.dbmi.odie.ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import edu.pitt.dbmi.odie.ui.Aesthetics;

public class StickyNotePanel extends Composite {

	private StyledText textWidget;

	public StickyNotePanel(Composite parent, int style) {
		super(parent, style);
		doSelfLayout();
		addStyledText();

	}

	private void doSelfLayout() {

		FillLayout fl = new FillLayout();
		fl.marginHeight = Aesthetics.TOP_MARGIN * 2;
		fl.marginWidth = Aesthetics.LEFT_MARGIN * 2;
		setLayout(fl);

		this.setBackgroundMode(SWT.INHERIT_DEFAULT);
		this.setBackground(Aesthetics.getStickyNoteBackground());
	}

	private void addStyledText() {
		textWidget = new StyledText(this, SWT.WRAP);
		increaseFontSize();

	}

	private void increaseFontSize() {
		Font initialFont = textWidget.getFont();
		FontData[] fontData = initialFont.getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setHeight(10);
		}
		Font newFont = new Font(Display.getCurrent(), fontData);
		textWidget.setFont(newFont);
	}

	public void setText(String text) {
		textWidget.setText(text);

	}

	public String getText() {
		return textWidget.getText();
	}

}

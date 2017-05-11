package edu.pitt.dbmi.odie.ui;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.forms.widgets.FormFonts;

public class FormSection extends Section {

	FormColors colors = new FormColors(Display.getCurrent());
	BoldFontHolder boldFontHolder = new BoldFontHolder();

	public FormSection(Composite parent, int sectionStyle) {
		super(parent, sectionStyle);

		setBackground(Aesthetics.getWhiteColor());

		if (this.toggle != null) {
			this.toggle.setHoverDecorationColor(colors
					.getColor(IFormColors.TB_TOGGLE_HOVER));
			this.toggle.setDecorationColor(colors
					.getColor(IFormColors.TB_TOGGLE));
		}
		this.setFont(boldFontHolder.getBoldFont(parent.getFont()));
		if ((sectionStyle & Section.TITLE_BAR) != 0
				|| (sectionStyle & Section.SHORT_TITLE_BAR) != 0) {
			colors.initializeSectionToolBarColors();
			this.setTitleBarBackground(colors.getColor(IFormColors.TB_BG));
			this.setTitleBarBorderColor(colors.getColor(IFormColors.TB_BORDER));
			this.setTitleBarForeground(colors.getColor(IFormColors.TB_TOGGLE));

		}
	}

	private class BoldFontHolder {
		private Font normalFont;

		private Font boldFont;

		public BoldFontHolder() {
		}

		public Font getBoldFont(Font font) {
			createBoldFont(font);
			return boldFont;
		}

		private void createBoldFont(Font font) {
			if (normalFont == null || !normalFont.equals(font)) {
				normalFont = font;
				dispose();
			}
			if (boldFont == null) {
				boldFont = FormFonts.getInstance().getBoldFont(
						colors.getDisplay(), normalFont);
			}
		}

		public void dispose() {
			if (boldFont != null) {
//				FormFonts.getInstance().markFinished(boldFont);
				boldFont = null;
			}
		}
	}
}

package edu.pitt.dbmi.odie.ui.editors.annotations;

import org.eclipse.jface.text.source.AnnotationPainter.BoxStrategy;
import org.eclipse.jface.text.source.AnnotationPainter.HighlightingStrategy;
import org.eclipse.jface.text.source.AnnotationPainter.ITextStyleStrategy;
import org.eclipse.jface.text.source.AnnotationPainter.UnderlineStrategy;
import org.eclipse.swt.SWT;

public enum AvailableTextStylingStrategies {

	HIGHLIGHTING(new HighlightingStrategy()), UNDERLINESINGLE(
			new UnderlineStrategy(SWT.UNDERLINE_SINGLE)), UNDERLINEDOUBLE(
			new UnderlineStrategy(SWT.UNDERLINE_DOUBLE)), UNDERLINESQUIGGLE(
			new UnderlineStrategy(SWT.UNDERLINE_SQUIGGLE)), BOXDASH(
			new BoxStrategy(SWT.BORDER_DASH)), BOXDOT(new BoxStrategy(
			SWT.BORDER_DOT)), BOXSOLID(new BoxStrategy(SWT.BORDER_SOLID));

	private final ITextStyleStrategy strategy;

	/**
	 * Initializes the current instance.
	 * 
	 * @param strategy
	 */
	private AvailableTextStylingStrategies(ITextStyleStrategy strategy) {
		this.strategy = strategy;
	}

	public ITextStyleStrategy getStrategy() {
		return strategy;
	}
}

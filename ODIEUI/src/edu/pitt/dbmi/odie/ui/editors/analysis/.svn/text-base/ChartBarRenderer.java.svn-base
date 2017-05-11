/**
 * @author Girish Chavan
 *
 * Oct 14, 2008
 */
package edu.pitt.dbmi.odie.ui.editors.analysis;

import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.renderer.category.BarRenderer;

import edu.pitt.dbmi.odie.model.Datapoint;
import edu.pitt.dbmi.odie.ui.Aesthetics;

final class ChartBarRenderer extends BarRenderer {
	@Override
	public Paint getItemPaint(int row, int column) {
		Datapoint dp = ((Datapoint) this.getPlot().getDataset().getColumnKey(
				column));
		Color c = null;
		org.eclipse.swt.graphics.Color swtcolor = Aesthetics
				.getColorForObject(dp.getOntologyURIString());
		// Aesthetics.getColorForAnnotationType(dp.getOntology());
		c = new Color(swtcolor.getRed(), swtcolor.getGreen(), swtcolor
				.getBlue());
		return (c != null ? c : super.getItemPaint(row, column));
	}
}
/**
 * @author Girish Chavan
 *
 * Oct 14, 2008
 */
package edu.pitt.dbmi.odie.ui.editors.analysis;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnit;
import org.jfree.chart.axis.TickUnitSource;

final class ChartTickUnitSource implements TickUnitSource {
	NumberFormat fm = new DecimalFormat("###,###");

	@Override
	public TickUnit getCeilingTickUnit(TickUnit tu) {
		return new NumberTickUnit((int) tu.getSize(), fm);

	}

	@Override
	public TickUnit getCeilingTickUnit(double size) {
		return new NumberTickUnit((int) size, fm);
	}

	@Override
	public TickUnit getLargerTickUnit(TickUnit tu) {
		return new NumberTickUnit((int) tu.getSize() + 1, fm);
	}
}
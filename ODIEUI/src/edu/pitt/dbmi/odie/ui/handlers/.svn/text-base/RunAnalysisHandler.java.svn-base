package edu.pitt.dbmi.odie.ui.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class RunAnalysisHandler extends AbstractHandler implements
		IElementUpdater {

	Logger logger = Logger.getLogger(this.getClass());
	private UIElement uiElement;

	public RunAnalysisHandler() {
		super();

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Analysis currentAnalysis = GeneralUtils.getFirstSelectionInAnalysisView();
		if (currentAnalysis == null) {
			logger.error("No Analysis selected in AnalysesView.");
			return null;
		}
		try {
			GeneralUtils.runAnalysis(currentAnalysis);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void updateElement(UIElement element, Map parameters) {
		uiElement = element;

	}

	public void refresh() {
		// //enable/disable is handled using handler activation
		// if (currentAnalysis != null) {
		// // this.setEnabled(true);
		// updateIconAndTooltip();
		// } else{
		// // this.setEnabled(false);
		// }
	}

	private void updateIconAndTooltip() {
		// if (!GeneralUtils.isPartiallyProcessed(currentAnalysis)) {

		uiElement.setIcon(Aesthetics.getRunImage());
		uiElement.setTooltip("Repeat the analysis");
		// } else {
		// uiElement.setIcon(resumeImage);
		// uiElement.setTooltip("Resume the analysis");
		// }

	}

}

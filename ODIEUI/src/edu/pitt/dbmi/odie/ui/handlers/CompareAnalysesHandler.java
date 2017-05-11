package edu.pitt.dbmi.odie.ui.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class CompareAnalysesHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		List<Analysis> alist = GeneralUtils.getSelectionsInAnalysisView();
		return GeneralUtils.openCompareAnalysesEditor(alist, true);
	}

}

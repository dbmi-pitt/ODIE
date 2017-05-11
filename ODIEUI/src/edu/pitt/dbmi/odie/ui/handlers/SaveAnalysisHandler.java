package edu.pitt.dbmi.odie.ui.handlers;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IEditorPart;

import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class SaveAnalysisHandler extends AbstractHandler implements IHandler {

	Logger logger = Logger.getLogger(this.getClass());

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editor = GeneralUtils.getActiveEditor();
		editor.doSave(null);
		return null;
	}

}

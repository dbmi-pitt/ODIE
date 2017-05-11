package edu.pitt.dbmi.odie.ui.dnd;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.ui.part.PluginTransfer;
import org.eclipse.ui.part.PluginTransferData;

import edu.pitt.dbmi.odie.model.Suggestion;

/**
 * Supports dragging gadgets from a structured viewer.
 */
public class SuggestionDragListener extends DragSourceAdapter {
	private StructuredViewer viewer;
	Logger logger = Logger.getLogger(SuggestionDragListener.class);

	public SuggestionDragListener(StructuredViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * Method declared on DragSourceListener
	 */
	public void dragFinished(DragSourceEvent event) {
		logger.debug("drag finished");
		if (!event.doit)
			return;
		// if the gadget was moved, remove it from the source viewer
		if (event.detail == DND.DROP_MOVE) {
			logger.warn("Move not yet supported");
		}
	}

	/**
	 * Method declared on DragSourceListener
	 */
	public void dragSetData(DragSourceEvent event) {
		logger.debug("dragset data");
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		Suggestion[] Suggestions = (Suggestion[]) selection.toList().toArray(
				new Suggestion[selection.size()]);
		if (SuggestionTransfer.getInstance().isSupportedType(event.dataType)) {
			event.data = Suggestions;
		} else if (PluginTransfer.getInstance().isSupportedType(event.dataType)) {
			byte[] data = SuggestionTransfer.getInstance().toByteArray(
					Suggestions);
			event.data = new PluginTransferData(
					"org.eclipse.ui.examples.gdt.SuggestionDrop", data);
		}
	}

	/**
	 * Method declared on DragSourceListener
	 */
	public void dragStart(DragSourceEvent event) {
		logger.debug("drag start");
		event.doit = !viewer.getSelection().isEmpty();
	}
}
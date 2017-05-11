package edu.pitt.dbmi.odie.ui.dnd;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;

import edu.pitt.dbmi.odie.model.Suggestion;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

/**
 * Supports dropping gadgets into a tree viewer.
 */
public class ProposalTreeDropAdapter extends DropTargetAdapter {
	StructuredViewer viewer;

	Logger logger = Logger.getLogger(ProposalTreeDropAdapter.class);

	// JFace DropTargetAdapter methods
	// public ProposalTreeDropAdapter(TreeViewer viewer) {
	// super(viewer);
	// }
	// /**
	// * Method declared on ViewerDropAdapter
	// */
	// public boolean performDrop(Object data) {
	// Suggestion[] toDrop = (Suggestion[])data;
	// return addProposals(toDrop);
	// }
	// /**
	// * Method declared on ViewerDropAdapter
	// */
	// public boolean validateDrop(Object target, int op, TransferData type) {
	// boolean status = SuggestionTransfer.getInstance().isSupportedType(type);
	// return status;
	// }

	public ProposalTreeDropAdapter(StructuredViewer viewer) {
		super();
		this.viewer = viewer;
	}

	public void dragEnter(DropTargetEvent event) {
		logger.debug("drag enter");
		event.detail = DND.DROP_COPY;
	}

	public void drop(DropTargetEvent event) {
		logger.debug("drop");
		Suggestion[] toDrop = (Suggestion[]) event.data;
		if (toDrop.length > 0) {
			GeneralUtils.addProposals(toDrop);
			GeneralUtils.refreshProposalLexicalSet(toDrop[0].getAnalysis());
			viewer.refresh();
		}
	}
}
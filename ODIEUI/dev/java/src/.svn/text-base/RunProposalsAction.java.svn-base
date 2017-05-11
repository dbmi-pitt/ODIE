package edu.pitt.dbmi.odie.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.AnalysisJDBC;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.views.AnalysesView;
import edu.pitt.dbmi.odie.ui.workers.AnalysisProcessor;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

@Deprecated
public class RunProposalsAction extends Action implements ISelectionListener {

   private static ImageDescriptor runProposalsImage;
   
   static {
	   runProposalsImage = Activator.getImageDescriptor("images/proposalrun.png");

   }

   private Analysis currentAnalysis;
	  
	public RunProposalsAction() {
		super("Evaluate Proposals");
		this.setImageDescriptor(runProposalsImage);
		this.setToolTipText("Re-analyze with proposals only");
		IWorkbenchPage page = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow().getActivePage();
		
		page.addSelectionListener(AnalysesView.ID, this);
	}

	@Override
	public void run() {
		ProgressMonitorDialog pd = new ProgressMonitorDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
		try {
			pd.run(true, true, new AnalysisProcessor(currentAnalysis, true, true));
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		refresh();
		GeneralUtils.refreshViews();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		Object element = ((TreeSelection)selection).getFirstElement();
		if(element instanceof Analysis){
			currentAnalysis = (Analysis) element;
			
			refresh();
		}
		else
			this.setEnabled(false);

	}

	public void refresh() {
		if(currentAnalysis != null && isAnalysisLoaded() && currentAnalysis.isOE() && !currentAnalysis.isPartiallyProcessed()){
			this.setEnabled(true);
		}
		else
			this.setEnabled(false);
	}

	private boolean isAnalysisLoaded() {
		if(currentAnalysis instanceof AnalysisJDBC)
			return ((AnalysisJDBC)currentAnalysis).isLoaded();
		else 
			return true;
			
	}
}

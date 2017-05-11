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
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.views.AnalysesView;
import edu.pitt.dbmi.odie.ui.workers.AnalysisRemover;
import edu.pitt.dbmi.odie.utils.GeneralUtils;
@Deprecated 
public class RemoveAnalysisAction extends Action implements ISelectionListener {
   private static ImageDescriptor deleteImage;
   
   
   static {
	   deleteImage = Activator.getImageDescriptor("images/202-delete.png");
   }

   private Analysis currentAnalysis;
	  
	public RemoveAnalysisAction() {
		super("Remove Analysis");
		this.setImageDescriptor(deleteImage);
		this.setToolTipText("Remove Analysis");
		IWorkbenchPage page = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow().getActivePage();
		
		page.addSelectionListener(AnalysesView.ID, this);
	}

	@Override
	public void run() {
		ProgressMonitorDialog pd = new ProgressMonitorDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
		try {
			pd.run(true, true, new AnalysisRemover(currentAnalysis));
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		GeneralUtils.refreshViews();
	}

	/**
	 * 
	 */
	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		Object element = ((TreeSelection)selection).getFirstElement();
		if(element instanceof Analysis){
			currentAnalysis = (Analysis) element;
			this.setEnabled(true);
		}
		else
			this.setEnabled(false);
	}

}

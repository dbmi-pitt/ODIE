package edu.pitt.dbmi.odie.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIndexRepository;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;

import edu.pitt.dbmi.odie.ui.workers.AnalysisEngineProcessor2;

public class NewAnalysisWizard2 extends Wizard {

	private SelectAEDescriptorPage aePage;

	Logger logger = Logger.getLogger(this.getClass());
	public NewAnalysisWizard2() {
		super();
		setNeedsProgressMonitor(true);
	}
	@Override
	public boolean performFinish() {
		//the analysis is run in separate thread. it blocks until it is complete or cancelled.
        try {
        	AnalysisEngineProcessor2 aep = new AnalysisEngineProcessor2(aePage.getAnalysisEngine());

        	getContainer().run(true, true, aep);
        	
        	CAS cas = aep.getCas();
        	FSIndexRepository ir = cas.getIndexRepository();
            Iterator<String> it = ir.getLabels();
            while (it.hasNext()) {
              String label = it.next();
              FSIndex index1 = ir.getIndex(label);
              logger.info(label+"(" + index1.size() + ")");
              createTypeTree(index1.getType(), cas.getTypeSystem(), label, ir);
              
            }
            
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
        return true;
	}
	
	 private void createTypeTree(Type type, TypeSystem ts, String label,
		      FSIndexRepository ir) {
		 	
		    int size = ir.getIndex(label, type).size();
		    logger.info(type.getName()+"(" + size + ")");
		    
		    List<Type> types = ts.getDirectSubtypes(type);
		    
		    final int max = types.size();
		    for (int i = 0; i < max; i++) {
		      if (ir.getIndex(label, types.get(i)) == null) {
		        continue;
		      }
		      createTypeTree(types.get(i), ts, label, ir);
		    }
	 }

	@Override
	public void addPages() {
		setWindowTitle("New Analysis2");

		aePage = new SelectAEDescriptorPage();
		addPage(aePage);
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		pageContainer.getShell().setSize(465,600);
	}
	
	
}

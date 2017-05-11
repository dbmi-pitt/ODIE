package edu.pitt.dbmi.odie.ui.workers;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.internal.util.Timer;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.ProcessTrace;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import edu.pitt.dbmi.odie.model.Analysis;

public class AnalysisEngineProcessor2 implements IRunnableWithProgress {
	AnalysisEngine ae;

	Analysis analysis;
	private CAS cas = null;

	private String language;

	Logger logger = Logger.getLogger(this.getClass());

	public CAS getCas() {
		return cas;
	}

	public AnalysisEngineProcessor2(AnalysisEngine analysis) {
//		this.analysis = analysis;
//		this.ae = analysis.getAnalysisEngine();;
	}

	private final void initCas() {
		this.cas.setDocumentLanguage(this.language);
		this.cas
				.setDocumentText("\nDr. Nutritious\n\nMedical Nutrition Therapy "
						+ "for Hyperlipidemia\n\nReferral from: Julie Tester, RD, LD, CNSD\nPhone "
						+ "contact: (555) 555-1212\nHeight: 144 cm   Current Weight: 45 kg   Date of "
						+ "current weight: 02-29-2001   Admit Weight:  53 kg   BMI: 18 kg/m2\nDiet: "
						+ "General\nDaily Calorie needs (kcals): 1500 calories, assessed as HB + 20% "
						+ "for activity.\nDaily Protein needs: 40 grams,  assessed as 1.0 g/kg.\nPt "
						+ "has been on a 3-day calorie count and has had an average intake of 1100"
						+ " calories.  She was instructed to drink 2-3 cans of liquid supplement to "
						+ "help promote weight gain.  She agrees with the plan and has my number for "
						+ "further assessment. May want a Resting Metabolic Rate as well. She takes an "
						+ "aspirin a day for knee pain.\n");
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		monitor.beginTask("Running " + ae.getAnalysisEngineMetaData().getName(), 3);

		
		if (monitor.isCanceled())
			return;

		monitor.worked(1);
		setupCAS();
		monitor.worked(1);
		runAE(true);
		monitor.worked(1);
		monitor.done();
		
	}

	protected boolean setupCAS() {
		try {
			this.cas = this.ae.newCAS();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
			return false;
		}
		initCas();
		return true;
	}

	public void runAE(boolean doCasReset) {
	    Timer timer = new Timer();
	    timer.start();
	    if (this.ae == null) {
	      return;
	    }
	    internalRunAE(doCasReset);
	    timer.stop();
	    
	  }

	protected void internalRunAE(boolean doCasReset) {
	    try {
	      if (doCasReset) {
	        // Change to Initial view
	        this.cas = this.cas.getView(CAS.NAME_DEFAULT_SOFA);
	        this.cas.reset();
	        initCas();
	      }
	      ProcessTrace lastRunProcessTrace = this.ae.process(this.cas);
	    } catch (Exception e) {
	      e.printStackTrace();
	    } catch (Error e) {
	      StringBuffer buf = new StringBuffer();
	      buf.append("A severe error has occured:\n");
	      e.printStackTrace();
	      throw e;
	    }
	  }

	
}

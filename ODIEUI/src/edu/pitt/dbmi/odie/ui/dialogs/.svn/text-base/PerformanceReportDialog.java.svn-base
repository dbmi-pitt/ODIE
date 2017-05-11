/**
 * @author Girish Chavan
 *
 * Sep 10, 2008
 */
package edu.pitt.dbmi.odie.ui.dialogs;

import java.util.List;

import org.apache.uima.util.AnalysisEnginePerformanceReports;
import org.apache.uima.util.ProcessTrace;
import org.apache.uima.util.ProcessTraceEvent;
import org.apache.uima.util.impl.ProcessTrace_impl;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.pitt.dbmi.odie.model.Analysis;

public class PerformanceReportDialog extends Dialog {
	private Text reportText;
	private Analysis analysis;

	/**
	 * @param parentShell
	 */
	public PerformanceReportDialog(Shell parentShell, Analysis analysis) {
		super(parentShell);
		this.analysis = analysis;
		setBlockOnOpen(false);

	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Performance Report");	
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite container = (Composite) super.createDialogArea(parent);

		GridLayout gl = new GridLayout(1, false);
		container.setLayout(gl);

		Label nameLabel = new Label(container, SWT.NONE);
		nameLabel.setText("Latest Performance Report");

		reportText = new Text(container, SWT.BORDER | SWT.WRAP);
		initReportText();

		GridData gd = new GridData();
		gd.heightHint = 150;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		reportText.setLayoutData(gd);

		return container;

	}

	private void initReportText() {
		List<ProcessTrace> reportList = analysis.getPerformanceReports();
		if (reportList.size() > 0) {
			StringBuffer text = new StringBuffer();
			ProcessTrace_impl pt = (ProcessTrace_impl) reportList
					.get(reportList.size() - 1);
			// for(Object o:pt.getEventsByType(ProcessTraceEvent.ANALYSIS,
			// true)){
			// ProcessTraceEvent pte = (ProcessTraceEvent)o;
			// text.append(pte.getComponentName() + ":" + pte.getDuration() +
			// "\n");
			// }
			text.append((new AnalysisEnginePerformanceReports(pt))
					.getFullReport());
			reportText.setText(text.toString());
		}
	}

}

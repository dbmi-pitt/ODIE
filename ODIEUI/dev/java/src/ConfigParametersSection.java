package edu.pitt.dbmi.odie.ui.editors;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import edu.pitt.dbmi.odie.utils.FormUtils;

public class ConfigParametersSection extends SectionPart {

	public ConfigParametersSection(Composite parent, FormToolkit toolkit,
			int style) {
		super(parent, toolkit, style);
	}


	public void initialize(final IManagedForm form) {
		super.initialize(form);
		
		FormToolkit toolkit = form.getToolkit();
		Section section = getSection();
		GridData gd = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(gd);
		section.addExpansionListener(new ExpansionAdapter() {
		
		public void expansionStateChanged(ExpansionEvent e) {
			form.reflow(true);
		  }
		});
		section.setText("Configuration Parameters");
		section.setDescription("All configuration parameters for the selected Analysis Engine.");
		
		
		Composite sectionClient = FormUtils.newNColumnComposite(toolkit, section, 2, true, false);
		FormUtils.setMargins(sectionClient, 5, 0);
	}
	
}

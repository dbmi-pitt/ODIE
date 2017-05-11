package edu.pitt.dbmi.odie.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import edu.pitt.dbmi.odie.ui.editors.DualPanelForm;

public class FormUtils {
	public static DualPanelForm setup2ColumnLayout(FormToolkit toolkit,
			IManagedForm managedForm, int w1, int w2) {
		Composite leftPanel;
		Composite rightPanel;
		SashForm sashForm;

		final ScrolledForm sform = managedForm.getForm();
		final Composite form = sform.getBody();
		form.setLayout(new GridLayout(1, false)); // this is required !
		Composite xtra = toolkit.createComposite(form);
		xtra.setLayout(new GridLayout(1, false));
		xtra.setLayoutData(new GridData(GridData.FILL_BOTH));
		Control c = xtra.getParent();
		while (!(c instanceof ScrolledComposite))
			c = c.getParent();
		((GridData) xtra.getLayoutData()).widthHint = c.getSize().x;
		((GridData) xtra.getLayoutData()).heightHint = c.getSize().y;
		sashForm = new SashForm(xtra, SWT.HORIZONTAL);

		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH)); // needed

		leftPanel = newComposite(toolkit, sashForm);
		((GridLayout) leftPanel.getLayout()).marginHeight = 5;
		((GridLayout) leftPanel.getLayout()).marginWidth = 5;
		rightPanel = newComposite(toolkit, sashForm);
		((GridLayout) rightPanel.getLayout()).marginHeight = 5;
		((GridLayout) rightPanel.getLayout()).marginWidth = 5;
		sashForm.setWeights(new int[] { w1, w2 });
		return new DualPanelForm(form, leftPanel, rightPanel);
	}

	public static DualPanelForm setupDividedLayout(FormToolkit toolkit,
			IManagedForm managedForm, int w1, int w2, int type) {
		Composite leftPanel;
		Composite rightPanel;
		SashForm sashForm;

		final ScrolledForm sform = managedForm.getForm();
		final Composite form = sform.getBody();
		form.setLayout(new GridLayout(1, false)); // this is required !
		Composite xtra = toolkit.createComposite(form);
		xtra.setLayout(new GridLayout(1, false));
		xtra.setLayoutData(new GridData(GridData.FILL_BOTH));
		Control c = xtra.getParent();
		while (!(c instanceof ScrolledComposite))
			c = c.getParent();
		((GridData) xtra.getLayoutData()).widthHint = c.getSize().x;
		((GridData) xtra.getLayoutData()).heightHint = c.getSize().y;
		sashForm = new SashForm(xtra, type);

		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH)); // needed

		leftPanel = newComposite(toolkit, sashForm);
		((GridLayout) leftPanel.getLayout()).marginHeight = 5;
		((GridLayout) leftPanel.getLayout()).marginWidth = 5;
		rightPanel = newComposite(toolkit, sashForm);
		((GridLayout) rightPanel.getLayout()).marginHeight = 5;
		((GridLayout) rightPanel.getLayout()).marginWidth = 5;
		sashForm.setWeights(new int[] { w1, w2 });
		return new DualPanelForm(form, leftPanel, rightPanel);
	}
	
	public static Composite newComposite(FormToolkit toolkit, Composite parent) {
		return newNColumnComposite(toolkit, parent, 1, true, true);
	}

	public static Composite newNColumnComposite(FormToolkit toolkit,
			Composite parent, int cols, boolean fillHorizontal,
			boolean fillVertical) {
		Composite composite = toolkit.createComposite(parent, SWT.NONE);

		if (parent instanceof ExpandableComposite)
			((ExpandableComposite) parent).setClient(composite);

		GridLayout layout = new GridLayout();
		layout.numColumns = cols;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);

		GridData gd = new GridData();
		if (fillHorizontal) {
			gd.grabExcessHorizontalSpace = true;
			gd.horizontalAlignment = GridData.FILL;
		}

		if (fillVertical) {
			gd.grabExcessVerticalSpace = true;
			gd.verticalAlignment = GridData.FILL;
		}
		composite.setLayoutData(gd);
		return composite;
	}

	public static void setMargins(Composite composite, int marginTop,
			int marginLeft) {
		Layout l = composite.getLayout();
		if (l instanceof GridLayout) {
			((GridLayout) l).marginTop = marginTop;
			((GridLayout) l).marginLeft = marginLeft;
		}

	}

	public static Text newLabeledTextField(FormToolkit toolkit,
			Composite parent, String labelKey, String textToolTip, int style) {
		createLabel(toolkit, parent, labelKey, textToolTip, style);
		return newTextWithTip(toolkit, parent, "", style, textToolTip); //$NON-NLS-1$
	}

	public static Label newLabeledLabel(FormToolkit toolkit, Composite parent,
			String labelKey, String textToolTip, int style) {
		createLabel(toolkit, parent, labelKey, textToolTip, style);
		return newLabelWithTip(toolkit, parent, "", style, textToolTip); //$NON-NLS-1$
	}

	private static Text newTextWithTip(FormToolkit toolkit, Composite parent,
			String text, int style, String tip) {
		Text t = toolkit.createText(parent, text, style);
		t.setToolTipText(tip);
		if ((style & SWT.V_SCROLL) == SWT.V_SCROLL) {
			t.setLayoutData(new GridData(GridData.FILL_BOTH));
		} else {
			t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}
		return t;
	}

	private static Label newLabelWithTip(FormToolkit toolkit, Composite parent,
			String text, int style, String tip) {
		Label label = toolkit.createLabel(parent, text, style);
		label.setToolTipText(tip);
		if ((style & SWT.V_SCROLL) == SWT.V_SCROLL) {
			label.setLayoutData(new GridData(GridData.FILL_BOTH));
		} else {
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}
		return label;
	}

	private static void createLabel(FormToolkit toolkit, Composite parent,
			String labelKey, String textToolTip, int style) {
		Label label = toolkit.createLabel(parent, labelKey);
		label.setToolTipText(textToolTip);
		label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
	}

	public static void setupFormLayout(IManagedForm managedForm) {
		final Composite form = getContainer(managedForm);
		GridLayout gl = new GridLayout(1, false);
		gl.marginWidth = 5;
		gl.marginHeight = 5;

		form.setLayout(gl); // this is required !
	}

	public static Composite getContainer(IManagedForm managedForm) {
		final ScrolledForm sform = managedForm.getForm();
		final Composite form = sform.getBody();
		return form;
	}

	public static DualPanelForm addSashForm(Composite form, FormToolkit toolkit) {
		SashForm sashForm = new SashForm(form, SWT.HORIZONTAL);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH)); // needed

		Composite leftPanel = FormUtils.newComposite(toolkit, sashForm);
		((GridLayout) leftPanel.getLayout()).marginHeight = 5;
		((GridLayout) leftPanel.getLayout()).marginWidth = 5;
		Composite rightPanel = FormUtils.newComposite(toolkit, sashForm);
		((GridLayout) rightPanel.getLayout()).marginHeight = 5;
		((GridLayout) rightPanel.getLayout()).marginWidth = 5;
		sashForm.setWeights(new int[] { 50, 50 });
		return new DualPanelForm(form, leftPanel, rightPanel);
	}
}

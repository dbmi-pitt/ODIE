/**
 * 
 */
package edu.pitt.dbmi.odie.ui.wizards.pages;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Timer;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.LanguageResource;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.ui.wizards.providers.BioportalContentProvider;
import edu.pitt.dbmi.odie.ui.wizards.providers.BioportalLabelProvider;
import edu.pitt.ontology.IOntology;

/**
 * @author Girish Chavan
 * 
 */
public class SelectBioportalOntologyPage extends WizardPage {
	Logger logger = Logger.getLogger(this.getClass());
	public static final String PAGE_NAME = "SelectBioportalOntologyPage";
	private StyledText detailsTextBox;
	private CheckboxTableViewer ontologyTable;

	private static final String SIZE_HEADER = "Size: ";
	private static final String NAME_HEADER = "Name: ";
	private static final String DESC_HEADER = "Description: ";
	private static final String LANG_HEADER = "Language: ";
	private static final String VER_HEADER = "Version: ";
	private static final String INSTALLED_VER_HEADER = "Installed Version: ";
	private static final String RELEASE_HEADER = "Release Date: ";
	private static final String FORMAT_HEADER = "Format: ";
	private static final String URL_HEADER = "URL: ";

	/**
	 * @param pageName
	 */
	public SelectBioportalOntologyPage() {
		super(PAGE_NAME);
		setTitle("Select ontology");
		setDescription("Select an ontology from the list");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout gl = new GridLayout();
		gl.verticalSpacing = Aesthetics.INTERGROUP_SPACING;
		gl.numColumns = 1;
		container.setLayout(gl);

		// Ontology List Group
		Group olistGrp = new Group(container, SWT.NONE);
		olistGrp.setText("Ontologies");
		gl = new GridLayout();
		gl.verticalSpacing = Aesthetics.INTRAGROUP_SPACING;
		olistGrp.setLayout(gl);

		GridData gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		gd.minimumHeight = 100;
		olistGrp.setLayoutData(gd);

		// ontology list

		ScrolledComposite scroll = new ScrolledComposite(olistGrp, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER);
		ontologyTable = CheckboxTableViewer.newCheckList(scroll, SWT.NULL);
		ontologyTable.setContentProvider(new BioportalContentProvider());
		ontologyTable.setLabelProvider(new BioportalLabelProvider());
		gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		scroll.setLayoutData(gd);
		scroll.setContent(ontologyTable.getControl());
		scroll.setMinSize(100, 100);
		scroll.setExpandHorizontal(true);
		scroll.setExpandVertical(true);

		// /////// Details group
		Label detailsGrp = new Label(olistGrp, SWT.NONE);
		detailsGrp.setText("Additional Info:");

		detailsTextBox = new StyledText(olistGrp, SWT.WRAP | SWT.READ_ONLY
				| SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		detailsTextBox.setText("Select an ontology above to see its details");

		gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		gd.minimumHeight = 100;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		detailsTextBox.setLayoutData(gd);

		hookListeners();
		setControl(container);

		setPageComplete(validatePage());

	}

	/**
	 * 
	 */
	private void hookListeners() {

		ontologyTable.getTable().addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (e.item.getData() instanceof IOntology) {
					updateDetailsBox((IOntology) e.item.getData());
				}

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.detail == SWT.CHECK)
					return;
				if (e.item.getData() instanceof IOntology) {
					IOntology o = (IOntology) e.item.getData();
					updateDetailsBox(o);
				}
			}

			private void updateDetailsBox(IOntology ontology) {
				Map<String, String> map = getOntologyMetadata(ontology);
				if (map == null) {
					detailsTextBox
							.setText("Error getting description for this ontology.");
					return;
				}
				StringBuffer text = new StringBuffer();

				//				

				// example release date format: 2008-07-22-07:00

				// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				//				
				// String elapsed = "";
				// String date = map.get(RELEASE_HEADER);
				// try {
				// logger.debug(date.substring(0,10));
				// Date d = df.parse(date.substring(0,10));
				// elapsed = " (";
				// elapsed += GeneralUtils.getPrettyDateTimeDifference(d, new
				// Date());
				// elapsed += " ago)";
				//					
				// } catch (ParseException e) {
				// // e.printStackTrace();
				// System.err.println("Could not parse release date");
				// }
				//				
				//				
				// DecimalFormat def = new DecimalFormat("###,###,###");
				// String size =
				// def.format(Integer.parseInt(map.get(SIZE_HEADER))) +
				// " bytes";

				text.append(NAME_HEADER + "\n" + map.get(NAME_HEADER));
				text.append("\n\n");
				text.append(VER_HEADER + map.get(VER_HEADER));
				
				if(map.get(INSTALLED_VER_HEADER)!=null){
					text.append("\t" + INSTALLED_VER_HEADER + map.get(INSTALLED_VER_HEADER));
				}
				text.append("\n\n");
				text.append(DESC_HEADER + "\n" + map.get(DESC_HEADER));
				text.append("\n\n");

				// text.append(FORMAT_HEADER + map.get(FORMAT_HEADER));
				// text.append("\n");
				// text.append(LANG_HEADER + map.get(LANG_HEADER));
				// text.append("\n");
				// does not accurately display size, hence not displayed for now
				// text.append(SIZE_HEADER + size);
				// text.append("\n");
				// text.append(RELEASE_HEADER + map.get(RELEASE_HEADER) +
				// elapsed);
				// text.append("\n");
				// text.append(URL_HEADER + map.get(URL_HEADER));

				detailsTextBox.setText(text.toString());

				applyStyles();
			}

			private void applyStyles() {
				String s = detailsTextBox.getText();

				int start = s.indexOf(NAME_HEADER);
				StyleRange sr;
				if (start >= 0) {
					sr = new StyleRange();
					sr.start = start;
					sr.length = NAME_HEADER.length();
					sr.fontStyle = SWT.BOLD;
					detailsTextBox.setStyleRange(sr);
				}

				start = s.indexOf(DESC_HEADER);
				if (start >= 0) {
					sr = new StyleRange();
					sr.start = start;
					sr.length = DESC_HEADER.length();
					sr.fontStyle = SWT.BOLD;
					detailsTextBox.setStyleRange(sr);
				}

				start = s.indexOf(LANG_HEADER);
				if (start >= 0) {
					sr = new StyleRange();
					sr.start = start;
					sr.length = LANG_HEADER.length();
					sr.fontStyle = SWT.BOLD;
					detailsTextBox.setStyleRange(sr);
				}

				start = s.indexOf(VER_HEADER);
				if (start >= 0) {
					sr = new StyleRange();
					sr.start = start;
					sr.length = VER_HEADER.length();
					sr.fontStyle = SWT.BOLD;
					detailsTextBox.setStyleRange(sr);
				}

				start = s.indexOf(INSTALLED_VER_HEADER);
				if (start >= 0) {
					sr = new StyleRange();
					sr.start = start;
					sr.length = INSTALLED_VER_HEADER.length();
					sr.fontStyle = SWT.BOLD;
					detailsTextBox.setStyleRange(sr);
				}
				
				start = s.indexOf(RELEASE_HEADER);
				if (start >= 0) {
					sr = new StyleRange();
					sr.start = start;
					sr.length = RELEASE_HEADER.length();
					sr.fontStyle = SWT.BOLD;
					detailsTextBox.setStyleRange(sr);
				}

				start = s.indexOf(FORMAT_HEADER);
				if (start >= 0) {
					sr = new StyleRange();
					sr.start = start;
					sr.length = FORMAT_HEADER.length();
					sr.fontStyle = SWT.BOLD;
					detailsTextBox.setStyleRange(sr);
				}

				start = s.indexOf(URL_HEADER);
				if (start >= 0) {
					sr = new StyleRange();
					sr.start = start;
					sr.length = URL_HEADER.length();
					sr.fontStyle = SWT.BOLD;
					detailsTextBox.setStyleRange(sr);
				}

				start = s.indexOf(SIZE_HEADER);
				if (start >= 0) {
					sr = new StyleRange();
					sr.start = start;
					sr.length = SIZE_HEADER.length();
					sr.fontStyle = SWT.BOLD;
					detailsTextBox.setStyleRange(sr);
				}
			}
		});

		ontologyTable.addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				setPageComplete(validatePage());
			}

		});

	}

	public IOntology[] getSelectedOntologies() {
		Object[] sel = ontologyTable.getCheckedElements();
		return Arrays.copyOf(sel, sel.length, IOntology[].class);
	}

	/**
	 * @return
	 */
	protected boolean validatePage() {
		if (ontologyTable.getCheckedElements().length <= 0) {
			setMessage("Select at least one ontology to import.");
			return false;
		} else {
			setMessage(ontologyTable.getCheckedElements().length
					+ " ontologies selected");
			return true;
		}

	}

	/**
	 * get content from simple tag
	 * 
	 * @param doc
	 * @param tag
	 * @return
	 */
	private String getContent(Document doc, String tag) {
		NodeList list = doc.getElementsByTagName(tag);
		if (list.getLength() > 0) {
			return list.item(0).getTextContent();
		}
		return "";
	}

	/**
	 * get selected ontology
	 * 
	 * @return null if none selected
	 */
	public Map<String, String> getOntologyMetadata(IOntology ont) {
		if (ont != null) {
			try {
				// setup meta map
				Map<String, String> map = new HashMap<String, String>();

				map.put(NAME_HEADER, ont.getName());
				map.put(DESC_HEADER, ont.getDescription());
				map.put(VER_HEADER, ont.getVersion());
				MiddleTier mt = Activator.getDefault().getMiddleTier();
				LanguageResource lr = mt.getLanguageResource(ont.getName());
				
				if(lr!=null){
					String ver = "Unknown";
					
					if(lr.getVersion()!=null && lr.getVersion().trim().length()>0)
						ver = lr.getVersion();
					
					map.put(INSTALLED_VER_HEADER,ver);
				}
				
				return map;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 
	 */
	public void prepareForDisplay() {
		try {
			this.getWizard().getContainer().run(true, true,
					new IRunnableWithProgress() {
						@Override
						public void run(final IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							monitor.beginTask("Accessing Bioportal...",
									IProgressMonitor.UNKNOWN);

							final Object semaphore = new Object();

							final Timer t = new Timer(100, null);
							t.addActionListener(new TimerListener(monitor, t,
									semaphore));
							t.start();

							Thread r = new Thread() {

								@Override
								public void run() {
									edu.pitt.ontology.bioportal.BioPortalRepository rep = Activator
											.getDefault().getMiddleTier()
											.getBioportalRepository();
									rep.getOntologies();
									synchronized (semaphore) {
										semaphore.notify();
									}

								}
							};
							r.start();

							synchronized (semaphore) {
								semaphore.wait();
							}
							t.stop();
							monitor.done();
						}

					});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ontologyTable.setInput("root");
	}

	class TimerListener implements ActionListener {

		IProgressMonitor monitor;
		Timer timer;
		Object semaphore;
		int restarts = 300;

		public TimerListener(IProgressMonitor monitor, Timer timer,
				Object semaphore) {
			super();
			this.monitor = monitor;
			this.timer = timer;
			this.semaphore = semaphore;
		}

		public void actionPerformed(ActionEvent ee) {
			timer.stop();
			if (monitor.isCanceled()) {
				synchronized (semaphore) {
					semaphore.notify();
				}
			} else {
				restarts--;
				if (restarts < 0) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(
							new Runnable() {
								@Override
								public void run() {
									if (MessageDialog
											.openQuestion(
													Display.getCurrent()
															.getActiveShell(),
													"Bioportal Web Services not responding",
													"Please check your Internet connection. \nDo you want to continue to wait?")) {
										restarts = 300;
										timer.restart();
									} else {
										synchronized (semaphore) {
											semaphore.notify();
										}
									}
								}
							});
				} else
					timer.restart();
			}
		}
	};

	/**
	 * @return
	 */
	public List<URI> getSelectedOntologyURIs() {
		Object[] sel = ontologyTable.getCheckedElements();

		List<URI> uriList = new ArrayList<URI>();

		for (int i = 0; i < sel.length; i++) {
			try {
				Map<String, String> map = getOntologyMetadata((IOntology) sel[i]);
				uriList.add(new URI(map.get(URL_HEADER)));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return uriList;
	}

}

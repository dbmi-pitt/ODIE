package edu.pitt.dbmi.odie.ui.editors.browser;


import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import edu.pitt.dbmi.odie.ui.Aesthetics;

public class BrowserEditor  extends EditorPart  {

	public static final String ID = "edu.pitt.dbmi.odie.ui.editors.browser.BrowserEditor";
	private URL currentUrl;
	private ToolItem backButton;
	private ToolItem forwardButton;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite,
	 * org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);

		setPartName("Browser");
		this.currentUrl = ((BrowserEditorInput)input).getURL();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */

	public void createPartControl(Composite parent) {
		createParentLayout(parent);
		addControlPanel(parent);
		addBrowserPanel(parent);
		
		loadURL();
	}

	
	private void addControlPanel(Composite parent) {
		Composite buttons = new Composite(parent,SWT.NONE);
		GridLayout gl = new GridLayout(2,false);
		gl.marginWidth = 2;
		gl.marginHeight = 2;
		
		buttons.setLayout(gl);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = gd.FILL;
		buttons.setLayoutData(gd);
		
		ToolBar toolbar = new ToolBar(buttons, SWT.FLAT);
		
		backButton = new ToolItem(toolbar,SWT.PUSH);
		backButton.setToolTipText("Go back one page");
		backButton.setImage(Aesthetics.getArrowLeftIcon());
		backButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				browser.back();
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		forwardButton = new ToolItem(toolbar,SWT.PUSH);
		forwardButton.setToolTipText("Go forward one page");
		forwardButton.setImage(Aesthetics.getArrowRightIcon());
		forwardButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				browser.forward();
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		ToolItem refreshButton = new ToolItem(toolbar,SWT.PUSH);
		refreshButton.setToolTipText("reload current page");
		refreshButton.setImage(Aesthetics.getRefreshIcon());
		refreshButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				browser.refresh();
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		locationBar = new Text(buttons,SWT.BORDER); 
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		locationBar.setLayoutData(gd);
		
		locationBar.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.keyCode == SWT.CR){
					try {
						String text = locationBar.getText();
						if(!text.startsWith("http://"))
							text = "http://" + text;
						
						URL url = new URL(text);
						currentUrl = url;
						loadURL();
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
						return;
					}
				}
			}

			
		});
	}


	private void loadURL() {
		if(currentUrl!=null){
			locationBar.setText(currentUrl.toString());
			browser.setUrl(currentUrl.toString());
			setPartName(currentUrl.toString());
		}
		
	}
	
	/**
	 * @param parent
	 */
	private void createParentLayout(Composite parent) {
		GridLayout gl = new GridLayout(1, false);
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.verticalSpacing = 0;
		parent.setLayout(gl);
	}


	private Browser browser;
	private Text locationBar;

	public void addBrowserPanel(Composite parent) {

		browser = new Browser(parent,SWT.NONE);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		browser.setLayoutData(gd);
		
		LocationListener locationListener = new LocationListener() {
		      public void changed(LocationEvent event) {
		            Browser browser = (Browser)event.widget;
		            backButton.setEnabled(browser.isBackEnabled());
		            forwardButton.setEnabled(browser.isForwardEnabled());
		         }
		      public void changing(LocationEvent event) {
		         }
		      };
		browser.addLocationListener(locationListener);
		   
		browser.addProgressListener(new ProgressListener() {
			
			@Override
			public void completed(ProgressEvent event) {
			
				
			}
			
			@Override
			public void changed(ProgressEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		
		browser.addTitleListener(new TitleListener() {
			
			@Override
			public void changed(TitleEvent event) {
				setPartName(event.title);
				
			}
		});

	}



	public void setFocus() {
		browser.setFocus();
	}

	/*
	 * ChartEditor is read-only. This method does not do anything.
	 * 
	 * @seeorg.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
	}

	/*
	 * ChartEditor is read-only. This method does not do anything.
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	public void doSaveAs() {
	}

	/*
	 * ChartEditor is read-only. Always returns false.
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	public boolean isDirty() {
		return false;
	}

	/*
	 * ChartEditor is read-only. Always returns false.
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}
}

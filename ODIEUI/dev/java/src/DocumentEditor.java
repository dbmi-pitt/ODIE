package edu.pitt.dbmi.odie.ui.editors;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.AnalysisEngineMetadata;
import edu.pitt.dbmi.odie.model.Annotation;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.ui.dialogs.AddToOntologyDialog;
import edu.pitt.dbmi.odie.ui.views.AnnotationsView;
import edu.pitt.dbmi.odie.ui.views.OrderedStructuredSelection;
import edu.pitt.ontology.IClass;

public class DocumentEditor extends EditorPart implements ISelectionListener {

	public static final String ID = "edu.pitt.dbmi.odie.ui.editors.DocumentEditor";
	private StyledText text;
	private DocumentStyler styler;
	private AnalysisDocument da;
	private DocumentEditor self =this;

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName());
		
		if(extractDocumentFromInput())
			initStyler();
		
	}

	private boolean extractDocumentFromInput() {
		if(this.getEditorInput() instanceof DocumentEditorInput){
			da = ((DocumentEditorInput)this.getEditorInput()).getAnalysisDocument();
			if(da!=null)
				return true;
		}

		return false;
	}

	private void initStyler() {
		if(da.getAnalysis().getType().equals(AnalysisEngineMetadata.TYPE_OE))
			styler = new OEDocumentStyler(da);
		else
			styler = new DocumentStyler(da);
	}

	@Override
	public void createPartControl(Composite parent) {
		
		createParentLayout(parent);
		applyParentAesthetics(parent);
		createTextWidget(parent);
				
		getSite().getPage().addSelectionListener(this);
		hookListeners();
	}

	/**
	 * @param parent
	 */
	private void applyParentAesthetics(Composite parent) {
		parent.setBackground(Aesthetics.getEnabledBackground());
		
	}

	/**
	 * @param parent
	 */
	private void createParentLayout(Composite parent) {
		GridLayout gl = new GridLayout();
		gl.marginWidth = Aesthetics.LEFT_MARGIN;
		gl.marginHeight = Aesthetics.TOP_MARGIN;
		parent.setLayout(gl);
		
	}

	/**
	 * @param parent
	 */
	private void createTextWidget(Composite parent) {
		text = new StyledText (parent, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP );
		
		GridData spec = new GridData(SWT.FILL, SWT.FILL, true, true);
		text.setLayoutData(spec);
		text.setEditable(false);
		Color bg = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
		text.setBackground(bg);
		text.setFont(new Font(Display.getCurrent(),new FontData("Arial", 10, SWT.NORMAL)));

		if(da!=null){
			loadText();
			text.addLineStyleListener(styler);
		}

		
	}
	
	private void loadText() {
		String textStr = da.getDocument().getText();
		
		if(textStr==null)
			text.setText("The file " + da.getDocument().getURI() + " was not found.");
		else
			text.setText(textStr);
		
	}

	private void hookListeners() {
		text.addMouseListener(new MouseListener(){

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseUp(MouseEvent e) {
				if(e.button == 1){
					//TODO add code to select the annotation in the DocumentEditor and the AnnotationsView.
//					int offset = text.getOffsetAtLocation(new Point(e.x,e.y));
//					List<Annotation> annList =  styler.getAnnotationsAtOffset(offset);
				}
			}
			
		});


		text.addMouseMoveListener(new MouseMoveListener(){

			
			@Override
			public void mouseMove(MouseEvent e) {
				int offset = text.getOffsetAtLocation(new Point(e.x,e.y));
				List<Annotation> annList =  styler.getAnnotationsAtOffset(offset);
				if(annList.isEmpty() || Activator.getDefault().getMiddleTier().isSystemAnnotation(annList.get(0)))
					text.setToolTipText(null);
				else{
					IClass annotationClass =  annList.get(0).getAnnotationClass();
					String tooltip = annotationClass.getName();
					tooltip += "(" + annotationClass.getOntology().getName() + ")";
					text.setToolTipText(tooltip);
				}
			}
			
		});
		
		
		text.addListener(SWT.MenuDetect, new Listener(){

			@Override
			public void handleEvent(Event event) {
				if(da.getAnalysis().getType().equals(AnalysisEngineMetadata.TYPE_OE))
					showMenu(event);
			}
			
			private void showMenu(Event e){
				Menu menu = new Menu(self.getEditorSite().getShell(), SWT.POP_UP);
				
		        MenuItem item = new MenuItem(menu, SWT.PUSH);
		        item.setText("Add as a proposal...");
		        item.addListener(SWT.Selection, new Listener() {
		          public void handleEvent(Event e) {
		        	  AddToOntologyDialog d = new AddToOntologyDialog(Display.getCurrent().getActiveShell());
		        	  
		        	  d.setPreferredName(text.getSelectionText().toLowerCase().trim().replaceAll("\\W","_"));
		        	  
		        	  int status = d.open();
		        	  if(status == IDialogConstants.OK_ID){
		        		  da.getAnalysis().createNewProposal(d.getPreferredName().toLowerCase().trim().replaceAll("\\W","_"));
		        	  }
		        	  else if(status == IDialogConstants.CANCEL_ID)
		        		  ;
		        	  
		        	  
		          }
		        });
		        
		        menu.setLocation(e.x, e.y);
		        menu.setVisible(true);
		        while (!menu.isDisposed() && menu.isVisible()) {
		          if (!Display.getCurrent().readAndDispatch())
		        	  Display.getCurrent().sleep();
		        }
		        menu.dispose();
			}
		});
	}



	@Override
	public void setFocus() {
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}
	
	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if(part instanceof AnnotationsView){
			
			if(selection instanceof OrderedStructuredSelection){
				OrderedStructuredSelection dss = (OrderedStructuredSelection)selection;
				styler.setDisplayOrder(dss.getDisplayOrder());
				styler.setSelectedItems(dss.toList());
//				text.setStyleRanges(styler.getAllStyleRanges());
				text.redrawRange(0, text.getText().length(), false);
			}
		}
	}
	
	public void refresh(){
		initStyler();
		loadText();
		text.addLineStyleListener(styler);
	}

}

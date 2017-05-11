package edu.pitt.dbmi.odie.ui.editors.analysis.analysisengine;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.ncbo.stanford.server.beans.jaws.PropertyValue;

import edu.pitt.dbmi.odie.ui.Aesthetics;

public class AnalysisEngineMetadataTable extends TableViewer {

	public static String columnHeaders[] = { 
		"Property", 
		"Value"}; 

	private ColumnLayoutData columnLayouts[] = { 
			new ColumnWeightData(20),
			new ColumnWeightData(80)};

	
	public AnalysisEngineMetadataTable(Composite parent) {
		super(parent, SWT.BORDER);
		
		getTable().setLinesVisible(true);
		getTable().setHeaderVisible(true);
		createColumns();
		attachProviders();
		
	    Listener paintListener = new Listener() {
	        public void handleEvent(Event event) {
	        	Object element = event.item.getData();
	          switch (event.type) {
		          case SWT.MeasureItem: {
	        	  	
	        	  	PropertyValue pv = (PropertyValue)element;
	  				String txt = pv.getValue();
	  				if(txt == null || txt.trim().length() == 0)
	  					txt = pv.getKey();
	  			
	  				Point size = event.gc.textExtent(txt);
	  				event.width = getTable().getColumn(event.index).getWidth();
	  				int lines = size.x / event.width + 1;
	  				event.height = size.y * lines;
	  				break;
		          }
		          case SWT.PaintItem: {
		  			String content = getColumnText(element, event.index);
//					event.gc.setFont(getFont(element, event.index));
					
					final TextLayout layout = new TextLayout(Display.getCurrent());
		  		    layout.setText(content);
		  		    layout.setFont(getFont(element, event.index));
		  		    layout.setWidth(event.width);
					layout.draw(event.gc, event.x, event.y);
					
		          }
		          case SWT.EraseItem: {
		            event.detail &= ~SWT.FOREGROUND;
		            break;
		          }
	          }
	        }
	        
			public String getColumnText(Object element, int columnIndex) {
				if(element instanceof PropertyValue){
					PropertyValue pv = (PropertyValue)element;
					
					switch(columnIndex){
					case 0:
						return pv.getKey();
					case 1:
						return pv.getValue();
					}
				}
				return "Not a PropertyValue";
			}
			

		
			public Font getFont(Object element, int columnIndex) {
				if(columnIndex == 0)
					return Aesthetics.getBoldFont();
				return Aesthetics.getRegularFont();
				
			}
	      };
	      
//	      getTable().addListener(SWT.MeasureItem, paintListener);
//	      getTable().addListener(SWT.PaintItem, paintListener);
//	      getTable().addListener(SWT.EraseItem, paintListener);
	      for (int i = 0; i < getTable().getColumnCount(); i++) {
	    	  getTable().getColumn(i).pack();
	      }
	}

	private void attachProviders() {
		setContentProvider(new IStructuredContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void dispose() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				
				if(inputElement instanceof AnalysisEngineMetaData){
					List<PropertyValue> pvlist = new ArrayList<PropertyValue>();
					
					AnalysisEngineMetaData aem = (AnalysisEngineMetaData)inputElement;
					
					pvlist.add(new PropertyValue("Name",aem.getName()));
					pvlist.add(new PropertyValue("Version",aem.getVersion()));
					pvlist.add(new PropertyValue("Description",aem.getDescription()));
					pvlist.add(new PropertyValue("Vendor",aem.getVendor()));
					pvlist.add(new PropertyValue("Copyright",aem.getCopyright()));
					pvlist.add(new PropertyValue("Source URL",aem.getSourceUrlString()));
					return pvlist.toArray();
				}
				return new Object[]{};
			}
		});
		
		setLabelProvider(new PropertyValueLabelProvider());
	}
	
	class PropertyValueLabelProvider implements ITableLabelProvider, ITableFontProvider{
		public String getColumnText(Object element, int columnIndex) {
			if(element instanceof PropertyValue){
				PropertyValue pv = (PropertyValue)element;
				
				switch(columnIndex){
				case 0:
					return pv.getKey();
				case 1:
					return pv.getValue();
				case 2:
					return "";
				}
			}
			return "Not a PropertyValue";
		}
		

	
		public Font getFont(Object element, int columnIndex) {
			if(columnIndex == 0)
				return Aesthetics.getBoldFont();
			return Aesthetics.getRegularFont();
			
		}



		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}



		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}



		@Override
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}



		@Override
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class PropertyValueLabelProvider2 extends OwnerDrawLabelProvider{

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.OwnerDrawLabelProvider#measure(org.eclipse.swt.widgets.Event, java.lang.Object)
		 */
		protected void measure(Event event, Object element) {
			PropertyValue pv = (PropertyValue)element;
			String txt = pv.getValue();
			if(txt == null || txt.trim().length() == 0)
				txt = pv.getKey();
			
			Point size = event.gc.textExtent(txt);
			event.width = getTable().getColumn(event.index).getWidth();
			int lines = size.x / event.width + 1;
			event.height = size.y * lines;

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.OwnerDrawLabelProvider#paint(org.eclipse.swt.widgets.Event,
		 *      java.lang.Object)
		 */
		protected void paint(Event event, Object element) {
			String content = getColumnText(element, event.index);
			event.gc.setFont(getFont(element, event.index));
			event.gc.drawText(content, event.x, event.y, true);
		}
		
		
		public String getColumnText(Object element, int columnIndex) {
			if(element instanceof PropertyValue){
				PropertyValue pv = (PropertyValue)element;
				
				switch(columnIndex){
				case 0:
					return pv.getKey();
				case 1:
					return pv.getValue();
				}
			}
			return "Not a PropertyValue";
		}
		

	
		public Font getFont(Object element, int columnIndex) {
			if(columnIndex == 0)
				return Aesthetics.getBoldFont();
			return Aesthetics.getRegularFont();
			
		}
		
	}
	
	void createColumns() {
		Table table = getTable();
		TableLayout layout = new TableLayout();
		table.setLayout(layout);

		for (int i = 0; i < columnHeaders.length; i++) {
			TableViewerColumn tvc = new TableViewerColumn(this, SWT.LEFT);
			TableColumn tc = tvc.getColumn();

			tc.setText(columnHeaders[i]);
			tc.setResizable(true);
			layout.addColumnData(columnLayouts[i]);

			// if(i == 1)
			// tvc.setEditingSupport(new ValueEditing(tableViewer));
		}

	}

}

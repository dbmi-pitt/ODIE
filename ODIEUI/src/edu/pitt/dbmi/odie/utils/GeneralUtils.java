/**
 * 
 */
package edu.pitt.dbmi.odie.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.apache.uima.cas.ArrayFS;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.resource.metadata.ConfigurationParameterDeclarations;
import org.apache.uima.resource.metadata.FsIndexDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.InvalidXMLException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.lexicalizer.lucenefinder.LexicalSetBuilder;
import edu.pitt.dbmi.odie.middletier.AnalysisSpaceMiddleTier;
import edu.pitt.dbmi.odie.middletier.Configuration;
import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.model.AnalysisEngineMetadata;
import edu.pitt.dbmi.odie.model.LanguageResource;
import edu.pitt.dbmi.odie.model.LexicalSet;
import edu.pitt.dbmi.odie.model.Suggestion;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.dialogs.AddToProposalOntologyDialog;
import edu.pitt.dbmi.odie.ui.dialogs.PerformanceReportDialog;
import edu.pitt.dbmi.odie.ui.dialogs.TextDialog;
import edu.pitt.dbmi.odie.ui.editors.analysis.AnalysisEditor;
import edu.pitt.dbmi.odie.ui.editors.analysis.AnalysisEditorInput;
import edu.pitt.dbmi.odie.ui.editors.analysiscomparison.AnalysesComparisonEditor;
import edu.pitt.dbmi.odie.ui.editors.analysiscomparison.AnalysisComparisonEditorInput;
import edu.pitt.dbmi.odie.ui.editors.browser.BrowserEditor;
import edu.pitt.dbmi.odie.ui.editors.browser.BrowserEditorInput;
import edu.pitt.dbmi.odie.ui.editors.document.AnnotatedDocumentEditor;
import edu.pitt.dbmi.odie.ui.editors.document.DocumentEditorInput;
import edu.pitt.dbmi.odie.ui.editors.ontology.OntologyEditor;
import edu.pitt.dbmi.odie.ui.editors.ontology.OntologyEditorInput;
import edu.pitt.dbmi.odie.ui.preferences.PreferenceConstants;
import edu.pitt.dbmi.odie.ui.preferences.PreferenceDefaults;
import edu.pitt.dbmi.odie.ui.views.AnalysesView;
import edu.pitt.dbmi.odie.ui.views.AnnotationTypeView;
import edu.pitt.dbmi.odie.ui.views.ConceptsView;
import edu.pitt.dbmi.odie.ui.views.DocumentsView;
import edu.pitt.dbmi.odie.ui.views.OntologiesView;
import edu.pitt.dbmi.odie.ui.workers.AnalysisProcessor;
import edu.pitt.dbmi.odie.ui.workers.OntologyLoader;
import edu.pitt.dbmi.odie.uima.util.ODIE_IFConstants;
import edu.pitt.dbmi.odie.uima.utils.UIMAUtils;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.IOntologyException;
import edu.pitt.ontology.IProperty;
import edu.pitt.ontology.IResource;
import edu.pitt.ontology.IResourceIterator;
import edu.pitt.ontology.OntologyUtils;
import edu.pitt.ontology.bioportal.BClass;

/**
 * @author Girish Chavan
 * 
 */
public class GeneralUtils {

	static Logger logger = Logger.getLogger(GeneralUtils.class);

	 // Used to identify the windows platform.
    /**
     * Field WIN_ID. (value is ""Windows"")
     */
    private static final String WIN_ID = "Windows";

    // The default system browser under windows.
    /**
     * Field WIN_PATH. (value is ""rundll32"")
     */
    private static final String WIN_PATH = "rundll32";

    // The flag to display a url.
    /**
     * Field WIN_FLAG. (value is ""url.dll,FileProtocolHandler"")
     */
    private static final String WIN_FLAG = "url.dll,FileProtocolHandler";

    // The default browser under unix.
    /**
     * Field UNIX_PATH. (value is ""netscape"")
     */
    private static final String UNIX_PATH = "netscape";

    // The flag to display a url.
    /**
     * Field UNIX_FLAG. (value is ""-remote openURL"")
     */
    private static final String UNIX_FLAG = "-remote openURL";
	 /**
     * Display a file in the system browser. If you want to display a file, you
     * must include the absolute path name.
     * 
     * @param url the file's url (the url must start with either "http://" or
     * "file://").
     */
    public static void displayURL(String url) {
        boolean windows = isWindowsPlatform();
        String cmd = null;
        try {
            if (windows) {
                // cmd = 'rundll32 url.dll,FileProtocolHandler http://...'
                cmd = WIN_PATH + " " + WIN_FLAG + " " + url;
                Process p = Runtime.getRuntime().exec(cmd);
            } else {
                // Under Unix, Netscape has to be running for the "-remote"
                // command to work. So, we try sending the command and
                // check for an exit value. If the exit command is 0,
                // it worked, otherwise we need to start the browser.
                // cmd = 'netscape -remote openURL(http://www.javaworld.com)'
                cmd = UNIX_PATH + " " + UNIX_FLAG + "(" + url + ")";
                Process p = Runtime.getRuntime().exec(cmd);
                try {
                    // wait for exit code -- if it's 0, command worked,
                    // otherwise we need to start the browser up.
                    int exitCode = p.waitFor();
                    if (exitCode != 0) {
                        // Command failed, start up the browser
                        // cmd = 'netscape http://www.javaworld.com'
                        cmd = UNIX_PATH + " " + url;
                        p = Runtime.getRuntime().exec(cmd);
                    }
                } catch (InterruptedException x) {
                    logger
                            .fatal("Error bringing up browser, cmd='" + cmd
                                    + "'");
                    logger.fatal("Caught: " + x);
                }
            }
        } catch (IOException x) {
            // couldn't exec browser
            logger.fatal("Could not invoke browser, command=" + cmd);
            logger.fatal("Caught: " + x);
        }
    }
    
    /**
     * Try to determine whether this application is running under Windows or
     * some other platform by examing the "os.name" property.
     * 
     * @return true if this application is running under a Windows OS
     */
    public static boolean isWindowsPlatform() {
        String os = System.getProperty("os.name");
        if (os != null && os.startsWith(WIN_ID))
            return true;
        else
            return false;

    }
    
	public static String getPrettyDateTimeDifference(Date d1, Date d2) {

		// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		String diffStr = "";
		long diff = d1.getTime() - d2.getTime();
		diff = Math.abs(diff);

		if ((diff / (1000L * 60L * 60L * 24L)) > 365)
			diffStr = "more than a year";
		else if ((diff / (1000L * 60L * 60L * 24L)) > 29)
			diffStr = (diff / (1000L * 60L * 60L * 24L * 30L)) + " months";
		else if ((diff / (1000L * 60L * 60L)) > 59)
			diffStr = (diff / (1000L * 60L * 60L * 24L)) + " days";
		else
			diffStr = "less than a day";

		return diffStr;
	}

	public static IPreferenceStore getPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}

	public static List getSuggestions(Analysis analysis) {
		AnalysisSpaceMiddleTier mt = Activator.getDefault()
				.getAnalysisMiddleTier(analysis);

		String scoreThreshold = getPreferenceStore().getString(
				PreferenceConstants.PROPERTY_KEY_SUGGESTION_THRESHOLD);
		Float ts;
		try {
			ts = new Float(scoreThreshold);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			ts = 0.8f;
		}
		return mt.getSuggestions(ts);
	}

	public static String getTypeForFile(File file) {
		String filename = file.getName();
		String ext = GeneralUtils.getExtensionForFilename(filename);

		if (ext.equalsIgnoreCase(ODIEConstants.EXTENSION_UIMA_PEAR_FILE))
			return ODIEConstants.FILETYPE_PEAR;
		else if (ext.equalsIgnoreCase(ODIEConstants.EXTENSION_OWL_FILE)
				|| (ext.equalsIgnoreCase(ODIEConstants.EXTENSION_OBO_FILE)))
			return ODIEConstants.FILETYPE_ONTOLOGY;
		else if (ext.equalsIgnoreCase(ODIEConstants.EXTENSION_ODIE_ONT_FILE))
			return ODIEConstants.FILETYPE_ODIEONTOLOGY;
		else
			return ODIEConstants.FILETYPE_UNKNOWN;
	}

	public static IClass getConceptClass(Analysis analysis, String uriString) throws Exception {
		for(LanguageResource lr:analysis.getLanguageResources()){
			GeneralUtils.initLanguageResourceIfRequired(lr);
			IOntology ont = (IOntology) lr.getResource();
			IClass c = ont.getClass(uriString);
			if(c!=null){
				return c;
			}
		}
		return null;
	}

	public static File getAEInstallationDir() {
		return Platform.getLocation()
				.append(ODIEConstants.AE_DIR_RELATIVE_PATH).toFile();
	}

	public static IPath getPluginFolderPath() {
		// try {
		return Platform.getLocation();
		// IPath path = p.append("/temp");
		// String ss = p.toOSString();
		// String s = path.toFile().getAbsolutePath();
		//			
		// return Platform.getLocation().toFile().toURI().toURL();
		// } catch (MalformedURLException e) {
		// e.printStackTrace();
		// } catch (IllegalStateException e) {
		// e.printStackTrace();
		// }
		// return null;
	}

	public static void closeAllAnnotatedDocumentEditorsFor(Analysis analysis) {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		if (page == null)
			return;

		for (IEditorReference editorRef : page.getEditorReferences()) {
			IEditorPart editor = editorRef.getEditor(false);
			if (editor != null && editor instanceof AnnotatedDocumentEditor) {
				Analysis a = ((DocumentEditorInput) editor.getEditorInput())
						.getAnalysisDocument().getAnalysis();
				if (a.equals(analysis))
					page.closeEditor(editor, false);
			}
		}
	}

	public static Analysis getFirstSelectionInAnalysisView() {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IViewPart part = page.findView(AnalysesView.ID);

		if (part == null) {
			logger
					.error("Could not find AnalysesView. It may not be created yet.");
			return null;
		}

		StructuredSelection selection = (StructuredSelection) ((AnalysesView) part)
				.getViewer().getSelection();
		if (selection == null)
			return null;

		return (Analysis) selection.getFirstElement();
	}

	public static List<Analysis> getSelectionsInAnalysisView() {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IViewPart part = page.findView(AnalysesView.ID);

		if (part == null) {
			logger
					.error("Could not find AnalysesView. It may not be created yet.");
			return null;
		}

		StructuredSelection selection = (StructuredSelection) ((AnalysesView) part)
				.getViewer().getSelection();
		if (selection == null)
			return null;

		return selection.toList();
	}

	
	public static AnalysisDocument getCurrentAnalysisDocument() {
		IEditorPart editor = GeneralUtils.getActiveEditor();
		if (editor instanceof AnnotatedDocumentEditor) {
			return ((DocumentEditorInput) ((AnnotatedDocumentEditor) editor)
					.getEditorInput()).getAnalysisDocument();
		}

		return null;
	}

	public static AnalysesComparisonEditor openCompareAnalysesEditor(List<Analysis> analyses, boolean create) {
		AnalysisComparisonEditorInput input = new AnalysisComparisonEditorInput(analyses);
		return (AnalysesComparisonEditor)openEditor(AnalysesComparisonEditor.ID,input,create);
	}

	public static IEditorPart openEditor(String editorID, IEditorInput input,
			boolean create) {
		IWorkbenchPage page = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow().getActivePage();
		IEditorPart part = page.findEditor(input);
		
		if (part != null) {
			page.bringToTop(part);
		} else if (create) {
			try {
				part = page.openEditor(input, editorID);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		
		return part;

		
	}

	public static OntologyEditor openOntologyEditor(IOntology ontology,
			boolean create) {
		if(create){
			ProgressMonitorDialog pd = GeneralUtils
					.getProgressMonitorDialog();
			try {
				pd.run(true, false, new OntologyLoader(ontology));
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		OntologyEditorInput input = new OntologyEditorInput(ontology);
		return (OntologyEditor)openEditor(OntologyEditor.ID, input,create);

	}
	
	public static boolean hasMySQLBundled(){
		return getBundledMySQLPath()!=null;
	}
	public static String getBundledMySQLPath(){
		boolean mysqlWindowsExists = (new File(GeneralUtils.getPluginFolderPath() + 
				File.separator + ODIEConstants.MYSQL_WIN_DIR))
				.exists();
		boolean mysqlLinuxExists = (new File(GeneralUtils.getPluginFolderPath() + 
				File.separator + ODIEConstants.MYSQL_LINUX_DIR))
				.exists();
		boolean mysqlOSXExists = (new File(GeneralUtils.getPluginFolderPath() + 
				File.separator + ODIEConstants.MYSQL_OSX_64DIR))
		.exists();
		
		String mysqlPath = null;
		if (mysqlWindowsExists) {
			mysqlPath = GeneralUtils.getPluginFolderPath() + 
			File.separator + ODIEConstants.MYSQL_WIN_DIR;
		}

		if (mysqlLinuxExists) {
			mysqlPath = GeneralUtils.getPluginFolderPath() + 
			File.separator + ODIEConstants.MYSQL_LINUX_DIR;
		}
		
		if (mysqlOSXExists) {
			mysqlPath = GeneralUtils.getPluginFolderPath() + 
			File.separator + ODIEConstants.MYSQL_OSX_64DIR;
		}
		
		System.out.println("MySQL Path:" + mysqlPath);
		return mysqlPath;
	}

	public static void closeEditor(Analysis a) {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		AnalysisEditorInput cei = new AnalysisEditorInput(a);
		IEditorPart editor = page.findEditor(cei);
		if (editor != null)
			page.closeEditor(editor, false);
	}

	public static void closeEditor(IOntology o) {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		OntologyEditorInput cei = new OntologyEditorInput(o);
		IEditorPart editor = page.findEditor(cei);
		if (editor != null)
			page.closeEditor(editor, false);
	}

	
	public static void refreshViews() {

		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		final IEditorReference[] editors = page.getEditorReferences();

		final IViewPart analysisPart = page.findView(AnalysesView.ID);
		final IViewPart conceptPart = page.findView(ConceptsView.ID);
		final IViewPart annotationTypePart = page
				.findView(AnnotationTypeView.ID);
		final IViewPart documentsPart = page.findView(DocumentsView.ID);
		final IViewPart ontologiesPart = page.findView(OntologiesView.ID);

		Display.getCurrent().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (analysisPart != null) {
					((AnalysesView) analysisPart).refresh();
				}

				if (conceptPart != null) {
					((ConceptsView) conceptPart).refresh();
				}

				if (annotationTypePart != null) {
					((AnnotationTypeView) annotationTypePart).refresh();
				}

				if (documentsPart != null) {
					((DocumentsView) documentsPart).refresh();
				}

				if (ontologiesPart != null) {
					((OntologiesView) ontologiesPart).refresh();
				}
				for (IEditorReference er : editors) {
					IEditorPart editor = er.getEditor(false);

					if (editor != null) {
						if (editor instanceof AnnotatedDocumentEditor) {
							// ((AnnotatedDocumentEditor)editor).refresh();
						} else if (editor instanceof AnalysisEditor) {
							((AnalysisEditor) editor).refresh();
						}
					}
				}

			}

		});

	}

	public static void initTypeSystemIfRequired(
			AnalysisEngineMetadata analysisEngineMetadata)
			throws CASRuntimeException, ResourceInitializationException,
			InvalidXMLException, IOException {
		if (analysisEngineMetadata.getTypeSystem() != null)
			return;

		String serializedTSD = analysisEngineMetadata
				.getSerializedTypeSystemDescriptor();
		TypeSystem ts = UIMAUtils
				.serializedTypeSystemDescriptorToTypeSystem(serializedTSD);
		analysisEngineMetadata.setTypeSystem(ts);

	}

	public static void showPerformanceDialog(Analysis analysis) {
		PerformanceReportDialog prd = new PerformanceReportDialog(Activator
				.getDefault().getWorkbench().getDisplay().getActiveShell(),
				analysis);
		prd.setBlockOnOpen(true);
		prd.open();
	}

	public static String nullSafe(String text) {
		return (text == null ? "" : text);
	}

	public static IEditorPart getActiveEditor() {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		return page.getActiveEditor();
	}

	public static void showInformationDialog(final String title,
			final String mesg) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				MessageDialog.openInformation(getShell(), title, mesg);

			}

		});

	}

	public static void showErrorDialog(final String title, final String mesg) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				MessageDialog.openError(getShell(), title, mesg);

			}

		});

	}

	public static void showScrolledErrorDialog(final String title,
			final String mesg) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				TextDialog dialog = new TextDialog(getShell(), title, mesg);
				dialog.open();
			}
		});
	}

	public static String getTablLimitedTableContents(TreeItem[] items, Tree tree){
		StringBuffer tabLimitedString = new StringBuffer();
		
		for(int i=0;i<items.length;i++){
			for(int j=0;j<tree.getColumnCount();j++){
				tabLimitedString.append(items[i].getText(j) + "\t");
			}
			tabLimitedString.append("\n");
			tabLimitedString.append(getTablLimitedTableContents(items[i].getItems(), tree));
		}
		
		return tabLimitedString.toString();
	}
	
	public static IViewPart findView(String id) {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		return page.findView(id);
	}

	public static void activatePart(String id) {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		page.activate(findView(id));

	}

	public static Shell getShell() {
		return PlatformUI.getWorkbench().getDisplay().getActiveShell();
	}

	public static String getExtensionForFilename(String filename) {
		return (filename.lastIndexOf(".") == -1) ? "" : filename.substring(
				filename.lastIndexOf(".") + 1, filename.length());
	}

	public static File getDropinDir() {
		return Platform.getLocation().append(
				ODIEConstants.DROPIN_DIR_RELATIVE_PATH).toFile();
	}

	public static void loadAnalysisEngine(Analysis analysis)
	throws Exception {
		if(analysis.getAnalysisEngine() != null)
			return;
		
		AnalysisEngine ae = UIMAUtils.loadAnalysisEngine(analysis
				.getAnalysisEngineMetadata().getURL());
		analysis.getAnalysisEngineMetadata().setAnalysisEngine(ae);
	}
	
	public static void loadAndConfigureAnalysisEngine(Analysis analysis)
			throws Exception {
		
		
		loadAnalysisEngine(analysis);
		
		String type = analysis.getAnalysisEngineMetadata().getType();

		if (ODIEConstants.AE_TYPE_NER.equals(type)) {
			configureNERAnalysisEngine(analysis);
			analysis.getAnalysisEngine().reconfigure();
		} else if (ODIEConstants.AE_TYPE_OE.equals(type)) {
			configureOEAnalysisEngine(analysis);
			analysis.getAnalysisEngine().reconfigure();
		}
	}

	private static void configureNERAnalysisEngine(Analysis analysis)
			throws ResourceConfigurationException {
		LexicalSet ls = analysis.getLexicalSet();
		MiddleTier mt = Activator.getDefault().getMiddleTier();
		
		AnalysisEngine ae = analysis.getAnalysisEngineMetadata()
				.getAnalysisEngine();

		logger.debug("Configuring NER Analysis Engine...");
		
		logger.debug(ODIEConstants.UIMAPARAM_LUCENE_FINDER_DIR + ":"
				+ ls.getLocation());
		ae.setConfigParameterValue(ODIEConstants.UIMAPARAM_LUCENE_FINDER_DIR, ls.getLocation());

		logger.debug("Configuration complete");
	}

	private static void configureOEAnalysisEngine(Analysis analysis)
			throws ResourceConfigurationException {
		LexicalSet ls = analysis.getLexicalSet();
		LexicalSet pls = analysis.getProposalLexicalSet();
		MiddleTier mt = Activator.getDefault().getMiddleTier();
		Configuration config = mt.getConfiguration();

		AnalysisEngine ae = analysis.getAnalysisEngineMetadata()
				.getAnalysisEngine();

		logger.debug("Configuring OE Analysis Engine...");

		logger.debug(ODIEConstants.UIMAPARAM_DB_DRIVER + ":"
				+ config.getDatabaseDriver());
		ae.setConfigParameterValue(ODIEConstants.UIMAPARAM_DB_DRIVER, config
				.getDatabaseDriver());
	
		logger.debug(ODIEConstants.UIMAPARAM_DB_USERNAME + ":"
				+ config.getUsername());
		ae.setConfigParameterValue(ODIEConstants.UIMAPARAM_DB_USERNAME, config
				.getUsername());

		logger.debug(ODIEConstants.UIMAPARAM_DB_PASSWORD + ":"
				+ config.getPassword());
		ae.setConfigParameterValue(ODIEConstants.UIMAPARAM_DB_PASSWORD, config
				.getPassword());
		logger.debug("Configuration complete");
		
		logger.debug(ODIEConstants.UIMAPARAM_ANALYSIS_DB_URL + ":"
				+ config.getDatabaseURLWithoutSchema()
				+ analysis.getDatabaseName());
		ae.setConfigParameterValue(ODIEConstants.UIMAPARAM_ANALYSIS_DB_URL,
				config.getDatabaseURLWithoutSchema()
						+ analysis.getDatabaseName());

		logger.debug(ODIEConstants.UIMAPARAM_OE_EXISTING_LUCENE_FINDER_DIR + ":" + ls.getLocation());
		ae.setConfigParameterValue(ODIEConstants.UIMAPARAM_OE_EXISTING_LUCENE_FINDER_DIR,
				ls.getLocation());
		
		logger.debug(ODIEConstants.UIMAPARAM_OE_PROPOSAL_LUCENE_FINDER_DIR + ":" + pls.getLocation());
		ae.setConfigParameterValue(ODIEConstants.UIMAPARAM_OE_PROPOSAL_LUCENE_FINDER_DIR,
				pls.getLocation());
		

	}

	public static boolean generatesNamedEntityAnnotationType(
			AnalysisEngineMetaData analysisEngineMetaData) {
		return (analysisEngineMetaData.getTypeSystem().getType(ODIE_IFConstants.NAMED_ENTITY_TYPE_FULLNAME)!=null);
	}
	
	public static boolean hasAllNERParameters(
			AnalysisEngineMetaData analysisEngineMetaData) {
		return hasAllParams(analysisEngineMetaData, ODIEConstants.NER_PARAMS);
	}

	public static boolean hasAllOEParameters(
			AnalysisEngineMetaData analysisEngineMetaData) {
		return hasAllParams(analysisEngineMetaData, ODIEConstants.OE_PARAMS);
	}

	private static boolean hasAllParams(
			AnalysisEngineMetaData analysisEngineMetaData, String[] params) {
		ConfigurationParameterDeclarations cpd = analysisEngineMetaData
				.getConfigurationParameterDeclarations();

		int count = 0;

		List<String> paramList = Arrays.asList(params);

		for (ConfigurationParameter cp : cpd.getConfigurationParameters())
			if (paramList.contains(cp.getName()))
				count++;

		return (count == paramList.size());
	}

	public static boolean isOE(Analysis a) {
		return a.getType().equals(ODIEConstants.AE_TYPE_OE);
	}

	public static boolean isPartiallyProcessed(Analysis a) {
		return (a.getAnalysisDocuments(AnalysisDocument.STATUS_PROCESSING)
				.size() != 0);
	}

	public static boolean isOther(Analysis analysis) {
		return analysis.getType().equals(ODIEConstants.AE_TYPE_OTHER);
	}

	public static void addToProposalOntologyWithDialog(Analysis analysis) {
		AddToProposalOntologyDialog d = new AddToProposalOntologyDialog(
				getShell(), GeneralUtils
						.getOntologiesInUse(analysis));
		d.setBlockOnOpen(true);

		// Open the main window
		if (d.open() == IDialogConstants.OK_ID) {
			addToProposalOntology(analysis, d.getName(), d.getSynonyms(), d
					.getParentClass());
			GeneralUtils.refreshProposalLexicalSet(analysis);
		}

	}

	public static boolean addToProposalOntology(Analysis analysis,
			String conceptName, List<String> synonyms, IClass parent) {
		MiddleTier mt = Activator.getDefault().getMiddleTier();

		LexicalSet proposalLS = analysis.getProposalLexicalSet();
		LanguageResource proposalLR = proposalLS
				.getLexicalSetLanguageResources().get(0).getLanguageResource();
		GeneralUtils.initLanguageResourceIfRequired(proposalLR);

		if (proposalLR.getResource() == null)
			return false;

		IOntology proposalOntology = (IOntology) proposalLR.getResource();

		if(proposalOntology.getClass(OntologyUtils.toResourceName(conceptName))!=null)
			return true;
		
		IClass c = mt.createClass(proposalOntology, conceptName);

		if (synonyms != null) {
			for (String label : synonyms)
				c.addLabel(label);
		}
		if (parent != null)
			mt.addParent(c, parent);

		try {
			proposalOntology.save();
			return true;
		} catch (IOntologyException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean refreshProposalLexicalSet(Analysis analysis) {
		LexicalSetBuilder builder = new LexicalSetBuilder(Activator
				.getDefault().getMiddleTier());
		LexicalSet proposalLS = analysis.getProposalLexicalSet();
		return builder.createNewLexicalSet(proposalLS, null);
	}

	public static boolean addProposals(Suggestion[] suggestions) {
		if (suggestions.length == 0)
			return true;

		// TODO We assume that the suggestion is being dropped in
		// the proposals tree of the same analysis. May not be
		// true in future versions.

		Analysis analysis = suggestions[0].getAnalysis();

		for (Suggestion s : suggestions) {
			String name = s.getNerNegative();
			if (!addToProposalOntology(analysis, name, null, null))
				return false;
		}
		return true;
	}

	public static void runAnalysis(Analysis currentAnalysis)
			throws InvocationTargetException, InterruptedException {
		ProgressMonitorDialog pd = getProgressMonitorDialog();
		pd.run(true, true, new AnalysisProcessor(currentAnalysis));

		GeneralUtils.openEditor(AnalysisEditor.ID,
				new AnalysisEditorInput(currentAnalysis), true);
		GeneralUtils.refreshViews();

		GeneralUtils.showPerformanceDialog(currentAnalysis);

	}

	public static List<Suggestion> getAggregatedSuggestions(Analysis analysis) {
		AnalysisSpaceMiddleTier mt = Activator.getDefault()
				.getAnalysisMiddleTier(analysis);

		String scoreThreshold = getPreferenceStore().getString(
				PreferenceConstants.PROPERTY_KEY_SUGGESTION_THRESHOLD);
		Float ts;
		try {
			ts = new Float(scoreThreshold);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			ts = new Float(
					PreferenceDefaults
							.getDefaultForProperty(PreferenceConstants.PROPERTY_KEY_SUGGESTION_THRESHOLD));
		}
		return mt.getAggregatedSuggestions(ts);
	}

	public static Suggestion getSuggestion(long suggestionId, long analysisId) {
		MiddleTier mt = Activator.getDefault().getMiddleTier();

		Analysis a = mt.getAnalysisForId(analysisId);
		AnalysisSpaceMiddleTier amt = Activator.getDefault()
				.getAnalysisMiddleTier(a);

		return amt.getSuggestionForId(suggestionId);
	}

	public static List<Suggestion> getSuggestionsForAggregate(Suggestion s) {
		AnalysisSpaceMiddleTier mt = Activator.getDefault()
				.getAnalysisMiddleTier(s.getAnalysis());

		String scoreThreshold = getPreferenceStore().getString(
				PreferenceConstants.PROPERTY_KEY_SUGGESTION_THRESHOLD);
		Float ts;
		try {
			ts = new Float(scoreThreshold);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			ts = 0.8f;
		}
		return mt.getSuggestionsForNerNegative(s.getNerNegative(), ts);
	}

	public static ProgressMonitorDialog getProgressMonitorDialog() {
		return new ProgressMonitorDialog(PlatformUI.getWorkbench().getDisplay()
				.getActiveShell());
	}

	public static void initCASIfRequired(AnalysisDocument ad) throws Exception {
		if (!ad.getStatus().equals(AnalysisDocument.STATUS_DONE)
				|| ad.getCas() != null) {
			return;
		} else
			assert ad.getSerializedCAS() != null;

		TypeSystemDescription tsDesc = UIMAUtils
				.deSerializeTypeSystemDescriptor(ad.getAnalysis()
						.getAnalysisEngineMetadata()
						.getSerializedTypeSystemDescriptor());
		tsDesc.resolveImports();

		CAS aCAS = CasCreationUtils.createCas(tsDesc, null,
				new FsIndexDescription[0]);
		UIMAUtils.deSerializeCAS(ad.getSerializedCAS(), aCAS);
		ad.setCas(aCAS);

	}

	public static String getPrettyPercentageFormat(float f) {
		float d = round(f * 100, 2);
		return "" + d + "%";
	}

	public static float round(float num, int places) {
		float p = (float) Math.pow(10, places);
		num = num * p;
		float tmp = Math.round(num);
		return (float) tmp / p;
	}

	public static boolean isCOREF(Analysis analysis) {
		Type t = analysis.getAnalysisEngineMetadata().getTypeSystem().getType(
				ODIE_IFConstants.CHAIN_TYPE_FULLNAME);

		return t != null;
	}

	public static void refreshOntologiesView() {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		final IViewPart ontologiesPart = page.findView(OntologiesView.ID);

		Display.getCurrent().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (ontologiesPart != null) {
					((OntologiesView) ontologiesPart).refresh();
				}
			}
		});
	}

	public static IOntology[] getOntologiesInUse(Analysis analysis) {
		List<IOntology> ontologies = new ArrayList<IOntology>();

		for (LanguageResource lr : analysis.getLanguageResources()) {
			GeneralUtils.initLanguageResourceIfRequired(lr);
			ontologies.add((IOntology) lr.getResource());
		}

		return ontologies.toArray(new IOntology[] {});
	}

	public static void initLanguageResourceIfRequired(LanguageResource lr) {
		if (lr.getResource() != null)
			return;

		MiddleTier mt = edu.pitt.dbmi.odie.ui.Activator.getDefault()
				.getMiddleTier();

		IResource r = mt.getResourceForURI(lr.getURI());

		if (r == null) {
			logger.error("Could not load resource(" + lr.getURI() + ")");
		} else {
			lr.setResource(r);
		}
	}

	public static void openAnnotationClassInOntologyEditor(ArrayFS arr) {
		if (arr.size() > 0) {
			FeatureStructure fs = arr.get(0);
			String conceptURIString = getConceptURIFromFS(fs);

			try {
				IResource r = Activator.getDefault().getMiddleTier()
						.getResourceForURI(new URI(conceptURIString));

				if (r instanceof IClass) {
					IClass c = (IClass) r;
					openClassInOntologyEditor(c);

				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}

	}

	public static String getOntologyURIFromFS(FeatureStructure fs) {
		Feature oidFeature = fs.getType().getFeatureByBaseName(
				ODIE_IFConstants.OID_FEATURE_NAME);
		String oidValue = fs.getStringValue(oidFeature);
		if(oidValue.contains("#")){
			String[] split = oidValue.split("#");
			return split[1];
		}
		else return oidValue;
	}
	
	public static String getConceptURIFromFS(FeatureStructure fs) {
		Feature oidFeature = fs.getType().getFeatureByBaseName(
				ODIE_IFConstants.OID_FEATURE_NAME);
		String oidValue = fs.getStringValue(oidFeature);
		if(oidValue.contains("#")){
			String[] split = oidValue.split("#");
			return split[1] + "#" + split[0];
		}
		
		Feature codeFeature = fs.getType().getFeatureByBaseName(
				ODIE_IFConstants.CODE_FEATURE_NAME);
		String codeValue = fs.getStringValue(codeFeature);

		return oidValue + "#" + codeValue;
	}

	public static void openClassInOntologyEditor(IClass c) {
		if(c==null){
			logger.error("openClassInOntologyEditor:Null class object.");
			return;
		}
		
		
		OntologyEditor editor = GeneralUtils.openOntologyEditor(c.getOntology(),true);
		editor.select(c);
		
//		IWorkbenchPage page = PlatformUI.getWorkbench()
//				.getActiveWorkbenchWindow().getActivePage();
//		page.bringToTop(editor);
		
	}
	
	public static void installBoldFont(Label label) {
		FontData[] fd = label.getFont().getFontData();
		FontData fd1 = fd[0];
		
		fd1.setStyle(SWT.BOLD);
		label.setFont(new Font(Display.getCurrent(),fd1));
	}
	
	public static boolean isProxyClass(IClass cl){
		String bpURIStr = (String)cl.getPropertyValue(cl.getOntology().getProperty(IProperty.RDFS_IS_DEFINED_BY));
		return bpURIStr!=null;
	}

	public static IOntology getFirstSelectionInOntologiesView() {
		IWorkbenchPage page = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow().getActivePage();
		IViewPart part = page.findView(OntologiesView.ID);
		
		if (part == null) {
			logger
					.error("Could not find OntologiesView. It may not be created yet.");
			return null;
		}
		
		StructuredSelection selection = (StructuredSelection) ((OntologiesView) part)
				.getViewer().getSelection();
		if (selection == null)
			return null;
		
		return (IOntology) selection.getFirstElement();
	}

	public static BClass getParentBioportalClass(IClass newc) {
		MiddleTier mt = Activator.getDefault().getMiddleTier();
		IClass[] parents = newc.getSuperClasses();
		if(parents.length>0){
			IClass parent = parents[0];
			String bpURIStr = (String)parent.getPropertyValue(parent.getOntology().getProperty(IProperty.RDFS_IS_DEFINED_BY));
			if(bpURIStr!=null){
				IResource r;
				try {
					r = mt.getBioportalRepository().getResource(new URI(bpURIStr));
					if(r != null){
						return (BClass)r;
					}	
					
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static void restartODIE() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(
		new Runnable() {
			
			@Override
			public void run() {
				PlatformUI.getWorkbench().restart();
			}
		});
		
	}

	public static void exportOntologyToCSVWithDialog(IOntology o) {
		FileDialog d = new FileDialog(getShell(), SWT.SAVE);
	    d
	        .setFilterNames(new String[] { "Comma Separated Value Files (*.csv)"});
	    d.setFilterExtensions(new String[] {"*.csv"}); // Windows
	                                    // wild
	                                    // cards
	    d.setFileName(o.getName() + ".csv");
	    String path = d.open();
	    
	    if (path !=null) {
			File f = new File(path);
			try {
				if(f.exists()){
					if(MessageDialog.openQuestion(getShell(), "Confirm Replace", 
							"The selected file already exists. Do you want to replace it?"))
						f.delete();
					else
						return;
				}
				
				f = new File(path);
				f.createNewFile();
				
				IResourceIterator it = o.getAllClasses();
				
				FileWriter fw = new FileWriter(f);
				
				fw.write("Concept Name, Concept URI, Synonyms, Parent Concept, Parent Concept URI" + 
						"\n");
			
				while(it.hasNext()){
					IClass c = (IClass) it.next();
					String str = c.getName();
					str += ", ";
					
					str += c.getURI().toASCIIString();
					str += ", ";
					
					for(String s:c.getLabels())
						str += s + ";";
					str += ", ";
					
					str += c.getSuperClasses()[0].getName();
					str += ", ";
					str += c.getSuperClasses()[0].getURI().toASCIIString();
					str += "\n";
					
					fw.write(str);
				}
				fw.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			GeneralUtils.showInformationDialog("Export Successful", "The " + o.getName() + " ontology " +
					"was successfully exported to " + path);
		}
	}

	public static BrowserEditor openURL(URL url) {
		BrowserEditorInput input = new BrowserEditorInput(url);
		return (BrowserEditor)openEditor(BrowserEditor.ID, input,true);
		
	}

	public static String getOntologySearchIndexLocation(LanguageResource lr) {
		String luceneDir = Activator.getDefault().getConfiguration().getLuceneIndexDirectory();
		return luceneDir + System.getProperty("file.separator") + lr.getId();
	}
}

/**
 * 
 */
package edu.pitt.dbmi.odie.ui.workers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Collections;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.middletier.SQLFileLoader;
import edu.pitt.dbmi.odie.model.LanguageResource;
import edu.pitt.dbmi.odie.model.LexicalSet;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.utils.GeneralUtils;
import edu.pitt.ontology.IRepository;

/**
 * @author Girish Chavan
 * 
 */
public class ODIEOntologyImporter implements IRunnableWithProgress {

	URI fileURI = null;
	boolean success = false;
	
	Logger logger = Logger.getLogger(ODIEOntologyImporter.class);
	
	public ODIEOntologyImporter(URI uri) {
		fileURI = uri;
	}
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		File f = new File(fileURI);
		success = unpackageZip(f,monitor);
		monitor.done();
	}

	public boolean wasSuccessful() {
		return success;
	}

    private boolean unpackageZip(File file, IProgressMonitor monitor){
        
        try {
			MiddleTier mt = Activator.getDefault().getMiddleTier();
			String tempDir = mt.getConfiguration().getTemporaryDirectory();
			
			ZipFile zip = new ZipFile(file);
   
			monitor.beginTask("Unpacking " + file.getName() , (Collections.list(zip.entries())).size()+2);

			monitor.subTask("Reading metadata");
			
			ZipEntry metaEntry = findZipEntry(zip, ODIEDataFileCreator.METADATA);
			if(metaEntry == null)
				return false;
			
			File metaFile = unpackZipEntry(zip,metaEntry,tempDir);
			
			monitor.subTask("Creating database entries");
			LanguageResource lr = createNewLanguageResource(metaFile);
			
			if(lr == null){
				return false;
			}
			
			String osiloc = GeneralUtils.getOntologySearchIndexLocation(lr);
			
			boolean fileExists = false;
			File f = new File(osiloc);
			
			if(!f.exists())
				fileExists = f.mkdirs();
			else
				fileExists = true;
			if(!fileExists){
				mt.delete(lr);
				return false;
			}
			LexicalSet ls = createNewLexicalSet(lr);
			if(ls == null){
				mt.delete(lr);
				return false;
			}
			
			f = new File(ls.getLocation());
			
			if(!f.exists())
				fileExists = f.mkdirs();
			else
				fileExists = true;
			
			if(!fileExists){
				mt.delete(ls);
				mt.delete(lr);
				return false;
			}
			monitor.worked(1);
			
			SQLFileLoader sqlFileLoader = new SQLFileLoader(mt.getConfiguration());
			
			boolean success = true;
			for(ZipEntry ze:Collections.list(zip.entries())){
				String name = ze.getName();
				monitor.subTask(getZipFileName(ze));
				if(name.contains(ODIEDataFileCreator.PROTEGE_DATABASE)){
					monitor.setTaskName("Extracting database file...");
					File sqlfile = unpackZipEntry(zip, ze, tempDir);
					monitor.setTaskName("Importing database...");
					if(!sqlFileLoader.loadSQLFile(sqlfile)){
						mt.delete(ls);
						mt.delete(lr);
						monitor.done();
						sqlFileLoader.destroy();
						return false;
					}
						
					monitor.worked(1);
					
				}
				else if(name.contains(ODIEDataFileCreator.LUCENE_FINDER)){
					if(unpackZipEntry(zip, ze, ls.getLocation())==null){
						success = false;
						break;
					}
				}
				else if(name.contains(ODIEDataFileCreator.LUCENE_ONTOLOGY_SEARCH)){
					if(unpackZipEntry(zip, ze, osiloc)==null){
						success = false;
						break;
					}
				}
				monitor.worked(1);
			}
			
			sqlFileLoader.destroy();
			
			return success;
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return false;
		}
    }
    
	private LexicalSet createNewLexicalSet(LanguageResource lr) {
		LexicalSet lexicalSet;
		try {
			lexicalSet = new LexicalSet();
			lexicalSet.setName(lr.getName());
			lexicalSet.setDescription("Vocabulary for " + lr.getName());

			MiddleTier mt = edu.pitt.dbmi.odie.ui.Activator.getDefault()
					.getMiddleTier();

			lexicalSet.addLanguageResource(lr);
			mt.persist(lexicalSet);
			
			StringBuffer sb = new StringBuffer(mt.getConfiguration().getLuceneIndexDirectory()) ;
			sb.append(File.separator) ;
			sb.append(edu.pitt.dbmi.odie.ODIEConstants.LUCENEFINDER_DIRECTORY) ;
			sb.append(File.separator) ;
			sb.append(lexicalSet.getId()) ;
			String lexicalSetLuceneIndexDirectoryName =  sb.toString();

			lexicalSet.setLocation(lexicalSetLuceneIndexDirectoryName);
			mt.persist(lexicalSet);
			
			return lexicalSet;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return null;
		}
		
	}
	private LanguageResource createNewLanguageResource(File metaFile) {
		MiddleTier mt = Activator.getDefault().getMiddleTier();
		Properties p = new Properties();
		 try {
			p.load(new FileReader(metaFile));
			LanguageResource lr = new LanguageResource();
			lr.setDescription(p.getProperty(ODIEConstants.PROPERTY_ONT_DESC));
			lr.setLocation(p.getProperty(ODIEConstants.PROPERTY_ONT_LOCATION));
			lr.setFormat(IRepository.FORMAT_DATABASE);
			lr.setName(p.getProperty(ODIEConstants.PROPERTY_ONT_NAME));
			lr.setType(IRepository.TYPE_ONTOLOGY);
			try {
				lr.setURI(new URI(p.getProperty(ODIEConstants.PROPERTY_ONT_URI)));
			} catch (Exception e) {
				e.printStackTrace();
			}
			lr.setVersion(p.getProperty(ODIEConstants.PROPERTY_ONT_VER));
			
			mt.persist(lr);
			return lr;
		 } catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return null;
		}
	}
	private File unpackZipEntry(ZipFile zip, ZipEntry ze, String parentDir) {
		try {
			String name = getZipFileName(ze);
			File f = new File(parentDir + System.getProperty("file.separator") + name);
			if(f.exists())
				f.delete();
			
			f.createNewFile();
			
			FileOutputStream zout = new FileOutputStream(f);

			InputStream is = zip.getInputStream(ze);
			
			byte[] buffer = new byte[1024];
			int length;
			while((length = is.read(buffer)) > 0){
				zout.write(buffer, 0, length);
			}
			
			return f;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return null;
		}
	}
	private String getZipFileName(ZipEntry ze) {
		String name = ze.getName();
		int ind = name.lastIndexOf("/");
		if(ind>=0)
			return name.substring(name.lastIndexOf("/")+1);
		
		return name;
	}
	private ZipEntry findZipEntry(ZipFile zip, String metadata) {
		for(ZipEntry e:Collections.list(zip.entries())){
        	
        	String name = e.getName();
        	if(name.contains(metadata)){
        		return e;
        	}
        }
		return null;
	}

}

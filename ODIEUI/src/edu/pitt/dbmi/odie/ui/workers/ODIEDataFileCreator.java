package edu.pitt.dbmi.odie.ui.workers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.protege.ProtegeRepository;


/**
 * this util will use built in MySQLDump and MySQL commands to backup a given table
 * @author tseytlin
 */
public class ODIEDataFileCreator {
	public static final String PROPERTY_EVENT = "ODIE_DATA_FILE_CREATOR";
	public static final String LUCENE_FINDER = "luceneFinder";
	public static final String LUCENE_ONTOLOGY_SEARCH = "luceneOntologySearch";
	public static final String PROTEGE_DATABASE = "protegeDatabase";
	public static final String METADATA = "metadata.txt";
	public static final String MYSQLDUMP = isWindows()?"mysqldump.exe":"mysqldump";
	
	
	private File searchIndexDirectory, indexFinderDirectory,outputDirectory,outputFile;
	private URI ontologyURI;
	private ProtegeRepository repository;
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	
	/**
	 * add property change listener to monitor progress
	 * @param l
	 */
	public void addPropertyChangeListener(PropertyChangeListener l){
		pcs.addPropertyChangeListener(l);
	}
	
	/**
	 * add property change listener to monitor progress
	 * @param l
	 */
	public void removePropertyChangeListener(PropertyChangeListener l){
		pcs.removePropertyChangeListener(l);
	}
	
	public ProtegeRepository getRepository() {
		return repository;
	}

	public void setRepository(ProtegeRepository repository) {
		this.repository = repository;
	}
	public File getOutputFile() {
		return outputFile;
	}

	
	public File getSearchIndexDirectory() {
		return searchIndexDirectory;
	}



	public void setSearchIndexDirectory(File searchIndexDirectory) {
		this.searchIndexDirectory = searchIndexDirectory;
	}



	public File getIndexFinderDirectory() {
		return indexFinderDirectory;
	}



	public void setIndexFinderDirectory(File indexFinderDirectory) {
		this.indexFinderDirectory = indexFinderDirectory;
	}



	public File getOutputDirectory() {
		return outputDirectory;
	}



	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}



	public URI getOntologyURI() {
		return ontologyURI;
	}

	public void setOntologyURI(URI ontologyURI) {
		this.ontologyURI = ontologyURI;
	}
	
	
	private void log(String s){
		pcs.firePropertyChange(PROPERTY_EVENT,null,s);
	}
	
	/**
	 * create data file
	 */
	public void save() throws IOException{
		// get ontology and its meta information
		IOntology ont = repository.getOntology(ontologyURI);
		
		// create ontology name
		String name = createName(ont.getName());
		
		log("saving ontology "+name+" ...");
		
		// create output directory
		File output = new File(outputDirectory,name);
		if(!output.exists())
			if(!output.mkdirs())
				throw new IOException("Unable to create directories "+output);
		
		// write out ontology meta data
		log("generating meta file ...");
		createMetaFile(ont,new File(output,METADATA));
		
		// copy directories
		log("copying index finder index ...");		
		copy(indexFinderDirectory,new File(output,LUCENE_FINDER));
		
		log("copying ontology search index ...");
		copy(searchIndexDirectory,new File(output,LUCENE_ONTOLOGY_SEARCH));
		
		// do database dump
		log("creating ontology sql dump ...");
		dump(ont,new File(output,PROTEGE_DATABASE));
		
		// zip the directory
		log("zipping the output folder ...");
		zip(output);
		
		// remove the directory
		log("removing temporary folder ...");
		delete(output);
		
		log("odie data file saved at "+outputFile.getAbsolutePath());
	}
	
	/**
	 * is this running on windows
	 * @return
	 */
	public static boolean isWindows(){
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}
	
	/**
	 * create data file
	 */
	public void load() throws IOException{
		// do some sanity checking
		
		//TODO:
	}

	
	
	/**
	 * copy either file or directory
	 * @param source
	 * @param target
	 */
	public static void copy(File source, File target) throws IOException{
		// decide which copy we do
		if(source.isDirectory()){
			// if target exist and not the same name as source, then target is target dir +name of source directory
			if(target.isDirectory() && !source.getName().equals(target.getName())){
				copy(source,new File(target,source.getName()));
			}else if(target.isFile()) {
				throw new IOException("Cannot copy directory "+source.getAbsolutePath()+" to a file "+target.getAbsolutePath());
			}else if(!target.exists()) {
				// create target dir
				if(!target.exists())
					if(!target.mkdirs())
						throw new IOException("Cannot create output directory "+target.getAbsolutePath());
					
				// do recurseive copy
				for(File file: source.listFiles()){
					copy(file,new File(target,file.getName()));
				}
			}
		}else if(source.isFile()){
			// just copy file to directory
			if(target.isDirectory()){
				copy(source,new File(target,source.getName()));
			}else{
				// copied from
				//http://stackoverflow.com/questions/106770/standard-concise-way-to-copy-a-file-in-java
				
				// either file exists (then we overwrite
				// or not, it is irrelevent at this point
				FileChannel sourceChannel = null;
				FileChannel destinationChannel = null;
				try {
					sourceChannel = new FileInputStream(source).getChannel();
					destinationChannel = new FileOutputStream(target).getChannel();
					destinationChannel.transferFrom(sourceChannel,0,sourceChannel.size());
				}finally {
					if(sourceChannel != null)
						sourceChannel.close();
					if(destinationChannel != null) 
						destinationChannel.close();
				}
			}
		}
	}
	
	
	/**
	 * create meta file
	 * @param ont
	 * @param file
	 */
	private void createMetaFile(IOntology ont, File file) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		
		// write out meta information
		writer.write(ODIEConstants.PROPERTY_ONT_NAME + "="+ont.getName()+"\n");
		writer.write(ODIEConstants.PROPERTY_ONT_LOCATION + "="+ont.getLocation()+"\n");
		writer.write(ODIEConstants.PROPERTY_ONT_URI + "="+ont.getURI()+"\n");
		writer.write(ODIEConstants.PROPERTY_ONT_VER + "="+ont.getVersion()+"\n");
		
		// remove newlines
		writer.write(ODIEConstants.PROPERTY_ONT_DESC + "="+ont.getDescription().replaceAll("\n"," "));

		// close
		writer.close();
	}
	
	/**
	 * zip directory
	 * @param dir
	 */
	private void zip(File sourceDir, File zipFile) throws IOException{
		// copied from
		//http://www.java-examples.com/create-zip-file-directory-recursively-using-zipoutputstream-example
		
		//create object of FileOutputStream
		FileOutputStream fout = new FileOutputStream(zipFile);
	
		//create object of ZipOutputStream from FileOutputStream
		ZipOutputStream zout = new ZipOutputStream(fout);
	
		// zip directory
		zip(sourceDir,zout);
		
		//close the ZipOutputStream
		zout.close();
		
		// set output file location
		outputFile = zipFile;
	}
	
	/**
	 * zip directory to default zip file location
	 * @param dir
	 */
	private void zip(File sourceDir) throws IOException{
		zip(sourceDir,new File(sourceDir.getParentFile(),sourceDir.getName()+".odie"));
	}
	
	/**
	 * zip directory
	 * @param dir
	 */
	private void zip(File sourceDir, ZipOutputStream zout) throws IOException{
		zip(sourceDir,zout,"");
	}
	
	/**
	 * zip directory
	 * @param dir
	 */
	private void zip(File sourceDir, ZipOutputStream zout,String prefix) throws IOException{
		//calculate prefix
		prefix = prefix+sourceDir.getName()+"/";
		
		// iterate over files
		for(File file: sourceDir.listFiles()){
			if(file.isDirectory()){
				zip(file,zout,prefix);
			}else{
				// copied from
				//http://www.java-examples.com/create-zip-file-directory-recursively-using-zipoutputstream-example
				
				//create byte buffer
				byte[] buffer = new byte[1024];
				
				//create object of FileInputStream
				FileInputStream fin = new FileInputStream(file);
				zout.putNextEntry(new ZipEntry(prefix+file.getName()));
				
				/*
				 * After creating entry in the zip file, actually
			     * write the file.
			     */
				int length;
				while((length = fin.read(buffer)) > 0){
					zout.write(buffer, 0, length);
				}
				/*
				 * After writing the file to ZipOutputStream, use
				 *
				 * void closeEntry() method of ZipOutputStream class to
				 * close the current entry and position the stream to
				 * write the next entry.
				 */
				zout.closeEntry();
				
				//close the InputStream
				fin.close();
			}
		}
	}	
	
	/**
	 * delete directory
	 * @param dir
	 */
	private void delete(File dir) throws IOException{
		// remove content of the directory
		if(dir.isDirectory()){
			for(File file: dir.listFiles()){
				delete(file);
			}
		}
		// delete file/directory
		if(!dir.delete()){
			throw new IOException("Could not remove "+dir.getAbsolutePath());
		}
	}
	
	/**
	 * do sql dump from already given parameters
	 * @param dir
	 */
	private void dump(IOntology ont, File dir) throws IOException{
		// lets get the output directory ready
		if(!dir.exists())
			if(!dir.mkdirs())
				throw new IOException("Could not create "+dir);
		
		// now we need to execute command to do a dump
		String host = getRepository().getDatabaseURL();
		String user  = getRepository().getDatabaseUsername();
		String pass  = getRepository().getDatabasePassword();
		String table = ont.getLocation();
		String database = "";
		String port = "3306";
		File outFile = new File(dir,table+".sql");
		
		// extract host and port
		int s = host.indexOf("mysql://");
		if(s > -1){
			// detect host name
			host = host.substring(s+"mysql://".length());
			
			// detect port and end
			int e = host.indexOf("/");
			if(e > -1){
				database = host.substring(e+1);
				host = host.substring(0,e);
			}
			int p = host.indexOf(":");
			if(p > -1){
				port = host.substring(p+1);
				host = host.substring(0,p);
			}
		}
		
		//mysqldump -h10.144.36.84 -uodieuser --port=3306 -podiepass odie ontology_nif
		List<String> commands = new ArrayList<String>();
		commands.add(MYSQLDUMP);
		commands.add("-h"+host);
		commands.add("--port="+port);
		commands.add("-u"+user);
		commands.add("-p"+pass);
		commands.add(database);
		commands.add(table);
		
		// do a dump
		String err = execute(dir,new FileOutputStream(outFile),commands.toArray(new String [0]));
		if(err.trim().length() > 0){
			throw new IOException(err.trim());
		}
	}
	
	/**
	 * trim list of arguments
	 * @param args
	 * @return
	 */
	private String [] trim(String [] args){
		List<String> list = new ArrayList<String>();
		for(String a: args){
			if(a != null && a.length() > 0)
				list.add(a);
		}
		return list.toArray(new String [0]);
	}
	
	
	/**
	 * execute a program w/ arguments 
	 * @param args
	 * @return output of this program
	 */
	private String execute(File workingDirectory,OutputStream output, String ... args) throws IOException {
		// trim list of arguments
		args = trim(args);
		
		// create a process builder
		ProcessBuilder pb = new ProcessBuilder(args);
		pb.directory(workingDirectory);
		
		// start a process
		try{
			Process process = pb.start();
			StreamGobbler out = null,err = null;
			
			// catch stdout/stderr
			InputStream in = process.getInputStream();
			if(output == null){
				pb.redirectErrorStream(true);
				out = new StreamGobbler(in);
			}else{
				out = new StreamGobbler(in,output);
				err = new StreamGobbler(process.getErrorStream());
				err.start();
			}
			
			// start output catchers
			out.start();
			
			// wait for process to end
			process.waitFor();
			
			// wait for outputs to catch up
			while(out.isAlive() || (err != null && err.isAlive())){
				Thread.sleep(20);
			}
			
			// now return output
			return (err != null)?err.getOutput():out.getOutput();
		}catch(Exception ex){
			throw new IOException("Could not execute "+args[0]+" command",ex);
		}
	}
	
	
	
	/**
	 * create file friendly name
	 * @param args
	 * @throws Exception
	 */
	private String createName(String name){
		int x = name.lastIndexOf('.');
		
		if(x > -1)
			name = name.substring(0,x);
		
		// create file friendly name
		name = name.replaceAll("\\W+"," ");
		
		return name;
	}
	
	
	/**
	 * This class reads stdout/stderr from the process
	 * it is taken from JavaWorld article:
	 * http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps_p.html
	 */
	private class StreamGobbler extends Thread {
		private InputStream is;
		private StringBuffer output;
		private OutputStream out;
		
		public StreamGobbler( InputStream is ) {
			this.is = is;
			output = new StringBuffer();
		}
		
		public StreamGobbler( InputStream is, OutputStream out) {
			this.is = is;
			this.out = out;
		}
		
		public void run() {
			try	{
				BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
				String line = null;
				while ( ( line = br.readLine() ) != null ) {
					if(out != null)
						out.write((line+"\n").getBytes());
					else if(output != null)
						output.append( line + "\n" );
				}
				br.close();
				if(out != null)
					out.close();
			} catch ( IOException ioe ) {
				ioe.printStackTrace();
			}
		}
		
		/**
		 * get output
		 * @return
		 */
		public String getOutput(){
			return (output != null)?output.toString():"";
		}
	}
	
	
	public static void main(String [] args) throws Exception {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://10.144.36.84:3306/odie";
		String user = "odieuser";
		String pass = "odiepass";
		String table = "odie_lr";
		String dir = System.getProperty("user.dir")+File.separator+"Temp";
		String ontology = "http://ontology.neuinfo.org/NIF/nif.owl";
		
		
		ProtegeRepository repository = new ProtegeRepository(driver, url, user, pass, table, dir);
		ODIEDataFileCreator creator = new ODIEDataFileCreator();
		creator.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				System.out.println(e.getNewValue());
			}
		});
		
		creator.setRepository(repository);
		creator.setOntologyURI(URI.create(ontology));
		creator.setIndexFinderDirectory(new File("/home/tseytlin/Download/odiezip/luceneFinder"));
		creator.setSearchIndexDirectory(new File("/home/tseytlin/Download/odiezip/luceneOntologySearch"));
		creator.setOutputDirectory(new File("/home/tseytlin/Output/ODIE/"));
		
		// now do some work
		creator.save();
		
		
	}
	
}

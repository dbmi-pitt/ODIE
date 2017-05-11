package edu.pitt.dbmi.odie.lexicalizer.lucenefinder;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

import edu.pitt.dbmi.odie.middletier.Configuration;
import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.IOntologyException;
import edu.pitt.ontology.IRepository;
import edu.pitt.ontology.IResource;
import edu.pitt.ontology.protege.ProtegeRepository;

public class ODIE_LuceneNerBuilder {
	
	private static final Logger logger = Logger
	.getLogger(ODIE_LuceneNerBuilder.class);
	
	private String dbUrl;
	private String username;
	private String password;
	private String driverName;
	private ODIE_LuceneNerFoundryBuilder foundryBuilder = null;
	private Connection connection;
	
	private Configuration configuration = null ;
	
	private String indexLocation = null ;
	
	private Configuration cfg = null ;
	
	public static void main(String[] args) {
		
//	    -targetIndex "C:\\index_radlex_200"
//	    -dbDriver "com.mysql.jdbc.Driver"
//	    -dbUrl "jdbc:mysql://localhost:3306/kai"
//	    -dbUser "caties"
//	    -dbPassword "caties"
//	    -repositoryTableName "odie_lr"
//      -ontologyName "ontology_radlex_200"
//	    -temporaryDirectory "./ontologies"
		
		Options options = new Options();
		options.addOption("targetIndex", true, "Location of the new lucene index.");
		options.addOption("targetIndexId", true, "Numeric name of the lowest level lucence index directory (built dynamically).");
		options.addOption("dbDriver", true, "Repository database driver");
		options.addOption("dbUrl", true, "Repository database url");
		options.addOption("dbUser", true, "Repository database user");
		options.addOption("dbPassword", true, "Repository database password");
		options.addOption("repositoryTableName", true, "Repository table name");
		options.addOption("ontologyName", true, "Repository ontology from which to build") ;
		options.addOption("temporaryDirectory", true, "Temporary directory name");
		
		try {
			
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse( options, args);
			
			Configuration conf = new Configuration();
			if (       !cmd.hasOption("targetIndex") 
					|| !cmd.hasOption("targetIndexId") 
					|| !cmd.hasOption("dbDriver") 
					|| !cmd.hasOption("dbUrl") 
					|| !cmd.hasOption("dbUser")
					|| !cmd.hasOption("dbPassword")
					|| !cmd.hasOption("repositoryTableName")
					|| !cmd.hasOption("ontologyName")
					|| !cmd.hasOption("temporaryDirectory")	
			) {
				usage(options) ;
			}
			else { 
				conf.setLuceneIndexDirectory(cmd.getOptionValue("targetIndex")) ;
				conf.setDatabaseDriver(cmd.getOptionValue("dbDriver"));
				conf.setDatabaseURL(cmd.getOptionValue("dbUrl"));
				conf.setUsername(cmd.getOptionValue("dbUser"));
				conf.setPassword(cmd.getOptionValue("dbPassword"));
				conf.setRepositoryTableName(cmd.getOptionValue("repositoryTableName"));
				conf.setTemporaryDirectory(cmd.getOptionValue("temporaryDirectory"));
				
				ODIE_LuceneNerBuilder luceneNerBuilder = new ODIE_LuceneNerBuilder(conf, "");
				luceneNerBuilder.setConfiguration(conf) ;
			
				IResource[] ontologies = getTestOntologies(conf,cmd.getOptionValue("ontologyName"));
				List<IResource> ontologyList = Arrays.asList(ontologies);
//				displayOntologies(ontologyList);
//				ontologyList = ontologyList.subList(0, 0) ;
				String lexicalSetLuceneIndexDirectoryName = buildLuceneFinderIndexDirectoryName(conf, cmd.getOptionValue("targetIndexId")) ;
				luceneNerBuilder.buildIndexFinderTables(lexicalSetLuceneIndexDirectoryName, ontologyList);
			}
			

		} catch (ParseException e) {
			System.err.println(e.getMessage()) ;
			usage(options) ;
		}
		
		
	}
	
	private static void usage(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		String command = "java " + ODIE_LuceneNerBuilder.class.getName() ;
		formatter.printHelp( command, options );
	}
	
	private static String buildLuceneFinderIndexDirectoryName(Configuration config, String id) {
		StringBuffer sb = new StringBuffer(config.getLuceneIndexDirectory()) ;
		sb.append(File.separator) ;
		sb.append(edu.pitt.dbmi.odie.ODIEConstants.LUCENEFINDER_DIRECTORY) ;
		sb.append(File.separator) ;
		sb.append(id) ;
		String lexicalSetLuceneIndexDirectoryName =  sb.toString();
		return lexicalSetLuceneIndexDirectoryName ;
	}
	
	public ODIE_LuceneNerBuilder(MiddleTier middleTier, String databaseName) {
		this() ;
		
		this.cfg = middleTier.getConfiguration() ;

		this.dbUrl = cfg.getDatabaseURLWithoutSchema() + databaseName;
		this.driverName = cfg.getDatabaseDriver();
		this.username = cfg.getUsername();
		this.password = cfg.getPassword();		
	}
	
	public ODIE_LuceneNerBuilder(Configuration cfg, String databaseName) {
		foundryBuilder = new ODIE_LuceneNerFoundryBuilder();
		this.dbUrl = cfg.getDatabaseURLWithoutSchema() + databaseName;
		this.driverName = cfg.getDatabaseDriver();
		this.username = cfg.getUsername();
		this.password = cfg.getPassword();		
	}
	
	public ODIE_LuceneNerBuilder() {
		foundryBuilder = new ODIE_LuceneNerFoundryBuilder();
	}

//	public boolean buildIndexFinderTables(List<IResource> resources) {
//		openDatabaseConnection();
//		String lexicalSetLuceneIndexDirectoryName = buildLuceneFinderIndexDirectoryName(this.cfg, cmd.getOptionValue("targetIndexId")) ;
//		foundryBuilder.storeFoundryAsIndexFinderTables(this.cfg, resources);
//		closeDatabaseConnection();
//		displayOntologies(resources);
//		return true;
//	}
	
	private static IOntology[] getTestOntologies(Configuration conf, String ontologyName) {
		IOntology[] ontologies = null;
		final IOntology[] result = new IOntology[1] ;
		try {
			

			IRepository repository = new ProtegeRepository(conf
					.getDatabaseDriver(), conf.getDatabaseURL(), conf
					.getUsername(), conf.getPassword(), conf
					.getRepositoryTableName(), conf.getTemporaryDirectory());

			ontologies = repository.getOntologies();
			
			System.out.println("Searching for ontology named " + ontologyName) ;
			for (IOntology ontology : ontologies) {
				System.out.println("Entry is " + ontology.getName()) ;
				if (ontology.getName().equals(ontologyName)) {
					System.out.println("Found ontology named " + ontologyName) ;
					result[0] = ontology ;
					break ;
				}
			}
		} catch (IOntologyException e) {
			e.printStackTrace();
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private static void displayOntologies(List<IResource> resourceList) {
		for (IResource resource : resourceList) {
			logger.debug(String.valueOf(resource.getName()));
			logger.debug(String.valueOf(resource.getDescription()));
			if (resource instanceof IOntology) {
				IOntology ontology = (IOntology) resource ;
				for (Iterator<IClass> it = ontology.getAllClasses(); it.hasNext();) {
					IClass c = (IClass) it.next();
					logger.debug(c);
				}
			}
		}
	}

	public boolean buildIndexFinderTables(String lexicalSetLuceneIndexDirectoryName, List<IResource> ontologyList) {
		foundryBuilder.storeFoundryAsLuceneFinderIndex(lexicalSetLuceneIndexDirectoryName, ontologyList);
//		displayOntologies(ontologyList);
		return true;
	}

	public void openDatabaseConnection() {
		try {
			Class.forName(getDriverName()).newInstance();
			String urlPath = getDbUrl() ;
			urlPath += "?autoReconnect=true" ;
			this.connection = DriverManager.getConnection(urlPath, getUsername(),
					getPassword());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void closeDatabaseConnection() {
		try {
			this.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		foundryBuilder.addPropertyChangeListener(listener);
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	
	public String getIndexLocation() {
		return indexLocation;
	}

	public void setIndexLocation(String indexLocation) {
		this.indexLocation = indexLocation;
	}
	
	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration conf) {
		this.configuration = conf;
		setDriverName(conf.getDatabaseDriver());
		setDbUrl(conf.getDatabaseURL());
		setUsername(conf.getUsername());
		setPassword(conf.getPassword());
		
	}
	
	
}

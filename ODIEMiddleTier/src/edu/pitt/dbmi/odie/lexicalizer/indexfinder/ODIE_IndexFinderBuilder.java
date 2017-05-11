package edu.pitt.dbmi.odie.lexicalizer.indexfinder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import edu.pitt.dbmi.odie.middletier.Configuration;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.IOntologyException;
import edu.pitt.ontology.IRepository;
import edu.pitt.ontology.IResource;
import edu.pitt.ontology.protege.ProtegeRepository;

public class ODIE_IndexFinderBuilder {
	
	private static final Logger logger = Logger
	.getLogger(ODIE_IndexFinderBuilder.class);
	
	private String dbUrl;
	private String username;
	private String password;
	private String driverName;
	private ODIE_IndexFinderFoundryBuilder foundryBuilder = null;
	private Connection connection;
	
	public static void main(String[] args) {
		ODIE_IndexFinderBuilder indexFinderBuilder = new ODIE_IndexFinderBuilder();
		indexFinderBuilder.setDriverName("com.mysql.jdbc.Driver");
		indexFinderBuilder.setDbUrl("jdbc:mysql://localhost:3306/kai");
		indexFinderBuilder.setUsername("caties");
		indexFinderBuilder.setPassword("caties");
		IResource[] ontologies = getTestOntologies();
		List<IResource> ontologyList = Arrays.asList(ontologies);
//		displayOntologies(ontologyList);
//		ontologyList = ontologyList.subList(0, 0) ;
		indexFinderBuilder.buildIndexFinderTables(ontologyList);
	}
	
	private static IOntology[] getTestOntologies() {
		IOntology[] ontologies = null;
		final IOntology[] result = new IOntology[1] ;
		try {
			Configuration conf = new Configuration();
			conf.setDatabaseDriver("com.mysql.jdbc.Driver");
			conf.setDatabaseURL("jdbc:mysql://localhost:3306/kai");
			conf.setUsername("caties");
			conf.setPassword("caties");
			conf.setRepositoryTableName("odie_lr");
			conf.setTemporaryDirectory("./ontologies");

			IRepository repository = new ProtegeRepository(conf
					.getDatabaseDriver(), conf.getDatabaseURL(), conf
					.getUsername(), conf.getPassword(), conf
					.getRepositoryTableName(), conf.getTemporaryDirectory());

			ontologies = repository.getOntologies();
			
			
			for (IOntology ontology : ontologies) {
				if (ontology.getName().toLowerCase().indexOf("gap")!=-1) {
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
			logger.debug(resource.getName());
			logger.debug(resource.getDescription());
			if (resource instanceof IOntology) {
				IOntology ontology = (IOntology) resource ;
				for (Iterator<IClass> it = ontology.getAllClasses(); it.hasNext();) {
					IClass c = (IClass) it.next();
					logger.debug(c);
				}
			}
		}
	}

	public boolean buildIndexFinderTables(List<IResource> ontologyList) {
		foundryBuilder = new ODIE_IndexFinderFoundryBuilder();
		openDatabaseConnection();
		foundryBuilder.setConn(connection);
		foundryBuilder.storeFoundryAsIndexFinderTables(ontologyList);
		closeDatabaseConnection();
		displayOntologies(ontologyList);
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
	
	
}

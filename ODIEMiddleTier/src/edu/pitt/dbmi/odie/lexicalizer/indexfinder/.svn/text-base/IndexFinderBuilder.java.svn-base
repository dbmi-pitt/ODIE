package edu.pitt.dbmi.odie.lexicalizer.indexfinder;

import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import edu.pitt.dbmi.odie.middletier.Configuration;
import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.IResource;

public class IndexFinderBuilder {
	private String dbUrl;
	private String username;
	private String password;
	private String driverName;
	private ODIE_IndexFinderFoundryBuilder foundryBuilder;
	private Connection connection;

	public IndexFinderBuilder(MiddleTier middleTier, String databaseName) {
		Configuration cfg = middleTier.getConfiguration();

		this.dbUrl = cfg.getDatabaseURLWithoutSchema() + databaseName;
		this.driverName = cfg.getDatabaseDriver();
		this.username = cfg.getUsername();
		this.password = cfg.getPassword();

		foundryBuilder = new ODIE_IndexFinderFoundryBuilder();
	}

	public boolean buildIndexFinderTables(List<IResource> resources) {
		openDatabaseConnection();
		foundryBuilder.setConn(connection);
		foundryBuilder.storeFoundryAsIndexFinderTables(resources);
		closeDatabaseConnection();
		displayOntologies(resources);
		return true;
	}

	@SuppressWarnings("unchecked")
	private static void displayOntologies(List<IResource> resources) {
		for (IResource resource : resources) {
			System.out.println(resource.getName());
			System.out.println(resource.getDescription());
			if (resource instanceof IOntology) {
				IOntology ontology = (IOntology) resource ;
				for (Iterator<IClass> it = ontology.getAllClasses(); it.hasNext();) {
					IClass c = (IClass) it.next();
					System.out.println(c);
				}
			}
			
		}
	}

	public void openDatabaseConnection() {
		try {
			Class.forName(getDriverName()).newInstance();
			String urlPath = getDbUrl()+ "?autoReconnect=true" ;
			this.connection = DriverManager.getConnection(getDbUrl(),
					getUsername(), getPassword());
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

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		foundryBuilder.removePropertyChangeListener(listener);
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

package edu.pitt.dbmi.odie.middletier;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;



public class MiddleTierJDBC{
	
	private Connection conn;

	Logger logger = Logger.getLogger(this.getClass());
	
	public MiddleTierJDBC(Configuration conf) throws Exception {
		init(conf);
	}

	
	public void init(Configuration config) throws Exception {
		initDBConnection(config.getDatabaseDriver(), 
						 config.getDatabaseURL(), 
						 config.getUsername(), 
						 config.getPassword());
	}

	private void initDBConnection(String dbDriver, String dbURL, String username, String password)
			throws Exception {
		logger.info("Initializing database connection");
		Class.forName(dbDriver).newInstance();
		conn = DriverManager.getConnection(dbURL, username, password);
	}
	
	String SQL_CREATE_SCHEMA = "CREATE SCHEMA IF NOT EXISTS ";
	
	public void createSchema(String schemaName) throws SQLException {
		Statement s = conn.createStatement();
		s.executeUpdate(SQL_CREATE_SCHEMA+schemaName);
		s.close();
	}
	
	

	public void dispose() {
		try {
			if (conn != null)
				conn.close();
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
	}


	public ResultSet executeQuery(String sql) throws SQLException {
		Statement s = conn.createStatement();
		ResultSet rs = s.executeQuery(sql);
		s.close();
		return rs;
	}
	
	public int executeUpdate(String sql) throws SQLException {
		Statement s = conn.createStatement();
		int retVal = s.executeUpdate(sql);
		s.close();
		return retVal;
	}
}

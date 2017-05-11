package edu.pitt.dbmi.odie.uima.lsp.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class ODIE_Term {
	
	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_Term.class);
	
	private static Connection conn ;
	
	private Long id ;
	private String term ;
	
	public static void main(String[] args) {
		openDatabaseConnection();
		dropTerm();
		createTerm();
		ODIE_Term termOne = ODIE_Term.fetchOrCreateTerm("prostate") ;
		ODIE_Term termTwo = ODIE_Term.fetchOrCreateTerm("carcinoma") ;
		System.out.println(termOne) ;
		System.out.println(termTwo) ;
		closeDatabaseConnection();
	}
	
	public static void openDatabaseConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/odie_090109",
					"caties", System.getProperty("odie.db.password"));
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

	public static void closeDatabaseConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ODIE_Term() {
	}
	
	public static void dropTerm() {
		String dropTableString = "drop table if exists lsp_term" ;
		Statement statement;
		try {
			statement = conn.createStatement();
			statement.executeUpdate(dropTableString) ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void createTerm() {
		String createOrReplaceString = "" ;
		createOrReplaceString += "CREATE TABLE IF NOT EXISTS  lsp_term (\n" ;
		createOrReplaceString += "id bigint(20) NOT NULL AUTO_INCREMENT,\n" ;
		createOrReplaceString += "term text,\n" ;
		createOrReplaceString += "PRIMARY KEY (id),\n" ;
		createOrReplaceString += "UNIQUE KEY id (id)\n" ;
		createOrReplaceString += ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1\n" ;
		Statement statement;
		try {
			statement = conn.createStatement();
			statement.executeUpdate(createOrReplaceString) ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static ODIE_Term fetchOrCreateTerm(String term) {
		ODIE_Term result = fetchTerm(term) ;
		if (result.getId()==null) {
			insertTerm(term) ;
			result = fetchTerm(term) ;
		}
		return result ;
	}
	
	
	public static ODIE_Term insertTerm(String term) {
		ODIE_Term result = new ODIE_Term() ;
		try {
			PreparedStatement pStmt = conn.prepareStatement("insert into lsp_term (term) values (?)") ;
			pStmt.setString(1, term) ;
			pStmt.executeUpdate() ;
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result ;
	}
	
	public static ODIE_Term fetchTerm(String term) {
		ODIE_Term result = new ODIE_Term() ;
		try {
			PreparedStatement pStmt = conn.prepareStatement("select * from lsp_term where term = ?") ;
			pStmt.setString(1, term) ;
			ResultSet rs = pStmt.executeQuery() ;
			if (rs.next()) {
				result.setId(rs.getLong("id")) ;
				result.setTerm(rs.getString("term")) ;
			}
			rs.close();
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result ;
	}

	public static Connection getConn() {
		return conn;
	}

	public static void setConn(Connection conn) {
		ODIE_Term.conn = conn;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}
	
	public String toString() {
		String result = "" ;
		result += getId() + ") " + getTerm() ;
		return result ;
		
	}

}

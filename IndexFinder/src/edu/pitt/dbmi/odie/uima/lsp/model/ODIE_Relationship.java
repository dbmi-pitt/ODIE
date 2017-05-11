package edu.pitt.dbmi.odie.uima.lsp.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class ODIE_Relationship {
	
	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_Relationship.class);
	
	private static Connection conn ;
	
	public static final String RELATIONSHIP_HYPERNOMY = "hypernomy" ;
	public static final String RELATIONSHIP_HYPONOMY = "hyponomy" ;
	public static final String RELATIONSHIP_SYNONYMY = "synonymy" ;
	public static final String RELATIONSHIP_MERONYMY = "meronymy" ;
	
	private Long id ;
	private String relationship ;
	
	public static void main(String[] args) {
		openDatabaseConnection();
		dropRelationship();
		createRelationship();
		generateDefaultRelationships() ;
		ODIE_Relationship synonymy = fetchRelationship(RELATIONSHIP_SYNONYMY) ;
		logger.debug("Got " + synonymy) ;
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

	public void destroy() {
		if(conn==null)
			return ;
		
		try {
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		conn = null ;
		
	}

	public ODIE_Relationship() {
	}
	
	public static void dropRelationship() {
		String dropTableString = "drop table if exists lsp_relationship" ;
		Statement statement;
		try {
			statement = conn.createStatement();
			statement.executeUpdate(dropTableString) ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void createRelationship() {
		String createOrReplaceString = "" ;
		createOrReplaceString += "CREATE TABLE IF NOT EXISTS lsp_relationship (" ;
		createOrReplaceString += "id bigint(20) NOT NULL AUTO_INCREMENT," ;
		createOrReplaceString += "relationship varchar(50) NOT NULL," ;
		createOrReplaceString += "PRIMARY KEY (id)," ;
		createOrReplaceString += "UNIQUE KEY id (id)," ;
		createOrReplaceString += "UNIQUE KEY relationship (relationship)" ;
		createOrReplaceString += ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1" ;
		Statement statement;
		try {
			statement = conn.createStatement();
			statement.executeUpdate(createOrReplaceString) ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void generateDefaultRelationships() {
		fetchOrCreateRelationship(RELATIONSHIP_HYPERNOMY) ;
		fetchOrCreateRelationship(RELATIONSHIP_HYPONOMY) ;
		fetchOrCreateRelationship(RELATIONSHIP_SYNONYMY) ;
		fetchOrCreateRelationship(RELATIONSHIP_MERONYMY) ;
	}
	
	public static ODIE_Relationship fetchOrCreateRelationship(String relationship) {
		ODIE_Relationship result = fetchRelationship(relationship) ;
		if (result.getId()==null) {
			insertRelationship(relationship) ;
			result = fetchRelationship(relationship) ;
		}
		return result ;
	}
	
	public static ODIE_Relationship insertRelationship(String relationship) {
		ODIE_Relationship result = new ODIE_Relationship() ;
		try {
			PreparedStatement pStmt = conn.prepareStatement("insert into lsp_relationship (relationship) values (?)") ;
			pStmt.setString(1, relationship) ;
			pStmt.executeUpdate() ;
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result ;
	}
	
	public static ODIE_Relationship fetchRelationship(String relationship) { 
		ODIE_Relationship result = new ODIE_Relationship() ;
		try {
			PreparedStatement pStmt = conn.prepareStatement("select * from lsp_relationship where relationship = ?") ;
			pStmt.setString(1, relationship) ;
			ResultSet rs = pStmt.executeQuery() ;
			if (rs.next()) {
				result.setId(rs.getLong("id")) ;
				result.setRelationship(rs.getString("relationship")) ;
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
		ODIE_Relationship.conn = conn;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String toString() {
		String result = "" ;
		result += getId() + ") " + getRelationship()   ;
		return result ;
	}


	public String getRelationship() {
		return this.relationship ;
	}
	
	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

}


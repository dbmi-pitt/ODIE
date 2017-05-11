package edu.pitt.dbmi.odie.uima.lsp.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class ODIE_Relation {
	
	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_Relation.class);

	private static Connection conn;

	private Long id;
	private Long termOneId;
	private Long termTwoId;
	private Long relationshipId;
	private Long frequency;
	
	public static void main(String[] args) {
		openDatabaseConnection();
		dropRelation();
		createRelation();
		ODIE_Term termOne = new ODIE_Term() ;
		termOne.setId(1L) ;
		termOne.setTerm("hello") ;
		ODIE_Term termTwo = new ODIE_Term() ;
		termTwo.setId(2L) ;
		termTwo.setTerm("two") ;
		ODIE_Relationship relationship = new ODIE_Relationship() ;
		relationship.setId(3L) ;
		relationship.setRelationship("synonymy") ;
		ODIE_Relation relation = ODIE_Relation.fetchOrCreateRelation(termOne, termTwo, relationship) ;
		System.out.println(relation) ;
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

	public ODIE_Relation() {
	}

	public static void dropRelation() {
		String dropTableString = "drop table if exists lsp_relation";
		Statement statement;
		try {
			statement = conn.createStatement();
			statement.executeUpdate(dropTableString);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void createRelation() {
		String createOrReplaceString = "";
		createOrReplaceString += "CREATE TABLE IF NOT EXISTS lsp_relation (\n";
		createOrReplaceString += "id bigint(20) NOT NULL AUTO_INCREMENT,\n";
		createOrReplaceString += "term_one_id bigint(20) NOT NULL,\n";
		createOrReplaceString += "term_two_id bigint(20) NOT NULL,\n";
		createOrReplaceString += "relationship_id bigint(20) NOT NULL,\n";
		createOrReplaceString += "frequency bigint(20) NOT NULL,\n";
		createOrReplaceString += "PRIMARY KEY (id),\n";
		createOrReplaceString += "KEY relationship_id (relationship_id),\n";
		createOrReplaceString += "KEY term_one_id (term_one_id),\n";
		createOrReplaceString += "KEY term_two_id (term_two_id)\n";
//		createOrReplaceString += "CONSTRAINT FK_lsp_relation_lsp_relationSHIP FOREIGN KEY (relationship_id) REFERENCES lsp_relationship (id),\n";
//		createOrReplaceString += "CONSTRAINT FK_lsp_relation_ODIE_TERM_ONE FOREIGN KEY (term_one_id) REFERENCES lsp_term (id),\n";
//		createOrReplaceString += "CONSTRAINT FK_lsp_relation_ODIE_TERM_TWO FOREIGN KEY (term_two_id) REFERENCES lsp_term (id)\n";
		createOrReplaceString += ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1\n";

//		System.out.println(createOrReplaceString);
		Statement statement;
		try {
			statement = conn.createStatement();
			statement.executeUpdate(createOrReplaceString);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static ODIE_Relation fetchOrCreateRelation(ODIE_Term termOne,
			ODIE_Term termTwo, ODIE_Relationship relationship) {
		ODIE_Relation result = fetchRelation(termOne, termTwo, relationship);
		if (result.getId() == null) {
			insertRelation(termOne, termTwo, relationship);
			result = fetchRelation(termOne, termTwo, relationship);
		}
		return result;
	}

	public static ODIE_Relation insertRelation(ODIE_Term termOne,
			ODIE_Term termTwo, ODIE_Relationship relationship) {
		ODIE_Relation result = new ODIE_Relation();
		try {
			PreparedStatement pStmt = conn
					.prepareStatement("insert into lsp_relation (term_one_id, term_two_id, relationship_id, frequency) values (?, ?, ?, ?)");
			pStmt.setLong(1, termOne.getId());
			pStmt.setLong(2, termTwo.getId());
			pStmt.setLong(3, relationship.getId());
			pStmt.setLong(4, new Long(0L));
			pStmt.executeUpdate();
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static ODIE_Relation fetchRelation(ODIE_Term termOne,
			ODIE_Term termTwo, ODIE_Relationship relationship) {
		ODIE_Relation result = new ODIE_Relation();
		try {
			PreparedStatement pStmt = conn
					.prepareStatement("select * from lsp_relation where term_one_id = ? and term_two_id = ? and relationship_id = ?");
			pStmt.setLong(1, termOne.getId());
			pStmt.setLong(2, termTwo.getId());
			pStmt.setLong(3, relationship.getId());
			ResultSet rs = pStmt.executeQuery();
			if (rs.next()) {
				result.setId(rs.getLong("id"));
				result.setTermOneId(rs.getLong("term_one_id"));
				result.setTermTwoId(rs.getLong("term_two_id"));
				result.setRelationshipId(rs.getLong("relationship_id"));
				result.setFrequency(rs.getLong("frequency"));
			}
			rs.close();
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void incrementRelation(ODIE_Relation relation) {
		try {
			PreparedStatement pStmt = conn
					.prepareStatement("update lsp_relation set frequency = ? where id = ?");
			pStmt
					.setLong(1, new Long(
							relation.getFrequency().longValue() + 1L));
			pStmt.setLong(2, relation.getId());
			pStmt.executeUpdate();
			pStmt.close();
			relation.setFrequency(new Long(
					relation.getFrequency().longValue() + 1L));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConn() {
		return conn;
	}

	public static void setConn(Connection conn) {
		ODIE_Relation.conn = conn;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String toString() {
		String result = "";
		result += getId() + ") " + getRelationshipId() + ", " + getTermOneId()
				+ ", " + getTermTwoId() + ", " + getFrequency();
		return result;
	}

	public Long getTermOneId() {
		return termOneId;
	}

	public void setTermOneId(Long termOneId) {
		this.termOneId = termOneId;
	}

	public Long getTermTwoId() {
		return termTwoId;
	}

	public void setTermTwoId(Long termTwoId) {
		this.termTwoId = termTwoId;
	}

	public Long getRelationshipId() {
		return relationshipId;
	}

	public void setRelationshipId(Long relationshipId) {
		this.relationshipId = relationshipId;
	}

	public Long getFrequency() {
		return frequency;
	}

	public void setFrequency(Long frequency) {
		this.frequency = frequency;
	}

}

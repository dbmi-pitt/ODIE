package edu.pitt.dbmi.odie.uima.lsp.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import edu.pitt.dbmi.odie.uima.lsp.ae.ODIE_ConceptConsolidator;

public class ODIE_LSP {
	
	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_LSP.class);

	private static Connection conn;

	private Long id;
	private String lspCorpus;
	private String lspAuthor;
	private String lspName;
	private String lspDefinition;
	private String lspDirection; // "F" or "B"
	private String lspRelationship;
	private Integer lspDocuments;
	private Integer lspSentences;
	private Integer lspUniqueSentence;
	private Long lspBytes;

	public static void main(String[] args) {
		openDatabaseConnection();
		dropLsp();
		createLsp();
		ODIE_LSP hearst01LSP = new ODIE_LSP() ;
		hearst01LSP.setLspCorpus("Pathology") ;
		hearst01LSP.setLspAuthor("Hearst") ;
		hearst01LSP.setLspName("Hearst_01") ;
		hearst01LSP.setLspDefinition("NP such as NP, and NP") ;
		hearst01LSP.setLspRelationship("Hypernomy") ;
		hearst01LSP.setLspDirection("F") ;
		hearst01LSP.setLspDocuments(0) ;
		hearst01LSP.setLspSentences(0) ;
		hearst01LSP.setLspUniqueSentence(0) ;
		hearst01LSP.setLspBytes(0L) ;
		hearst01LSP = fetchOrCreateLsp(hearst01LSP) ;
		System.out.println(hearst01LSP) ;
		
		hearst01LSP.setLspCorpus("Radiology") ;
		hearst01LSP = updateLsp(hearst01LSP) ;
		System.out.println(hearst01LSP) ;
	}

	public ODIE_LSP() {
	}

	public static void dropLsp() {
		String dropTableString = "drop table if exists lsp_lsp";
		Statement statement;
		try {
			statement = conn.createStatement();
			statement.executeUpdate(dropTableString);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void createLsp() {
		String createString = "";
		createString += "CREATE TABLE IF NOT EXISTS  lsp_lsp (\n";
		createString += "id bigint(20) NOT NULL AUTO_INCREMENT,\n";
		createString += "lsp_corpus varchar(50) DEFAULT NULL,\n";
		createString += "lsp_author varchar(50) DEFAULT NULL,\n";
		createString += "lsp_name varchar(50) DEFAULT NULL,\n";
		createString += "lsp_definition text,\n";
		createString += "lsp_relationship varchar(50) DEFAULT NULL,\n";
		createString += "lsp_direction char(1) DEFAULT NULL,\n";
		createString += "lsp_documents int(11) DEFAULT NULL,\n";
		createString += "lsp_sentences int(11) DEFAULT NULL,\n";
		createString += "lsp_unique_sentences int(11) DEFAULT NULL,\n";
		createString += "lsp_bytes bigint(20) DEFAULT NULL,\n";
		createString += "PRIMARY KEY (id)\n";
		createString += ") ENGINE=InnoDB DEFAULT CHARSET=latin1\n";
//		System.out.println(createString);
		Statement statement;
		try {
			statement = conn.createStatement();
			statement.executeUpdate(createString);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static ODIE_LSP fetchOrCreateLsp(ODIE_LSP lsp) {
		ODIE_LSP result = null ;
		if (lsp.getId() == null) {
			insertLsp(lsp);
		}
		result = fetchLsp(lsp);
		return result;
	}
	
	public static ODIE_LSP updateLsp(ODIE_LSP lsp) {
		ODIE_LSP result = new ODIE_LSP();
		try {
			if (lsp.getId()!=null) {
				String pStmtString = "";
				pStmtString += "update lsp_lsp \n";
				pStmtString += "set lsp_corpus = ?,\n";
				pStmtString += "lsp_author = ?,\n";
				pStmtString += "lsp_name = ?,\n";
				pStmtString += "lsp_definition = ?,\n";
				pStmtString += "lsp_relationship = ?,\n";
				pStmtString += "lsp_direction = ?,\n";
				pStmtString += "lsp_documents = ?,\n";
				pStmtString += "lsp_sentences = ?,\n";
				pStmtString += "lsp_unique_sentences = ?,\n";
				pStmtString += "lsp_bytes = ?\n";
				pStmtString += "where id = ?\n" ;
				PreparedStatement pStmt = conn
						.prepareStatement(pStmtString);
				pStmt.setString(1, lsp.getLspCorpus());         // Pathology | Radiology
				pStmt.setString(2, lsp.getLspAuthor()) ;        // Hearst | Snow | Lui
				pStmt.setString(3, lsp.getLspName()) ;          // Hearst_01 | Snow_02
				pStmt.setString(4, lsp.getLspDefinition()) ;    // NP such as NP ...
				pStmt.setString(5, lsp.getLspRelationship()) ;  // Hypernomy | Meronomy
				pStmt.setString(6, lsp.getLspDirection()) ;     // Forward | Backward
				pStmt.setLong(7, lsp.getLspDocuments()) ;       // # of docs having this LSP
				pStmt.setLong(8, lsp.getLspSentences()) ;       // # of sentences having this LSP
				pStmt.setLong(9, lsp.getLspUniqueSentence()) ;  // # of unique sentences "" "" ""
				pStmt.setLong(10, lsp.getLspBytes()) ;          // # of bytes "" "" ""
				pStmt.setLong(11, lsp.getId()) ;
				pStmt.executeUpdate();
				pStmt.close();
			}
			else {
				insertLsp(lsp) ;
			}
			
			result = fetchLsp(lsp) ;
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static ODIE_LSP insertLsp(ODIE_LSP lsp) {
		ODIE_LSP result = new ODIE_LSP();
		try {
			String pStmtString = "";
			pStmtString += "insert into lsp_lsp (\n";
			pStmtString += "lsp_corpus,\n";
			pStmtString += "lsp_author,\n";
			pStmtString += "lsp_name,\n";
			pStmtString += "lsp_definition,\n";
			pStmtString += "lsp_relationship,\n";
			pStmtString += "lsp_direction,\n";
			pStmtString += "lsp_documents,\n";
			pStmtString += "lsp_sentences,\n";
			pStmtString += "lsp_unique_sentences,\n";
			pStmtString += "lsp_bytes\n";
			pStmtString += ")\n" ;
			pStmtString += "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" ;
			PreparedStatement pStmt = conn
					.prepareStatement(pStmtString);
			pStmt.setString(1, lsp.getLspCorpus());         // Pathology | Radiology
			pStmt.setString(2, lsp.getLspAuthor()) ;        // Hearst | Snow | Lui
			pStmt.setString(3, lsp.getLspName()) ;          // Hearst_01 | Snow_02
			pStmt.setString(4, lsp.getLspDefinition()) ;    // NP such as NP ...
			pStmt.setString(5, lsp.getLspRelationship()) ;  // Hypernomy | Meronomy
			pStmt.setString(6, lsp.getLspDirection()) ;     // Forward | Backward
			pStmt.setLong(7, lsp.getLspDocuments()) ;       // # of docs having this LSP
			pStmt.setLong(8, lsp.getLspSentences()) ;       // # of sentences having this LSP
			pStmt.setLong(9, lsp.getLspUniqueSentence()) ;  // # of unique sentences "" "" ""
			pStmt.setLong(10, lsp.getLspBytes()) ;          // # of bytes "" "" ""
			pStmt.executeUpdate();
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static ODIE_LSP fetchLsp(ODIE_LSP lsp) {
		ODIE_LSP result = new ODIE_LSP();
		try {
			String pStmtString = "" ;
			pStmtString += "select * from lsp_lsp where\n" ;
			pStmtString += "lsp_corpus = ? and \n";
			pStmtString += "lsp_author = ? and \n";
			pStmtString += "lsp_name = ? and \n";
			pStmtString += "lsp_definition = ? and \n";
			pStmtString += "lsp_relationship = ? and \n";
			pStmtString += "lsp_direction = ? and \n";
			pStmtString += "lsp_documents = ? and \n";
			pStmtString += "lsp_sentences = ? and \n";
			pStmtString += "lsp_unique_sentences = ? and \n";
			pStmtString += "lsp_bytes = ?\n";
//			System.out.println(pStmtString) ;
			PreparedStatement pStmt = conn
					.prepareStatement(pStmtString);
			pStmt.setString(1, lsp.getLspCorpus());         // Pathology | Radiology
			pStmt.setString(2, lsp.getLspAuthor()) ;        // Hearst | Snow | Lui
			pStmt.setString(3, lsp.getLspName()) ;          // Hearst_01 | Snow_02
			pStmt.setString(4, lsp.getLspDefinition()) ;    // NP such as NP ...
			pStmt.setString(5, lsp.getLspRelationship()) ;  // Hypernomy | Meronomy
			pStmt.setString(6, lsp.getLspDirection()) ;     // Forward | Backward
			pStmt.setLong(7, lsp.getLspDocuments()) ;       // # of docs having this LSP
			pStmt.setLong(8, lsp.getLspSentences()) ;       // # of sentences having this LSP
			pStmt.setLong(9, lsp.getLspUniqueSentence()) ;  // # of unique sentences "" "" ""
			pStmt.setLong(10, lsp.getLspBytes()) ;          // # of bytes "" "" ""
			ResultSet rs = pStmt.executeQuery();
			if (rs.next()) {
				result.setId(rs.getLong("id"));
				result.setLspCorpus(rs.getString("lsp_corpus"));
				result.setLspAuthor(rs.getString("lsp_author")) ;
				result.setLspName(rs.getString("lsp_name")) ;
				result.setLspDefinition(rs.getString("lsp_definition")) ;
				result.setLspRelationship(rs.getString("lsp_relationship")) ;
				result.setLspDirection(rs.getString("lsp_direction")) ;
				result.setLspDocuments(rs.getInt("lsp_documents")) ;
				result.setLspSentences(rs.getInt("lsp_sentences")) ;
				result.setLspUniqueSentence(rs.getInt("lsp_unique_sentences")) ;
				result.setLspBytes(rs.getLong("lsp_bytes")) ;
			}
			rs.close();
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Connection getConn() {
		return conn;
	}

	public static void setConn(Connection conn) {
		ODIE_LSP.conn = conn;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLspCorpus() {
		return lspCorpus;
	}

	public void setLspCorpus(String lspCorpus) {
		this.lspCorpus = lspCorpus;
	}

	public String getLspAuthor() {
		return lspAuthor;
	}

	public void setLspAuthor(String lspAuthor) {
		this.lspAuthor = lspAuthor;
	}

	public String getLspName() {
		return lspName;
	}

	public void setLspName(String lspName) {
		this.lspName = lspName;
	}

	public String getLspDefinition() {
		return lspDefinition;
	}

	public void setLspDefinition(String lspDefinition) {
		this.lspDefinition = lspDefinition;
	}

	public String getLspDirection() {
		return lspDirection;
	}

	public void setLspDirection(String lspDirection) {
		this.lspDirection = lspDirection;
	}

	public Integer getLspDocuments() {
		return lspDocuments;
	}

	public void setLspDocuments(Integer lspDocuments) {
		this.lspDocuments = lspDocuments;
	}

	public Integer getLspSentences() {
		return lspSentences;
	}

	public void setLspSentences(Integer lspSentences) {
		this.lspSentences = lspSentences;
	}

	public Integer getLspUniqueSentence() {
		return lspUniqueSentence;
	}

	public void setLspUniqueSentence(Integer lspUniqueSentence) {
		this.lspUniqueSentence = lspUniqueSentence;
	}

	public Long getLspBytes() {
		return lspBytes;
	}

	public void setLspBytes(Long lspBytes) {
		this.lspBytes = lspBytes;
	}

	public String getLspRelationship() {
		return lspRelationship;
	}

	public void setLspRelationship(String lspRelationship) {
		this.lspRelationship = lspRelationship;
	}
	
	public String toString() {
		String result = "" ;
		result += "\nLSP ==> " + getId() ;
		result += "\nCorpus ==> " + getLspCorpus() ;
		result += "\nAuthor ==> " + getLspAuthor() ;
		result += "\nName ==> " + getLspName() ;
		result += "\nDefinition ==> " + getLspDefinition() ;
		result += "\nRelationship ==> " + getLspRelationship() ;
		result += "\nDirection ==> " + getLspDirection() ;
		result += "\nDocuments ==> " + getLspDocuments() ;
		result += "\nSentences ==> " + getLspSentences() ;
		result += "\nUniqueSentence ==> " + getLspUniqueSentence() ;
		result += "\nBytes ==> " + getLspBytes() ;
		return result ;
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

}

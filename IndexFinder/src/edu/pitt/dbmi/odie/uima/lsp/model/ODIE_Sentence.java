package edu.pitt.dbmi.odie.uima.lsp.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.pitt.dbmi.odie.uima.util.ODIE_FormatUtils;

public class ODIE_Sentence {
	
	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_Sentence.class);

	private static Connection conn;

	public static final String BLANK = "BLANK";
	
	public static final int MAX_SENTENCE_LENGTH = 500 ;

	private static final int defaultNumberOfTerms = 12;
	
	private static String tableName = "lsp_sentence" ;

	private Long id;
	private Long lspId;
	private Long documentId;
	private Long sPosition;
	private Long ePosition;
	private String fragment;
	private ArrayList<String> terms;
	private ODIE_LSP lsp;

	public static void main(String[] args) {
		openDatabaseConnection();
		dropSentence();
		createSentence();
		
		ODIE_LSP lsp = new ODIE_LSP();
		lsp.setId(1000L);
		
		ODIE_Sentence sentence = newBlankSentence();
		sentence.setLsp(lsp) ;
		sentence.setLspId(lsp.getId()) ;
		sentence.setDocumentId(33L) ;
		sentence.setSPosition(33L) ;
		sentence.setEPosition(333L) ;
		sentence.setFragment("Prostate shows signs of adenocarcinoma");
		sentence.getTerms().set(0, "prostate");
		sentence.getTerms().set(1, "adenocarcinoma");
		sentence = fetchOrCreateSentence(sentence);
		logger.debug(sentence) ;

		sentence = newBlankSentence();
		sentence.setLsp(lsp) ;
		sentence.setLspId(lsp.getId()) ;
		sentence.setDocumentId(55L) ;
		sentence.setSPosition(55L) ;
		sentence.setEPosition(555L) ;
		sentence.setFragment("Lobular Carcinoma of the Breast is seen");
		sentence.getTerms().set(0, "lobular carcinoma");
		sentence.getTerms().set(1, "breast");
		sentence = fetchOrCreateSentence(sentence);
		logger.debug(sentence) ;
	}

	public ODIE_Sentence() {
	}
	
	public void setHeadTerm(String term) {
		if (terms != null && terms.size() > 0) {
			terms.set(0, term) ;
		}
	}
	
	public void setTailTerms(ArrayList<String> tailTerms) {
		int idx = 1;
		for (String termFragment : tailTerms) {
			if (idx < terms.size()) {
				terms.set(idx, termFragment);
			}
			idx++;
		}
	}

	public static void dropSentence() {
		String dropTableString = "drop table if exists " + getTableName() ;
		Statement statement;
		try {
			statement = conn.createStatement();
			statement.executeUpdate(dropTableString);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void createSentence() {
		String createOrReplaceString = "";
		createOrReplaceString += "CREATE TABLE IF NOT EXISTS  " + getTableName() + " (\n";
		createOrReplaceString += "id bigint(20) NOT NULL AUTO_INCREMENT,\n";
		createOrReplaceString += "lsp_id bigint(20),\n";
		createOrReplaceString += "document_id bigint(20) NOT NULL DEFAULT -1,\n";
		createOrReplaceString += "spos integer NOT NULL DEFAULT -1,\n";
		createOrReplaceString += "epos integer NOT NULL DEFAULT -1,\n";
		createOrReplaceString += "fragment TEXT,\n";
		for (int idx = 0; idx < defaultNumberOfTerms; idx++) {
			createOrReplaceString += "term_"
					+ ODIE_FormatUtils.formatIntegerAsDigitString(new Integer(
							idx), "00") + " varchar(250),\n";
		}
		createOrReplaceString += "PRIMARY KEY (id),\n";
//		createOrReplaceString += "KEY lsp_sentence_fragment_idx (fragment),";
		createOrReplaceString += "KEY lsp_sentence_term_idx (";
		for (int idx = 0; idx < defaultNumberOfTerms; idx++) {
			createOrReplaceString += "term_"
					+ ODIE_FormatUtils.formatIntegerAsDigitString(new Integer(
							idx), "00") + ",";
		}
		createOrReplaceString = createOrReplaceString.replaceAll(",$", "")
				+ ")\n";
		createOrReplaceString += ") ENGINE=InnoDB DEFAULT CHARSET=latin1";
		Statement statement;
		try {
			statement = conn.createStatement();
			statement.executeUpdate(createOrReplaceString);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static ODIE_Sentence newBlankSentence() {
		ODIE_Sentence result = new ODIE_Sentence();
		ArrayList<String> terms = new ArrayList<String>(defaultNumberOfTerms);
		result.setFragment(BLANK);
		for (int idx = 0; idx < defaultNumberOfTerms; idx++) {
			terms.add(BLANK);
		}
		result.setTerms(terms);
		return result;
	}


	public static ODIE_Sentence fetchOrCreateSentence(ODIE_Sentence sentence) {
		ODIE_Sentence result = fetchSentenceBasedOnAll(sentence);
		if (result.getId() == null) {
			insertSentence(sentence);
			result = fetchSentenceBasedOnAll(sentence);
//			result = fetchSentenceBasedOnFragment(sentence.getFragment(), sentence.getTerms()) ;
		}
		return result;
	}

	public static ODIE_Sentence insertSentence(ODIE_Sentence sentenceToInsert) {
		ODIE_Sentence result = new ODIE_Sentence();
		try {
			ODIE_LSP lsp = sentenceToInsert.getLsp() ;
			String fragment = sentenceToInsert.getFragment() ;
			Long documentId = sentenceToInsert.getDocumentId() ;
			Long sPosition = sentenceToInsert.getSPosition() ;
			Long ePosition = sentenceToInsert.getEPosition();
			ArrayList<String> terms = sentenceToInsert.getTerms() ;
			
			String pStmtString = "insert into " + getTableName() + " (\n";
			pStmtString += "lsp_id, \n";
			pStmtString += "fragment, \n";
			pStmtString += "document_id, \n";
			pStmtString += "spos, \n";
			pStmtString += "epos, \n";
			for (int idx = 0; idx < defaultNumberOfTerms; idx++) {
				pStmtString += "term_"
						+ ODIE_FormatUtils.formatIntegerAsDigitString(
								new Integer(idx), "00") + ", \n";
			}
			pStmtString = pStmtString.substring(0, pStmtString
					.lastIndexOf(", \n"));
			pStmtString += ")\nvalues (?, ?, ?, ?, ?,";
			for (int idx = 0; idx < defaultNumberOfTerms; idx++) {
				pStmtString += "?, ";
			}
			pStmtString = pStmtString.substring(0, pStmtString
					.lastIndexOf(", "));
			pStmtString += ")";

			PreparedStatement pStmt = conn.prepareStatement(pStmtString);
			pStmt.setLong(1, lsp.getId());
			pStmt.setString(2, fragment);
			pStmt.setLong(3, documentId);
			pStmt.setLong(4, sPosition);
			pStmt.setLong(5, ePosition);
			for (int idx = 0; idx < defaultNumberOfTerms; idx++) {
				String termToAdd = terms.get(idx);
				int indexToAdd = idx + 6;
				pStmt.setString(indexToAdd, termToAdd);
			}
			pStmt.executeUpdate();
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static String getTermFieldForIdx(int idx) {
		return "term_"
		+ ODIE_FormatUtils.formatIntegerAsDigitString(
				new Integer(idx), "00") ;
	}

	public static ODIE_Sentence fetchSentence(ODIE_Sentence searchSentence) {
		ODIE_Sentence result = new ODIE_Sentence();
		try {
			ArrayList<String> terms = searchSentence.getTerms() ;
			String pStmtString = "select * from " + getTableName() + " where ";
			for (int idx = 0; idx < defaultNumberOfTerms; idx++) {
				pStmtString += getTermFieldForIdx(idx) + " = ? and ";
			}
			pStmtString = pStmtString.substring(0, pStmtString
					.lastIndexOf(" and "));
			PreparedStatement pStmt = conn.prepareStatement(pStmtString);
			for (int idx = 0; idx < defaultNumberOfTerms; idx++) {
				pStmt.setString(idx + 1, queryTarget(terms.get(idx)));
			}
			ResultSet rs = pStmt.executeQuery();
			result = pullSentenceFromResultSet(rs);
			rs.close();
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static String queryTarget(String s) {
		String result = (s == null) ? "BLANK" :  s ;
		return result ;
	}

	public static ODIE_Sentence fetchSentenceBasedOnFragment(String fragment,
			ArrayList<String> terms) {
		ODIE_Sentence result = new ODIE_Sentence();
		try {
			String pStmtString = "select * from " + getTableName() + " where fragment = ?";
			PreparedStatement pStmt = conn.prepareStatement(pStmtString);
			pStmt.setString(1, fragment);
			ResultSet rs = pStmt.executeQuery();
			result = pullSentenceFromResultSet(rs);
			rs.close();
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static ODIE_Sentence fetchSentenceBasedOnId(Long id) {
		ODIE_Sentence result = new ODIE_Sentence();
		try {
			String pStmtString = "select * from " + getTableName() + " where id = ?";
			PreparedStatement pStmt = conn.prepareStatement(pStmtString);
			pStmt.setLong(1, id);
			ResultSet rs = pStmt.executeQuery();
			result = pullSentenceFromResultSet(rs);
			rs.close();
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static ODIE_Sentence fetchSentenceBasedOnAll(ODIE_Sentence sentence) {
		ODIE_Sentence result = new ODIE_Sentence();
		try {
			ODIE_LSP lsp = sentence.getLsp() ;
			Long lspId = sentence.getLspId() ;
			String fragment = sentence.getFragment() ;
			Long documentId = sentence.getDocumentId() ;
			Long sPosition = sentence.getSPosition() ;
			Long ePosition = sentence.getEPosition();
			ArrayList<String> terms = sentence.getTerms() ;
			
			String pStmtString = "";
			pStmtString = "select * from " + getTableName() + " where \n";
			pStmtString += "lsp_id = ? and \n";
			pStmtString += "document_id = ? and \n";
			pStmtString += "spos = ? and \n";
			pStmtString += "epos = ? and \n";
			pStmtString += "fragment = ? and \n";
			for (int idx = 0; idx < defaultNumberOfTerms; idx++) {
				pStmtString += getTermFieldForIdx(idx) + " = ? and \n";
			}
			pStmtString = pStmtString.substring(0, pStmtString
					.lastIndexOf(" and \n"));

			PreparedStatement pStmt = conn.prepareStatement(pStmtString);
			pStmt.setLong(1, lspId);
			pStmt.setLong(2, documentId);
			pStmt.setLong(3, sPosition);
			pStmt.setLong(4, ePosition);
			pStmt.setString(5, fragment);
			for (int idx = 0; idx < defaultNumberOfTerms; idx++) {
				String termAsString = queryTarget(terms.get(idx)) ;
				pStmt.setString(idx + 6, termAsString);
			}
			ResultSet rs = pStmt.executeQuery();
			result = pullSentenceFromResultSet(rs);
			rs.close();
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static ODIE_Sentence pullSentenceFromResultSet(ResultSet rs) {
		ODIE_Sentence resultSentence = newBlankSentence();
		try {
			if (rs.next()) {
				resultSentence.setId(rs.getLong("id"));
				resultSentence.setLspId(rs.getLong("lsp_id"));
				ODIE_LSP lsp = new ODIE_LSP() ;
				lsp.setId(resultSentence.getLspId()) ;
				resultSentence.setLsp(lsp) ;
				resultSentence.setFragment(rs.getString("fragment"));
				resultSentence.setDocumentId(rs.getLong("document_id"));
				resultSentence.setSPosition(rs.getLong("spos"));
				resultSentence.setEPosition(rs.getLong("epos"));
				ArrayList<String> resultTerms = resultSentence.getTerms();
				for (int idx = 0; idx < defaultNumberOfTerms; idx++) {
					resultTerms.set(idx, rs.getString(getTermFieldForIdx(idx)));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultSentence;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer() ;
		sb.append("") ;
		sb.append(getLspId()) ;
		sb.append("." + getId() + " ") ;
		sb.append(getFragment() + "\n") ;
		for (int idx = 0; idx < defaultNumberOfTerms; idx++) {
			sb.append("\t"
					+ getTermFieldForIdx(idx) + " = " + this.getTerms().get(idx)
					+ "\n") ;
		}
		return sb.toString() ;
	}

	public static Connection getConn() {
		return conn;
	}

	public static void setConn(Connection conn) {
		ODIE_Sentence.conn = conn;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getLspId() {
		return lspId;
	}

	public void setLspId(Long lspId) {
		this.lspId = lspId;
	}

	public String getFragment() {
		return fragment;
	}

	public void setFragment(String fragment) {
		this.fragment = fragment;
	}

	public ArrayList<String> getTerms() {
		return terms;
	}

	public void setTerms(ArrayList<String> terms) {
		this.terms = terms;
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

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public Long getSPosition() {
		return sPosition;
	}

	public void setSPosition(Long position) {
		sPosition = position;
	}

	public Long getEPosition() {
		return ePosition;
	}

	public void setEPosition(Long position) {
		ePosition = position;
	}

	public static String getTableName() {
		return tableName;
	}

	public static void setTableName(String tableName) {
		ODIE_Sentence.tableName = tableName;
	}

	public ODIE_LSP getLsp() {
		return lsp;
	}

	public void setLsp(ODIE_LSP lsp) {
		this.lsp = lsp;
	}

}
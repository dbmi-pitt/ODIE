/*
 *  MiniParSimilarityClusterPR.java
 *
 *
 * Copyright (c) 2000-2001, The University of Sheffield.
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June1991.
 *
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://gate.ac.uk/gate/licence.html.
 *
 *  mitchellkj, 16/6/2009
 *
 *  $Id: MiniParSimilarityClusterPR.jav 9992 2008-10-31 16:53:29Z ian_roberts $
 *
 * For details on the configuration options, see the user guide:
 * http://gate.ac.uk/cgi-bin/userguide/sec:creole-model:config
 */

package edu.pitt.dbmi.odie.gate.pr.dekanlin;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.FeatureMap;
import gate.LanguageAnalyser;
import gate.Resource;
import gate.creole.AbstractProcessingResource;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleResource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

/**
 * This class is the implementation of the resource MINIPARSIMILARITYCLUSTERPR.
 */
@CreoleResource(name = "MiniParSimilarityClusterPR", comment = "Add a descriptive comment about this resource")
public class ODIE_MiniparSimilarityClusterPR extends AbstractProcessingResource
		implements LanguageAnalyser {

	private static final long serialVersionUID = 1L;

	/**
	 * JDBC connection information for RDBMS
	 */
	public static final String KEY_DB_DRIVER = "odie.db.driver";
	public static final String KEY_DB_URL = "odie.db.url";
	public static final String KEY_DB_USER_NAME = "odie.db.user.name";
	public static final String KEY_DB_USER_PASSWORD = "odie.db.user.password";
	public static final String KEY_LANGUAGE = "odie.language";
	
	protected Document document;

	protected Corpus corpus;

	private Connection conn = null;

	private PreparedStatement preparedStatementIsTripleEntered = null;
	private PreparedStatement preparedStatementEnterTriple = null;
	private PreparedStatement preparedStatementUpdateTriple = null;

	private long startTime;

	private boolean isInitialized;

	private String driver = null;
	private String url = null;
	private String userName = null;
	private String password = null;

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/** Initialise this resource, and return it. */
	public Resource init() throws ResourceInstantiationException {
		Resource resource = super.init();
		return resource;
	}

	public void reInit() throws ResourceInstantiationException {
		this.isInitialized = false;
		init();
	} // reInit()

	public void initialize() {
		
		if (!this.isInitialized) {

			startTime = System.currentTimeMillis();
//			System.out.println("Time: "
//					+ (System.currentTimeMillis() - startTime)
//					+ " initialize() called");

			try {
				Class.forName(driver).newInstance();
				conn = DriverManager.getConnection(url, userName, password);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (conn != null) {
				;
//				System.out
//						.println("Time: "
//								+ (System.currentTimeMillis() - startTime)
//								+ " ODIE_MiniparSimilarityClusterPR: Connected to RDBMS: "
//								+ url);
			} else {
				System.err
						.println("Time: "
								+ (System.currentTimeMillis() - startTime)
								+ " ODIE_MiniparSimilarityClusterPR: FAILED to Connect to RDBMS: "
								+ url);
			}

			// Databases typically use user-names and passwords; these can
			// be passed as //properties to the getConnection method.

			// drop the table in case it's already present
			// This isn't needed because we're starting from an empty
			// database,
			// but leave here for tutorial reasons
			Statement sqlStmt = null;
			try {
				sqlStmt = conn.createStatement();
				sqlStmt.execute("drop table if exists lin_analysis.triples");
				sqlStmt.close();

				StringBuffer sb = new StringBuffer();
				sb.append("CREATE TABLE lin_analysis.triples (\n");
				sb.append("  ID bigint(20) NOT NULL AUTO_INCREMENT,\n");
				sb
						.append("  WORD_ONE varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
				sb
						.append("  RELATION varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
				sb
						.append("  WORD_TWO varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
				sb.append("  FREQ int(11) NOT NULL DEFAULT 0,\n");
				sb.append("  INFO int(11) NOT NULL DEFAULT 0,\n");
				sb.append("  PRIMARY KEY (ID),\n");
				sb.append("  KEY INDEX_WORD (WORD_ONE, RELATION, WORD_TWO)\n");
				sb
						.append(") ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC");
				sqlStmt = conn.createStatement();
				sqlStmt.execute(sb.toString());
				sqlStmt.close();

//				System.out
//						.println("Time: "
//								+ (System.currentTimeMillis() - startTime)
//								+ " ODIE_MiniparSimilarityClusterPR: First Time Initiailization: Created the TRIPLES table.");

				preparedStatementIsTripleEntered = conn
						.prepareStatement("select count(*) from triples where WORD_ONE = ? and RELATION = ? and WORD_TWO = ?");
				preparedStatementEnterTriple = conn
						.prepareStatement("insert into lin_analysis.triples (WORD_ONE, RELATION, WORD_TWO, FREQ) values (?, ?, ?, 1)");
				preparedStatementUpdateTriple = conn
						.prepareStatement("update lin_analysis.triples set FREQ = FREQ + 1 where WORD_ONE = ? and RELATION = ? and WORD_TWO = ?");

			} catch (SQLException e) {
				e.printStackTrace();
			}

			this.isInitialized = true;
		}

	}

	public void execute() {

		initialize() ;

		try {
			AnnotationSet defaultAnnotations = getDocument().getAnnotations();
			for (Iterator<Annotation> annotIterator = defaultAnnotations.iterator(); annotIterator
					.hasNext();) {
				Annotation annot = annotIterator.next();
				FeatureMap features = annot.getFeatures() ;
				String headWord = (String) features.get("head_word") ;
				String childWord = (String) features.get("child_word") ;
				String relation = annot.getType() ;
				if (headWord != null && childWord != null) {
					//reportAnnotation(annot) ;
					if (preparedStatementIsTripleEntered == null) {
						System.err.println("Null prepared statements!") ;
						break ;
					}
					preparedStatementIsTripleEntered.clearParameters() ;
					preparedStatementIsTripleEntered.setString(1, headWord);
					preparedStatementIsTripleEntered.setString(2, relation);
					preparedStatementIsTripleEntered.setString(3, childWord);
					ResultSet rs = preparedStatementIsTripleEntered.executeQuery();
					int wordEnteredFlag = 0;
					if (rs.next()) {
						wordEnteredFlag = rs.getInt(1);
					}
					if (wordEnteredFlag <= 0) {
						preparedStatementEnterTriple.clearParameters();
						preparedStatementEnterTriple.setString(1, headWord);
						preparedStatementEnterTriple.setString(2, relation);
						preparedStatementEnterTriple.setString(3, childWord);
						preparedStatementEnterTriple.executeUpdate();
					} else {
//						System.out.println("Update: " + headWord + ", " + relation + ", " + childWord) ;
						preparedStatementUpdateTriple.clearParameters();
						preparedStatementUpdateTriple.setString(1, headWord);
						preparedStatementUpdateTriple.setString(2, relation);
						preparedStatementUpdateTriple.setString(3, childWord);
						preparedStatementUpdateTriple.executeUpdate();
					}
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		

	}
	
	private void reportAnnotation(Annotation annot) {
		StringBuffer diagnostic = new StringBuffer();
		diagnostic.append("Type: " + annot.getType() + "\n");
		diagnostic.append("SPOS: " + annot.getStartNode().getOffset()
				+ "\n");
		diagnostic.append("EPOS: " + annot.getEndNode().getOffset() + "\n");
//		System.out.println(diagnostic.toString());
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public void setCorpus(Corpus corpus) {
		this.corpus = corpus;
	}

	public Corpus getCorpus() {
		return corpus;
	}

} // class MiniParSimilarityClusterPR

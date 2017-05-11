/*
 *  PosHistogramGeneratorPR.java
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
 *  mitchellkj, 19/6/2009
 *
 *  $Id: PosHistogramGeneratorPR.jav 9992 2008-10-31 16:53:29Z ian_roberts $
 *
 * For details on the configuration options, see the user guide:
 * http://gate.ac.uk/cgi-bin/userguide/sec:creole-model:config
 */

package edu.pitt.dbmi.odie.gate.pr.dekanlin;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
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
 * This class is the implementation of the resource
 * ODIE_POSHISTOGRAMGENERATORPR.
 */
@CreoleResource(name = "ODIE_PosHistogramGeneratorPR", comment = "Add a descriptive comment about this resource")
public class ODIE_PosHistogramGeneratorPR extends AbstractProcessingResource
		implements LanguageAnalyser {

	private static final long serialVersionUID = 1L;

	protected Document document;

	protected Corpus corpus;

	private Connection conn = null;
	private PreparedStatement preparedStatementIsWordEntered = null;
	private PreparedStatement preparedStatementEnterWord = null;
	private PreparedStatement preparedStatementUpdateWord = null;

	private long startTime;

	private boolean isInitialized;

	private String driver = null;
	private String url = null;
	private String userName = null;
	private String password = null;
	private String tagger;

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
	
	public String getTagger() {
		return tagger;
	}

	public void setTagger(String tagger) {
		this.tagger = tagger;
	}

	/** Initialize this resource, and return it. */
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

//			System.out.println("Initializing...");
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
//				System.out.println("Time: "
//						+ (System.currentTimeMillis() - startTime)
//						+ " ODIE_PosHistogramGeneratorPR: Connected to RDBMS: "
//						+ url);
			} else {
				System.err
						.println("Time: "
								+ (System.currentTimeMillis() - startTime)
								+ " ODIE_PosHistogramGeneratorPR: FAILED to Connect to RDBMS: "
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
				sqlStmt.execute("drop table if exists lin_analysis.histogram");
				sqlStmt.close();

				StringBuffer sb = new StringBuffer();
				sb.append("CREATE TABLE lin_analysis.histogram (\n");
				sb.append("  ID bigint(20) NOT NULL AUTO_INCREMENT,\n");
				sb
						.append("  WORD varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
				sb.append("  POS varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
				sb
						.append("  TAGGER varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
				sb.append("  FREQ int(11) NOT NULL DEFAULT 0,\n");
				sb.append("  PRIMARY KEY (ID),\n") ;
				sb.append("  KEY INDEX_WORD (WORD, POS)\n") ;		
				sb
						.append(") ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC");
				sqlStmt = conn.createStatement();
				sqlStmt.execute(sb.toString());
				sqlStmt.close();

//				System.out
//						.println("Time: "
//								+ (System.currentTimeMillis() - startTime)
//								+ " ODIE_PosHistogramGeneratorPR: First Time Initiailization: Created the TRIPLES table.");

				preparedStatementIsWordEntered = conn
						.prepareStatement("select count(*) from lin_analysis.histogram where WORD = ? and POS = ? and TAGGER = ?");
				preparedStatementEnterWord = conn
						.prepareStatement("insert into lin_analysis.histogram (WORD, POS, TAGGER, FREQ) values (?, ?, ?, 1)");
				preparedStatementUpdateWord = conn
						.prepareStatement("update lin_analysis.histogram set FREQ = FREQ + 1 where WORD = ? and POS = ? and TAGGER = ?");

			} catch (Exception e) {
				e.printStackTrace();
			}

			this.isInitialized = true;
		}

	}

	public void execute() {

		initialize();

		try {
			FeatureMap filterFeatures = Factory.newFeatureMap();
			filterFeatures.put("kind", "word");
			AnnotationSet tokenAnnots = document.getAnnotations().get("Token",
					filterFeatures);
			for (Iterator<Annotation> tokenIterator = tokenAnnots.iterator(); tokenIterator
					.hasNext();) {
				Annotation token = tokenIterator.next();
				String word = (String) token.getFeatures().get("string");
				String posTag = (String) token.getFeatures().get("category");
				word = word.toLowerCase();
				if (preparedStatementIsWordEntered == null) {
					System.err.println("Null prepared statements!");
					break;
				}
				preparedStatementIsWordEntered.clearParameters();
				preparedStatementIsWordEntered.setString(1, word);
				preparedStatementIsWordEntered.setString(2, posTag);
				preparedStatementIsWordEntered.setString(3, tagger);
				ResultSet rs = preparedStatementIsWordEntered.executeQuery();
				int wordEnteredFlag = 0;
				if (rs.next()) {
					wordEnteredFlag = rs.getInt(1);
				}
				if (wordEnteredFlag <= 0) {
					preparedStatementEnterWord.clearParameters();
					preparedStatementEnterWord.setString(1, word);
					preparedStatementEnterWord.setString(2, posTag);
					preparedStatementEnterWord.setString(3, tagger);
					preparedStatementEnterWord.executeUpdate();
				} else {
//					System.out.println("Update: " + word + ", " + posTag + ", "
//							+ tagger);
					preparedStatementUpdateWord.clearParameters();
					preparedStatementUpdateWord.setString(1, word);
					preparedStatementUpdateWord.setString(2, posTag);
					preparedStatementUpdateWord.setString(3, tagger);
					preparedStatementUpdateWord.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

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

} // class PosHistogramGeneratorPR

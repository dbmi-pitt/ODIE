/*
 *
 */

package edu.pitt.dbmi.odie.uima.church.reader;

import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import edu.pitt.dbmi.odie.uima.util.ODIE_IFConstants;

/**
 * A simple collection reader that reads documents from a directory in the
 * filesystem. It can be configured with the following parameters:
 * <ul>
 * <li><code>InputDirectory</code> - path to directory containing files</li>
 * <li><code>Encoding</code> (optional) - character encoding of the input files</li>
 * <li><code>Language</code> (optional) - language of the input documents</li>
 * </ul>
 * 
 * 
 */
public class ODIE_JdbcBigResultSetReader extends CollectionReader_ImplBase {

	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_JdbcBigResultSetReader.class);

	private Connection conn = null;

	ArrayList<String> mUuids = new ArrayList<String>();

	private int mCurrentIndex;

	private int reportsToProcess = 1;

	private int reportsAvailable = -1;

	private ResultSet rs = null;

	/**
	 * @see org.apache.uima.collection.CollectionReader_ImplBase#initialize()
	 */
	public void initialize() throws ResourceInitializationException {

		String driver = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_DRIVER);
		String url = (String) getConfigParameterValue(ODIE_IFConstants.IF_PARAM_DB_URL);
		String userName = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_USERNAME);
		String password = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_PASSWORD);
		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url, userName, password);
			logger.debug("Connected to the database");

			logger.debug("Disconnected from database");
		} catch (Exception e) {
			e.printStackTrace();
		}

		String reportsToProcessAsString = (String) getConfigParameterValue(ODIE_IFConstants.IF_PARAM_REPORTS_TO_PROCESS);
		if (reportsToProcessAsString != null) {
			this.reportsToProcess = Integer.valueOf(reportsToProcessAsString);
		}

//		this.reportsAvailable = determineReportsAvailable("path_report");
		this.reportsAvailable = 200000 ;

		this.reportsToProcess = Math.min(this.reportsToProcess,
				this.reportsAvailable);

		try {
			Date timeStart = new Date() ;
			String sqlQuery = "select uuid, document_text from path_report where application_status = 'ANALYZING' limit " + this.reportsToProcess ;
			PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
			this.rs = pStmt.executeQuery(sqlQuery);
			long elapsedTime = (new Date()).getTime() - timeStart.getTime();
			System.out.println("Got document_text result set in " + elapsedTime
					+ " milliseconds");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		mCurrentIndex = 0;
	}

	private int determineReportsAvailable(String tableName) {
		int reportsAvailable = 0;
		try {
			String sqlQuery = "select count(*) reportsAvailable from  "
					+ tableName;
			PreparedStatement pstmt = this.conn.prepareStatement(sqlQuery);
			ResultSet rs = pstmt.executeQuery();
			reportsAvailable = rs.getInt(1) ; 
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return reportsAvailable;
	}

	private String fetchTextForUuid(String uuid) {
		String text = "";
		String sql = "select DOCUMENT_TEXT from PATH_REPORT where uuid = ?";
		try {
			PreparedStatement pStmt = conn.prepareStatement(sql);
			pStmt.setString(1, uuid);
			ResultSet rs = pStmt.executeQuery();
			while (rs.next()) {
				Clob documentTextClob = rs.getClob(1);
				text = documentTextClob.getSubString(1, (int) documentTextClob
						.length());
			}
			pStmt.close() ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return text;
	}

	/**
	 * @see org.apache.uima.collection.CollectionReader#hasNext()
	 */
	public boolean hasNext() {
		return mCurrentIndex < this.reportsToProcess;
	}

	/**
	 * @see org.apache.uima.collection.CollectionReader#getNext(org.apache.uima.cas.CAS)
	 */
	public void getNext(CAS aCAS) throws IOException, CollectionException {
		JCas jcas;
		try {
			jcas = aCAS.getJCas();

			// put document in CAS
			String text = "";
			rs.next();
			String uuid = rs.getString(1) ;
			Clob documentTextClob = rs.getClob(2);
			text = documentTextClob.getSubString(1, (int) documentTextClob
					.length());

			logger.debug("Processing document text\n\n" + text);
			System.out.println("Processing document uuid\n\n" + uuid);
			jcas.setDocumentText(text);

			SourceDocumentInformation srcDocInfo = new SourceDocumentInformation(
					jcas);
			srcDocInfo.setUri(uuid);
			srcDocInfo.setOffsetInSource(0);
			srcDocInfo.setDocumentSize((int) text.length());
			srcDocInfo.setLastSegment(mCurrentIndex == this.reportsToProcess);
			srcDocInfo.addToIndexes();

			mCurrentIndex++;

		} catch (CASException e) {
			throw new CollectionException(e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#close()
	 */
	public void close() throws IOException {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#getProgress()
	 */
	public Progress[] getProgress() {
		return new Progress[] { new ProgressImpl(mCurrentIndex, this.reportsToProcess,
				Progress.ENTITIES) };
	}

	/**
	 * Gets the total number of documents that will be returned by this
	 * collection reader. This is not part of the general collection reader
	 * interface.
	 * 
	 * @return the number of documents in the collection
	 */
	public int getNumberOfDocuments() {
		return this.reportsToProcess ;
	}

}

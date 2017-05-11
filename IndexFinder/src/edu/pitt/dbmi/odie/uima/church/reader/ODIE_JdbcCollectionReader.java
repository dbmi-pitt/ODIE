/*
 * ODIE_JdbcCollectionReader
 * 
 * Works with underlying RDBMS to maintain an ordered flow of
 * documents from the database into the pipeline.
 * 
 * Documents in application_status = 'ANALYZING' are read and placed in the
 * pipeline after which they are marked 'IDLING'
 */

package edu.pitt.dbmi.odie.uima.church.reader;

import java.io.IOException;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

import edu.pitt.dbmi.odie.uima.util.ODIE_Connection;
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
public class ODIE_JdbcCollectionReader extends CollectionReader_ImplBase {

	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_JdbcCollectionReader.class);

	private ODIE_Connection conn = null;

	private int mCurrentIndex;
	private String currentUuid = null ;
	private String currentDocumentText = null ;

	private int reportsToProcess = 1;

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
			conn = new ODIE_Connection(driver, url, userName, password) ;
			System.out.println("Connected to the database");

			logger.debug("Disconnected from database");
		} catch (Exception e) {
			e.printStackTrace();
		}

		String reportsToProcessAsString = (String) getConfigParameterValue(ODIE_IFConstants.IF_PARAM_REPORTS_TO_PROCESS);
		if (reportsToProcessAsString != null) {
			this.reportsToProcess = Integer.valueOf(reportsToProcessAsString);
		}
		int availableReports = this.getAvailableReportsToProcess() ;
		this.reportsToProcess = Math.min(availableReports, this.reportsToProcess) ;

		this.mCurrentIndex = 0 ;
	}

	/**
	 * @see org.apache.uima.collection.CollectionReader#hasNext()
	 */
	public boolean hasNext() {
		try {
			this.currentUuid = null ;
			this.currentDocumentText = null;
			String sql = "select UUID, DOCUMENT_TEXT from PATH_REPORT where application_status like 'ANALYZING' limit 1";
			PreparedStatement pStmt = conn.prepareStatement(sql);
			ResultSet rs = pStmt.executeQuery();
			while (rs.next()) {
				this.currentUuid = rs.getString(1) ;
				Clob documentTextClob = rs.getClob(2) ;
				this.currentDocumentText = documentTextClob.getSubString(1, (int) documentTextClob
						.length());
			}
			rs.close();
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		boolean result = this.currentUuid != null ;
		result = result && (this.mCurrentIndex < this.reportsToProcess) ;
		return result ;
	}

	/**
	 * @see org.apache.uima.collection.CollectionReader#getNext(org.apache.uima.cas.CAS)
	 */
	public void getNext(CAS aCAS) throws IOException, CollectionException {
		JCas jcas;
		try {
		
			jcas = aCAS.getJCas();
			
			logger.info("Processing document text\n\n" + this.currentDocumentText);
			jcas.setDocumentText(this.currentDocumentText);

			SourceDocumentInformation srcDocInfo = new SourceDocumentInformation(
					jcas);
			srcDocInfo.setUri(this.currentUuid);
			srcDocInfo.setOffsetInSource(0);
			srcDocInfo.setDocumentSize((int) this.currentDocumentText.length());
			srcDocInfo.setLastSegment(mCurrentIndex == this.reportsToProcess);
			srcDocInfo.addToIndexes();
			
			String sql = "update path_report set application_status = 'IDLING' where uuid = ?" ;
			PreparedStatement pStmt = this.conn.prepareStatement(sql) ;
			pStmt.setString(1, this.currentUuid) ;
			pStmt.executeUpdate() ;
			pStmt.close();
			
			this.mCurrentIndex++ ;
			
			
		} catch (CASException e) {
			throw new CollectionException(e);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private int getAvailableReportsToProcess() {
		int availableReportsToProcess = 0;
		try {
			String sql = "select count(*) theCount from path_report where application_status = 'ANALYZING'";
			PreparedStatement pStmt = this.conn.prepareStatement(sql);
			ResultSet rs = pStmt.executeQuery();
			if (rs.next()) {
				availableReportsToProcess = rs.getInt("theCount");
			}
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return availableReportsToProcess;
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

/*
 *
 */

package edu.pitt.dbmi.odie.uima.church.reader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
public class ODIE_SingleColumnJdbcCollectionReader extends CollectionReader_ImplBase {

	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_SingleColumnJdbcCollectionReader.class);
	
	private Connection conn = null;

	ArrayList<String> mUuids = new ArrayList<String>();
	
	private int mCurrentIndex;
	private String mUuid;
	
	private int reportsToProcess = 1 ;

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
			this.reportsToProcess = Integer.valueOf(reportsToProcessAsString) ;
		}

		String sql = (String) getConfigParameterValue(ODIE_IFConstants.IF_PARAM_DB_SELECT_SQL);
		if (sql == null || sql.trim().length() == 0) {
			sql = "select id from radlex_200_vs_303 where is_old = 0 and is_new = 1 " ;
		}
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				mUuid = rs.getString(1);
				mUuids.add(mUuid);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		

		mCurrentIndex = 0;
	}
	
	private String fetchTextForUuid(String uuid) {
		String text = "" ;
		String sql =  "select cui from radlex_200_vs_303 where id = ?" ;
		try {
			PreparedStatement pStmt = conn.prepareStatement(sql);
			pStmt.setString(1, uuid) ;
			ResultSet rs = pStmt.executeQuery() ;
			while (rs.next()) {
				text = rs.getString(1) ;
				ArrayList<String> unCamelCasedText = unCamelCase(text, false /* don't add words at the end */) ;
				text = unCamelCasedText.get(0) ;
			}
			rs.close();
			pStmt.close() ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return text ;
	}

	/**
	 * @see org.apache.uima.collection.CollectionReader#hasNext()
	 */
	public boolean hasNext() {
		return mCurrentIndex < mUuids.size();
	}

	/**
	 * @see org.apache.uima.collection.CollectionReader#getNext(org.apache.uima.cas.CAS)
	 */
	public void getNext(CAS aCAS) throws IOException, CollectionException {
		JCas jcas;
		try {
			jcas = aCAS.getJCas();
		} catch (CASException e) {
			throw new CollectionException(e);
		}

		// put document in CAS
		String text = fetchTextForUuid(mUuids.get(mCurrentIndex));
		logger.debug("Processing document text\n\n" + text) ;
		jcas.setDocumentText(text);

		// set language if it was explicitly specified as a configuration
		// parameter
//		if (mLanguage != null) {
//			((DocumentAnnotation) jcas.getDocumentAnnotationFs())
//					.setLanguage(mLanguage);
//		}

		// Also store location of source document in CAS. This information is
		// critical
		// if CAS Consumers will need to know where the original document
		// contents are located.
		// For example, the Semantic Search CAS Indexer writes this information
		// into the
		// search index that it creates, which allows applications that use the
		// search index to
		// locate the documents that satisfy their semantic queries.
		SourceDocumentInformation srcDocInfo = new SourceDocumentInformation(
				jcas);
		srcDocInfo.setUri(mUuids.get(mCurrentIndex));
		srcDocInfo.setOffsetInSource(0);
		srcDocInfo.setDocumentSize((int) text.length());
		srcDocInfo.setLastSegment(mCurrentIndex == mUuids.size());
		srcDocInfo.addToIndexes();
		
		mCurrentIndex++ ;

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
		return new Progress[] { new ProgressImpl(mCurrentIndex,
				mUuids.size(), Progress.ENTITIES) };
	}

	/**
	 * Gets the total number of documents that will be returned by this
	 * collection reader. This is not part of the general collection reader
	 * interface.
	 * 
	 * @return the number of documents in the collection
	 */
	public int getNumberOfDocuments() {
		return mUuids.size();
	}
	
	
	/**
	 * isCamelCase
	 * 
	 * @param input
	 * @return true or false
	 * 
	 *         Return true if and only if input is of form
	 *         ProstaticAdenocarcinoma or DiseasesAndDisorders 
	 *         
	 *         It also is lenient in recoginizing 'diseasesAndDisorders'
	 */
	public static boolean isCamelCase(String input) {
		boolean result = false;
		char[] carray = input.toCharArray();
		int stateAcceptingAnyCase = 0;
		int stateAcceptingLowerCase = 1;
		int stateFailed = 2;
		int stateCurrent = stateAcceptingAnyCase;

		for (int idx = 0; idx < carray.length; idx++) {
			char currentChar = carray[idx];
			if (stateCurrent == stateFailed) {
				break ;
			}
			else if (!Character.isLetter(currentChar)) {
				stateCurrent = stateFailed;
			}
			else if (stateCurrent == stateAcceptingAnyCase) {
				if (Character.isUpperCase(currentChar)) {
					stateCurrent = stateAcceptingLowerCase;
				}
			}
			else if (stateCurrent == stateAcceptingLowerCase) {
				if (!Character.isUpperCase(currentChar)) {
					stateCurrent = stateAcceptingAnyCase;
				}
				else {
					stateCurrent = stateFailed ;
				}
			}
		}
		result = stateCurrent != stateFailed ;
		
		return result;
	}
	
	/**
	 * unCamelCase
	 * 
	 * @param input
	 * @param isAddingWords
	 * @return ArrayList<String>
	 * 
	 *         For an input like ProstaticAdenocarcinoma this method returns
	 * 
	 *         ['prostatic adenocarcinoma']
	 * 
	 *         if the isAddingWords flag is set it will also append each
	 *         individual word
	 * 
	 *         ['prostatic adenocarcinoma', 'prostatic', 'carcinoma']
	 * 
	 */
	public static ArrayList<String> unCamelCase(String input,
			boolean isAddingWords) {
		ArrayList<String> result = new ArrayList<String>();
		if (!isCamelCase(input)) {
			result.add(input.toLowerCase());
		} else {
			StringBuffer sb = new StringBuffer();
			char[] inputChars = input.toCharArray();
			for (int idx = 0; idx < inputChars.length; idx++) {
				char currentChar = inputChars[idx];
				if (Character.isUpperCase(currentChar)) {
					if (idx > 0) {
						sb.append(' ');
					}
					sb.append(Character.toLowerCase(currentChar));
				} else {
					sb.append(currentChar);
				}
			}
			result.add(sb.toString());
			if (isAddingWords) {
				String[] words = sb.toString().split("\\s");
				for (int wdx = 0; wdx < words.length; wdx++) {
					result.add(words[wdx]);
				}
			}
		}
		return result;
	}

}

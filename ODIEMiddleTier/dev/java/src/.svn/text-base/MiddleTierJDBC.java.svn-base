package edu.pitt.dbmi.odie.middletier;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.AnalysisJDBC;
import edu.pitt.dbmi.odie.model.AnalysisLanguageResource;
import edu.pitt.dbmi.odie.model.AnalysisLanguageResourceJDBC;
import edu.pitt.dbmi.odie.model.Annotation;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.model.DocumentAnalysisJDBC;
import edu.pitt.dbmi.odie.model.Document;
import edu.pitt.dbmi.odie.model.IDocument;
import edu.pitt.dbmi.odie.model.LanguageResource;
import edu.pitt.dbmi.odie.model.utils.StatisticsUpdater;
import edu.pitt.ontology.IRepository;
import edu.pitt.ontology.protege.ProtegeRepository;

@Deprecated
public class MiddleTierJDBC extends BaseMiddleTierImpl {
	
	private Connection conn;

	private String TABLE_LANG_RES = "odie_lang_resource";

	Logger logger = Logger.getLogger(this.getClass());
	
	public static MiddleTierJDBC getInstance(Configuration conf) {
		if (singleton == null) {
			singleton = new MiddleTierJDBC();
			((MiddleTierJDBC) singleton).init(conf);
		}
		return (MiddleTierJDBC) singleton;
	}
	
	public void init(Configuration config) {
		try {
			initDBConnection(config.getDatabaseDriver(), 
							 config.getDatabaseURL(), 
							 config.getUsername(), 
							 config.getPassword());
			if (conn != null)
				createTables();
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}

		initRepositoryFromDatabase(config);
		
	}

	private void initDBConnection(String dbDriver, String dbURL, String username, String password)
			throws Exception {
		logger.info("Initializing database connection");
		Class.forName(dbDriver).newInstance();
		conn = DriverManager.getConnection(dbURL, username, password);
	}
	
	private void createTables() {

		Statement statement;
		try {
			statement = conn.createStatement();
			statement.executeUpdate(SQLStatements.SQL_CREATE_DOCUMENT);
			statement.close();

			statement = conn.createStatement();
			statement.executeUpdate(SQLStatements.SQL_CREATE_ANALYSIS);
			statement.close();

			statement = conn.createStatement();
			statement.executeUpdate(SQLStatements.SQL_CREATE_LANG_RESOURCE);
			statement.close();

			statement = conn.createStatement();
			statement.executeUpdate(SQLStatements.SQL_CREATE_ANALYSIS_DOCUMENT);
			statement.close();

			statement = conn.createStatement();
			statement.executeUpdate(SQLStatements.SQL_CREATE_ANALYSIS_LR);
			statement.close();

			statement = conn.createStatement();
			statement.executeUpdate(SQLStatements.SQL_CREATE_ANNOTATION);
			statement.close();

			statement = conn.createStatement();
			statement.executeUpdate(SQLStatements.SQL_CREATE_DATAPOINT);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

	}
	
	private void initRepositoryFromDatabase(Configuration config) {
		// TODO make rep temp location configurable. currently using user.home
		setRepository(new ProtegeRepository(config.getDatabaseDriver(), config
				.getDatabaseURL(), config.getUsername(), config.getPassword(),
				TABLE_LANG_RES, System.getProperty("user.home")));
		
		((ProtegeRepository) getRepository()).setOntologyPrefix("ontology_");
	}

	public List<Analysis> getAnalyses() {
		if(super.getAnalyses().size()==0){
			Statement st = null;
			ResultSet result = null;
			try {
				st = conn.createStatement();
				result = st.executeQuery(SQLStatements.SQL_SELECT_ALL_ANALYSIS);
				while (result.next()) {
					int id = result.getInt("ID");
					String name = result.getString("name");
					String desc = result.getString("description");
					String type = result.getString("type");
					// setup analysis object
					Analysis a = new AnalysisJDBC();
					a.setID(id);
					a.setName(name);
					a.setDescription(desc);
					a.setType(type);
					super.getAnalyses().add(a);
				}
			} catch (Exception ex) {
				logger.error(ex.getMessage());
			} finally {
				try {
					result.close();
					st.close();
				} catch (Exception ex) {
					logger.error(ex.getMessage());
				}
			}
		}
		return super.getAnalyses();
	}

	public List<AnalysisLanguageResource> getAnalysisLanguageResources(
			AnalysisJDBC analysis) {
		PreparedStatement st = null;
		ResultSet rs = null;
		List<AnalysisLanguageResource> aolist = new ArrayList<AnalysisLanguageResource>();
		try {
			st = conn.prepareStatement(SQLStatements.SQL_SELECT_ANALYSIS_LR_FOR_ANALYSIS);
			st.setInt(1, analysis.getID());

			rs = st.executeQuery();
			while (rs.next()) {
				AnalysisLanguageResourceJDBC ao = new AnalysisLanguageResourceJDBC();
				ao.setID(rs.getInt("ID"));
				ao.setAnalysisId(rs.getInt("analysis_id"));
				ao.setAnalysis(analysis);
				ao.setOntologyId(rs.getInt("lr_id"));
				ao.setIsProposalOntology(rs.getBoolean("is_proposal"));
				aolist.add(ao);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			aolist = null;
		} finally {
			try {
				rs.close();
				st.close();
			} catch (Exception ex) {
				logger.error(ex.getMessage());
			}
		}
		return aolist;
	}

	public SortedSet<Annotation> getAnnotations(AnalysisJDBC analysisImpl) {
		PreparedStatement st = null;
		ResultSet rs = null;
		HashMap<Integer, DocumentAnalysisJDBC> damap = new HashMap<Integer, DocumentAnalysisJDBC>();

		SortedSet<Annotation> annlist = new TreeSet<Annotation>();
		try {
			st = conn.prepareStatement(SQLStatements.SQL_SELECT_ANNOTATION_FOR_ANALYSIS);
			st.setInt(1, analysisImpl.getID());
			rs = st.executeQuery();
			while (rs.next()) {
				Annotation ann = new Annotation();
				ann.setID(rs.getInt("a.id"));
				ann.setStartOffset(rs.getInt("a.start_offset"));
				ann.setEndOffset(rs.getInt("a.end_offset"));
				ann.setMetadata(rs.getString("a.metadata"));
				String uristr = rs.getString("a.type_uri");
				ann.setAnnotationClassURI(new URI(uristr));

				int adid = rs.getInt("ad.id");

				DocumentAnalysisJDBC da = damap.get(new Integer(adid));
				if (da == null) {
					da = new DocumentAnalysisJDBC(analysisImpl, adid);
					da.setAnnotations(new TreeSet<Annotation>());
					damap.put(new Integer(adid), da);
				}
				ann.setDocumentAnalysis(da);
				da.getAnnotations().add(ann);
				annlist.add(ann);
			}
			rs.close();
			st.close();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			annlist = null;
		} finally {
			try {
				rs.close();
				st.close();
			} catch (Exception e1) {
				e1.printStackTrace();
				logger.error(e1.getMessage());
			}
		}
		return annlist;
	}

	public List<Annotation> getAnnotations(AnalysisJDBC analysisImpl, URI uri) {
		PreparedStatement st = null;
		ResultSet rs = null;

		List<Annotation> annlist = null;
		try {
			st = conn
					.prepareStatement(SQLStatements.SQL_SELECT_ANNOTATION_FOR_ANALYSIS_AND_URI);
			st.setInt(1, analysisImpl.getID());
			st.setString(2, uri.toString());
			rs = st.executeQuery();
			annlist = new ArrayList<Annotation>();
			while (rs.next()) {
				Annotation ann = new Annotation();
				ann.setID(rs.getInt("a.id"));
				ann.setStartOffset(rs.getInt("a.start_offset"));
				ann.setEndOffset(rs.getInt("a.end_offset"));
				ann.setMetadata(rs.getString("a.metadata"));
				ann.setDocumentAnalysis(new DocumentAnalysisJDBC(analysisImpl,
						rs.getInt("ad.id")));
				String uristr = rs.getString("a.type_uri");
				ann.setAnnotationClassURI(new URI(uristr));

				annlist.add(ann);
			}
			rs.close();
			st.close();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			annlist = null;
		} finally {
			try {
				rs.close();
				st.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return annlist;

	}

	public SortedSet<Annotation> getAnnotations(
			DocumentAnalysisJDBC documentAnalysis) {
		PreparedStatement st = null;
		ResultSet rs = null;

		SortedSet<Annotation> annlist = null;
		try {
			st = conn
					.prepareStatement(SQLStatements.SQL_SELECT_ANNOTATION_FOR_ANALYSIS_DOCUMENT);
			st.setInt(1, documentAnalysis.getID());

			rs = st.executeQuery();
			annlist = new TreeSet<Annotation>();
			while (rs.next()) {
				Annotation ann = new Annotation();
				ann.setID(rs.getInt("ID"));
				ann.setStartOffset(rs.getInt("start_offset"));
				ann.setEndOffset(rs.getInt("end_offset"));
				ann.setMetadata(rs.getString("metadata"));
				ann.setDocumentAnalysis(documentAnalysis);
				String uristr = rs.getString("type_uri");
				ann.setAnnotationClassURI(new URI(uristr));

				annlist.add(ann);
			}
			rs.close();
			st.close();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			annlist = null;
		} finally {
			try {
				rs.close();
				st.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return annlist;

	}

	public IDocument getDocument(DocumentAnalysisJDBC documentAnalysis) {
		PreparedStatement st = null;
		ResultSet rs = null;

		IDocument doc = null;

		try {
			st = conn.prepareStatement(SQLStatements.SQL_SELECT_DOCUMENT_FOR_ID);
			st.setInt(1, documentAnalysis.getDocumentID());

			rs = st.executeQuery();
			if (rs.next()) {
				doc = new Document();
				doc.setID(rs.getInt("ID"));
				doc.setName(rs.getString("NAME"));
				doc.setURI(new URI(rs.getString("URI")));
				rs.close();
				st.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
			} catch (Exception e1) {
				e1.printStackTrace();
				logger.error(e1.getMessage());
			}
		}
		return doc;
	}

	public List<AnalysisDocument> getDocumentAnalyses(Analysis analysis) {
		PreparedStatement st = null;
		ResultSet rs = null;
		List<AnalysisDocument> dalist = new ArrayList<AnalysisDocument>();
		try {
			st = conn
					.prepareStatement(SQLStatements.SQL_SELECT_ANALYSIS_DOCUMENT_FOR_ANALYSIS);
			st.setInt(1, analysis.getID());

			rs = st.executeQuery();
			while (rs.next()) {
				DocumentAnalysisJDBC da = new DocumentAnalysisJDBC(analysis, rs
						.getInt("ID"));
				da.setAnalysisID(analysis.getID());
				da.setDocumentID(rs.getInt("document_id"));
				da.setStatus(rs.getString("STATUS"));
				dalist.add(da);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			dalist = null;
		} finally {
			try {
				rs.close();
				st.close();
			} catch (Exception ex) {
				logger.error(ex.getMessage());;
//				logger.error(ex.getMessage());
			}
		}
		return dalist;

	}

	public LanguageResource getLanguageResource(AnalysisLanguageResourceJDBC ao) {
		PreparedStatement st = null;
		ResultSet rs = null;

		LanguageResource ont = null;

		try {
			st = conn.prepareStatement(SQLStatements.SQL_SELECT_LR_FOR_ID);
			st.setInt(1, ao.getOntologyId());

			rs = st.executeQuery();
			if (rs.next()) {
				ont = new LanguageResource();
				ont.setID(rs.getInt("ID"));
				ont.setName(rs.getString("name"));
				ont.setType(rs.getString("type"));
				ont.setURI(new URI(rs.getString("uri")));
				ont.setDescription(rs.getString("description"));
				ont.setFormat(rs.getString("format"));
				ont.setLocation(rs.getString("location"));
				ont.setVersion(rs.getString("version"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
			} catch (Exception e1) {
				e1.printStackTrace();
				logger.error(e1.getMessage());
			}
		}
		return ont;

	}

	protected boolean addAnalysisInternal(Analysis analysis) {
		
		try {

			// /////insert new analysis
			insertAnalysis(analysis);

			// ////// fetch the newly created analysis' id.
			analysis.setID(getAnalysisID(analysis));

			// ////////// add document analysis rows
			List<AnalysisDocument> dalist = analysis.getAnalysisDocuments();

			for (AnalysisDocument da : dalist) {
				IDocument doc = da.getDocument();

				if (doc != null) {

					// add document if required, get id if already added
					PreparedStatement st = conn.prepareStatement(SQLStatements.SQL_SELECT_DOCUMENT_FOR_URI);
					st.setString(1, doc.getURI().toString());
					ResultSet rs = st.executeQuery();
					if (rs.next()) {
						doc.setID(rs.getInt("ID"));
					} else {
						addDocumentInternal(doc);
					}
					st.close();
					

					// add document_analysis row
					st = conn.prepareStatement(SQLStatements.SQL_INSERT_ANALYSIS_DOCUMENT);
					st.setInt(1, analysis.getID());
					st.setInt(2, doc.getID());
					st.setString(3, da.getStatus());
					st.executeUpdate();
					st.close();

					// get id for newly added row
					st = conn
							.prepareStatement(SQLStatements.SQL_SELECT_ANALYSIS_DOCUMENT_FOR_ANALYSIS_AND_DOCUMENT);
					st.setInt(1, analysis.getID());
					st.setInt(2, doc.getID());
					rs = st.executeQuery();
					if (rs.next()) {
						da.setID(rs.getInt("ID"));
					}
					st.close();
				}
			}

			// ////////// add analysis ontology rows
			List<AnalysisLanguageResource> olist = analysis
					.getAnalysisLanguageResources();

			for (AnalysisLanguageResource ao : olist) {
				LanguageResource o = ao.getLanguageResource();

				if (o != null) {

					// add ontology if required, get id if already added
					PreparedStatement st = conn.prepareStatement(SQLStatements.SQL_SELECT_LR_FOR_URI);
					st.setString(1, o.getURI().toString());
					ResultSet rs = st.executeQuery();
					if (rs.next()) {
						o.setID(rs.getInt("ID"));
					} else {
						st.close();
						return false;
					}
					st.close();

					// add analysis ontology row
					st = conn.prepareStatement(SQLStatements.SQL_INSERT_ANALYSIS_LR);
					st.setInt(1, analysis.getID());
					st.setInt(2, o.getID());
					st.setBoolean(3, ao.isProposalOntology());
					st.executeUpdate();
					st.close();

					// get id for newly added row
					st = conn
							.prepareStatement(SQLStatements.SQL_SELECT_ANALYSIS_LR_FOR_ANALYSIS_AND_LR);
					st.setInt(1, analysis.getID());
					st.setInt(2, o.getID());
					rs = st.executeQuery();
					if (rs.next()) {
						ao.setID(rs.getInt("ID"));
					}
					st.close();
				}
			}
			
			super.addAnalysisInternal(analysis);
			return true;

		} catch (Exception ex) {
			logger.error(ex.getMessage());;
			return false;
		}
	}


	private void insertAnalysis(Analysis analysis) throws SQLException {
		PreparedStatement st = conn.prepareStatement(SQLStatements.SQL_INSERT_ANALYSIS);
		st.setString(1, analysis.getName());
		st.setString(2, analysis.getDescription());
		st.setString(3, analysis.getType());
		st.executeUpdate();
		st.close();
	}

	private int getAnalysisID(Analysis analysis)
			throws SQLException {
		PreparedStatement st;
		st = conn.prepareStatement(SQLStatements.SQL_SELECT_ANALYSIS);
		st.setString(1, analysis.getName());
		ResultSet rs = st.executeQuery();

		int retValue = -1;
		if (rs.next()) {
			retValue = rs.getInt("ID");
		}
		rs.close();
		st.close();
		return retValue;
	}

	protected boolean updateAnalysisInternal(Analysis analysis) {

		if (analysis.getID() == 0)
			return false;

		PreparedStatement st = null;
		try {

			// /////insert new analysis
			st = conn.prepareStatement(SQLStatements.SQL_UPDATE_ANALYSIS);
			st.setString(1, analysis.getName());
			st.setString(2, analysis.getDescription());
			st.setString(3, analysis.getType());
			st.setInt(4, analysis.getID());
			st.executeUpdate();
			st.close();

			// ////////// update document analysis rows
			List<AnalysisDocument> dalist = analysis.getAnalysisDocuments();

			for (AnalysisDocument da : dalist) {
				// update document_analysis row
				st = conn.prepareStatement(SQLStatements.SQL_UPDATE_ANALYSIS_DOCUMENT);
				st.setString(1, da.getStatus());
				st.setInt(2, da.getID());
				st.executeUpdate();
				st.close();
			}
			return true;

		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return false;
		} finally {
			try {
				st.close();
			} catch (Exception ex) {
				logger.error(ex.getMessage());
			}
		}
	}
	
	private boolean addDocumentInternal(IDocument doc) throws SQLException {
		PreparedStatement st = conn.prepareStatement(SQLStatements.SQL_INSERT_DOCUMENT);
		st.setString(1, doc.getName());
		st.setString(2, doc.getURI().toString());
		st.executeUpdate();

		// get id for newly added row
		st = conn.prepareStatement(SQLStatements.SQL_SELECT_DOCUMENT_FOR_URI);
		st.setString(1, doc.getURI().toString());
		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			doc.setID(rs.getInt("ID"));
			rs.close();
			st.close();
			return true;
		} else {
			st.close();
			return false;
		}

	}
	
	protected boolean updateDocumentAnalysisInternal(AnalysisDocument da) {
		if (da.getID() < 0)
			return false;

		PreparedStatement st = null;
		try {
			// //// Update DocumentAnalysis

			st = conn.prepareStatement(SQLStatements.SQL_UPDATE_ANALYSIS_DOCUMENT);
			st.setString(1, da.getStatus());
			st.setInt(2, da.getID());
			st.executeUpdate();
			st.close();

			// //// Insert Datapoints
			for (Annotation ann : da.getAnnotations()) {
				if (ann.getID() < 1) {
					// check if already exists
					st = conn
							.prepareStatement(SQLStatements.SQL_SELECT_ANNOTATION_FOR_ANALYSIS_DOCUMENT_OFFSETS_AND_URI);
					st.setInt(1, da.getID());
					st.setString(2, ann.getAnnotationClassURI().toString());
					st.setInt(3, ann.getStartOffset());
					st.setInt(4, ann.getEndOffset());
					ResultSet rs = st.executeQuery();
					if (!rs.next()) {
						rs.close();
						st.close();// close prev. statement

						st = conn.prepareStatement(SQLStatements.SQL_INSERT_ANNOTATION);
						st.setInt(1, ann.getStartOffset());
						st.setInt(2, ann.getEndOffset());
						st.setString(3, ann.getMetadata());
						st.setString(4, ann.getAnnotationClassURI().toString());
						st.setInt(5, da.getID());
						st.executeUpdate();
						st.close();

						st = conn.prepareStatement(SQLStatements.SQL_INSERT_DATAPOINT);
						st.setInt(1, da.getID());
						st.setString(2, ann.getAnnotationClassURI().toString());
						st.executeUpdate();
						st.close();
					} else {
						rs.close();
						st.close();
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			try {
				st.close();
			} catch (Exception e1) {
				e1.printStackTrace();
				logger.error(e1.getMessage());
			}
			return false;
		}

		return true;
	}

	protected boolean addLanguageResource(LanguageResource lr) {
		PreparedStatement st = null;
		// /////insert new analysis
		try {
			st = conn.prepareStatement(SQLStatements.SQL_INSERT_LR);
			st.setString(1, lr.getURI().toString());
			st.setString(2, lr.getName());
			st.setString(3, lr.getType());
			st.setString(4, lr.getLocation());
			st.setString(5, lr.getFormat());
			st.setString(6, lr.getDescription());
			st.setString(7, lr.getVersion());
			st.executeUpdate();
			st.close();

			// ////// fetch the newly created analysis' id.
			st = conn.prepareStatement(SQLStatements.SQL_SELECT_LR_FOR_URI);
			st.setString(1, lr.getURI().toString());
			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				lr.setID(rs.getInt("ID"));
				rs.close();
			} else
				return false;
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		}

		return true;
	}

	public void loadDocumentAnalysisFromDatabase(DocumentAnalysisJDBC da) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(SQLStatements.SQL_SELECT_ANALYSIS_DOCUMENT_FOR_ID);
			st.setInt(1, da.getID());

			rs = st.executeQuery();
			if (rs.next()) {
				da.setAnalysisID(rs.getInt("analysis_id"));
				da.setDocumentID(rs.getInt("document_id"));
				da.setStatus(rs.getString("STATUS"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
			} catch (Exception ex) {
				logger.error(ex.getMessage());;
//				logger.error(ex.getMessage());
			}
		}

	}
	
	public void dispose() {
		super.dispose();
		try {
			if (conn != null)
				conn.close();
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
	}
	
	public static void main(String[] args) {
		Configuration config = new Configuration();
		config
				.setRepositoryConfigLocation("/home/tseytlin/Work/ontologies/repository.3.4.conf");
		config.setDatabaseDriver("com.mysql.jdbc.Driver");
		// config.setDatabaseURL("jdbc:mysql://localhost:3306/odie");
		config
				.setDatabaseURL("jdbc:mysql://1upmc-opi-cab52:3307/odie_analysis_4");
		config.setUsername("caties");
		config.setPassword("caties");

		MiddleTierJDBC mt = MiddleTierJDBC.getInstance(config);

		testCreation(mt);
		 testInsertUpdate(mt);
		 testRetrieval(mt);
	}

	
	private static void testCreation(MiddleTierJDBC mt) {
		mt.createTables();

	}
	
	private static void testInsertUpdate(MiddleTierJDBC mt) {

		AnalysisJDBC analysis = new AnalysisJDBC();
		analysis.setName("A test analysis");
		analysis.setDescription("This is a test analysis");
		analysis.setType(Analysis.TYPE_NER);

		try {
			List<AnalysisLanguageResource> olist = new ArrayList<AnalysisLanguageResource>();

			LanguageResource lr = new LanguageResource();
			lr.setName("LR1");
			lr.setDescription("This is LR1");
			lr.setURI(new URI("someuri1/lr1"));
			lr.setFormat(IRepository.FORMAT_DATABASE);
			lr.setType(IRepository.TYPE_ONTOLOGY);
			lr.setVersion("1.0");
			lr.setLocation("Some Location 1");
			mt.addLanguageResource(lr);

			AnalysisLanguageResource alr = new AnalysisLanguageResource();
			alr.setAnalysis(analysis);
			alr.setLanguageResource(lr);
			olist.add(alr);

			lr = new LanguageResource();
			lr.setName("LR2");
			lr.setURI(new URI("someuri1/lr2"));
			lr.setDescription("This is LR2");
			lr.setFormat(IRepository.FORMAT_DATABASE);
			lr.setType(IRepository.TYPE_SYSTEM_ONTOLOGY);
			lr.setVersion("2.0");
			lr.setLocation("Some Location 2");
			mt.addLanguageResource(lr);

			alr = new AnalysisLanguageResource();
			alr.setAnalysis(analysis);
			alr.setLanguageResource(lr);
			olist.add(alr);

			lr = new LanguageResource();
			lr.setName("LR3");
			lr.setURI(new URI("someuri1/lr3"));
			lr.setDescription("This is LR3");
			lr.setFormat(IRepository.FORMAT_DATABASE);
			lr.setType(IRepository.TYPE_TERMINOLOGY);
			lr.setVersion("3.0");
			lr.setLocation("Some Location 3");
			mt.addLanguageResource(lr);

			alr = new AnalysisLanguageResource();
			alr.setAnalysis(analysis);
			alr.setLanguageResource(lr);
			olist.add(alr);

			analysis.setAnalysisLanguageResources(olist);
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		List<AnalysisDocument> list = new ArrayList<AnalysisDocument>();

		Document doc = new Document();
		File f = new File("c:/dummy/report1.txt");
		doc.setURI(f.toURI());
		doc.setName("report1.txt");
		AnalysisDocument da = new AnalysisDocument(analysis, doc);
		list.add(da);

		doc = new Document();
		f = new File("c:/dummy/report2.txt");
		doc.setURI(f.toURI());
		doc.setName("report2.txt");
		da = new AnalysisDocument(analysis, doc);
		list.add(da);

		doc = new Document();
		f = new File("c:/dummy/report3.txt");
		doc.setURI(f.toURI());
		doc.setName("report3.txt");
		AnalysisDocument da1 = new AnalysisDocument(analysis, doc);
		list.add(da1);

		analysis.setAnalysisDocuments(list);

		mt.addAnalysisInternal(analysis);

		SortedSet<Annotation> annlist = new TreeSet<Annotation>();
		Annotation ann;
		try {
			StatisticsUpdater updater = new StatisticsUpdater(analysis
					.getStatistics());
			ann = new Annotation();
			ann.setAnnotationClassURI(new URI("Some/Ontology/Class1"));
			ann.setDocumentAnalysis(da);
			ann.setStartOffset(10);
			ann.setEndOffset(20);
			annlist.add(ann);
			updater.updateStatistics(ann);
			da.setAnnotations(annlist);
			mt.updateDocumentAnalysisInternal(da);

			ann = new Annotation();
			ann.setAnnotationClassURI(new URI("Some/Ontology/Class2"));
			ann.setDocumentAnalysis(da1);
			ann.setStartOffset(30);
			ann.setEndOffset(40);
			annlist.add(ann);
			updater.updateStatistics(ann);

			ann = new Annotation();
			ann.setAnnotationClassURI(new URI("Some/Ontology/Class3"));
			ann.setDocumentAnalysis(da1);
			ann.setStartOffset(40);
			ann.setEndOffset(50);
			annlist.add(ann);
			da1.setAnnotations(annlist);
			updater.updateStatistics(ann);

			mt.updateDocumentAnalysisInternal(da1);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		da.setAnnotations(annlist);
	}
	
	private static void testRetrieval(MiddleTierJDBC mt) {
		List<Analysis> alist = mt.getAnalyses();
		System.out.println(alist.size() + " analyses found");

		Analysis a = alist.get(0);

		System.out.println(a.getAnalysisLanguageResources().size()
				+ " lr for the analysis");

		System.out.println(a.getAnalysisDocuments().size()
				+ " doc analyses for the analysis");

		AnalysisDocument da = a.getAnalysisDocuments().get(2);

		System.out.println(da.getAnnotations().size()
				+ " annotation for the da");
		System.out.println(da.getDocument().getName()
				+ " is the name of the document");

		System.out.println(a.getStatistics().getDatapoints().size()
				+ " datapoints for the analysis");

	}

	@Override
	protected boolean removeAnalysisInternal(Analysis analysis) {
		PreparedStatement st = null;
		
		boolean success = false;

		try {
			st = conn.prepareStatement(SQLStatements.SQL_DELETE_ANALYSIS_FOR_ID);
			st.setInt(1, analysis.getID());
			st.executeUpdate();
			success = true;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			success = false;
		} finally {
			try {
				st.close();
			} catch (Exception e1) {
				e1.printStackTrace();
				logger.error(e1.getMessage());
			}
		}
		if(success)
			super.removeAnalysisInternal(analysis);

		return success;
	}
	
}

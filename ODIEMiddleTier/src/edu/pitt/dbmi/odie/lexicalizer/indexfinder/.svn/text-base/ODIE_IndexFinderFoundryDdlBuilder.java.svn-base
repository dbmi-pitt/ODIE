package edu.pitt.dbmi.odie.lexicalizer.indexfinder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 * 
 * @author mitchellkj
 * 
 *         The Foundry Builder traverses the ontology visiting each named class
 *         once and using the terminology associated with that class to seed the
 *         Named Entity Recognition engine of ODIE which is a modified version
 *         of the IndexFinder method.
 * 
 *         The primary component of the IndexFinder representation is the
 *         inverse index of word to phrase, here populated in the table
 *         if_wid2pids
 * 
 *         Build sorted phrase table. Phrase here is used loosely to be the
 *         equivalent of a term of a possibly multiple term class identifier.
 * 
 *         Foreach phrase Add the the phrase to a word histogram that includes a
 *         text field of phrase identifiers (pids). Make use of the invariant
 *         condition that phrases are accessible in order
 */

public class ODIE_IndexFinderFoundryDdlBuilder {

	private static final Logger logger = Logger
			.getLogger(ODIE_IndexFinderFoundryDdlBuilder.class);

	private Connection connection = null;
	private static ODIE_IndexFinderFoundryDdlBuilder singleton = null;

	public static ODIE_IndexFinderFoundryDdlBuilder getInstance() {
		if (singleton == null) {
			singleton = new ODIE_IndexFinderFoundryDdlBuilder();
		}
		return singleton;
	}

	private ODIE_IndexFinderFoundryDdlBuilder() {
	}

	public void createTables() {
		logger.debug("Creating tables.");
		createNamespace();
		createCui2Cls();
		createCuis();
		createPhraseTable("if_phrase");
		createWordHt() ;
		createWid2Pids();
		createPLen();
	}

	public void createNamespace() {
		try {
			dropTableIfExists("if_namespace");

			String sql = "";
			sql = "CREATE TABLE IF NOT EXISTS if_namespace ( "
					+ "   cui int NOT NULL AUTO_INCREMENT, "
					+ "   namespace text, " + "   PRIMARY KEY (cui)" + " ) ";

			PreparedStatement statement = connection.prepareStatement(sql);
			statement.executeUpdate();

			sql = "ALTER TABLE if_namespace AUTO_INCREMENT=1";
			statement = connection.prepareStatement(sql);
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createCui2Cls() {
		try {
			dropTableIfExists("if_cui2cls");

			String sql = "";
			sql = "create table IF NOT EXISTS if_cui2cls ( "
					+ "   cui int NOT NULL AUTO_INCREMENT," + "   nsid int, "
					+ "   parents text, " + "   ancestory text, "
					+ "   cname text, " + "   PRIMARY KEY (cui)" + " )";
			sql = "create table IF NOT EXISTS if_cui2cls ( "
					+ "   cui int NOT NULL AUTO_INCREMENT," + "   nsid int, "
					+ "   parents varchar(2048), "
					+ "   ancestory varchar(2048), "
					+ "   cname varchar(2048), " + "   PRIMARY KEY (cui)"
					+ " ) " + "   ENGINE = MYISAM";

			PreparedStatement statement = connection.prepareStatement(sql);
			statement.executeUpdate();

			sql = "CREATE INDEX idx_nsid_cname USING BTREE ON if_cui2cls (nsid,cname(128))";
			statement = connection.prepareStatement(sql);
			statement.executeUpdate();

			sql = "ALTER TABLE if_cui2cls AUTO_INCREMENT=1000000";
			statement = connection.prepareStatement(sql);
			statement.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createCuis() {
		try {
			dropTableIfExists("if_cuis");

			String sql = "";
			sql = "create table IF NOT EXISTS if_cuis ( " + "   pid int, "
					+ "   cui int" + " ) ";
			sql = "create table IF NOT EXISTS if_cuis ( " + "   pid int, "
					+ "   cui int" + " ) ENGINE = MYISAM";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.executeUpdate();

		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public void createPhraseTable(String tableName) {
		try {
			dropTableIfExists(tableName);

			String sql = "";
			sql = "create table IF NOT EXISTS " + tableName + "( "
					+ "   pid int not null AUTO_INCREMENT PRIMARY KEY, "
					+ "   words text, " + "   word_count int, " + "   cui int"
					+ " ) ";
			sql = "create table IF NOT EXISTS " + tableName + "( "
					+ "   pid int not null AUTO_INCREMENT PRIMARY KEY, "
					+ "   words text, " + "   word_count int, " + "   cui int"
					+ " ) ENGINE = MYISAM";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.executeUpdate();

			sql = "create index " + tableName + "_idx on " + tableName
					+ "(word_count, words(1028), cui)";
			Statement createIndexStatement = connection.createStatement();
			createIndexStatement.executeUpdate(sql);
			createIndexStatement.close();

		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public void createWordHt() {
		try {
			dropTableIfExists("if_word_ht");
			String sql = "CREATE TABLE IF NOT EXISTS if_word_ht ("
					+ " wid int NOT NULL AUTO_INCREMENT,"
					+ " word varchar(1000) DEFAULT NULL,"
					+ " PRIMARY KEY (wid)," + " KEY if_word_ht_idx (word)"
					+ " ) ENGINE = MyISAM DEFAULT CHARSET=latin1 ";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.executeUpdate();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public void createWid2Pids() {
		try {
			dropTableIfExists("if_wid2pids");
			String sql = "";
			sql = "create table IF NOT EXISTS if_wid2pids ( " + "   wid int, "
					+ "   pids varchar(50000) default 'ODIE-UNDEFINED', "
					+ "   PRIMARY KEY (wid) "
					+ " ) ENGINE = MyISAM DEFAULT CHARSET=latin1 ";
			sql = "create table IF NOT EXISTS if_wid2pids ( " + "   wid int, "
					+ "   pids text, "
					+ "   PRIMARY KEY (wid) "
					+ " ) ENGINE = MyISAM DEFAULT CHARSET=latin1 ";

			PreparedStatement statement = connection.prepareStatement(sql);
			statement.executeUpdate();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public void createPLen() {
		try {
			dropTableIfExists("if_plen");
			String sql = "";
			sql = "create table IF NOT EXISTS if_plen ( " + "   len int, "
					+ "   uppid int" + " ) ";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void dropTableIfExists(String tableName) {
		try {
			String sql = "DROP TABLE IF EXISTS " + tableName;
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clearTables() {
		try {
			final String[] tableNames = { "if_namespace", "if_cui2cls",
					"if_cuis", "if_phrase", "if_word_ht", "if_wid2pids",
					"if_plen" };
			for (int idx = 0; idx < tableNames.length; idx++) {
				String tableName = tableNames[idx];
				String sqlUpdate = "delete from " + tableName;
				PreparedStatement pstmt = this.connection
						.prepareStatement(sqlUpdate);
				pstmt.executeUpdate();
				pstmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConn() {
		return connection;
	}

	public void setConn(Connection conn) {
		this.connection = conn;
	}

}

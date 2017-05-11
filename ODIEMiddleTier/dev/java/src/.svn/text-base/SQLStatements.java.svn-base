/**
 * @author Girish Chavan
 *
 * Oct 14, 2008
 */
package edu.pitt.dbmi.odie.middletier;

public class SQLStatements {
	public static final String SQL_DELETE_ANALYSIS_FOR_ID = "DELETE FROM `odie_analysis` WHERE id = ?";

	public static String SQL_CREATE_ANALYSIS = "CREATE TABLE IF NOT EXISTS `odie_analysis` ("
		+ "`id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
		+ "`name` varchar(45) NOT NULL,"
		+ "`description` varchar(255) DEFAULT NULL,"
		+ "`type` varchar(45) NOT NULL,"
		+ "PRIMARY KEY (`id`)) "
		+ "ENGINE=InnoDB DEFAULT CHARSET=latin1";

	public static String SQL_CREATE_ANALYSIS_DOCUMENT = "CREATE TABLE IF NOT EXISTS `odie_analysis_document` ("
			+ "`id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
			+ "`analysis_id` int(10) unsigned NOT NULL,"
			+ "`document_id` int(10) unsigned NOT NULL,"
			+ "`status` varchar(45) NOT NULL,"
			+ "PRIMARY KEY (`id`),"
			+ "KEY `FK_odie_analysis_document_1` (`analysis_id`),"
			+ "KEY `FK_odie_analysis_document_2` (`document_id`),"
			+ "CONSTRAINT `FK_odie_analysis_document_1` "
			+ "FOREIGN KEY (`analysis_id`) "
			+ "REFERENCES `odie_analysis` (`id`) ON DELETE CASCADE,"
			+ "CONSTRAINT `FK_odie_analysis_document_2` "
			+ "FOREIGN KEY (`document_id`) "
			+ "REFERENCES `odie_document` (`id`) ON DELETE CASCADE) "
			+ "ENGINE=InnoDB DEFAULT CHARSET=latin1";
	
	public static String SQL_CREATE_ANALYSIS_LR = "CREATE TABLE IF NOT EXISTS `odie_analysis_lr` ("
			+ "`id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
			+ "`analysis_id` int(10) unsigned NOT NULL,"
			+ "`lr_id` int(10) unsigned NOT NULL,"
			+ "`is_proposal` tinyint(1) NOT NULL,"
			+ "PRIMARY KEY (`id`),"
			+ "KEY `FK_odie_analysis_lr_1` (`analysis_id`),"
			+ "KEY `FK_odie_analysis_lr_2` (`lr_id`),"
			+ "CONSTRAINT `FK_odie_analysis_lr_1` "
			+ "FOREIGN KEY (`analysis_id`) "
			+ "REFERENCES `odie_analysis` (`id`) ON DELETE CASCADE,"
			+ "CONSTRAINT `FK_odie_analysis_lr_2` "
			+ "FOREIGN KEY (`lr_id`) "
			+ "REFERENCES `odie_lang_resource` (`id`) ON DELETE CASCADE) "
			+ "ENGINE=InnoDB DEFAULT CHARSET=latin1";
	
	public static String SQL_CREATE_ANNOTATION = "CREATE TABLE IF NOT EXISTS `odie_annotation` ("
			+ "`id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
			+ "`start_offset` int(10) unsigned NOT NULL,"
			+ "`end_offset` int(10) unsigned NOT NULL,"
			+ "`metadata` varchar(255),"
			+ "`type_uri` varchar(1024) NOT NULL,"
			+ "`analysis_document_id` int(10) unsigned NOT NULL,"
			+ "PRIMARY KEY (`id`),"
			+ "KEY `FK_odie_annotation_2` (`analysis_document_id`) USING BTREE,"
			+ "CONSTRAINT `FK_odie_annotation_1` "
			+ "FOREIGN KEY (`analysis_document_id`) "
			+ "REFERENCES `odie_analysis_document` (`id`) ON DELETE CASCADE) "
			+ "ENGINE=InnoDB DEFAULT CHARSET=latin1";
	
	public static String SQL_CREATE_DATAPOINT = "CREATE TABLE  IF NOT EXISTS `odie_datapoint` ("
			+ "`id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
			+ "`analysis_document_id` int(10) unsigned NOT NULL,"
			+ "`annotation_type_uri` varchar(255) NOT NULL,"
			+ "PRIMARY KEY (`id`),"
			+ "KEY `FK_odie_datapoint_1` (`analysis_document_id`),"
			+ "CONSTRAINT `FK_odie_datapoint_1` "
			+ "FOREIGN KEY (`analysis_document_id`) "
			+ "REFERENCES `odie_analysis_document` (`id`) ON DELETE CASCADE) "
			+ "ENGINE=InnoDB DEFAULT CHARSET=latin1";
	
	public static String SQL_CREATE_DOCUMENT = "CREATE TABLE  IF NOT EXISTS `odie_document` ("
			+ "`id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
			+ "`name` varchar(45) NOT NULL,"
			+ "`uri` varchar(1024) NOT NULL,"
			+ "PRIMARY KEY (`id`)) " + "ENGINE=InnoDB DEFAULT CHARSET=latin1";
	
	public static String SQL_CREATE_LANG_RESOURCE = "CREATE TABLE IF NOT EXISTS `odie_lang_resource` ("
			+ "`id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
			+ "`uri` varchar(1024) NOT NULL,"
			+ "`name` varchar(45) NOT NULL,"
			+ "`type` varchar(45) NOT NULL,"
			+ "`location` varchar(1024) NOT NULL,"
			+ "`format` varchar(45) NOT NULL,"
			+ "`description` varchar(1024) DEFAULT NULL,"
			+ "`version` varchar(45) DEFAULT NULL,"
			+ "PRIMARY KEY (`id`)) "
			+ "ENGINE=InnoDB DEFAULT CHARSET=latin1;";
	
	public static String SQL_INSERT_ANALYSIS = "INSERT INTO `odie_analysis` (name,description,type) VALUES(?,?,?)";
	
	public static String SQL_INSERT_ANALYSIS_DOCUMENT = "INSERT INTO `odie_analysis_document` (analysis_id,document_id,status) VALUES(?,?,?)";
	
	public static String SQL_INSERT_ANALYSIS_LR = "INSERT INTO `odie_analysis_lr` (analysis_id,lr_id,is_proposal) VALUES(?,?,?)";
	
	public static String SQL_INSERT_ANNOTATION = "INSERT INTO `odie_annotation` (start_offset,end_offset,metadata,type_uri,analysis_document_id) VALUES(?,?,?,?,?)";
	
	public static String SQL_INSERT_DATAPOINT = "INSERT INTO `odie_datapoint` (analysis_document_id, annotation_type_uri) "
			+ "VALUES(?,?)";
	
	public static String SQL_INSERT_DOCUMENT = "INSERT INTO `odie_document` (name,uri) VALUES(?,?)";
	
	public static String SQL_INSERT_LR = "INSERT INTO `odie_lang_resource` (uri,name,type,location,format,description,version) VALUES(?,?,?,?,?,?,?)";
	
	public static String SQL_SELECT_ALL_ANALYSIS = "SELECT * FROM `odie_analysis`";
	
	public static String SQL_SELECT_ANALYSIS = SQL_SELECT_ALL_ANALYSIS
			+ " WHERE name=?";
	
	public static String SQL_SELECT_ANALYSIS_DOCUMENT_FOR_ANALYSIS = "SELECT * FROM `odie_analysis_document` WHERE analysis_id=?";
	
	public static String SQL_SELECT_ANALYSIS_DOCUMENT_FOR_ANALYSIS_AND_DOCUMENT = "SELECT * FROM `odie_analysis_document` WHERE analysis_id=? AND document_id=?";
	
	public static String SQL_SELECT_ANALYSIS_DOCUMENT_FOR_ID = "SELECT * FROM `odie_analysis_document` WHERE id=?";
	
	public static String SQL_SELECT_ANALYSIS_LR_FOR_ANALYSIS = "SELECT * FROM `odie_analysis_lr` WHERE analysis_id=?";
	
	public static String SQL_SELECT_ANALYSIS_LR_FOR_ANALYSIS_AND_LR = "SELECT * FROM `odie_analysis_lr` WHERE analysis_id=? AND lr_id=?";
	
	public static String SQL_SELECT_ANNOTATION_FOR_ANALYSIS = "SELECT * FROM `odie_annotation` a,`odie_analysis_document` ad "
			+ "WHERE a.analysis_document_id = ad.id AND "
			+ "ad.analysis_id = ?";
	
	public static String SQL_SELECT_ANNOTATION_FOR_ANALYSIS_AND_URI = SQL_SELECT_ANNOTATION_FOR_ANALYSIS
			+ " AND a.type_uri = ?";
	
	public static String SQL_SELECT_ANNOTATION_FOR_ANALYSIS_DOCUMENT = "SELECT * FROM `odie_annotation` WHERE analysis_document_id=?";
	
	public static String SQL_SELECT_ANNOTATION_FOR_ANALYSIS_DOCUMENT_OFFSETS_AND_URI = SQL_SELECT_ANNOTATION_FOR_ANALYSIS_DOCUMENT
			+ " AND type_uri = ? " + "AND start_offset = ? AND end_offset = ?";
	
	public static String SQL_SELECT_DOCUMENT_FOR_ID = "SELECT * FROM `odie_document` WHERE id=?";
	
	public static String SQL_SELECT_DOCUMENT_FOR_URI = "SELECT * FROM `odie_document` WHERE uri=?";
	
	public static String SQL_SELECT_LR_FOR_ID = "SELECT * FROM `odie_lang_resource` WHERE id=?";
	
	public static String SQL_SELECT_LR_FOR_URI = "SELECT * FROM `odie_lang_resource` WHERE uri=?";;
	
	public static String SQL_UPDATE_ANALYSIS = "UPDATE `odie_analysis` SET name=?, description=?, type=? WHERE id=?";
	
	public static String SQL_UPDATE_ANALYSIS_DOCUMENT = "UPDATE `odie_analysis_document` "
			+ "SET status=? WHERE id=?";
}

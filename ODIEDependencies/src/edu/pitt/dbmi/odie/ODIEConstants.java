package edu.pitt.dbmi.odie;

import java.io.File;

public class ODIEConstants {
	public static final String ANALYSIS_SPACE_PREFIX = "od_space_";
	public static final String LEXSET_PREFIX = "od_lex_";
	
	//MYSQL LIMIT 64
	//ORACLE LIMIT ON SCHEMA 30
	//ORACLE LIMIT ON DATABASE 8
	public  static final int MAX_DB_NAME_LENGTH = 30;
	
//	public static final Object IF_NER_AE_NAME = "ODIE_IndexFinderPipeline";

	public static final String AE_TYPE_NER = "NER";

	public static final String AE_TYPE_OTHER = "OTHER";

	public static final String AE_TYPE_OE = "OE";
	
	public static final String PROPOSAL_ONTOLOGY_URI_PREFIX = "http://odie.dbmi.pitt.edu/";
	public static final String PROPOSAL_ONTOLOGY_URI_POSTFIX = "_Proposal";
	
	public static final String EXTENSION_UIMA_PEAR_FILE = "pear";
	public static final String EXTENSION_ODIE_ONT_FILE = "odie";
	public static final String EXTENSION_OBO_FILE = "obo";
	public static final String EXTENSION_OWL_FILE = "owl";
	public static final String EXTENSION_PPRJ_FILE = "pprj";
	
	public static final String FILETYPE_ONTOLOGY = "Ontology";
	public static final String FILETYPE_ODIEONTOLOGY = "ODIE Ontology Package";
	public static final String FILETYPE_PEAR = "UIMA PEAR";
	public static final String FILETYPE_UNKNOWN = "Unknown";
	
	public static final String DROPIN_DIR_RELATIVE_PATH = "/dropins";
	public static final String AE_DIR_RELATIVE_PATH = "/analysisengines";
	
	public static final String PROPERTY_PROGRESS_MSG = "ProgressMessage";
	
	public static final String ANALYIS_NAME_VAR = "$ANALYSISNAME";
	
	public static final String PROPOSALONTOLOGY_DESC = "This ontology contains new concepts and relationships created as part of the '" +ANALYIS_NAME_VAR+ "' analysis";
	public static final String PROPOSALLEXICALSET_DESC = "This lexicon contains new concepts and relationships created as part of the '" +ANALYIS_NAME_VAR+ "' analysis";
	
	public static final String UIMAPARAM_ANALYSIS_DB_URL = "analysis.db.url";
//	public static final String UIMAPARAM_IF_DB_URL = "if.db.url";
//	public static final String UIMAPARAM_PROPOSAL_IF_DB_URL = "proposal.if.db.url";
	public static final String UIMAPARAM_DB_USERNAME = "db.username";
	public static final String UIMAPARAM_DB_PASSWORD = "db.password";
	public static final String UIMAPARAM_DB_DRIVER = "db.driver";
	public static final String UIMAPARAM_OE_EXISTING_LUCENE_FINDER_DIR = "odie.fs.directory.path.existing";
	public static final String UIMAPARAM_OE_PROPOSAL_LUCENE_FINDER_DIR = "odie.fs.directory.path.proposal";
	
	public static final String UIMAPARAM_LUCENE_FINDER_DIR = "odie.fs.directory.path";
	
	public static final String NAMED_ENTITY_ONTOLOGY_CONCEPTS_FEATURE_NAME = "edu.mayo.bmi.uima.core.type.NamedEntity:ontologyConceptArr";
	public static final String[] NER_PARAMS = new String[]{
		UIMAPARAM_LUCENE_FINDER_DIR
	};

	public static final String[] OE_PARAMS = new String[]{
		UIMAPARAM_ANALYSIS_DB_URL,
		UIMAPARAM_OE_EXISTING_LUCENE_FINDER_DIR,
		UIMAPARAM_OE_PROPOSAL_LUCENE_FINDER_DIR,
		UIMAPARAM_DB_DRIVER,
		UIMAPARAM_DB_USERNAME,
		UIMAPARAM_DB_PASSWORD};
	
	public static final String XMLELEMENT_ROOT = "odie";
//	public static final String XMLELEMENT_METADATA = "metadata";
	public static final String XMLELEMENT_STATISTICS = "statistics";
	public static final String XMLELEMENT_ANALYSIS_DOCUMENT = "AnalysisDocument";
	public static final String XMLELEMENT_NP_COUNT = "NPCount";
	public static final String XMLELEMENT_SENTENCE_COUNT = "SentenceCount";
	public static final String XMLELEMENT_CHARACTER_COUNT = "CharacterCount";
	public static final String XMLELEMENT_COVERED_CHARACTER_COUNT = "CoveredCharacterCount";
	public static final String XMLELEMENT_NAMED_ENTITY_COUNT = "NamedEntityCount";
	public static final String XMLATTRIBUTE_ID = "id";
	public static final String XMLELEMENT_ANALYSIS = "Analysis";
	public static final String XMLELEMENT_ONTOLOGY = "Ontology";
	public static final String XMLELEMENT_UNIQUE_CONCEPTS_COUNT = "UniqueConceptsCount";
	public static final String LUCENE_ONTOLOGY_FIELD_URI = "uri";
	public static final String LUCENE_ONTOLOGY_FIELD_CONTENT = "content";	
	
	public static final String[] ENGLISH_STOP_WORDS = {
	    "a", "an", "and", "are", "as", "at", "be", "but", "by",
	    "for", "i", "if", "in", "into", "is",
	    "no", "not", "of", "on", "or", "s", "such",
	    "t", "that", "the", "their", "then", "there", "these",
	    "they", "this", "to", "was", "will", "with"
	    };

	
	public static final String MYSQL_LINUX_DIR = "mysql-5.1.45-linux-i686-glibc23";
	public static final String MYSQL_WIN_DIR = "mysql-5.1.51-win32";
	public static final String MYSQL_OSX_64DIR = "mysql-5.1.51-osx10.6-x86_64";
	public static final String MYSQL_OSX_32DIR = "mysql-5.1.51-osx10.6-x86_32";
	
	public static final String MYSQL_PORT = "23456";
	public static final String MYSQL_USERNAME = "root";
	public static final String MYSQL_PASSWORD = "odiepass";
	
	public static final String ONTOLOGIES_LABEL = "Ontologies Used";
	public static final String DOCUMENTS_LABEL = "Documents";
	public static final String UCONCEPTS_LABEL = "Unique Concepts";
	public static final String OCCURENCES_LABEL = "Occurences";
	public static final String COVERAGE_LABEL = "Coverage";
	public static final String PERFORMANCE_LABEL = "Overall Performance";
	
	public static final String BIOPORTAL_NOTES_URL_POSTFIX = "notes/";
	
	public static final String LUCENEFINDER_DIRECTORY = "lucenefinder";
	
	public static final String BIOPORTAL_NOTE_ID_PROPERTY = "BioportalNoteID";
	public static final String BIOPORTAL_ONTOLOGY_ID_PROPERTY = "BioportalOntologyID";;
	
	public static final String NEUROLEX_WIKI_CREATE_PAGE_PREFIX = "http://neurolex.org/wiki/Category:";
	
	public static final String PROPERTY_ONT_NAME = "ONTOLOGY_NAME";
	public static final String PROPERTY_ONT_LOCATION = "ONTOLOGY_LOCATION";
	public static final String PROPERTY_ONT_URI = "ONTOLOGY_URI";
	public static final String PROPERTY_ONT_VER = "ONTOLOGY_VERSION";
	public static final String PROPERTY_ONT_DESC = "ONTOLOGY_DESCRIPTION";
	
}

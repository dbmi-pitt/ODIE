package edu.pitt.dbmi.odie.lexicalizer.lucenefinder;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.pitt.ontology.IClass;

@SuppressWarnings("deprecation")
public class ODIE_LuceneNerFoundryIndexBuilder {

	private static final Logger logger = Logger
			.getLogger(ODIE_LuceneNerFoundryIndexBuilder.class);
	
	private String indexLocation = "C:\\index_radlex_200" ;

	private FSDirectory neFsDirectory;

	private IndexSearcher neSearcher = null;

	private IndexWriter neWriter = null;

	public ODIE_LuceneNerFoundryIndexBuilder() {
	}

	public void initialize() {
		openNerLuceneIndex();
	}

	public void finalize() {
		try {
			neSearcher.close();
			neWriter.close();
			neFsDirectory.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean openNerLuceneIndex() {

		if (neFsDirectory != null && neWriter != null && neSearcher != null) {
			logger.debug("Lucene Ner Index already open. No re-opening.");
			return true;
		}

		try {

			File file = new File(this.indexLocation);
			
			if (!file.exists()) {
				file.mkdir();
			}

			neFsDirectory = FSDirectory.open(new File(this.indexLocation)) ;

			neWriter = new IndexWriter(neFsDirectory, new StandardAnalyzer(Version.LUCENE_29),
					MaxFieldLength.UNLIMITED);
		
			neWriter.setMergeFactor(20);
			neWriter.optimize();

			boolean readOnly = true ;
			neSearcher = new IndexSearcher(IndexReader.open(neFsDirectory, readOnly)) ;

			logger.debug("Opened the sequential lucene index at "
					+ this.indexLocation);
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
			neFsDirectory = null;
			return false;
		}
	}

	public boolean isIndexedCls(String odieCui) {
		boolean result = false;
		Query luceneQueryObject;
		try {
			QueryParser queryParser = new QueryParser(Version.LUCENE_29,"odieCui",
					new KeywordAnalyzer());
			luceneQueryObject = queryParser.parse(odieCui);
			int maxHits = 1;
			TopDocs results = neSearcher.search(luceneQueryObject,null,maxHits);
			
			return results.totalHits>0;
		} catch (ParseException e) {
			result = false;
		} catch (IOException e) {
			result = false;
		}
		return result;
	}
	
	public boolean isIndexedClsName(String className) {
		boolean result = false;
		Query luceneQueryObject;
		try {
			QueryParser queryParser = new QueryParser(Version.LUCENE_29,"className",
					new KeywordAnalyzer());
			luceneQueryObject = queryParser.parse(className);
			int maxHits = 1;
			TopDocs results = neSearcher.search(luceneQueryObject,null,maxHits);
			
			return results.totalHits>0;
		} catch (ParseException e) {
			e.printStackTrace();
			result = false;
		} catch (IOException e){
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public void indexCls(IClass cls, String odieCui, String className,
			String delineatedTerms, int wordCount, String parents, String ancestors, String umlsCui, String umlsTuis, int cTakesSemanticType) {

		try {

			Document d = new Document();

			d.add(new Field("odieCui", odieCui, Field.Store.YES,
					Field.Index.NOT_ANALYZED));
			d.add(new Field("uri", cls.getURI().toString(), Field.Store.YES,
					Field.Index.NOT_ANALYZED));
			d.add(new Field("className", className, Field.Store.YES,
					Field.Index.NOT_ANALYZED));
			d.add(new Field("delineatedTerms", delineatedTerms,
					Field.Store.YES, Field.Index.ANALYZED));
			String wordCountAsString = ODIE_LexicalizerFormatUtils.formatIntegerAsDigitString(wordCount, "0000") ;
			d.add(new Field("wordCount", wordCountAsString, Field.Store.YES,
					Field.Index.NO));
			d.add(new Field("parents", parents, Field.Store.YES,
					Field.Index.NO));
			d.add(new Field("ancestors", ancestors, Field.Store.YES,
					Field.Index.NO));
			d.add(new Field("umlsCui", umlsCui, Field.Store.YES,
					Field.Index.NOT_ANALYZED));
			d.add(new Field("umlsTuis", umlsTuis, Field.Store.YES,
					Field.Index.ANALYZED));
			d.add(new Field("cTakesSemanticType", cTakesSemanticType+"", Field.Store.YES,
					Field.Index.NOT_ANALYZED));
			
			neWriter.addDocument(d);
			
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public String getIndexLocation() {
		return indexLocation;
	}

	public void setIndexLocation(String indexLocation) {
		this.indexLocation = indexLocation;
	}

	

}

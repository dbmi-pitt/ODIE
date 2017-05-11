package edu.pitt.dbmi.odie.middletier;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.Suggestion;

public class AnalysisSpaceMiddleTier {

	private Configuration configuration;

	private Analysis analysis;
	
	
	public AnalysisSpaceMiddleTier(Configuration conf, Analysis a) {
		this.analysis = a;
		this.configuration = conf;
		init(configuration);
	}
	
	Logger logger = Logger.getLogger(this.getClass());

	private Session session;
	private SessionFactory sessionFactory;

	
	public void dispose() {
		closeHibernateSession();
	}

	private void closeHibernateSession() {
		if(session!=null)
			session.close();
		
		session = null;
	}

	public void init(Configuration config){
		closeHibernateSession();
		initHibernate(config);
	}


	// private SessionFactory sessionFactory;
	private void initHibernate(Configuration config) {
		
		try {
			AnnotationConfiguration aconfig = new AnnotationConfiguration();

//			 aconfig.setProperty("hibernate.dialect",
//			 "org.hibernate.dialect.HSQLDialect");
			aconfig.setProperty("hibernate.connection.driver_class", config
					.getDatabaseDriver());
			aconfig.setProperty("hibernate.connection.url", config
					.getDatabaseURL());
			aconfig.setProperty("hibernate.connection.username", config
					.getUsername());
			aconfig.setProperty("hibernate.connection.password", config
					.getPassword());
			aconfig.setProperty("hibernate.connection.pool_size", "1");
			aconfig.setProperty("hibernate.cache.provider_class",
					"org.hibernate.cache.NoCacheProvider");

			aconfig.setProperty("hibernate.show_sql", "true");
			aconfig.setProperty("hibernate.transaction.factory_class",
					"org.hibernate.transaction.JDBCTransactionFactory");
			aconfig.setProperty("hibernate.current_session_context_class",
					"managed");

			if (config.getHBM2DDLPolicy() != null)
				aconfig.setProperty("hibernate.hbm2ddl.auto", config
						.getHBM2DDLPolicy());

			//Order is important
			aconfig.addAnnotatedClass(Suggestion.class);
			
			sessionFactory = aconfig.buildSessionFactory();
			resetSession();
		} catch (Throwable ex) {
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}

	}

	public void resetSession() {
		if(session!=null){
			session.close();
			session = null;
		}
	
		session = sessionFactory.openSession();
		session.beginTransaction();
		
	}

	public List getSuggestions(float scoreThreshold) {
		Criteria c = session.createCriteria(Suggestion.class);
		c.add(Restrictions.ge("score", scoreThreshold));
		List<Suggestion> slist = c.list();
		for(Suggestion s:slist){
			s.setAnalysis(analysis);
		}
		return slist;
	}

	public Suggestion getSuggestionForId(long suggestionId) {
		Criteria c = session.createCriteria(Suggestion.class);
		c.add(Restrictions.eq("id", suggestionId));
		Suggestion s = (Suggestion) c.uniqueResult();
		s.setAnalysis(analysis);
		return s;
	}

	public List<Suggestion> getSuggestionsForNerNegative(String nerNegative, float scoreThreshold) {
		Criteria c = session.createCriteria(Suggestion.class);
		c.add(Restrictions.eq("nerNegative", nerNegative));
		c.add(Restrictions.ge("score", scoreThreshold));
		
		List<Suggestion> slist = c.list();
		for(Suggestion s:slist){
			s.setAggregate(false);
			s.setAnalysis(analysis);
		}
		return slist;
	}
	public List<Suggestion> getAggregatedSuggestions(float scoreThreshold) {
		
		Criteria c = session.createCriteria(Suggestion.class);
	    c.setProjection( Projections.projectionList()
	        .add( Projections.max("score") )
	        .add( Projections.groupProperty("nerNegative")
	        )
	    );
	    c.add(Restrictions.ge("score", scoreThreshold));
	    
	    List<Suggestion> slist = new ArrayList<Suggestion>();
	    
		for(Object o:c.list()){
			Suggestion s = new Suggestion();
			s.setScore((Float) ((Object[])o)[0]);
			s.setNerNegative((String) ((Object[])o)[1]);
			s.setAnalysis(analysis);
			s.setAggregate(true);
			slist.add(s);
		}
		return slist;
	}

}

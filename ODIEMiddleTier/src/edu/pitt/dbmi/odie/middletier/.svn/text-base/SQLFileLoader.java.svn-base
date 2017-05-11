package edu.pitt.dbmi.odie.middletier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.persistence.Table;

import org.apache.log4j.Logger;

import edu.pitt.dbmi.odie.model.LanguageResource;

public class SQLFileLoader  {
	private Connection conn;

	Logger logger = Logger.getLogger(this.getClass());
	
	public SQLFileLoader(Configuration conf) throws Exception {
		init(conf);
	}
	
	private void init(Configuration config) throws Exception {
		initDBConnection(config.getDatabaseDriver(), 
						 config.getDatabaseURL(), 
						 config.getUsername(), 
						 config.getPassword());
	}

	private void initDBConnection(String dbDriver, String dbURL, String username, String password)
			throws Exception {
		logger.info("Initializing database connection");
		Class.forName(dbDriver).newInstance();
		conn = DriverManager.getConnection(dbURL, username, password);
	}
	

    public boolean loadSQLFile(File sqlFile) throws SQLException
    {
    	String s = new String();
        StringBuffer sb = new StringBuffer();

        try
        {
            FileReader fr = new FileReader(sqlFile);
            // be sure to not have line starting with "--" or "/*" or any other non aplhabetical character
            Statement st = conn.createStatement();
            BufferedReader br = new BufferedReader(fr);

            while((s = br.readLine()) != null)
            {
            	if(s.startsWith("/*") || s.startsWith("--") || s.trim().length() == 0)
            		continue;
            			
            	sb.append(s + "\n");
            	String stmt = sb.toString();
            	if(stmt.endsWith(";\n")){
	                // we ensure that there is no spaces before or after the request string
	                // in order to not execute empty statements
	                if(stmt.toUpperCase().startsWith("CREATE TABLE") ||
	                   stmt.toUpperCase().startsWith("INSERT INTO") || stmt.toUpperCase().startsWith("DROP TABLE")){
	                	st.executeUpdate(stmt);
	                    logger.debug(">>"+stmt);
	                }
	                sb.setLength(0);
	            }
            	
            }
            st.close();
            br.close();
            return true;
        }
        catch(Exception e)
        {
            logger.debug("*** Error : "+e.toString());
            logger.debug("*** ");
            logger.debug("*** Error : ");
            e.printStackTrace();
            logger.debug("################################################");
            logger.debug(sb.toString());
            return false;
        }

    }
    
    public void destroy(){
    	if(conn!=null)
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
    }
    
    public static void main(String[] args) {
		
		Configuration conf = new Configuration();
		conf.setHBM2DDLPolicy("create");
		conf.setDatabaseDriver("com.mysql.jdbc.Driver");
		conf.setDatabaseURL("jdbc:mysql://localhost:3306/testod");
		conf.setUsername("odieuser");
		conf.setPassword("odiepass");
		conf.setRepositoryTableName(LanguageResource.class.getAnnotation(Table.class).name());
		conf.setTemporaryDirectory(System.getProperty("java.io.tmpdir"));
		conf.setLuceneIndexDirectory("c:/tmp/indices");
		
		try {
			SQLFileLoader mt = new SQLFileLoader(conf);
			File f = new File("C:/temp/NIF Neuron Circuit Role Bridge/NIF Neuron Circuit Role Bridge/protegeDatabase/ontology_NIF_Neuron_Circuit_Role_Bridge.sql");
			mt.loadSQLFile(f);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

				
	}

}

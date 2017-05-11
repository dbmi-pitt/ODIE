/*
 * Copyright: (c) 2009   Mayo Foundation for Medical Education and 
 * Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 *
 * Except as contained in the copyright notice above, or as used to identify 
 * MFMER as the author of this software, the trade names, trademarks, service
 * marks, or product names of the copyright holder shall not be used in
 * advertising, promotion or otherwise in connection with this software without
 * prior written authorization of the copyright holder.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package edu.pitt.dbmi.odie.uima.util;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.Logger;

import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;
import org.apache.uima.resource.metadata.ConfigurationParameterSettings;
import org.apache.uima.resource.metadata.ResourceMetaData;

import edu.mayo.bmi.uima.core.resource.JdbcConnectionResource;

//import edu.mayo.mir.utility.sqlReconnect.WrappedConnection;

/**
 * Implementation of JdbcConnectionResource. Uses the WrappedConnection from the
 * MIR Utility project mostly for it's auto-reconnect capability.
 * 
 * @author Mayo Clinic
 */
public class JdbcConnectionResourceImpl implements JdbcConnectionResource,
		SharedResourceObject {
	
	private Logger iv_logger = Logger.getLogger(getClass().getName());

	/**
	 * JDBC driver ClassName.
	 */
	public static final String PARAM_DRIVER_CLASS = "DriverClassName";

	/**
	 * JDBC URL that specifies db network location and db name.
	 */
	public static final String PARAM_URL = "URL";

	/**
	 * Username for db authentication.
	 */
	public static final String PARAM_USERNAME = "Username";

	/**
	 * Password for db authentication.
	 */
	public static final String PARAM_PASSWORD = "Password";

	/**
	 * Flag that determines whether to keep JDBC connection open no matter what.
	 */
	public static final String PARAM_KEEP_ALIVE = "KeepConnectionAlive";

	/**
	 * Transaction isolation level. Value should be a static fieldname from
	 * java.sql.Connection such as TRANSACTION_READ_UNCOMMITTED. This parameter
	 * is optional.
	 */
	public static final String PARAM_ISOLATION = "TransactionIsolation";

	private Connection iv_conn;

	public void load(DataResource dr) throws ResourceInitializationException {
		
		String driverClassName = "com.mysql.jdbc.Driver" ;
		String urlStr = "jdbc:mysql://localhost:3306/umls2010_ncit_snomed";
		String username = "caties" ;
		String password = "caties" ;
		Boolean keepAlive = new Boolean(false) ;
		String isolationStr = Connection.TRANSACTION_NONE + "" ;
		
		ResourceMetaData metaData = dr.getMetaData();
		if (metaData != null) {
			ConfigurationParameterSettings cps = metaData
					.getConfigurationParameterSettings();

			driverClassName = (String) cps
					.getParameterValue(PARAM_DRIVER_CLASS);

			urlStr = (String) cps.getParameterValue(PARAM_URL);

			username = (String) cps.getParameterValue(PARAM_USERNAME);

			password = (String) cps.getParameterValue(PARAM_PASSWORD);

			Boolean keepAliveAsBoolean = (Boolean) cps.getParameterValue(PARAM_KEEP_ALIVE) ;
			if (keepAliveAsBoolean != null) {
				keepAlive = new Boolean(keepAliveAsBoolean);
			}
			else {
				keepAlive = new Boolean(false) ;
			}
			

			isolationStr = (String) cps
					.getParameterValue(PARAM_ISOLATION);
		}
		
		try {
			if (keepAlive.booleanValue()) {
				iv_logger.info("Instantiating wrapped connection.");
				// iv_conn = new WrappedConnection(username,
				// password,
				// driverClassName,
				// urlStr);
			} else {
				Class.forName(driverClassName);
				iv_conn = DriverManager.getConnection(urlStr, username,
						password);
			}

			iv_logger.info("Connection established to: " + urlStr);

		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	public Connection getConnection() {
		return iv_conn;
	}
}
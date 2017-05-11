package edu.pitt.dbmi.odie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

public class ODIEUtils {
	
	static Logger logger = Logger.getLogger(ODIEUtils.class);
	
	public static String convertToValidDatabaseName(String s){
		String out = s.replaceAll("[^\\w]", "");
		if(out.length()> ODIEConstants.MAX_DB_NAME_LENGTH)
			return out.substring(0,ODIEConstants.MAX_DB_NAME_LENGTH-1);
		else
			return out;
	}
	
	public static String deContaminate(String inputString) {
		String result = inputString;
		result = result.replaceAll("[\\:\\+\\-\\s)('\"]", "_");
		return result;

	}

	public static String extractDatabaseNameFromDbUrl(String dbUrl) {
		int sPos = dbUrl.lastIndexOf("/") ;
		String result =dbUrl.substring(sPos+1, dbUrl.length());
		return result ;
	}
	
	public static URL convertToNativeURL(URL url) {
		  URL nativeURL = url;
			try {
				if (url.getProtocol().equals("bundleresource")) {
					Class<?> platformCls = Class
							.forName("org.eclipse.core.runtime.Platform");
					Class<?>[] resolveMethodSignature = { java.net.URL.class };
					Method resolveMethod = platformCls.getMethod("resolve",
							resolveMethodSignature);
					Object[] resolveMethodCallArguments = { url };
					nativeURL = (URL) resolveMethod.invoke(platformCls,
							resolveMethodCallArguments);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return nativeURL;
	}
	
	public static String readStream(InputStream in) throws IOException {
		StringBuffer strBuf = new StringBuffer();
		BufferedReader buf = new BufferedReader(new InputStreamReader(in));
		try {
			for (String line = buf.readLine(); line != null; line = buf
					.readLine()) {
				strBuf.append(line.trim() + "\n");
			}
		} catch (IOException ex) {
			throw ex;
		} finally {
			buf.close();
		}
		return strBuf.toString();
	}

	public static Object[] converHashMapToArray(HashMap map) {

		List outList = new ArrayList();
		
		for(Object key:map.keySet()){
			Object[] item = new Object[2];
			item[0] = key;
			item[1] = map.get(key);
			outList.add(item);
		}
		return outList.toArray();
		
	}
	

	
	public static HttpInputStreamWrapper doHttpPost(String postUrl,
			HashMap<String, String> postParams) throws Exception {
		InputStream is = null;
		String postData = new String("");
		String encoding = "UTF-8";

		// Send data
		URL url = new URL(postUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// Construct post parameters
		if (postParams != null) {
			for (String key : postParams.keySet()) {
				postData += URLEncoder.encode(key, encoding) + "="
						+ URLEncoder.encode(postParams.get(key), encoding)
						+ "&";
			}

			postData = (postData.length() > 0) ? postData.substring(0, postData
					.length() - 1) : postData;
			
			logger.debug("HTTP Post:" + postData);
			
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn
					.getOutputStream());
			wr.write(postData);
			wr.flush();
			wr.close();
		}

		try {
			is = conn.getInputStream();
		} catch (Exception e) {
			is = conn.getErrorStream();
		}
		


		int statusCode = conn.getResponseCode();

		return new HttpInputStreamWrapper(statusCode, is);
	}

}

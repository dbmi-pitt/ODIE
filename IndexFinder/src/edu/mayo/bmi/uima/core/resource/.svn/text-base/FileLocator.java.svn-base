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
package edu.mayo.bmi.uima.core.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * Utility class that attempts to locate files. Modified by Pitt to handle problems
 * running this within Eclipse RCP
 * 
 * @author Kevin J Mitchell
 */
public class FileLocator {
	static Logger logger = Logger.getLogger(FileLocator.class);

	public static File locateFile(String location) throws FileNotFoundException {
		try {
			return locateOnClasspath(location);
		} catch (Exception e) {
			return locateExplicitly(location);
		}
	}

	private static File locateOnClasspath(String cpLocation)
			throws FileNotFoundException, URISyntaxException {
		ClassLoader cl = FileLocator.class.getClassLoader();
		URL indexUrl = cl.getResource(cpLocation);
		if (indexUrl == null) {
			throw new FileNotFoundException(cpLocation);
		}
		indexUrl = convertToNativeURL(indexUrl);
		String uriStr = indexUrl.toExternalForm().replaceAll(" ", "%20");
		URI indexUri = new URI(uriStr);
		File f = new File(indexUri);
		return f;
	}

	private static File locateExplicitly(String explicitLocation)
			throws FileNotFoundException {
		File f = new File(explicitLocation);
		if (!f.exists()) {
			throw new FileNotFoundException(explicitLocation);
		}
		return f;
	}

	public static URI convertToNativeURI(URI uri) {
		URL url = null;
		try {
			url = uri.toURL();
			url = convertToNativeURL(url);
			uri = url.toURI();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uri;
	}

	public static URL convertToNativeURL(URL url) {
		logger.debug("Converting to native URL");
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
}
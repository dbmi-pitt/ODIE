package edu.pitt.dbmi.odie.uima.util;

import gov.nih.nlm.nls.lvg.Api.NormApi;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.text.NumberFormatter;

public class ODIE_IndexFinderUtils {
	
	public static boolean isCamelCase(String input) {
		boolean result = false ;
		String onesString = input.replaceAll("[A-Z][a-z]+", "1") ;
		return onesString.matches("^[1]+$") ;
	}
	
	
	public static String dbColConform(String word) {
		// Reduce word to length not exceeding 254 characters
		if (word == null) {
			word = "UNDEFINED" ;
		}
		else {
			word = word.substring(0, Math.min(word.length(), 254)) ;
			
		}
		return word ;
	}

	public static ArrayList<String> unCamelCase(String input,
			boolean isAddingWords) {
		ArrayList<String> result = new ArrayList<String>();
		if (!isCamelCase(input)) {
			result.add(input);
		} else {
			StringBuffer sb = new StringBuffer();
			char[] inputChars = input.toCharArray();
			for (int idx = 0; idx < inputChars.length; idx++) {
				char currentChar = inputChars[idx];
				if (Character.isUpperCase(currentChar)) {
					if (idx > 0) {
						sb.append(' ');
					}
					sb.append(Character.toLowerCase(currentChar));
				} else {
					sb.append(currentChar);
				}
			}
			result.add(sb.toString());
			if (isAddingWords) {
				String[] words = sb.toString().split("\\s");
				for (int wdx = 0; wdx < words.length; wdx++) {
					result.add(words[wdx]);
				}
			}
		}
		return result;
	}
	
	public static ArrayList<String> unCamelCaseOld(String input,
			boolean isAddingWords) {
		ArrayList<String> result = new ArrayList<String>();
		if (input.matches("^CD[0-9]+$")) {
			result.add(input.toLowerCase());
		} else if (!input.matches("^[A-Za-z]+$")) {
			result.add(input.toLowerCase());
		} else {
			StringBuffer sb = new StringBuffer();
			char[] inputChars = input.toCharArray();
			for (int idx = 0; idx < inputChars.length; idx++) {
				char currentChar = inputChars[idx];
				if (Character.isUpperCase(currentChar)) {
					if (idx > 0) {
						sb.append(' ');
					}
					sb.append(Character.toLowerCase(currentChar));
				} else {
					sb.append(currentChar);
				}
			}
			result.add(sb.toString());
			if (isAddingWords) {
				String[] words = sb.toString().split("\\s");
				for (int wdx = 0; wdx < words.length; wdx++) {
					result.add(words[wdx]);
				}
			}
		}
		return result;
	}
	
	public static String mutateWithNorm(String tokenString, NormApi normApi) {
		String result = tokenString;
		if (normApi != null) {
			Vector<String> v = null;
			try {
				v = normApi.Mutate(tokenString);
			} catch (Exception e) {
				System.err.println("Failed to mutate token " + tokenString);
			}
			for (Enumeration<String> mutationsEnum = v.elements(); mutationsEnum
					.hasMoreElements();) {
				result = (String) mutationsEnum.nextElement();
				break;
			}
		}
		return result;
	}

	public static String deContaminate(String inputString) {
		String result = inputString;
		result = result.replaceAll("\\W+", "_");
		return result;
	}
	
	public static String stipNamespacePrefix(String clsName) {
		String result = clsName.replaceAll("^[^:]+:", "") ;
		return result ;
	}
	
	public static String formatForXcelRecord(String inputString) {
		String result = inputString;
		result = result.replaceAll("[\\n\\r]", " ");
		result = result.replaceAll("\\t+", " ");
		return result;
	}
	
	public static String forceToSingleLine(String inputString) {
		String result = inputString;
		result = result.replaceAll("\\n", " ");
		result = result.replaceAll("\\s+", " ");
		result = result.toLowerCase();
		return result;
	}

	public static String getSimpleClsNameFromQName(String clsQName) {
		String result = clsQName;
		String delimiter = "#";
		if (clsQName.contains(delimiter)) {
			int sPos = clsQName.lastIndexOf(delimiter) + delimiter.length();
			int ePos = clsQName.length();
			result = clsQName.substring(sPos, ePos);
		}
		return result;
	}

	/**
	 * Method formatIntegerAsDigitString.
	 * 
	 * @param value
	 *            BigDecimal
	 * @param digitPattern
	 *            String
	 * 
	 * @return String
	 */
	public static String formatIntegerAsDigitString(Integer value,
			String digitPattern) {
		String retValue = null;
		try {
			DecimalFormat decimalFormat = new DecimalFormat(digitPattern);
			NumberFormatter textFormatter = new NumberFormatter(decimalFormat);
			textFormatter.setOverwriteMode(true);
			textFormatter.setAllowsInvalid(false);
			retValue = textFormatter.valueToString(value);
		} catch (Exception x) {
			retValue = null;
		}

		return retValue;

	}

	/**
	 * Method formatLongAsDigitString.
	 * 
	 * @param value
	 *            BigDecimal
	 * @param digitPattern
	 *            String
	 * 
	 * @return String
	 */
	public static String formatLongAsDigitString(Long value, String digitPattern) {
		String retValue = null;
		try {
			DecimalFormat decimalFormat = new DecimalFormat(digitPattern);
			NumberFormatter textFormatter = new NumberFormatter(decimalFormat);
			textFormatter.setOverwriteMode(true);
			textFormatter.setAllowsInvalid(false);
			retValue = textFormatter.valueToString(value);
		} catch (Exception x) {
			retValue = null;
		}

		return retValue;

	}

	public static ArrayList<String> normalizePhraseWords(
			ArrayList<String> currentWords, NormApi normApi) {
		ArrayList<String> normalizedWords = new ArrayList<String>();
		for (String currentWord : currentWords) {
			normalizedWords.add(ODIE_IndexFinderUtils.mutateWithNorm(
					currentWord, normApi));
		}
		return normalizedWords;
	}

	public static ArrayList<String> sortPhraseWords(
			ArrayList<String> currentWords) {
		Collections.sort(currentWords);
		return currentWords;
	}

	public static String implodeWordListToTerm(ArrayList<String> currentWords,
			String separator) {
		String result = "";
		for (String word : currentWords) {
			result += word + separator;
		}
		result = result.substring(0, result.lastIndexOf(separator));
		return result;
	}

	public static ArrayList<String> explodeTermToWordList(String term,
			String separator) {
		ArrayList<String> result = new ArrayList<String>();
		String regex = "[" + separator + "]+" ;
		String[] words = term.split(regex);
		for (String word : words) {
			if (word.trim().length()>0) {
				result.add(word);
			}
			
		}
		return result;
	}
	
	public static String deriveFullUrlFromRelativeUrl(String relativeUrl) {
		String result = relativeUrl;
		String curDir = System.getProperty("user.dir");
		if (curDir != null) {
			result = "file:/" + curDir + relativeUrl;
		}
		return result;
	}

}

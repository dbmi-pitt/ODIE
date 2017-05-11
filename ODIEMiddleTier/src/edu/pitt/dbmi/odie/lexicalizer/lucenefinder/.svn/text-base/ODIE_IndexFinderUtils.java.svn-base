package edu.pitt.dbmi.odie.lexicalizer.lucenefinder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.text.NumberFormatter;

public class ODIE_IndexFinderUtils {

	/**
	 * isCamelCase
	 * 
	 * @param input
	 * @return true or false
	 * 
	 *         Return true if and only if input is of form
	 *         ProstaticAdenocarcinoma or DiseasesAndDisorders 
	 *         
	 *         It also is lenient in recoginizing 'diseasesAndDisorders'
	 */
	public static boolean isCamelCase(String input) {
		boolean result = false;
		char[] carray = input.toCharArray();
		int stateAcceptingAnyCase = 0;
		int stateAcceptingLowerCase = 1;
		int stateFailed = 2;
		int stateCurrent = stateAcceptingAnyCase;

		for (int idx = 0; idx < carray.length; idx++) {
			char currentChar = carray[idx];
			if (stateCurrent == stateFailed) {
				break ;
			}
			else if (!Character.isLetter(currentChar)) {
				stateCurrent = stateFailed;
			}
			else if (stateCurrent == stateAcceptingAnyCase) {
				if (Character.isUpperCase(currentChar)) {
					stateCurrent = stateAcceptingLowerCase;
				}
			}
			else if (stateCurrent == stateAcceptingLowerCase) {
				if (!Character.isUpperCase(currentChar)) {
					stateCurrent = stateAcceptingAnyCase;
				}
				else {
					stateCurrent = stateFailed ;
				}
			}
		}
		result = stateCurrent != stateFailed ;
		
		return result;
	}

	/**
	 * isCamelCaseRegex
	 * 
	 * @param input
	 * @return true or false
	 * 
	 *         Return true if and only if input is of form
	 *         ProstaticAdenocarcinoma or DiseasesAndDisorders This check fails
	 *         for the case of ProstateCancerStageI since there are no
	 *         additional small letters after the final 'I'
	 */
	public static boolean isCamelCaseRegex(String input) {
		boolean result = false;
		String onesString = input.replaceAll("[A-Z][a-z]+", "1");
		result = onesString.matches("^[1]+$");
		return result;
	}
	
	

	/**
	 * unCamelCase
	 * 
	 * @param input
	 * @param isAddingWords
	 * @return ArrayList<String>
	 * 
	 *         For an input like ProstaticAdenocarcinoma this method returns
	 * 
	 *         ['prostatic adenocarcinoma']
	 * 
	 *         if the isAddingWords flag is set it will also append each
	 *         individual word
	 * 
	 *         ['prostatic adenocarcinoma', 'prostatic', 'carcinoma']
	 * 
	 */
	public static ArrayList<String> unCamelCase(String input,
			boolean isAddingWords) {
		ArrayList<String> result = new ArrayList<String>();
		if (!isCamelCase(input)) {
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

	public static String deContaminateNamespace(String namespace) {
		String httpPrefix = "http://";
		namespace = namespace.replaceFirst(httpPrefix, "");
		return httpPrefix
				+ stipNamespacePrefix(namespace).replaceAll("//", "/");
	}

	public static String deContaminate(String inputString) {
		String result = inputString;
		result = result.replaceAll("\\W+", "_");
		return result;
	}

	public static String stipNamespacePrefix(String clsName) {
		String result = clsName.replaceAll("^[^:]+:", "");
		return result;
	}

	public static String normalizeTerm(String termValue) {
		termValue = stipNamespacePrefix(termValue);
		termValue = unCamelCase(termValue, false).get(0);
		termValue = termValue.toLowerCase();
		termValue = termValue.replaceAll("\\W[(][^)]+[)]\\W*$", ""); // SNOMED
		// Filter
		termValue = deContaminate(termValue); // Replace all non apha-numerics
		// with underscore
		termValue = termValue.replaceAll("[_]+", " ");
		termValue = ODIE_MorphologicalNormalizer.getInstance().normalizeTerm(
				termValue); // normalize with LVG tools
		return termValue;
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
		String regex = "[" + separator + "]+";
		String[] words = term.split(regex);
		for (String word : words) {
			if (word.trim().length() > 0) {
				result.add(word.toLowerCase());
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

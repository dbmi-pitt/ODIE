/*
 * caTIES. Copyright 2005  University of Pittsburgh Cancer Institute ("Cancer Center")  
 * 
 * Copyright Notice.  
 * The software subject to this notice and license includes both 
 * human readable source code form and machine readable, binary, object code form 
 * (the “caBIG™ Software”). The caBIG™ Software License (the “License”) is between Cancer 
 * Center and You.  “You (or “Your”) shall mean a person or an entity, and all other entities 
 * that control, are controlled by, or are under common control with the entity.  
 * “Control” for purposes of this definition means (i) the direct or indirect power to cause 
 * the direction or management of such entity, whether by contract or otherwise, or (ii) 
 * ownership of fifty percent (50%) or more of the outstanding shares, or (iii) beneficial 
 * ownership of such entity.  This License is granted provided that You agree to the conditions 
 * described in the license agreement.  The full text of that agreement is in file 
 * caTIES/license.htm in this distribution.
 * 
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT 
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY, NON-INFRINGEMENT AND FITNESS FOR 
 * A PARTICULAR PURPOSE) ARE DISCLAIMED.  IN NO EVENT SHALL THE University of Pittsburgh Cancer 
 * Institute OR ITS AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY 
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 */

/*
 * $Log: CaTIES_FormatUtils.java,v $
 * Revision 1.3  2010/05/14 18:52:43  girish_c1980
 * *** empty log message ***
 *
 * Revision 1.2  2010/01/15 21:42:21  mitchellkj01
 * When aggregating dimension is collectionYear use collectionDateTime on the back end for correct behavior.
 *
 * Revision 1.1  2009/06/02 19:06:30  girish_c1980
 * Initial Import v3.6
 *
 * Revision 1.1  2009/05/07 15:52:01  1upmc-acct\chavgx
 * Major Restructuring
 *
 * Revision 1.12  2009/04/30 19:39:56  mitchellkj
 * Support for caTIES oracle and beginning to support postgresql and mssqlserver
 *
 * Revision 1.11  2009/01/21 16:35:12  mitchellkj
 * Force all cuis flowing to the ancestry index to by CL format while all returned cuis
 * are in C9 format.   This is a temporary fix needed until ancestry index is converted to purely C9 format.
 *
 * Revision 1.10  2008/05/08 15:45:56  mitchellkj
 * Added formatting function for working with simple four digit years YYYY, this supports new collectionYear field in the index
 *
 * Revision 1.9  2007/11/30 17:16:58  mitchellkj
 * Clear GregorianCalendars after construction before specifying years, months etc
 *
 * Revision 1.8  2007/10/17 19:06:29  mitchellkj
 * Added collection year to possible cohort specifications
 *
 * Revision 1.7  2007/04/20 21:52:49  mitchellkj
 * Added methods to enable a JUnit test harness execution of search
 *
 * Revision 1.6  2007/02/07 18:51:41  mitchellkj
 * Added a SimpleDateFormat for temporal query work
 *
 * Revision 1.5  2007/01/18 17:16:11  1upmc-acct\mitchellkj
 * Added formatLongAsString
 *
 * Revision 1.4  2006/09/07 19:36:34  1upmc-acct\mitchellkj
 * Provide more elaborate documentation for phase two JavaDoc release
 *
 */

package edu.pitt.dbmi.odie.lexicalizer.lucenefinder;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import javax.swing.text.NumberFormatter;

/**
 * Formatting Utilities for CaTIES.
 * 
 * @author mitchellkj@upmc.edu
 * @version $Id
 * @since 1.4.2_04
 */
public class ODIE_LexicalizerFormatUtils {

	public static final SimpleDateFormat indexDateFormatter = new SimpleDateFormat(
			"yyyy-MM-dd-HH-mm-ss-SSSS");
	public static final SimpleDateFormat indexYearFormatter = new SimpleDateFormat(
	"yyyy");
	public static final String minimumTimeString = "0000-00-00-00-00-00-0000";
	public static final String maximumTimeString = "9999-99-99-99-99-99-9999";

	public static void main(String[] args) {
		String ncui = "CL337421";
		String mcui = reformatNCImCuiToMMTxCui(ncui);
		System.out.println(ncui + " ==> " + mcui);

		ncui = "C9337421";
		mcui = reformatMMTxCuiToNCImCui(ncui);
		System.out.println(ncui + " ==> " + mcui);

		String parents = "CL107025:3, C0006826:5, CL337141:12, CL028506:2, C0001418:2, C0027651:9, C0936223:1, C0178829:8, C0017417:6, C0027627:4, C0033578:5, C0027653:8, CL026653:6, C0376358:4, C0334277:1, C0007112:1, C0033575:6, CL330089:2, C0236099:7, CL026711:3, CL031825:4, C0012634:10, C0153606:5, CL331289:11, CL342882:2, CL072585:3, C0600139:3, CL026946:3, CL105879:6, C0205853:5, CL053849:9, C0178830:7, C0007097:4, CL111304:6";
		String mmtxParents = reformatNCItParentList(parents);
		System.out.println(parents + "\n" + mmtxParents);
	}

	public static String formatEqualsYear(String luceneFieldName,
			String yearAsString) {
		return luceneFieldName + ":( " + yearAsString + " ) ";
	}

	/*
	 * This suite of time query builders works with four digit years YYYY They
	 * assume a range of 0000 to 9999 and work under the precondition that all
	 * input will be exclusively within the year range 0 to 9999.
	 */
	public static String formatNotEqualsYear(String luceneFieldName,
			String yearAsString) {
		int yearAsInt = Integer.parseInt(yearAsString);
		int lastYearAsInt = Math.max(yearAsInt - 1, 0);
		int lowerBound = 0;
		int nextYearAsInt = Math.min(yearAsInt + 1, 9999);
		int upperBound = 9999;
		String formattedLastYear = formatIntegerAsDigitString(new Integer(
				lastYearAsInt), "0000");
		String formattedLowerBound = formatIntegerAsDigitString(new Integer(
				lowerBound), "0000");
		String formattedNextYear = formatIntegerAsDigitString(new Integer(
				nextYearAsInt), "0000");
		String formattedUpperBound = formatIntegerAsDigitString(new Integer(
				upperBound), "0000");
		String result = luceneFieldName + ":[ " + formattedLowerBound + " TO "
				+ formattedLastYear + " ] ";
		result += " OR ";
		result += luceneFieldName + ":[ " + formattedNextYear + " TO "
				+ formattedUpperBound + " ] ";
		return " ( " + result + " ) ";
	}

	public static String formatLessThanYear(String luceneFieldName,
			String yearAsString) {
		int yearAsInt = Integer.parseInt(yearAsString);
		int lastYearAsInt = Math.max(yearAsInt - 1, 0);
		int lowerBound = 0;
		String formattedLastYear = formatIntegerAsDigitString(new Integer(
				lastYearAsInt), "0000");
		String formattedLowerBound = formatIntegerAsDigitString(new Integer(
				lowerBound), "0000");
		return luceneFieldName + ":[ " + formattedLowerBound + " TO "
				+ formattedLastYear + " ] ";
	}

	public static String formatLessOrEqualToYear(String luceneFieldName,
			String yearAsString) {
		int yearAsInt = Integer.parseInt(yearAsString);
		int lowerBound = 0;
		String formattedLastYear = formatIntegerAsDigitString(new Integer(
				yearAsInt), "0000");
		String formattedLowerBound = formatIntegerAsDigitString(new Integer(
				lowerBound), "0000");
		return luceneFieldName + ":[ " + formattedLowerBound + " TO "
				+ formattedLastYear + " ] ";
	}

	public static String formatGreaterThanYear(String luceneFieldName,
			String yearAsString) {
		int yearAsInt = Integer.parseInt(yearAsString);
		int nextYearAsInt = Math.min(yearAsInt + 1, 9999);
		int upperBound = 9999;
		String formattedNextYear = formatIntegerAsDigitString(new Integer(
				nextYearAsInt), "0000");
		String formattedUpperBound = formatIntegerAsDigitString(new Integer(
				upperBound), "0000");
		return luceneFieldName + ":[ " + formattedNextYear + " TO "
				+ formattedUpperBound + " ] ";
	}

	public static String formatGreaterThanOrEqualToYear(String luceneFieldName,
			String yearAsString) {
		int yearAsInt = Integer.parseInt(yearAsString);
		int upperBound = 9999;
		String formattedNextYear = formatIntegerAsDigitString(new Integer(
				yearAsInt), "0000");
		String formattedUpperBound = formatIntegerAsDigitString(new Integer(
				upperBound), "0000");
		return luceneFieldName + ":[ " + formattedNextYear + " TO "
				+ formattedUpperBound + " ] ";
	}

	/*
	 * This suite of time query builders works with full date/times formatted as
	 * yyyy-MM-dd-HH-mm-ss-SSSS Java's GregorianCalendar is used to do date
	 * arithmetic, but the general assumption is that all data processed will be
	 * in a recent to near future time frame.
	 */

	public static String formatEqualsDateTimeYear(String luceneFieldName,
			String yearAsString) {
		int yearAsInt = Integer.parseInt(yearAsString);
		GregorianCalendar startCalendar = new GregorianCalendar();
		startCalendar.clear();
		startCalendar.set(Calendar.YEAR, yearAsInt);
		GregorianCalendar endCalendar = new GregorianCalendar();
		endCalendar.clear();
		endCalendar.set(Calendar.YEAR, yearAsInt + 1);
		endCalendar.set(Calendar.MILLISECOND, -1);
		String formattedStartDate = ODIE_LexicalizerFormatUtils.indexDateFormatter
				.format(startCalendar.getTime());
		String formattedEndDate = ODIE_LexicalizerFormatUtils.indexDateFormatter
				.format(endCalendar.getTime());
		return luceneFieldName + ":[ " + formattedStartDate + " TO "
				+ formattedEndDate + " ] ";
	}

	public static String formatNotEqualsDateTimeYear(String luceneFieldName,
			String yearAsString) {
		int yearAsInt = Integer.parseInt(yearAsString);
		GregorianCalendar startCalendar = new GregorianCalendar();
		startCalendar.clear();
		startCalendar.set(Calendar.YEAR, yearAsInt);
		startCalendar.set(Calendar.MILLISECOND, -1);
		GregorianCalendar endCalendar = new GregorianCalendar();
		endCalendar.clear();
		endCalendar.set(Calendar.YEAR, yearAsInt + 1);
		String formattedStartDate = minimumTimeString;
		String formattedEndDate = ODIE_LexicalizerFormatUtils.indexDateFormatter
				.format(startCalendar.getTime());
		String result = luceneFieldName + ":[ " + formattedStartDate + " TO "
				+ formattedEndDate + " ] ";
		formattedStartDate = ODIE_LexicalizerFormatUtils.indexDateFormatter
				.format(endCalendar.getTime());
		formattedEndDate = maximumTimeString;
		result += " OR ";
		result += luceneFieldName + ":[ " + formattedStartDate + " TO "
				+ formattedEndDate + " ] ";
		return " ( " + result + " ) ";
	}

	public static String formatLessThanDateTimeYear(String luceneFieldName,
			String yearAsString) {
		int yearAsInt = Integer.parseInt(yearAsString);
		GregorianCalendar endCalendar = new GregorianCalendar();
		endCalendar.clear();
		endCalendar.set(Calendar.YEAR, yearAsInt);
		endCalendar.set(Calendar.MILLISECOND, -1);
		String formattedStartDate = minimumTimeString;
		String formattedEndDate = ODIE_LexicalizerFormatUtils.indexDateFormatter
				.format(endCalendar.getTime());
		return luceneFieldName + ":[ " + formattedStartDate + " TO "
				+ formattedEndDate + " ] ";
	}

	public static String formatLessOrEqualToDateTimeYear(
			String luceneFieldName, String yearAsString) {
		int yearAsInt = Integer.parseInt(yearAsString);
		GregorianCalendar endCalendar = new GregorianCalendar();
		endCalendar.clear();
		endCalendar.set(Calendar.YEAR, yearAsInt + 1);
		endCalendar.set(Calendar.MILLISECOND, -1);
		String formattedStartDate = minimumTimeString;
		String formattedEndDate = ODIE_LexicalizerFormatUtils.indexDateFormatter
				.format(endCalendar.getTime());
		return luceneFieldName + ":[ " + formattedStartDate + " TO "
				+ formattedEndDate + " ] ";
	}

	public static String formatGreaterThanDateTimeYear(String luceneFieldName,
			String yearAsString) {
		int yearAsInt = Integer.parseInt(yearAsString);
		GregorianCalendar startCalendar = new GregorianCalendar();
		startCalendar.clear();
		startCalendar.set(Calendar.YEAR, yearAsInt + 1);
		startCalendar.set(Calendar.MILLISECOND, -1);
		String formattedStartDate = ODIE_LexicalizerFormatUtils.indexDateFormatter
				.format(startCalendar.getTime());
		String formattedEndDate = maximumTimeString;
		return luceneFieldName + ":[ " + formattedStartDate + " TO "
				+ formattedEndDate + " ] ";
	}

	public static String formatGreaterThanOrEqualToDateTimeYear(
			String luceneFieldName, String yearAsString) {
		int yearAsInt = Integer.parseInt(yearAsString);
		GregorianCalendar startCalendar = new GregorianCalendar();
		startCalendar.clear();
		startCalendar.set(Calendar.YEAR, yearAsInt);
		startCalendar.set(Calendar.MILLISECOND, -1);
		String formattedStartDate = ODIE_LexicalizerFormatUtils.indexDateFormatter
				.format(startCalendar.getTime());
		String formattedEndDate = maximumTimeString;
		return luceneFieldName + ":[ " + formattedStartDate + " TO "
				+ formattedEndDate + " ] ";
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

	/**
	 * Method rightJustify.
	 * 
	 * @param value
	 *            Long
	 * @param spacePattern
	 *            String
	 * 
	 * @return String
	 */
	public static String rightJustify(Long value, String spacePattern) {
		return rightJustify(value + "", spacePattern.length());
	}

	/**
	 * Method rightJustify.
	 * 
	 * @param value
	 *            Long
	 * @param spacePattern
	 *            String
	 * 
	 * @return String
	 */
	public static String rightJustify(String value, int width) {
		String result = value;
		if (value.length() < width) {
			StringBuffer sb = new StringBuffer();
			for (int idx = value.length() - 1; idx >= 0; idx--) {
				sb.append(value.charAt(idx));
			}
			for (int idx = 0; idx < width - value.length(); idx++) {
				sb.append(' ');
			}
			sb = sb.reverse();
			result = sb.toString();
		}
		return result;
	}

	/**
	 * Method timeStampToString.
	 * 
	 * @param showSecond
	 *            boolean
	 * @param ts
	 *            Timestamp
	 * 
	 * @return String
	 */
	public static String timeStampToString(Timestamp ts, boolean showSecond) {
		long time = ts.getTime();
		SimpleDateFormat dateFormatter = null;
		Date dt = new Date(time);
		if (showSecond) {
			dateFormatter = new java.text.SimpleDateFormat(
					"MM-dd-yyyy HH:mm:ss");
		} else {
			dateFormatter = new java.text.SimpleDateFormat("MM-dd-yyyy HH:mm");
		}
		return dateFormatter.format(dt);
	}

	/**
	 * Method formatTimestampForMySQL.
	 * 
	 * @param timeStamp
	 *            Timestamp
	 * 
	 * @return String
	 */
	public static String formatTimestampForMySQL(Timestamp timeStamp) {
		String result = null;
		try {
			SimpleDateFormat dateFormatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			result = dateFormatter
					.format(new java.sql.Date(timeStamp.getTime()));
		} catch (Exception x) {
			;
		}

		return result;

	}

	/**
	 * This method converts an oracle formatted date (yyyy-mm-dd) to a standard
	 * US format String: mm-dd-yyyy
	 */
	public static String convertToOracleDateTimeFormat(java.sql.Date dt) {
		try {
			java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat(
					"MM-dd-yyyy HH:mm");
			return dateFormatter.format(dt);
		} catch (Exception e) {
			return "";
		}
	}
	
    public static String convertToOracleDateTimeFormat( java.sql.Timestamp ts) {
        long time = ts.getTime();
        java.sql.Date dt = new java.sql.Date(time);
	    return convertToOracleDateTimeFormat(dt) ;
   }

	/**
	 * Method formatDateForMySQL.
	 * 
	 * @param theDate
	 *            the the date
	 * @param timeStamp
	 *            Timestamp
	 * 
	 * @return String
	 */
	public static String formatDateForMySQL(Date theDate) {
		return formatTimestampForMySQL(new Timestamp(theDate.getTime()));
	}

	/**
	 * Method repeatCharacterSequence
	 * 
	 * @param c
	 *            the character to be repeated
	 * @param sequenceLength
	 * 
	 * @return String
	 */
	public static String repeatCharacterSequence(char c, int sequenceLength) {
		StringBuffer sb = new StringBuffer();
		for (int idx = 0; idx < sequenceLength; idx++) {
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * reformatNCImCuiToMMTxCui
	 * 
	 * @param ncimCui
	 *            may have format of "CL0012345"
	 * 
	 * @return C90012345
	 */
	public static String reformatNCImCuiToMMTxCui(String ncimCui) {
		return ncimCui.replaceAll("^CL", "C9");
	}

	/**
	 * reformatMMTxCuiToNCImCui
	 * 
	 * @param mmtxCui
	 *            may have format of "C90012345"
	 * 
	 * @return CL0012345
	 */
	public static String reformatMMTxCuiToNCImCui(String mmtxCui) {
		return mmtxCui.replaceAll("^C9", "CL");
	}

	/**
	 * reformatNCImParentList
	 * 
	 * @param ncitParents
	 *            may have format of "CL107025:3, C0006826:5"
	 * 
	 * @return "C9107025:3, C0006826:5"
	 */
	public static String reformatNCItParentList(String ncitParents) {
		String mmtxParents = "";
		String parentSeparator = ", ";
		StringTokenizer st = new StringTokenizer(ncitParents, ",");
		int numberOfTokens = st.countTokens();
		System.out.println("numberOfTokens = " + numberOfTokens);
		for (int jdx = 0; jdx < numberOfTokens; jdx++) {
			String parent = st.nextToken();
			parent = parent.replaceAll("^\\s+", "");
			mmtxParents += ODIE_LexicalizerFormatUtils.reformatNCImCuiToMMTxCui(parent)
					+ parentSeparator;
		}
		if (mmtxParents.endsWith(parentSeparator)) {
			mmtxParents = mmtxParents.substring(0, mmtxParents.length()
					- parentSeparator.length());
		}
		return mmtxParents;
	}

	public static String truncate(String inputString) {
		return (inputString != null) ? inputString.substring(0, Math.min(inputString.length(), 25)) : null ;
	}

}

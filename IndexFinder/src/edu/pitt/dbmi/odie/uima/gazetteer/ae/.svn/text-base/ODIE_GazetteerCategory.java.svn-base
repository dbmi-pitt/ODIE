package edu.pitt.dbmi.odie.uima.gazetteer.ae;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

import net.openai.util.fsm.AnyCondition;
import net.openai.util.fsm.Condition;
import net.openai.util.fsm.Machine;
import net.openai.util.fsm.State;

import edu.mayo.bmi.fsm.condition.WordSetCondition;
import edu.mayo.bmi.fsm.state.NamedState;
import edu.upmc.opi.caBIG.common.CaBIG_ReadWriteTextFile;

public class ODIE_GazetteerCategory {
	
	private static final String COMMENT_DELIMITER = "#" ;
	
	private String majorCategory ;
	
	private String minorCategory ;
	
	private Machine fsm ;
	

	public void setDirectoryUrl(URL directoryUrl) {
		this.directoryUrl = directoryUrl;
	}

	private String termFileName ;
	
	private final HashSet<String> terms = new HashSet<String>() ;
	
	public ODIE_GazetteerCategory() {
		;
	}
	
	public void initialize() {
		initializeTermList() ;
		initializeFsm() ;
	}
	
	private void initializeFsm() {
		
		State startState = new NamedState("START");
		State endState = new NamedState("END");
		endState.setEndStateFlag(true);
	
		Condition termCondition = new WordSetCondition(
				terms, false);
		
		Condition caseSensitiveTermCondition = new WordSetCondition(terms, true) ;
		
		startState.addTransition(termCondition, endState) ;
//		startState.addTransition(caseSensitiveTermCondition, endState) ;
		startState.addTransition(new AnyCondition(), startState);
		endState.addTransition(new AnyCondition(), startState);
		
		this.fsm = new Machine(startState);
		
	}

	private void initializeTermList() {
		URL termFileUrl;
		try {
			
			//
			// Cache the list file associated with this majorCategory, minorCategory
			//
			String directoryPath = directoryUrl.toString() ;
			String fullTermFileUrlPath = directoryPath.replaceAll("[\\/][\\w\\.\\s]+$", "") + "/" + termFileName ;
			termFileUrl = new URL(fullTermFileUrlPath);
			String termFileContents = CaBIG_ReadWriteTextFile.readFileFromURL(termFileUrl) ;
			String[] termFileLines = termFileContents.split("\n") ;
			for (int idx = 0 ; idx < termFileLines.length ; idx++) {
				String termFileLine = termFileLines[ idx ] ;
				termFileLine = termFileLine.replace("^\\s+", "").replace("\\s+$", "") ;
				if (termFileLine.length() == 0) {
					;
				}
				else if (termFileLine.startsWith(COMMENT_DELIMITER)) {
					;
				}
				else if (!termFileLine.matches("^\\w+$")) {
					;
				}
				else {
					terms.add(termFileLine.toLowerCase()) ;
				}
			}
			
			
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public String getMajorCategory() {
		return majorCategory;
	}

	public void setMajorCategory(String majorCategory) {
		this.majorCategory = majorCategory;
	}

	public String getMinorCategory() {
		return minorCategory;
	}

	public void setMinorCategory(String minorCategory) {
		this.minorCategory = minorCategory;
	}
	
	public String getTermFileName() {
		return termFileName;
	}

	public void setTermFileName(String termFileName) {
		this.termFileName = termFileName;
	}

	public HashSet<String> getTerms() {
		return terms;
	}
	
	private URL directoryUrl ;
	
	public URL getDirectoryUrl() {
		return directoryUrl;
	}
	
	public Machine getFsm() {
		return fsm;
	}

	public void setFsm(Machine fsm) {
		this.fsm = fsm;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer() ;
		sb.append("majorCategory = " + this.majorCategory + "\n") ;
		sb.append("minorCategory = " + this.minorCategory + "\n") ;
		for (String term : this.terms) {
			sb.append("\t" + term + "\n") ;
		}
		return sb.toString();
	}
	
}

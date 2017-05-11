package edu.pitt.dbmi.odie.uima.dekanlin.model;

public class ODIE_MiniparTriplePair {
	
	public ODIE_MiniparTriple tripleOne;
	public ODIE_MiniparTriple tripleTwo;

	public ODIE_MiniparTriplePair() {
		;
	}

	public double getMinimumInfo() {
		return Math.min(tripleOne.info, tripleTwo.info);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Triple Pair: ");
		sb.append(tripleOne.toString());
		sb.append(", ");
		sb.append(tripleTwo.toString());
		return sb.toString();
	}
}

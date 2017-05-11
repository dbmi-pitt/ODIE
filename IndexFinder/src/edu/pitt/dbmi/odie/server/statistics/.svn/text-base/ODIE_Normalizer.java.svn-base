package edu.pitt.dbmi.odie.server.statistics;

public class ODIE_Normalizer {
	
	private Double[] data ;
	private Double dataMax = 0.0d ;
	private Double dataMin = 0.0d ;
	private Double normMax = 0.0d ;
	private Double normMin = 0.0d ;
	
	public static void main(String[] args) {
	}
	
	public ODIE_Normalizer() {
		;
	}
	
	public Double normalize(Double x) {
		//
		// When converting a range between A,...,B to a range 1,...,10
		//
		//      y = 1 + (x-A) * (10-1) / (B-A)
		//
		Double dataRange = dataMax - dataMin ;
		Double normRange = normMax - normMin ;
		Double rangeRatio = normRange / dataRange ;
		Double y = normMin + (x - dataMin) * rangeRatio ;
		return y ;
	}
	
	public void normalizeDataSet() {
		for (int idx = 0 ; idx < data.length ; idx++) {
			data[ idx ] = normalize(data[ idx ]) ;
		}
	}
	
	public Double[] getData() {
		return data;
	}

	public void setData(Double[] data) {
		this.data = data;
	}

	public Double getDataMax() {
		return dataMax;
	}

	public void setDataMax(Double dataMax) {
		this.dataMax = dataMax;
	}

	public Double getDataMin() {
		return dataMin;
	}

	public void setDataMin(Double dataMin) {
		this.dataMin = dataMin;
	}

	public Double getNormMax() {
		return normMax;
	}

	public void setNormMax(Double normMax) {
		this.normMax = normMax;
	}

	public Double getNormMin() {
		return normMin;
	}

	public void setNormMin(Double normMin) {
		this.normMin = normMin;
	}
	
}

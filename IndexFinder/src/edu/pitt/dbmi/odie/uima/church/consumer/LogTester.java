package edu.pitt.dbmi.odie.uima.church.consumer;

public class LogTester {
	
	public static void main(String[] args) {
		

//		log(base 2) x=log(base e) x/log(base e) 2
		double n = 180.0d ;
		double fx = 4.0d ;
		double fy = 1.0d ;
		double px = fx / n ;
		double py = fy / n ;
		double fxy = 1.0d ;
		double pxy = 1.0d / n ;
		
	    double numerator = pxy ;
	    double denominator = px * py ;
	    double quotient = numerator / denominator ;
		double result = log2(quotient) ;
		
		result = Math.log( 32.0d / 7.0d ) ;
		
		System.out.println("The result is " + result) ;
		
	}
	
	private static double log2(double num) {
		return (Math.log(num) / Math.log(2));
	}
}

package edu.pitt.dbmi.odie.uima.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ODIE_NormalizationUtils {

	public static void normalizeRanges(Connection conn, String qualifiedTableName, String whereClause, String srcColumnName, String tgtColumnName) {
		try {
			Double dataMin = getDataMin(conn, qualifiedTableName, whereClause, srcColumnName) ;
			Double dataMax = getDataMax(conn, qualifiedTableName, whereClause, srcColumnName) ;
			Double dataRange = dataMax - dataMin ;
			if (dataRange > 1.0e-5d) {
				Double normalizationRatio = 1.0d / dataRange ;
				StringBuffer sb = new StringBuffer() ;
				sb.append("update " + qualifiedTableName) ;
				sb.append(" set " + tgtColumnName + " = (" + srcColumnName + " - ?) * ? ") ;
				sb.append(" where " + srcColumnName + " >= 0.00") ;
				sb.append(" and ") ;
				sb.append(whereClause) ;
				String sql = sb.toString() ;
				PreparedStatement normalizePreparedStatement;
				normalizePreparedStatement = conn.prepareStatement(sql);
				normalizePreparedStatement.setDouble(1, dataMin) ;
				normalizePreparedStatement.setDouble(2, normalizationRatio) ;
				normalizePreparedStatement.executeUpdate() ;
				normalizePreparedStatement.close();
			}
			else {
				// All scores are equal.  Force all scores to either 0.0 or 1.0.
				StringBuffer sb = new StringBuffer() ;
				sb.append("update " + qualifiedTableName) ;
				sb.append(" set " + tgtColumnName + " = 1.0") ;
				sb.append(" where " + srcColumnName + " > 0.00") ;
				sb.append(" and ") ;
				sb.append(whereClause) ;
				String sql = sb.toString() ;
				PreparedStatement normalizePreparedStatement;
				normalizePreparedStatement = conn.prepareStatement(sql);
				normalizePreparedStatement.executeUpdate() ;
				normalizePreparedStatement.close() ;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
		
	private static Double getDataMax(Connection conn, String qualifiedTableName, String whereClause, String srcColumnName) {
		Double result = new Double(-1.0d) ;
		try {
			StringBuffer sb = new StringBuffer() ;
			sb.append("select max(" + srcColumnName + ") ") ;
			sb.append("from " + qualifiedTableName + " ") ;
			sb.append(" where ") ;
			sb.append(srcColumnName + " >= 0.00") ;
			sb.append(" and ") ;
			sb.append(whereClause) ;
			String sql = sb.toString() ;	
			PreparedStatement selectMaxValuePreparedStatement = conn.prepareStatement(sql);
			ResultSet rs = selectMaxValuePreparedStatement.executeQuery() ;
			if (rs.next()) {
				result = rs.getDouble(1) ;
			}
			selectMaxValuePreparedStatement.close();
		} catch (Exception e) {
			;
		} 
		return result ;
	}
	
	private static Double getDataMin(Connection conn, String qualifiedTableName, String whereClause, String srcColumnName) {
		Double result = null ;
		try {
			StringBuffer sb = new StringBuffer() ;
			sb.append("select min(" + srcColumnName + ") ") ;
			sb.append("from " + qualifiedTableName + " ") ;
			sb.append(" where ") ;
			sb.append(srcColumnName + " >= 0.00") ;
			sb.append(" and ") ;
			sb.append(whereClause) ;
			String sql = sb.toString() ;	
			PreparedStatement selectMaxValuePreparedStatement = conn.prepareStatement(sql);
			ResultSet rs = selectMaxValuePreparedStatement.executeQuery() ;
			if (rs.next()) {
				result = rs.getDouble(1) ;
			}
			selectMaxValuePreparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result ;
	}
	
	public static double log2(double num) {
		return (Math.log(num) / Math.log(2));
	}
}

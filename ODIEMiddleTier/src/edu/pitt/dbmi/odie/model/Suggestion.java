package edu.pitt.dbmi.odie.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import edu.pitt.ontology.IClass;


/**
 * @author Girish Chavan
 * 
 */

@Entity
@Table(name = "suggestion")
public class Suggestion {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable=false, nullable=false)
	private Long id;

	@Column(name="NER_NEGATIVE")
	String nerNegative;
	
	@Column(name="NER_POSITIVE")
	String nerPositive;
	
	String method;
	
	String rule;
	
	Float score = 0f;
	
	//used to differentiate between a collapsed suggestion and regular suggestion in the tree table
	@Transient
	boolean isAggregate = false;

	@Transient
	IClass conceptContextClass;
	@Transient
	boolean goodSuggestion = false;
	@Transient
	String newConceptName = "";
	
	public List<AnalysisDocument> getAnalysisDocuments() {
		return analysisDocuments;
	}

	public void setAnalysisDocuments(List<AnalysisDocument> analysisDocuments) {
		this.analysisDocuments = analysisDocuments;
	}

	@Transient
	Analysis analysis;
	
	@Transient 
	List<AnalysisDocument> analysisDocuments = null;
	
	public Analysis getAnalysis() {
		return analysis;
	}

	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}

	public String getNewConceptName() {
		return newConceptName;
	}

	public void setNewConceptName(String newConceptName) {
		this.newConceptName = newConceptName;
	}

	public Boolean isGoodSuggestion() {
		return goodSuggestion;
	}

	public void setGoodSuggestion(Boolean goodSuggestion) {
		this.goodSuggestion = goodSuggestion;
	}

	public boolean isAggregate() {
		return isAggregate;
	}

	public void setAggregate(boolean isAggregate) {
		this.isAggregate = isAggregate;
	}

	public IClass getConceptContextClass() {
		return conceptContextClass;
	}

	public void setConceptContextClass(IClass conceptContextClass) {
		this.conceptContextClass = conceptContextClass;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNerNegative() {
		return nerNegative;
	}

	public void setNerNegative(String nerNegative) {
		this.nerNegative = nerNegative;
	}

	public String getNerPositive() {
		return nerPositive;
	}

	public void setNerPositive(String nerPositive) {
		this.nerPositive = nerPositive;
	}

	
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public Float getScore() {
		return score;
	}

	public void setScore(Float score) {
		this.score = score;
	}


}


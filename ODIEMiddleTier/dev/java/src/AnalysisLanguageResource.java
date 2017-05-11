/**
 * 
 */
package edu.pitt.dbmi.odie.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Girish Chavan
 * 
 */

@Entity
@Table(name="odie_analysis_lr")
public class AnalysisLanguageResource {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable=false, nullable=false)
	private Long id;
	
	boolean isProposalOntology = false;
	
	
	@ManyToOne(optional=false)
	Analysis analysis;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="lr_id")
	LanguageResource languageResource;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AnalysisLanguageResource() {

	}

	public AnalysisLanguageResource(Analysis analysis, LanguageResource lr) {
		super();
		this.analysis = analysis;
		this.languageResource = lr;
	}

	public Analysis getAnalysis() {
		return analysis;
	}


	public LanguageResource getLanguageResource() {
		return languageResource;
	}

	public boolean isProposalOntology() {
		return isProposalOntology;
	}

	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pitt.dbmi.odie.model.IPersistable#getID()
	 */

	public void setIsProposalOntology(boolean isProposalOntology) {
		this.isProposalOntology = isProposalOntology;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pitt.dbmi.odie.model.IPersistable#setID(int)
	 */

	public void setLanguageResource(LanguageResource lr) {
		this.languageResource = lr;
	}

}

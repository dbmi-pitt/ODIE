package edu.pitt.dbmi.odie.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="odie_lexicalset")
public class LexicalSet {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable=false, nullable=false)
	private Long id;
	
	String name;
	String location;
	
	String description;
	
	@OneToMany(cascade={CascadeType.ALL}, mappedBy="lexicalSet", fetch=FetchType.LAZY)
	List<LexicalSetLanguageResource> lexicalSetLanguageResources = new ArrayList<LexicalSetLanguageResource>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * 
	 * @return Returns a string with ontology names separated by ','
	 */
	public String getOntologyNames(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");
		
		Collections.sort(lexicalSetLanguageResources,new LSLRComparator());
		
		for(LexicalSetLanguageResource lslr:lexicalSetLanguageResources)
			sb.append(lslr.getLanguageResourceName() + ",");
		if(sb.length()>1){
			String out = sb.toString();
			return out.substring(0,out.length()-1) + "}";
		}
		else
			return "{}";
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public List<LexicalSetLanguageResource> getLexicalSetLanguageResources() {
		return lexicalSetLanguageResources;
	}

	public void setLexicalSetLanguageResources(
			List<LexicalSetLanguageResource> lexicalSetLanguageResources) {
		this.lexicalSetLanguageResources = lexicalSetLanguageResources;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/*
	 * Adds a new LanguageResource to this LexicalSet. This will not save it to the database. You
	 * must persist this object separately after calling this method.
	 */
	public void addLanguageResource(LanguageResource lr) {
		LexicalSetLanguageResource lslr = new LexicalSetLanguageResource();
		lslr.setLexicalSet(this);
		lslr.setLanguageResource(lr);
		getLexicalSetLanguageResources().add(lslr);
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void addLanguageResource(LanguageResource lr,
			URI subsetParentURI) {
		LexicalSetLanguageResource lslr = new LexicalSetLanguageResource();
		lslr.setLexicalSet(this);
		lslr.setLanguageResource(lr);
		lslr.setSubsetParentURI(subsetParentURI);
		getLexicalSetLanguageResources().add(lslr);
		
	}
	
	
	
}

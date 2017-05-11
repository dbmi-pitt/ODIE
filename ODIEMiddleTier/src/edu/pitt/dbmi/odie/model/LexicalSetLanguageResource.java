package edu.pitt.dbmi.odie.model;

import java.net.URI;
import java.net.URISyntaxException;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import edu.pitt.ontology.IResource;

@Entity
@Table(name="odie_lexicalset_lr")
public class LexicalSetLanguageResource {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable=false, nullable=false)
	private Long id;
	
	@ManyToOne(optional=false)
	private LanguageResource languageResource;
	
	@ManyToOne(optional=false)
	private LexicalSet lexicalSet;

	@Column(name="subsetParentURI", length=1024)
	String subsetParentURIString;
	
	@Transient
	URI subsetParentURI;

	@Transient
	IResource resource;

	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LanguageResource getLanguageResource() {
		return languageResource;
	}

	public void setLanguageResource(LanguageResource languageResource) {
		this.languageResource = languageResource;
	}

	public LexicalSet getLexicalSet() {
		return lexicalSet;
	}

	public void setLexicalSet(LexicalSet lexicalSet) {
		this.lexicalSet = lexicalSet;
	}

	public String getLanguageResourceName() {
		return getLanguageResource().getName();
	}
	
	private String getSubsetParentUriString() {
		return subsetParentURIString;
	}

	private void setSubsetParentUriString(String uriStr) {
		this.subsetParentURIString = uriStr;
	}
	public URI getSubsetParentURI() {
		if(subsetParentURI==null && subsetParentURIString!=null){
			try {
				subsetParentURI = new URI(subsetParentURIString);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return subsetParentURI;
	}

	
	public void setSubsetParentURI(URI uri) {
		this.subsetParentURI = uri;
		setSubsetParentUriString(uri.toASCIIString());
	}
}

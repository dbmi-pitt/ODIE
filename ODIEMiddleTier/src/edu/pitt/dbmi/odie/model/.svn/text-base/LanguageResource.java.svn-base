/**
 * 
 */
package edu.pitt.dbmi.odie.model;

import java.net.URI;
import java.net.URISyntaxException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import edu.pitt.ontology.IResource;

/**
 * @author Girish Chavan
 * 
 */

@Entity
@Table(name = "odie_lr")
public class LanguageResource {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable=false, nullable=false)
	private Long id;

	String name;
	@Column(length=1024)
	String description;
	String type;
	
	@Column(length=1024)
	String location;
	String format;
	String version;
	
	@Column(name="uri", length=1024)
	String uriString;
	
	@Transient
	URI uri;

	@Transient
	IResource resource;

	private String getUriString() {
		return uriString;
	}

	private void setUriString(String uriStr) {
		this.uriString = uriStr;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		if (description == null && uri != null)
			description = getResource().getDescription();

		return description;
	}

	public String getFormat() {
		return format;
	}

	public String getLocation() {
		return location;
	}

	public String getName() {
//		if (name == null && uri != null) {
//			name = getResource().getName();
//		}

		return name;
	}

	public IResource getResource() {
		return resource;
	}

	public String getType() {
		return type;
	}

	public URI getURI() {
		if(uri==null && uriString!=null){
			try {
				uri = new URI(uriString);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return uri;
	}

	public String getVersion() {
		return version;
	}



	public void setDescription(String description) {
		this.description = description;
	}

	public void setFormat(String format) {
		this.format = format;
	}


	public void setLocation(String location) {
		this.location = location;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setResource(IResource lr) {
		this.resource = lr;
//		initObjectFromResource();
	}

//	private void initObjectFromResource() {
//		setURI(resource.getURI());
//		setName(resource.getName());
//		setDescription(resource.getDescription());
//		setVersion(resource.getVersion());
//		setFormat(resource.getFormat());
//		setLocation(resource.getLocation());
//	}

	public void setType(String type) {
		this.type = type;
	}

	public void setURI(URI uri) {
		this.uri = uri;
		setUriString(uri.toASCIIString());
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		
		if(obj instanceof LanguageResource){
			return (((LanguageResource)obj).getId() == this.getId());
		}
		
		return false;
	}
	
	
}

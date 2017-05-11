/**
 * 
 */
package edu.pitt.dbmi.odie.model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import edu.pitt.dbmi.odie.ODIEUtils;

/**
 * @author Girish Chavan
 * 
 */

@Entity
@Indexed
@Table(name="odie_document")
public class Document{
	
	@Id
	@DocumentId
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable=false, nullable=false)
	private Long id;
	
	private String name;
	
	@Column(name="uri", unique=true)
	private String uriString;
	
	@Transient
	Logger logger = Logger.getLogger(this.getClass());

	@Transient
	URI uri;

	@Transient
	@Field
	private String text = null;
	
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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Document) {
			return getURI().equals(((Document) obj).getURI());
		}
		return super.equals(obj);
	}


	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pitt.dbmi.odie.model.IODIEDocument#getSize()
	 */
	public long getSize() {
		if (text != null)
			return text.length();
		else
			return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pitt.dbmi.odie.model.IODIEDocument#getText()
	 */
	public String getText() {
		return text;
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

	@Override
	public int hashCode() {
		return uri.hashCode();
	}

	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pitt.dbmi.odie.model.IODIEDocument#setText(java.lang.String)
	 */
	public void setText(String text) {
		this.text = text;

	}

	public void setURI(URI uri) throws MalformedURLException, IOException {
		this.uri = uri;
		setUriString(uri.toASCIIString());
		loadTextFromURI();
		
	}
	
	/**
	 * Initializes the transient text field with text read from the URI stream. URI must be 
	 * not null prior to invoking this method. 
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public void loadTextFromURI() throws MalformedURLException, IOException, NullPointerException {
		if(text==null)
			setText(ODIEUtils.readStream(getURI().toURL().openStream()));
	}

}

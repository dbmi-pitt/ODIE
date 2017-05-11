package edu.pitt.dbmi.odie.model;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.TypeSystem;

import edu.pitt.dbmi.odie.ODIEException;

@Entity
@Table(name="odie_analysis_engine")
public class AnalysisEngineMetadata {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable=false, nullable=false)
	Long id;
	
	String name;
	String type;
	
	@Column(length=1000)
	String description;
	
	@Column(name="url")
	private String urlString;
	public static final int TS_COLUMN_LENGTH = 500000;
	
	@Column(length=TS_COLUMN_LENGTH)
	private String serializedTypeSystemDescriptor;
	
	@Transient
	URL url;

	@Transient
	AnalysisEngine analysisEngine;

	@Transient
	TypeSystem typeSystem;
	
	@Transient
	DisplayPreferences displayPreferences;
	
	
	
	public DisplayPreferences getDisplayPreferences() {
		return displayPreferences;
	}

	public void setDisplayPreferences(DisplayPreferences displayPreferences) {
		this.displayPreferences = displayPreferences;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private String getUrlString() {
		return urlString;
	}

	private void setUrlString(String uriString) {
		this.urlString = uriString;
	}

	public URL getURL() {
		if(url==null && urlString!=null){
			try {
				url = new URL(urlString);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return url;
	}

	public void setURL(URL url) {
		this.url = url;
		setUrlString(url.toExternalForm());
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AnalysisEngine getAnalysisEngine() {
		return analysisEngine;
	}

	public void setAnalysisEngine(AnalysisEngine analysisEngine) {
		this.analysisEngine = analysisEngine;
	}

	public String getSerializedTypeSystemDescriptor() {
		return serializedTypeSystemDescriptor;
	}

	public void setSerializedTypeSystemDescriptor(String serializedTypeSystemDescriptor) throws ODIEException {
		this.serializedTypeSystemDescriptor = serializedTypeSystemDescriptor;
		
		if(serializedTypeSystemDescriptor.length()> TS_COLUMN_LENGTH)
			throw new ODIEException("Serialized Type System Descriptor Length is larger than database column length. " +
					"Data loss might occur when saved to the database.");
		
	}

	/*
	 * Returns the TypeSystem for this Analysis Engine. Type System must be initialized
	 * before this method is called. See GeneralUtils.initTypeSystemIfRequired()
	 */
	public TypeSystem getTypeSystem() {
		return typeSystem;
	}

	public void setTypeSystem(TypeSystem typeSystem) {
		this.typeSystem = typeSystem;
	}
	
	
}

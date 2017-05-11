package edu.pitt.dbmi.odie.middletier;

import java.util.HashMap;
import java.util.Properties;

import edu.pitt.ontology.IClass;
import edu.pitt.ontology.bioportal.BOntology;

public class BioportalNote {
	
	String id;
	String ontologyId;
	String type;
	String appliesToID;
	String appliesToType;
	String subject;
	String authorID;
	String reasonForChange;
	String contactInfo;
	String newTermDefinition;
	String newTermID;
	String newTermParentID;
	String newTermPreferredName;
	String newTermSynonyms;
	
	IClass newTermParent;
	IClass newConceptClass;
	BOntology appliesToOntology;
	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOntologyId() {
		return ontologyId;
	}
	public void setOntologyId(String ontologyId) {
		this.ontologyId = ontologyId;
	}
	public IClass getNewConceptClass() {
		return newConceptClass;
	}
	public void setNewConceptClass(IClass newConceptClass) {
		this.newConceptClass = newConceptClass;
	}
	public void setAppliesToOntology(BOntology appliesToOntology) {
		this.appliesToOntology = appliesToOntology;
		this.appliesToID = getIDForBOntology(appliesToOntology);
	}
	public IClass getNewTermParent() {
		return newTermParent;
	}
	public void setNewTermParent(IClass newTermParent) {
		this.newTermParent = newTermParent;
	}
	public String getNewTermSynonyms() {
		return newTermSynonyms;
	}
	public void setNewTermSynonyms(String newTermSynonyms) {
		this.newTermSynonyms = newTermSynonyms;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAppliesToID() {
		return appliesToID;
	}
	public void setAppliesToID(String appliesToID) {
		this.appliesToID = appliesToID;
	}
	public String getAppliesToType() {
		return appliesToType;
	}
	public void setAppliesToType(String appliesToType) {
		this.appliesToType = appliesToType;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getAuthorID() {
		return authorID;
	}
	public void setAuthorID(String authorID) {
		this.authorID = authorID;
	}
	public String getReasonForChange() {
		return reasonForChange;
	}
	public void setReasonForChange(String reasonForChange) {
		this.reasonForChange = reasonForChange;
	}
	public String getContactInfo() {
		return contactInfo;
	}
	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}
	public String getNewTermDefinition() {
		return newTermDefinition;
	}
	public void setNewTermDefinition(String newTermDefinition) {
		this.newTermDefinition = newTermDefinition;
	}
	public String getNewTermID() {
		return newTermID;
	}
	private void setNewTermID(String newTermID) {
		this.newTermID = newTermID;
	}
	public String getNewTermParentID() {
		return newTermParentID;
	}

	public String getNewTermPreferredName() {
		return newTermPreferredName;
	}
	public void setNewTermPreferredName(String newTermPreferredName) {
		this.newTermPreferredName = newTermPreferredName;
	}
	
	public HashMap<String,String> getParameters(){
		HashMap<String,String> map = new HashMap<String, String>();
		
		if(getType()!=null)
			map.put("type",getType());
		
		if(getAppliesToOntology()!=null)
			map.put("appliesto",getIDForBOntology(getAppliesToOntology()));
		
		if(getAppliesToType()!=null)
			map.put("appliestotype",getAppliesToType());
			
		if(getSubject()!=null)
			map.put("subject",getSubject());
			
		if(getAuthorID()!=null)
			map.put("author",getAuthorID());
			
		if(getReasonForChange()!=null)
			map.put("reasonforchange",getReasonForChange());
			
		if(getContactInfo()!=null)
			map.put("contactinfo",getContactInfo());
			
		if(getNewTermID()!=null)
			map.put("termid",getNewTermID());
	
		if(getNewTermParent()!=null)
			map.put("termparent",getNewTermParent().getName());
			
		if(getNewTermPreferredName()!=null)
			map.put("termpreferredname",getNewTermPreferredName());
			
		if(getNewTermDefinition()!=null)
			map.put("termdefinition",getNewTermDefinition());
		
		if(getNewTermSynonyms()!=null)
			map.put("termsynonyms",getNewTermSynonyms());
			
		return map;
	
	}
	private String getIDForBOntology(BOntology o) {
		Properties p = o.getResourceProperties();
		return p.get("id").toString();
	}
	
	public BOntology getAppliesToOntology() {
		return appliesToOntology;
	}
}

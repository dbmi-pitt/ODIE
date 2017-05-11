package edu.pitt.dbmi.odie.model;

import java.net.URI;

@Deprecated
public interface IDocument {

	public boolean equals(Object document);

	public String getName();

	public long getSize();

	public String getText();

	public URI getURI();

	public void setName(String name);

	public void setText(String text);

	public void setURI(URI uri);
}

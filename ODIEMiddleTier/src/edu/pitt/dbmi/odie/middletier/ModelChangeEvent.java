package edu.pitt.dbmi.odie.middletier;

import java.util.EventObject;

public class ModelChangeEvent extends EventObject {

	public static final int ADDED = 1;
	public static final int DELETED = 2;
	public static final int MODIFIED = 0;

	public int type;

	public ModelChangeEvent(Object context, int type) {
		super(context);
		this.type = type;
	}

}

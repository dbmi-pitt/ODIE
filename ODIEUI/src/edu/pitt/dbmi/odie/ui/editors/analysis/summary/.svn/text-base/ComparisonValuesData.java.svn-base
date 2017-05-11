package edu.pitt.dbmi.odie.ui.editors.analysis.summary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ComparisonValuesData {
	private String label;
	private String tooltip;
	
	private List<Object> values;
	private boolean noDifferences = true;
	private Number greatestValue;
	private Number smallestValue;
	private Object sameValue;
	private List<ComparisonValuesData> children;
	private ComparisonValuesData parent;
	private HashMap<Object, String> labelMap;
	
	
	
	public ComparisonValuesData getParent() {
		return parent;
	}

	public String getTooltip() {
		if(tooltip == null)
			return label;
		
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public void setParent(ComparisonValuesData parent) {
		this.parent = parent;
	}

	public List<ComparisonValuesData> getChildren() {
		return children;
	}

	public void setChildren(List<ComparisonValuesData> children) {
		this.children = children;
	}

	public boolean noDifferences() {
		return noDifferences;
	}

	public ComparisonValuesData() {
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<Object> getValues() {
		return values;
	}


	public Number getGreatestValue() {
		return greatestValue;
	}


	public Number getSmallestValue() {
		return smallestValue;
	}

	public void addValueAtIndex(int index, Object o){
		addValueAtIndex(index, o, o.toString());
	}
	
	public void addValueAtIndex(int index, Object o, String label){
		if(values == null){
			values = new ArrayList<Object>();
			labelMap = new HashMap<Object,String>();
		}
		
		//if the index is out of range, then fill in empty strings into the list
		//upto and including the index.
		int diff = index+1 - values.size();
		for(int i=0;i<diff;i++)
			values.add("");
		
		//replace the empty string with the value
		values.set(index,o);
		labelMap.put(o, label);
		
		//if value is a number, update greatest and smallest values
		if(o instanceof Number){
			Number val = (Number)o;
			if(greatestValue == null || smallestValue == null){
				greatestValue = val;
				smallestValue = val;
			}
			else{
				if(smallestValue.floatValue() > val.floatValue())
					smallestValue = val;
				if(greatestValue.floatValue() < val.floatValue()){
					greatestValue = val;
				}
			}
			
		}
		
		//update noDifferences flag by comparing value with the other values already stored
		if(noDifferences){
			if(sameValue == null){
				sameValue = o;
				return;
			}
			noDifferences = sameValue.equals(o);
		}

	}
	
	public void addValue(Object o, String label){
		if(values == null){
			values = new ArrayList<Object>();
			labelMap = new HashMap<Object,String>();
		}
		addValueAtIndex(values.size(),o,label);
	}
	
	void addValue(Object o) {
		addValue(o,o.toString());
	}

	public String getValueLabel(int i) {
		if(values == null)
			return "";
		
		if(i<0 || i>=values.size())
			return "";
		
		Object val = values.get(i);
		if(val==null)
			return "";
		
		String vlabel = labelMap.get(val);
		return (vlabel==null?val.toString():vlabel);
				
	}

	boolean lesserIsHighlighted = false;
	
	
	public boolean isColumnHighlighted(int columnIndex) {
		if(values==null || columnIndex<0 || columnIndex >= values.size())
			return false;
		
		Object val = values.get(columnIndex);
		
		if(!noDifferences){
			if(lesserIsHighlighted)
				return smallestValue == val;
			else 
				return greatestValue == val;
				
		}
		return false;
	}

	public boolean isLesserIsHighlighted() {
		return lesserIsHighlighted;
	}

	public void setLesserIsHighlighted(boolean lesserIsHighlighted) {
		this.lesserIsHighlighted = lesserIsHighlighted;
	}
	
	
}
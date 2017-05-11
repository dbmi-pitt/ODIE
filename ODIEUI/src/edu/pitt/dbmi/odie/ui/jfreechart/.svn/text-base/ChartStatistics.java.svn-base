package edu.pitt.dbmi.odie.ui.jfreechart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.jfree.data.KeyedValues;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;

import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.Datapoint;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.views.DatapointComparator;
import edu.pitt.dbmi.odie.ui.views.DatapointComparator.SortOrder;
import edu.pitt.ontology.IOntology;

/**
 * @author Girish Chavan
 * 
 */
public class ChartStatistics implements KeyedValues, CategoryDataset {

	/** Storage for registered change listeners. */
	private transient EventListenerList listenerList = new EventListenerList();

	List<Datapoint> datapoints;
	List<String> rowKeys = new ArrayList<String>();

	private Analysis analysis;

	{
		rowKeys.add("Concepts");
	}

	public ChartStatistics(Analysis analysis) {
		super();
		this.analysis = analysis;
		initDatapoints(analysis);
	}

	HashMap<IOntology, Long> ontologyMap;

	public Analysis getAnalysis() {
		return analysis;
	}

	Logger logger = Logger.getLogger(ChartStatistics.class);

	private DatapointComparator comparator;

	private void initDatapoints(final Analysis analysis) {
		datapoints = analysis.getDatapoints();

		MiddleTier mt = Activator.getDefault().getMiddleTier();
		// ontologyMap = mt.getHitCountForAllOntologies(analysis,true);
		//		
		comparator = new DatapointComparator(SortOrder.ONTOLOGY_DOCFREQUENCY);
		for (Datapoint dp : datapoints) {
			dp.setComparator(comparator);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jfree.data.Values2D#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return getItemCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jfree.data.KeyedValues2D#getColumnIndex(java.lang.Comparable)
	 */
	@Override
	public int getColumnIndex(Comparable obj) {
		return getIndex(obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jfree.data.KeyedValues2D#getColumnKey(int)
	 */
	@Override
	public Comparable getColumnKey(int index) {
		return getKey(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jfree.data.KeyedValues2D#getColumnKeys()
	 */
	@Override
	public List getColumnKeys() {
		return getKeys();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jfree.data.general.Dataset#getGroup()
	 */
	@Override
	public DatasetGroup getGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jfree.data.KeyedValues#getIndex(java.lang.Comparable)
	 */
	@Override
	public int getIndex(Comparable obj) {
		return datapoints.indexOf(obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jfree.data.Values#getItemCount()
	 */
	@Override
	public int getItemCount() {
		return datapoints.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jfree.data.KeyedValues#getKey(int)
	 */
	@Override
	public Comparable getKey(int index) {
		return datapoints.get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jfree.data.KeyedValues#getKeys()
	 */
	@Override
	public List getKeys() {
		return datapoints;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jfree.data.Values2D#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jfree.data.KeyedValues2D#getRowIndex(java.lang.Comparable)
	 */
	@Override
	public int getRowIndex(Comparable arg0) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jfree.data.KeyedValues2D#getRowKey(int)
	 */
	@Override
	public Comparable getRowKey(int index) {
		return rowKeys.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jfree.data.KeyedValues2D#getRowKeys()
	 */
	@Override
	public List getRowKeys() {
		return rowKeys;
	}

	SortOrder mode = SortOrder.ONTOLOGY_DOCFREQUENCY;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jfree.data.KeyedValues#getValue(java.lang.Comparable)
	 */
	@Override
	public Number getValue(Comparable obj) {
		if (obj instanceof Datapoint) {
			if (mode == SortOrder.ONTOLOGY_DOCFREQUENCY)
				return ((Datapoint) obj).getDocumentFrequency();
			else if (mode == SortOrder.ONTOLOGY_OCCURENCES)
				return ((Datapoint) obj).getOccurences();
			else
				return 0;
		} else
			throw new IllegalArgumentException(obj.toString()
					+ " not found in list");
	}

	public SortOrder getMode() {
		return mode;
	}

	public void setMode(SortOrder mode) {
		this.mode = mode;
		handleModeChange();
	}

	private void handleModeChange() {
		comparator.order = mode;
		Collections.sort(datapoints);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jfree.data.KeyedValues2D#getValue(java.lang.Comparable,
	 * java.lang.Comparable)
	 */
	@Override
	public Number getValue(Comparable rowKey, Comparable colkey) {
		return getValue(colkey);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jfree.data.Values#getValue(int)
	 */
	@Override
	public Number getValue(int index) {
		return getValue(datapoints.get(index));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jfree.data.Values2D#getValue(int, int)
	 */
	@Override
	public Number getValue(int rowIndex, int colIndex) {

		return getValue(colIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jfree.data.general.Dataset#setGroup(org.jfree.data.general.DatasetGroup
	 * )
	 */
	@Override
	public void setGroup(DatasetGroup arg0) {
	}

	/**
	 * Notifies all registered listeners that the dataset has changed.
	 */
	protected void fireDatasetChanged() {
		notifyListeners(new DatasetChangeEvent(this, this));
	}

	/**
	 * Notifies all registered listeners that the dataset has changed.
	 * 
	 * @param event
	 *            contains information about the event that triggered the
	 *            notification.
	 */
	protected void notifyListeners(DatasetChangeEvent event) {

		Object[] listeners = this.listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == DatasetChangeListener.class) {
				((DatasetChangeListener) listeners[i + 1])
						.datasetChanged(event);
			}
		}
	}

	/**
	 * Registers an object to receive notification of changes to the dataset.
	 * 
	 * @param listener
	 *            the object to register.
	 */
	public void addChangeListener(DatasetChangeListener listener) {
		this.listenerList.add(DatasetChangeListener.class, listener);
	}

	public void removeChangeListener(DatasetChangeListener listener) {
		this.listenerList.remove(DatasetChangeListener.class, listener);
	}

	public boolean hasListener(EventListener listener) {
		List list = Arrays.asList(this.listenerList.getListenerList());
		return list.contains(listener);
	}

	public HashMap getOntologyHitMap() {
		return ontologyMap;
	}

}

/**
 *@author chavgx
 *Sep 6, 2008 
 *
 */
package edu.pitt.dbmi.odie.ui.views;

/**
 * @author Girish Chavan
 * 
 */
public interface IPrioritizedSorter {
	public void setTopPriority(int priority);

	public int getTopPriority();

	public int[] getPriorities();

	public void setTopPriorityDirection(int direction);

	public int getTopPriorityDirection();

	public void reverseTopPriority();

}

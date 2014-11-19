package model;

import java.util.ArrayList;
import java.util.Calendar;

//@author A0118007R
/**
 * This class is the StreamTask class - the task object acting as the heart of
 * the software.
 * 
 * This class contains all the attributes, constructor, getter, and setters for
 * the task object.
 * 
 * @version V0.5
 */
public class StreamTask {

	// Attributes
	private String taskName;
	private String taskDescription;
	private Calendar startTime;
	private Calendar deadline;
	private ArrayList<String> tags;
	private boolean isDone;
	private String rank;

	// Constructor
	public StreamTask(String taskName) {
		this.taskName = taskName;
		this.taskDescription = null;
		this.startTime = null;
		this.deadline = null;
		this.tags = new ArrayList<String>();
		this.isDone = false;
		this.rank = "low";
	}

	// Getters and Setters

	/**
	 * Gets the name of a task
	 * 
	 * @return this.taskName - the name of the task
	 */
	public String getTaskName() {
		return this.taskName;
	}

	/**
	 * Sets the name of a task
	 * 
	 * @param newTaskName - the new task's name
	 */
	public void setTaskName(String newTaskName) {
		this.taskName = newTaskName;
	}

	/**
	 * Gets the description of a task
	 * 
	 * @return this.taskDescription - the description of the task
	 */
	public String getDescription() {
		return this.taskDescription;
	}

	/**
	 * Sets the description of a task
	 * 
	 * @param description - the new description of the task
	 */
	public void setDescription(String description) {
		this.taskDescription = description;
	}

	//@author A0093874N
	/**
	 * Gets the start time of a task
	 * 
	 * @return this.startTime - the start time of the task
	 */
	public Calendar getStartTime() {
		return this.startTime;
	}

	/**
	 * Sets the start time of a task
	 * 
	 * @param startTime - the new start time of the task
	 */
	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}

	/**
	 * Checks whether a task is overdue
	 * 
	 * @return true if not overdue, false otherwise
	 */
	public boolean isOverdue() {
		if (deadline == null) {
			return false;
		} else {
			return deadline.before(Calendar.getInstance());
		}
	}

	/**
	 * Checks whether a task is inactive
	 * 
	 * @return true if inactive, false otherwise
	 */
	public boolean isInactive() {
		if (startTime == null) {
			return false;
		} else {
			return startTime.after(Calendar.getInstance());
		}
	}

	//@author A0119401U
	/**
	 * Gets the deadline of a task
	 * 
	 * @return this.deadline - the deadline of the task
	 */
	public Calendar getDeadline() {
		return this.deadline;
	}

	/**
	 * Sets the deadline of a task
	 * 
	 * @param deadline - the new deadline of the task
	 */
	public void setDeadline(Calendar deadline) {
		this.deadline = deadline;
	}

	/**
	 * Gets the tags of a task as an ArrayList of strings
	 * 
	 * @return tags - the tags of the task
	 */
	public ArrayList<String> getTags() {
		return tags;
	}

	/**
	 * Gets the rank of a task
	 * 
	 * @return this.rank - The rank of the task
	 */
	public String getRank() {
		return this.rank;
	}

	/**
	 * Sets the rank of a task
	 * 
	 * @param newRank - the new rank of the task
	 */
	public void setRank(String newRank) {
		this.rank = newRank;
	}

	/**
	 * Checks whether a given task is done
	 * 
	 * @return true if done, false otherwise
	 */
	public boolean isDone() {
		return this.isDone;
	}

	/**
	 * Marks a task as done
	 * 
	 */
	public void markAsDone() {
		setDone(true);
	}

	/**
	 * Marks a task as ongoing
	 * 
	 */
	public void markAsOngoing() {
		setDone(false);
	}

	//@author A0096529N
	/**
	 * Sets a task ask done or not
	 * 
	 * @param done
	 */
	public void setDone(boolean done) {
		this.isDone = done;
	}

	/**
	 * Checks whether a given task is a timed task
	 * 
	 * @return true if it is a timed task, false otherwise
	 */
	public boolean isTimedTask() {
		return startTime != null;
	}

	/**
	 * Checks whether a given task has a deadline
	 * 
	 * @return true if it has a deadline, false otherwise
	 */
	public boolean isDeadlineTask() {
		return deadline != null;
	}

	/**
	 * Checks whether a given task is a floating task (no start and end time)
	 * 
	 * @return true if it is floating, false otherwise
	 */
	public boolean isFloatingTask() {
		return startTime == null && deadline == null;
	}

	/**
	 * Checks whether a task has the specified tag
	 * 
	 * @param tag - the tag to be checked
	 * @return true if the task contains the specified tag, false otherwise
	 */
	public boolean hasTag(String tag) {
		return tags.contains(tag.toUpperCase());
	}

	/**
	 * Checks whether a task has a specified tag within an array of tags
	 * 
	 * @param tags - the tags to be checked
	 * @return true if the task contains a specified tag within an array of tags, false otherwise
	 */
	public boolean hasTag(String[] tags) {
		for (String tag : tags) {
			if (this.hasTag(tag)) {
				return true;
			}
		}
		return false;
	}

}
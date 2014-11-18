package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Stack;

import parser.StreamParser;
import util.StreamConstants;
import util.StreamUtil;
import model.StreamTask;

public class StackLogic extends BaseLogic {
	private Stack<String> inputStack;
	private Stack<StreamTask> dumpedTasks;
	private Stack<ArrayList<String>> orderingStack;

	private static final String CMD_DISMISS = "dismiss %1$s";
	private static final String CMD_RECOVER = "recover %1$s";
	private static final String CMD_DESC = "desc %1$s %2$s";
	private static final String CMD_DUE = "due %1$s %2$s";
	private static final String CMD_START = "start %1$s %2$s";
	private static final String CMD_RANK = "rank %1$s %2$s";
	private static final String CMD_MARK = "mark %1$s %2$s";
	private static final String CMD_TAG = "tag %1$s %2$s";
	private static final String CMD_UNTAG = "untag %1$s %2$s";
	private static final String CMD_NAME = "name %1$s %2$s";
	private static final String CMD_UNSORT = "unsort";

	private StackLogic() {
		inputStack = new Stack<String>();
		dumpedTasks = new Stack<StreamTask>();
		orderingStack = new Stack<ArrayList<String>>();
	}

	public static StackLogic init() {
		return new StackLogic();
	}

	//@author A0118007R
	/**
	 * Add the inverse command to undo stack
	 * 
	 * @param taskIndex
	 *            index of task that was modified
	 * @param currentDeadline
	 *            time to be reverted to
	 */
	public void pushInverseDueCommand(int taskIndex, Calendar currentDeadline) {
		String inverseCommand = null;
		if (currentDeadline == null) {
			inverseCommand = String.format(CMD_DUE, taskIndex, "null");
		} else {
			inverseCommand = String.format(CMD_DUE, taskIndex,
					StreamParser.tp.translate(currentDeadline));
		}
		pushInput(inverseCommand);
	}

	//@author A0119401U
	/**
	 * Add the inverse command to undo stack
	 * 
	 * @param taskIndex
	 *            index of task that was modified
	 * @param currentStartTime
	 *            time to be reverted to
	 */
	public void pushInverseStartCommand(int taskIndex, Calendar currentStartTime) {
		String inverseCommand = null;
		if (currentStartTime == null) {
			inverseCommand = String.format(CMD_START, taskIndex, "null");
		} else {
			inverseCommand = String.format(CMD_START, taskIndex,
					StreamParser.tp.translate(currentStartTime));
		}
		pushInput(inverseCommand);
	}

	//@author A0096529N
	/**
	 * Add the inverse command to undo stack
	 * 
	 * @param taskIndex
	 *            index of task that was added
	 */
	public void pushInverseAddCommand(int index) {
		pushInput(String.format(CMD_DISMISS, index));
	}

	//@author A0093874N
	/**
	 * Add the inverse command to undo stack
	 * 
	 * @param deletedTask
	 *            task that was deleted
	 * @param order
	 *            order of tasks to be reverted to
	 */
	public void pushInverseDeleteCommand(StreamTask deletedTask,
			ArrayList<String> order) {
		pushOrder(order);
		pushDumpedTask(deletedTask);
		pushInput(String.format(CMD_RECOVER, 1));
	}

	//@author A0096529N
	/**
	 * Add the inverse command to undo stack
	 * 
	 * @param originalOrder
	 *            order of tasks to be reverted to
	 * @param deletedTasks
	 *            tasks that were deleted
	 */
	public void pushInverseClearCommand(ArrayList<String> originalOrder,
			ArrayList<StreamTask> deletedTasks) {
		pushOrder(originalOrder);
		for (StreamTask task : deletedTasks) {
			pushDumpedTask(task);
		}
		pushInput(String.format(CMD_RECOVER, deletedTasks.size()));
	}

	//@author A0093874N
	/**
	 * Add the inverse command to undo stack
	 * 
	 * @param taskIndex
	 *            index of task that was modified
	 * @param oldRank
	 *            rank to be reverted to
	 */
	public void pushInverseSetRankingCommand(int index, String oldRank) {
		pushInput(String.format(CMD_RANK, index, oldRank));
	}

	//@author A0093874N
	/**
	 * Add the inverse command to undo stack
	 * 
	 * @param taskIndex
	 *            index of task that was modified
	 * @param oldDescription
	 *            description to be reverted to
	 */
	public void pushInverseSetDescriptionCommand(int index,
			String oldDescription) {
		pushInput(String.format(CMD_DESC, index, oldDescription));
	}

	//@author A0118007R
	/**
	 * Add the inverse command to undo stack
	 * 
	 * @param wasDone
	 *            boolean state to be reverted to
	 * @param taskIndex
	 *            index of task that was modified
	 */
	public void pushInverseSetDoneCommand(boolean wasDone, int index) {
		String inverseCommand = String.format(CMD_MARK, index,
				StreamParser.mp.translate(StreamParser.mp.parse(wasDone)));
		pushInput(inverseCommand);
	}

	//@author A0096529N
	/**
	 * Add the inverse command to undo stack
	 * 
	 * @param taskIndex
	 *            index of task that was modified
	 * @param oldTaskName
	 */
	public void pushInverseSetNameCommand(int taskIndex, String oldTaskName) {
		pushInput(String.format(CMD_NAME, taskIndex, oldTaskName));
	}

	//@author A0096529N
	/**
	 * Add the inverse command to undo stack
	 * 
	 * @param inverseCommand
	 *            entire command for reversion of action
	 */
	public void pushInverseModifyCommand(String inverseCommand) {
		pushInput(inverseCommand.trim());
	}

	//@author A0096529N
	/**
	 * Add the inverse command to undo stack
	 * 
	 * @param oldOrdering
	 *            order of tasks to be reverted to
	 */
	public void pushInverseSortCommand(ArrayList<String> oldOrdering) {
		pushOrder(oldOrdering);
		pushInput(CMD_UNSORT);
	}

	//@author A0118007R
	/**
	 * Add the inverse command to undo stack
	 * 
	 * @param taskIndex
	 *            index of task that was modified
	 * @param tagsRemoved
	 *            tags removed during modification to be added back on reversion
	 */
	public void pushInverseUntagCommand(int taskIndex,
			ArrayList<String> tagsRemoved) {
		if (tagsRemoved.size() > 0) {
			pushInput(String.format(CMD_TAG, taskIndex,
					StreamUtil.listDownArrayContent(tagsRemoved, " ")));
		}
	}

	//@author A0118007R
	/**
	 * Add the inverse command to undo stack
	 * 
	 * @param taskIndex
	 *            index of task that was modified
	 * @param tagsAdded
	 *            tags added during modification to be removed on reversion
	 */
	public void pushInverseAddTagCommand(int taskIndex,
			ArrayList<String> tagsAdded) {
		if (tagsAdded.size() > 0) {
			pushInput(String.format(CMD_UNTAG, taskIndex,
					StreamUtil.listDownArrayContent(tagsAdded, " ")));
		}
	}

	//@author A0118007R
	/**
	 * Prepares the inverse modify command to be used as undo command for the
	 * multi-modify command
	 * 
	 * @param taskName
	 * @param taskIndex
	 * @param currTask
	 * @return inverseCommand - the inverse command for multi-modify input
	 */
	public String prepareInverseModifyCommand(String taskName, int taskIndex,
			StreamTask currTask) {
		String inverseCommand = "modify " + taskIndex + " -name " + taskName
				+ " ";
		// added by A0093874N
		Boolean isDone = currTask.isDone();
		inverseCommand = buildInverseModifyRank(currTask, inverseCommand,
				isDone);
		// end of addition by A0093874N //
		inverseCommand = buildInverseModifyDescription(currTask, inverseCommand);
		inverseCommand = buildInverseModifyDeadline(currTask, inverseCommand);
		inverseCommand = buildInverseModifyTag(currTask, inverseCommand);
		return inverseCommand;
	}

	//@author A0093874N
	private String buildInverseModifyRank(StreamTask currTask,
			String inverseCommand, Boolean isDone) {
		inverseCommand = determineStatus(inverseCommand, isDone);
		String oldRank = currTask.getRank();
		inverseCommand += "-rank " + oldRank + " ";
		return inverseCommand;
	}

	//@author A0093874N
	private String determineStatus(String inverseCommand, Boolean isDone) {
		if (isDone) {
			inverseCommand += "-mark done ";
		} else {
			inverseCommand += "-mark ongoing ";
		}
		return inverseCommand;
	}

	//@author A0096529N
	private String buildInverseModifyTag(StreamTask currTask,
			String inverseCommand) {
		inverseCommand += "-settags ";
		for (String tag : currTask.getTags()) {
			inverseCommand += tag + " ";
		}
		return inverseCommand;
	}

	//@author A0118007R
	private String buildInverseModifyDeadline(StreamTask currTask,
			String inverseCommand) {
		Calendar oldDue = currTask.getDeadline();
		if (oldDue != null) {
			String dueString = StreamParser.tp.translate(oldDue);
			inverseCommand = inverseCommand + "-due " + dueString + " ";
		} else {
			inverseCommand = inverseCommand + "-due null ";
		}
		return inverseCommand;
	}

	//@author A0118007R
	private String buildInverseModifyDescription(StreamTask currTask,
			String inverseCommand) {
		String oldDesc = currTask.getDescription();
		if (oldDesc != null) {
			inverseCommand = inverseCommand + "-desc " + oldDesc + " ";
		} else {
			inverseCommand = inverseCommand + "-desc null ";
		}
		return inverseCommand;
	}

	//@author A0096529N
	private void pushDumpedTask(StreamTask deletedTask) {
		assert (deletedTask != null) : StreamConstants.Assertion.NULL_INVERSE_TASK;
		dumpedTasks.push(deletedTask);
		logDebug(String.format(StreamConstants.LogMessage.PUSH_INVERSE_TASK,
				deletedTask.getTaskName()));
	}

	//@author A0096529N
	public StreamTask recoverTask() {
		StreamTask dumpedTask = dumpedTasks.pop();
		logDebug(String.format(StreamConstants.LogMessage.POP_INVERSE_TASK,
				dumpedTask.getTaskName()));
		return dumpedTask;
	}

	//@author A0096529N
	private void pushInput(String inverseCommand) {
		assert (inverseCommand != null && !inverseCommand.isEmpty()) : StreamConstants.Assertion.EMPTY_INVERSE_COMMAND;
		inputStack.push(inverseCommand);
		logDebug(String.format(StreamConstants.LogMessage.PUSH_INVERSE_COMMAND,
				inverseCommand));
	}

	//@author A0096529N
	/**
	 * Pops the inverse command for undoing purposes.
	 * 
	 * @return inverseCommand - the string consisting of the inverse command.
	 */
	public String popInverseCommand() {
		String inverseCommand = inputStack.pop();
		logDebug(String.format(StreamConstants.LogMessage.POP_INVERSE_COMMAND,
				inverseCommand));
		return inverseCommand;
	}

	//@author A0096529N
	/**
	 * Checks whether there exists an inverse input inside the input stack. If
	 * it is empty then there is nothing to undo.
	 * 
	 * @return isEmpty - true if the stack is empty.
	 */
	public boolean hasInverseInput() {
		return !inputStack.isEmpty();
	}

	//@author A0096529N
	/**
	 * pushes the place holder for undo
	 * 
	 */
	public void pushPlaceholderInput() {
		pushInput("placeholderforundo");
	}

	//@author A0096529N
	private void pushOrder(ArrayList<String> order) {
		assert (order != null && !order.isEmpty()) : StreamConstants.Assertion.EMPTY_INVERSE_ORDER;
		orderingStack.push(order);
		logDebug(String.format(StreamConstants.LogMessage.PUSH_ORDER,
				Arrays.toString(order.toArray())));
	}

	//@author A0096529N
	/**
	 * Pops the order on the top of the ordering stack
	 * 
	 * @return taskList List of taskNames in the order that was pushed
	 *         previously
	 */
	public ArrayList<String> popOrder() {
		ArrayList<String> order = orderingStack.pop();
		logDebug(String.format(StreamConstants.LogMessage.POP_ORDER,
				Arrays.toString(order.toArray())));
		return order;
	}

	//@author A0096529N
	/**
	 * @deprecated use StreamUtil's getCalendarWriteUp
	 */
	@SuppressWarnings("unused")
	private String getInputDate(Calendar currentDeadline) {
		return currentDeadline.get(Calendar.MONTH + 1)
				+ "/"
				+ (currentDeadline.get(Calendar.DATE) + "/" + currentDeadline
						.get(Calendar.YEAR));
	}

	//@author generated
	@Override
	protected String getLoggerComponentName() {
		return StreamConstants.ComponentTag.STREAMSTACK;
	}

	// Depreciated methods

	//@author A0118007R
	/**
	 * 
	 * @param inverseCommand
	 * @param oldTags
	 * @param newTags
	 * @return
	 * @deprecated replaced with new methodology use settags to clear tags then
	 *             add previous tag state
	 */
	public String processTagModification(String inverseCommand,
			ArrayList<String> oldTags, ArrayList<String> newTags) {
		String inverseTag = compareTagged(oldTags, newTags);
		String inverseUntag = compareUntagged(oldTags, newTags);
		if (inverseTag != "tag ") {
			inverseCommand += inverseTag;
		}
		if (inverseUntag != "untag ") {
			inverseCommand += inverseUntag;
		}
		return inverseCommand;
	}

	//@author A0093874N
	/**
	 * 
	 * @param oldTags
	 * @param newTags
	 * @return
	 * @deprecated replaced with new methodology use settags to clear tags then
	 *             add previous tag state
	 */
	private String compareTagged(ArrayList<String> oldTags,
			ArrayList<String> newTags) {
		String inverseTag = "tag ";
		inverseTag = buildInverseTag(oldTags, newTags, inverseTag);
		return inverseTag;
	}

	//@author A0118007R
	/**
	 * 
	 * @param oldTags
	 * @param newTags
	 * @param inverseTag
	 * @return
	 * @deprecated replaced with new methodology use settags to clear tags then
	 *             add previous tag state
	 */
	private String buildInverseTag(ArrayList<String> oldTags,
			ArrayList<String> newTags, String inverseTag) {
		for (String old : oldTags) {
			if (!newTags.contains(old)) {
				inverseTag = inverseTag + old + " ";
			}
		}
		return inverseTag;
	}

	//@author A0093874N
	/**
	 * 
	 * @param oldTags
	 * @param newTags
	 * @return
	 * @deprecated replaced with new methodology use settags to clear tags then
	 *             add previous tag state
	 */
	private String compareUntagged(ArrayList<String> oldTags,
			ArrayList<String> newTags) {
		String inverseUntag = "untag ";
		inverseUntag = buildInverseUntag(oldTags, newTags, inverseUntag);
		return inverseUntag;
	}

	//@author A0118007R
	/**
	 * 
	 * @param oldTags
	 * @param newTags
	 * @param inverseUntag
	 * @return
	 * @deprecated replaced with new methodology use settags to clear tags then
	 *             add previous tag state
	 */
	private String buildInverseUntag(ArrayList<String> oldTags,
			ArrayList<String> newTags, String inverseUntag) {
		for (String newer : newTags) {
			if (!oldTags.contains(newer)) {
				inverseUntag += inverseUntag + newer + " ";
			}
		}
		return inverseUntag;
	}

}

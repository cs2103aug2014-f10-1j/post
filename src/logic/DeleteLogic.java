package logic;

import java.util.Stack;

import util.StreamConstants;
import logger.Loggable;
import model.StreamTask;

public class DeleteLogic extends Loggable implements StackLogic {

	private Stack<StreamTask> deletedTasks;
	
	//@author A0096529N

	private DeleteLogic() {
		deletedTasks = new Stack<StreamTask>();
	}
	
	public static DeleteLogic init() {
		return new DeleteLogic();
	}
	
	@Override
	public void push(Object obj) {
		StreamTask deletedTask = (StreamTask) obj;
		assert (deletedTask != null) : StreamConstants.Assertion.NULL_INVERSE_TASK;
		deletedTasks.push(deletedTask);
		logDebug(String.format(StreamConstants.LogMessage.PUSH_INVERSE_TASK,
				deletedTask.getTaskName()));
	}

	@Override
	public StreamTask pop() {
		StreamTask deletedTask = deletedTasks.pop();
		logDebug(String.format(StreamConstants.LogMessage.POP_INVERSE_TASK,
				deletedTask.getTaskName()));
		return deletedTask;
	}

	@Override
	public String getComponentName() {
		return "DELETELOGIC";
	}
	
}

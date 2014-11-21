package logic;

//@author A0093874N
/**
 * A basic stack interface implemented to support undo processes.
 */
public interface StackLogic {

	public void push(Object obj);
	
	public Object pop();
	
}

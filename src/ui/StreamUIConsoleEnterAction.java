package ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import stream.Stream;

//@author A0093874N

/**
 * <p>
 * The action invoked upon pressing "enter" in console. It fires the text in
 * console to the input parser and subsequently processor.
 * </p>
 * <p>
 * Credits to developers from F10-4J for this idea.
 * </p>
 * 
 * @version V0.5
 */
public class StreamUIConsoleEnterAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private Stream stream;
	private StreamUIConsole console;

	@Override
	public void actionPerformed(ActionEvent e) {
		String input = console.getText();
		stream.filterAndProcessInput(input);
		console.setText("");
	}

	StreamUIConsoleEnterAction(Stream st, StreamUIConsole cons) {
		this.stream = st;
		this.console = cons;
	}

}
package ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import stream.Stream;

//@author A0093874N
/**
 * <p>
 * Fires the text input to the parser and processor upon pressing enter.
 * </p>
 * <p>
 * Credits to developers from F10-4J for this idea.
 * </p>
 */
public class ConsoleEnterAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private Stream stream;
	private ConsoleUI console;

	@Override
	public void actionPerformed(ActionEvent e) {
		String input = console.getText();
		stream.filterAndProcessInput(input);
		console.setText("");
	}

	ConsoleEnterAction(Stream st, ConsoleUI cons) {
		this.stream = st;
		this.console = cons;
	}

}
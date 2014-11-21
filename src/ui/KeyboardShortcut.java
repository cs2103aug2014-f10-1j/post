package ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

//@author A0093874N
/**
 * Appends actions to several chosen keyboard buttons pressed from outside the
 * <b>ConsoleUI</b>.
 */
public class KeyboardShortcut extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private ConsoleUI console;
	private String text;

	@Override
	public void actionPerformed(ActionEvent e) {
		console.setText(text);
		console.requestFocus();
	}

	KeyboardShortcut(ConsoleUI cons, String str) {
		this.console = cons;
		this.text = str;
	}

}
package ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

//@author A0093874N

/**
 * The action invoked upon pressing the keyboard shortcut, i.e matching the key
 * pressed to the corresponding command.
 * 
 * @version V0.5
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
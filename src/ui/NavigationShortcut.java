package ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import stream.Stream;

//@author A0093874N
/**
 * Appends actions to navigation buttons (left, right, up, down) pressed from
 * outside the <b>ConsoleUI</b>.
 */
public class NavigationShortcut extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private String command;
	private Stream stream;

	@Override
	public void actionPerformed(ActionEvent e) {
		stream.filterAndProcessInput(command);
	}

	NavigationShortcut(Stream str, String cmd) {
		this.stream = str;
		this.command = cmd;
	}

}

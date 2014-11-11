package ui;

import javax.swing.JTextField;

import util.StreamConstants;

//@author A0093874N

/**
 * <p>
 * A simple, non-editable <b>JTextField</b> component to assist users in
 * entering their commands.
 * </p>
 * 
 * @version V0.5
 */
public class StreamUIFeedback extends JTextField {

	private static final long serialVersionUID = 1L;

	StreamUIFeedback() {
		super();
		setBackground(StreamConstants.UI.COLOR_FEEDBACK);
		setForeground(StreamConstants.UI.COLOR_HELP_MSG);
		setMargin(StreamConstants.UI.MARGIN_CONSOLE);
		setEditable(false);
	}

}
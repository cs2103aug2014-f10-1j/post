package ui;

import javax.swing.JTextArea;

import util.StreamConstants;
import util.StreamUtil;

//@author A0093874N

/**
 * <p>
 * A simple, non-editable <b>JTextArea</b> component to display log messages.
 * Error messages and normal log messages are displayed differently.
 * </p>
 * 
 * <h3>API</h3>
 * <ul>
 * <li>StreamUILogger.showLogMessage(String logMsg)</li>
 * <li>StreamUILogger.showErrorMessage(String errMsg)</li>
 * </ul>
 * <p>
 * Refer to method documentation for details.
 * </p>
 * 
 * @version V0.5
 */
public class LoggerUI extends JTextArea {

	private static final long serialVersionUID = 1L;

	LoggerUI() {
		super();
		setBackground(StreamConstants.UI.COLOR_LOGGER);
		setMargin(StreamConstants.UI.MARGIN_LOGGER);
		setEditable(false);
	}

	/**
	 * Displays normal log messages.
	 * 
	 * @param logMsg
	 *            - the message to be logged
	 */
	void showLogMessage(String logMsg) {
		setForeground(StreamConstants.UI.COLOR_LOG_MSG);
		setText(StreamUtil.showAsTerminalResponse(logMsg));
		setCaretPosition(0);
	}

	/**
	 * Displays error messages.
	 * 
	 * @param errMsg
	 *            - the error message to be logged
	 */
	void showErrorMessage(String errMsg) {
		setForeground(StreamConstants.UI.COLOR_ERR_MSG);
		setText(StreamUtil.showAsTerminalResponse(errMsg));
		setCaretPosition(0);
	}

}

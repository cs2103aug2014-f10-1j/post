package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import util.StreamConstants;

//@author A0093874N

/**
 * <p>
 * A simple <b>JTextField</b> functioning as console for user input. Equipped
 * with placeholder text so that user knows where to go. Also equipped with
 * auto-complete and built-in user guide for user's convenience.
 * </p>
 * <p>
 * The auto-complete code is adapted from
 * http://java2s.com/Code/Java/Swing-Components/AutoCompleteTextField.htm.
 * Credits to the author, Brandon Buck.
 * </p>
 * 
 * <h3>API</h3>
 * <ul>
 * <li>StreamUIConsole.addPossibility(String possibility, String helpText)</li>
 * </ul>
 * <p>
 * Refer to method documentation for details.
 * </p>
 * 
 * @version V0.5
 */
public class ConsoleUI extends JTextField implements KeyListener,
		DocumentListener {

	private static final long serialVersionUID = 1L;
	private ArrayList<String> possibilities;
	private int currentGuess;
	private boolean isGuessing;
	private boolean isFound;
	private HashMap<String, String> helpTextMap;
	private FeedbackUI feedback;

	private static final String DEFAULT_HELP_TEXT = "Press space to fill";
	private static final String UNKNOWN_COMMAND = "Warning: unknown command";
	private static final String COMMAND_PROMPT = "Enter your command here";
	private static final String HELP_TEXT_EMPTY = StreamConstants.Message.WELCOME
			+ " Type \"help\" and press enter to get some assistance!";

	private static final int GUESS_NOT_FOUND = -1;
	private static final int COORD_X = 5;
	private static final int COORD_Y = 20;

	ConsoleUI(FeedbackUI fdb) {
		super();
		setMargin(StreamConstants.UI.MARGIN_CONSOLE);
		this.possibilities = new ArrayList<String>();
		this.currentGuess = GUESS_NOT_FOUND;
		this.isGuessing = false;
		this.addKeyListener(this);
		this.getDocument().addDocumentListener(this);
		this.helpTextMap = new HashMap<String, String>();
		this.feedback = fdb;
	}

	/**
	 * Adds a new possibility to the list of possibilities for the auto-complete
	 * processor to process.
	 * 
	 * @param possibility
	 *            - the new possibility to add
	 * @param helpText
	 *            - the accompanying help text
	 */
	public void addPossibility(String possibility, String helpText) {
		this.possibilities.add(possibility);
		this.helpTextMap.put(possibility, helpText);
	}

	/**
	 * Returns the string at the location of the current guess in the list of
	 * possibilities.
	 * 
	 * @return <b>String</b> - the current guess
	 */
	private String getCurrentGuess() {
		if (this.currentGuess != GUESS_NOT_FOUND) {
			isFound = true;
			return this.possibilities.get(this.currentGuess);
		}
		isFound = false;
		return this.getText();
	}

	/**
	 * Sets the current guess based on the text entered in the console.
	 */
	private void findCurrentGuess() {
		String entered = this.getText().toLowerCase();

		for (int i = 0; i < this.possibilities.size(); i++) {
			currentGuess = GUESS_NOT_FOUND;

			String possibility = this.possibilities.get(i);
			possibility = possibility.toLowerCase();
			if (possibility.startsWith(entered)) {
				this.currentGuess = i;
				break;
			}
		}
	}

	@Override
	public void setText(String text) {
		super.setText(text);
		this.isGuessing = false;
		this.currentGuess = GUESS_NOT_FOUND;
	}

	@Override
	public void paintComponent(Graphics g) {
		String guess = this.getCurrentGuess();
		String drawGuess = guess;

		super.paintComponent(g);
		String entered = this.getText();
		Rectangle2D enteredBounds = g.getFontMetrics().getStringBounds(entered,
				g);

		entered = entered.toLowerCase();
		guess = guess.toLowerCase();

		if (!(guess.startsWith(entered))) {
			this.isGuessing = false;
		}

		if (!(entered.trim().equals(""))) {
			String subGuess = drawGuess.substring(entered.length(),
					drawGuess.length());
			g.setColor(Color.GRAY);
			g.drawString(subGuess, (int) (enteredBounds.getWidth()) + COORD_X
					+ 1, COORD_Y);
			String[] typed = entered.trim().split(" ");
			if (helpTextMap.containsKey(typed[0])) {
				feedback.setText(helpTextMap.get(typed[0]));
			} else if (isFound) {
				feedback.setText(DEFAULT_HELP_TEXT);
			} else {
				feedback.setText(UNKNOWN_COMMAND);
			}
		} else {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setFont(StreamConstants.UI.FONT_CONSOLE);
			g2.setColor(Color.GRAY);
			g2.drawString(COMMAND_PROMPT, COORD_X, COORD_Y);
			g2.dispose();
			feedback.setText(HELP_TEXT_EMPTY);
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			if (this.isGuessing) {
				this.setText(this.getCurrentGuess());
				this.isGuessing = false;
				e.consume();
			}
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void insertUpdate(DocumentEvent e) {
		String temp = this.getText();

		if (temp.length() == 1) {
			this.isGuessing = true;
		}
		if (this.isGuessing) {
			this.findCurrentGuess();
		}
	}

	public void removeUpdate(DocumentEvent e) {
		String temp = this.getText();

		if (!(this.isGuessing)) {
			this.isGuessing = true;
		}
		if (temp.length() == 0) {
			this.isGuessing = false;
		} else if (this.isGuessing) {
			this.findCurrentGuess();
		}
	}

	public void changedUpdate(DocumentEvent e) {
	}

}
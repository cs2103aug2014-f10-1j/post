package ui;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import logger.Loggable;
import model.StreamTask;
import stream.Stream;
import util.StreamConstants;
import util.StreamExternals;

//@author A0093874N

/**
 * <p>
 * StreamUI is the GUI for STREAM, featuring graphical view of user's added
 * tasks, console for user input, logger for terminal feedback, and helper box
 * for user assistance. Also equipped with some keyboard shortcuts and simple
 * auto-completion for user's convenience.
 * </p>
 * 
 * <h3>API</h3>
 * <ul>
 * <li>StreamUI.resetAvailableTasks(ArrayList&lt;Integer&gt; indices,
 * ArrayList&lt;StreamTask&gt; tasks, Boolean isReset, Boolean isSearching)</li>
 * <li>StreamUI.log(String logMsg, Boolean isErrorMsg)</li>
 * <li>StreamUI.displayDetails(StreamTask task)</li>
 * <li>StreamUI.getNumberOfTasksStored()</li>
 * <li>StreamUI.goToFirstPage()</li>
 * <li>StreamUI.goToPrevPage()</li>
 * <li>StreamUI.goToNextPage()</li>
 * <li>StreamUI.goToLastPage()</li>
 * </ul>
 * <p>
 * Refer to method documentation for details.
 * </p>
 * 
 * @version V0.5
 */
public class StreamUI extends Loggable {

	private Stream stream;

	private JFrame mainFrame;
	private JPanel contentPanel;
	private ConsoleUI console;
	private FeedbackUI feedback;
	private LoggerUI logger;
	private JLabel pageNumber;

	private boolean isSearch;
	private boolean isTaskHighlighted;
	private int pageShown;
	private int totalPage;
	private TaskViewUI[] shownTasks;
	private ArrayList<StreamTask> availTasks;
	private ArrayList<Integer> availIndices;
	private StreamTask activeTask;

	private static final String LOG_PAGE_MOVED = "Task viewer moved to page %1$s/%2$s";
	private static final String LOG_REFRESH = "Task viewer refreshed with %1$s new tasks";
	private static final String LOG_DETAILS = "Displaying details for %1$s";

	private static final String TEXT_TITLE = "STREAM " + Stream.VERSION
			+ ": Simple Task Reader and Manager";
	private static final String TEXT_FOOTER = "Copyright \u00a9 2014 CS2103AUG2014-F10-01J."
			+ " All rights reserved.";
	static final String TEXT_WELCOME = "Welcome to STREAM " + Stream.VERSION
			+ "!";

	private static final String TITLE_HELP = "Help for STREAM";
	private static final String TEXT_HELP = "<html><body width='400'><h2>"
			+ TEXT_WELCOME
			+ "</h2><p>Here are some keywords that you can use:</p>"
			+ "<p>add, delete, name, rank, start, due, tag, mark, modify,"
			+ "view, search, sort, filter, clrsrc, page, undo, exit</p><p>"
			+ "Our smart helper will tell you what each command does and assist you "
			+ "in syntax suggestion.</p><p>Visit our page at https://github.com/cs2103aug2014-f10-1j/main "
			+ "for more comprehensive user guide!";

	public StreamUI(Stream str) {

		initParams(str);
		setupLookAndFeel();
		addMainFrame();
		addContentPanel();
		addHeader();
		setUpView();
		addFeedbackBox();
		addConsole();
		addAutocomplete();
		empowerConsole(new ConsoleEnterAction(stream, console));
		addLogger();
		addKeyboardShortcuts();
		addNavigShortcuts();
		addPageNumber();
		addFooter();
		setFocusTraversal();
		log(TEXT_WELCOME, false);
		presentToUser();
	}

	private void initParams(Stream str) {
		stream = str;
		isSearch = false;
		pageShown = 1;
		totalPage = 1;
		availTasks = new ArrayList<StreamTask>();
		availIndices = new ArrayList<Integer>();
	}

	//@author A0096529N
	/**
	 * Sets the active task for highlighting
	 * 
	 * @param task
	 *            the task to be highlighted on next UI update
	 * 
	 */
	public void setActiveTask(StreamTask task) {
		activeTask = task;
		isTaskHighlighted = false;
	}

	/**
	 * Highlights the task view containing the active task
	 */
	private void highlightActiveTaskView() {
		int index = availTasks.indexOf(activeTask);
		assert (index >= 0) : StreamConstants.Assertion.TASK_TAG_NOTFOUND;
		int page = index / StreamConstants.UI.MAX_VIEWABLE_TASK + 1;
		goToPage(page);
		fadeBorder(shownTasks[index % StreamConstants.UI.MAX_VIEWABLE_TASK]);
		isTaskHighlighted = true;
	}

	/**
	 * Fades the border by setting the alpha value.
	 * <p>
	 * Starts a daemon background thread to alter border
	 * </p>
	 * 
	 * @param taskView
	 *            to execute the fade effect on
	 */
	private void fadeBorder(final TaskViewUI taskView) {
		new Thread() {
			@Override
			public void run() {
				try {
					for (int i = 255; i > 0; i -= 10) {
						taskView.setBorder(BorderFactory
								.createLineBorder(new Color(48, 111, 163, i))); // #2d6ea3
						Thread.sleep(10);
					}
					for (int i = 0; i < 255; i += 10) {
						taskView.setBorder(BorderFactory
								.createLineBorder(new Color(48, 111, 163, i)));
						Thread.sleep(10);
					}
					for (int i = 255; i > 0; i -= 2) {
						taskView.setBorder(BorderFactory
								.createLineBorder(new Color(48, 111, 163, i)));
						Thread.sleep(10);
					}
					taskView.setBorder(null);
				} catch (Exception e) {
					logError(String.format(
											StreamConstants.ExceptionMessage.ERR_UI_FADE_THREAD,
											e.getClass().getSimpleName(),
											e.getMessage()));
				}
			}
		}.start();
	}

	/**
	 * Sets up the UI according to system theme i.e. MacOS, Windows, Ubuntu,
	 * etc.
	 */
	private void setupLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			logError(StreamConstants.LogMessage.UI_LOOKANDFEEL_FAIL);
		}
	}

	//@author A0093874N

	/**
	 * Constructs the autocomplete helper texts.
	 */
	private void addAutocomplete() {
		HashMap<String, String> helpTexts = new HashMap<String, String>();
		helpTexts.put("add",
				"add (task name) (properties): Adds a new task here");
		helpTexts
				.put("due",
						"due (index) (time): Sets the deadline for a task based on index number");
		helpTexts
				.put("end",
						"end (index) (time): Sets the end time for a task based on index number");
		helpTexts
				.put("start",
						"start (index) (time): Sets the start time for a task based on index number");
		helpTexts
				.put("search",
						"search (keyphrase): Searches tasks by its name, description, or tags");
		helpTexts
				.put("delete", "delete (index): Deletes based on index number");
		helpTexts.put("del", "del (index): Deletes based on index number");
		helpTexts
				.put("desc",
						"desc (index) (description): Sets a description to a task based on index number");
		helpTexts
				.put("describe",
						"describe (index) (description): Sets a description to a task based on index number");
		helpTexts.put("filter",
				"filter (criteria): Filters tasks by dates or ranks");
		helpTexts.put("mark",
				"mark (index) (mark type): Marks task as done or ongoing");
		helpTexts
				.put("modify",
						"modify (index) (properties): Modifies multiple parameters of a task in one go");
		helpTexts.put("view", "view (index): Views the details of a task");
		helpTexts
				.put("tag",
						"tag (index) (tag1) ... (tagN): Adds tags to a task based on index number");
		helpTexts
				.put("name",
						"name (index) (new name): Changes a task's name based on index number");
		helpTexts
				.put("untag",
						"tag (index) (tag1) ... (tagN): Removes tags of a task based on index number");
		helpTexts
				.put("sort",
						"sort (criteria): Sorts tasks by alphabetical or chronological order");
		helpTexts.put("clear", "Clears all added tasks");
		helpTexts
				.put("clrsrc", "CLeaR SeaRCh - Clears search or filter result");
		helpTexts
				.put("rank",
						"rank (index) (rank type): Changes the rank of a task based on index number");
		helpTexts.put("first ", "Go to the first page");
		helpTexts.put("last", "Go to the last page");
		helpTexts.put("next", "Go to the next page");
		helpTexts.put("prev", "Go to the previous page");
		helpTexts.put("page", "page (page): Go to a specific page");
		helpTexts.put("undo", "Undoes the last action");
		helpTexts.put("help", "Opens the help dialog box");
		helpTexts.put("exit", "Exits the program");
		for (String h : helpTexts.keySet()) {
			console.addPossibility(h, helpTexts.get(h));
		}
	}

	/**
	 * Adds the keyboard shortcuts.
	 */
	private void addKeyboardShortcuts() {
		HashMap<Character, String> shortcut = new HashMap<Character, String>();
		shortcut.put('a', "add ");
		shortcut.put('s', "search ");
		shortcut.put('d', "delete ");
		shortcut.put('f', "filter ");
		shortcut.put('m', "mark ");
		shortcut.put('y', "modify ");
		shortcut.put('v', "view ");
		shortcut.put('t', "sort ");
		shortcut.put('u', "undo");
		shortcut.put('p', "page ");
		shortcut.put('h', "help");
		shortcut.put('e', "exit");
		shortcut.put('c', ""); // placeholder shortcut to reset
		for (Character c : shortcut.keySet()) {
			empowerKeyboardShortcuts(c, shortcut.get(c));
		}
	}

	/**
	 * Adds the navigation shortcuts.
	 */
	private void addNavigShortcuts() {
		HashMap<String, String> navig = new HashMap<String, String>();
		navig.put("DOWN", "first");
		navig.put("LEFT", "prev");
		navig.put("RIGHT", "next");
		navig.put("UP", "last");
		for (String s : navig.keySet()) {
			empowerNavigationShortcuts(s, navig.get(s));
		}
	}

	/**
	 * Sets the customized tab-based focus traversal policy.
	 */
	private void setFocusTraversal() {
		Vector<Component> order = new Vector<Component>(2);
		order.add(console);
		order.add(logger);
		mainFrame.setFocusTraversalPolicy(new FocusTraversal(order));
	}

	/**
	 * Constructs the main frame for Stream's User Interface.
	 */
	private void addMainFrame() {
		mainFrame = new JFrame(TEXT_TITLE);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(StreamConstants.UI.WIDTH_MAINFRAME,
				StreamConstants.UI.HEIGHT_MAINFRAME);
		mainFrame.setResizable(false);
		mainFrame.setLocationRelativeTo(null);
	}

	/**
	 * Constructs the content panel for Stream's User Interface.
	 */
	private void addContentPanel() {
		contentPanel = new JPanel();
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setLayout(null);
		mainFrame.setContentPane(contentPanel);
	}

	/**
	 * Constructs the task view panel.
	 */
	private void setUpView() {
		shownTasks = new TaskViewUI[StreamConstants.UI.MAX_VIEWABLE_TASK];
		for (int i = 0; i < StreamConstants.UI.MAX_VIEWABLE_TASK; i++) {
			TaskViewUI taskPanel = new TaskViewUI();
			taskPanel
					.setBounds(
							StreamConstants.UI.MARGIN_SIDE,
							StreamConstants.UI.MARGIN_COMPONENT
									* 2
									+ StreamConstants.UI.HEIGHT_HEADER
									+ i
									* (StreamConstants.UI.HEIGHT_TASKPANEL + StreamConstants.UI.MARGIN_COMPONENT),
							StreamConstants.UI.COMPONENT_WIDTH,
							StreamConstants.UI.HEIGHT_TASKPANEL);
			contentPanel.add(taskPanel);
			shownTasks[i] = taskPanel;
			taskPanel.hideView();
		}
	}

	/**
	 * Constructs the header portion.
	 */
	private void addHeader() {
		JLabel title = new JLabel();
		title.setIcon(StreamExternals.HEADER);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setBounds(StreamConstants.UI.BOUNDS_HEADER);
		contentPanel.add(title);
	}

	/**
	 * Constructs the console for user input.
	 */
	private void addConsole() {
		console = new ConsoleUI(feedback);
		console.setFont(StreamConstants.UI.FONT_CONSOLE);
		console.setBounds(StreamConstants.UI.BOUNDS_CONSOLE);
		contentPanel.add(console);
	}

	private void addFeedbackBox() {
		feedback = new FeedbackUI();
		feedback.setFont(StreamConstants.UI.FONT_CONSOLE);
		feedback.setBounds(StreamConstants.UI.BOUNDS_FEEDBACK);
		contentPanel.add(feedback);
	}

	/**
	 * Constructs the logger panel to display terminal response.
	 */
	private void addLogger() {
		logger = new LoggerUI();
		logger.setFont(StreamConstants.UI.FONT_LOGGER);
		logger.setBounds(StreamConstants.UI.BOUNDS_LOGGER);
		contentPanel.add(logger);
	}

	/**
	 * Constructs the page number portion.
	 */
	private void addPageNumber() {
		pageNumber = new JLabel();
		pageNumber.setFont(StreamConstants.UI.FONT_PAGE_NUM);
		pageNumber.setHorizontalAlignment(SwingConstants.LEFT);
		pageNumber.setBounds(StreamConstants.UI.BOUNDS_PAGE_NUM);
		contentPanel.add(pageNumber);
	}

	/**
	 * Constructs the footer portion.
	 */
	private void addFooter() {
		JLabel footer = new JLabel(TEXT_FOOTER);
		footer.setFont(StreamConstants.UI.FONT_FOOTER);
		footer.setHorizontalAlignment(SwingConstants.RIGHT);
		footer.setBounds(StreamConstants.UI.BOUNDS_FOOTER);
		contentPanel.add(footer);
	}

	private void presentToUser() {
		mainFrame.setVisible(true);
	}

	/**
	 * Equips the console with the action invoked upon pressing enter.
	 * 
	 * @param action
	 *            - the enter action
	 */
	private void empowerConsole(Action action) {
		console.getInputMap().put(KeyStroke.getKeyStroke("ENTER"),
				"processInput");
		console.getActionMap().put("processInput", action);
	}

	/**
	 * Equips keyboard shortcut to elements outside the command line.
	 * 
	 * @param key
	 *            - the key shortcut
	 * @param cmd
	 *            - the target command
	 */
	private void empowerKeyboardShortcuts(char key, String cmd) {
		feedback.getInputMap().put(KeyStroke.getKeyStroke(key), cmd);
		feedback.getActionMap().put(cmd, new KeyboardShortcut(console, cmd));
		logger.getInputMap().put(KeyStroke.getKeyStroke(key), cmd);
		logger.getActionMap().put(cmd, new KeyboardShortcut(console, cmd));
	}

	/**
	 * Equips navigation shortcut to elements outside the command line.
	 * 
	 * @param dir
	 *            - the key shortcut (L/R/U/D)
	 * @param cmd
	 *            - the target command
	 */
	private void empowerNavigationShortcuts(String dir, String cmd) {
		feedback.getInputMap().put(KeyStroke.getKeyStroke(dir), cmd);
		feedback.getActionMap().put(cmd,
				new NavigationShortcut(stream, logger, cmd));
		logger.getInputMap().put(KeyStroke.getKeyStroke(dir), cmd);
		logger.getActionMap().put(cmd,
				new NavigationShortcut(stream, logger, cmd));
	}

	/**
	 * Resets the viewable tasks according to the page shown.
	 * 
	 * @param page
	 *            - the page number to be shown
	 */
	public void goToPage(int page) {
		if (page > totalPage) {
			page = totalPage;
		} else if (page < 1) {
			page = 1;
		}
		pageShown = page;
		int startPoint = (pageShown - 1) * StreamConstants.UI.MAX_VIEWABLE_TASK;
		for (int i = 0; i < StreamConstants.UI.MAX_VIEWABLE_TASK; i++) {
			TaskViewUI taskPanel = shownTasks[i];
			try {
				int index = availIndices.get(startPoint + i);
				StreamTask task = availTasks.get(startPoint + i);
				taskPanel.updateView(index, task);
			} catch (IndexOutOfBoundsException e) {
				taskPanel.hideView();
			}
		}
		pageNumber.setText(Displayer.displayPageNumber(pageShown, totalPage));
		logDebug(String.format(LOG_PAGE_MOVED, pageShown, totalPage));
	}

	/**
	 * Resets the viewable tasks to the chosen indices and <b>StreamTask</b>s.
	 * 
	 * @param indices
	 *            - the list of indices to be displayed
	 * @param tasks
	 *            - the list of <b>StreamTask</b>s to be displayed
	 * @param isReset
	 *            - indicating if the view needs to be reset to the first page
	 * @param isSearching
	 *            - indicating if this is a search result
	 */
	public void resetAvailableTasks(ArrayList<Integer> indices,
			ArrayList<StreamTask> tasks, Boolean isReset, Boolean isSearching) {
		// error: length not the same
		assert (indices.size() == tasks.size()) : StreamConstants.Assertion.SIZE_DIFFERENT;
		availIndices = indices;
		availTasks = tasks;
		if (tasks.size() == 0) {
			// no task added: go to page one
			totalPage = 1;
		} else {
			totalPage = (int) Math.ceil(1.0 * tasks.size()
					/ StreamConstants.UI.MAX_VIEWABLE_TASK);
		}
		if (isReset || tasks.size() == 0 || isSearch) {
			/*
			 * resetting or clearing search result automatically resets the view
			 * back to first page
			 */
			goToPage(1);
			isSearch = isSearching;
		} else {
			if ((int) Math.ceil(1.0 * tasks.size()
					/ StreamConstants.UI.MAX_VIEWABLE_TASK) < pageShown) {
				// last task in the last page deleted: move back one page
				assert (pageShown != 1) : StreamConstants.Assertion.NO_PREV_PAGE;
				goToPage(pageShown - 1);
			} else {
				goToPage(pageShown);
			}
		}
		if (activeTask != null && !isTaskHighlighted) {
			highlightActiveTaskView();
		}
		logDebug(String.format(LOG_REFRESH, indices.size()));
	}

	/**
	 * Logs a message/error message to the logger.
	 * 
	 * @param logMsg
	 *            - the message to be logged
	 * @param isErrorMsg
	 *            - determines the formatting (different for error message)
	 */
	public void log(String logMsg, Boolean isErrorMsg) {
		if (isErrorMsg) {
			logger.showErrorMessage(logMsg);
		} else {
			logger.showLogMessage(logMsg);
		}
	}

	/**
	 * Displays the detailed information of a task in a dialog window.
	 * 
	 * @param task
	 *            - the <b>StreamTask</b> from which the information is obtained
	 *            from
	 */
	public void displayDetails(StreamTask task) {
		String taskName = Displayer.displayDetails(mainFrame, task);
		logDebug(String.format(LOG_DETAILS, taskName));
	}

	/**
	 * Gets the number of tasks stored in the task viewer after
	 * search/filter/add/remove/...
	 * 
	 * @return <b>int</b> - the number of tasks stored in task viewer
	 */
	public int getNumberOfTasksStored() {
		return availTasks.size();
	}

	/**
	 * Navigates to the first page.
	 */
	public void goToFirstPage() {
		goToPage(1);
	}

	/**
	 * Navigates to the previous page.
	 */
	public void goToPrevPage() {
		if (pageShown != 1) {
			goToPage(pageShown - 1);
		}
	}

	/**
	 * Navigates to the next page.
	 */
	public void goToNextPage() {
		if (pageShown != totalPage) {
			goToPage(pageShown + 1);
		}
	}

	/**
	 * Navigates to the last page.
	 */
	public void goToLastPage() {
		goToPage(totalPage);
	}

	/**
	 * Opens the help dialog panel.
	 */
	public void openHelpBox() {
		JOptionPane.showMessageDialog(mainFrame, TEXT_HELP, TITLE_HELP,
				JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public String getComponentName() {
		return "STREAMUI";
	}

}
package ui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import model.StreamTask;
import util.StreamConstants;
import util.StreamExternals;

//@author A0093874N

/**
 * <p>
 * The task graphical view as seen by the user.
 * </p>
 * 
 * <h3>API</h3>
 * <ul>
 * <li>StreamTaskView.hideView()</li>
 * <li>StreamTaskView.updateView(final Integer ind, StreamTask task)</li>
 * </ul>
 * <p>
 * Refer to method documentation for details.
 * </p>
 * 
 * @version V0.5
 */
public class TaskViewUI extends JPanel {

	private JLabel index;
	private CalendarIconUI startCal;
	private CalendarIconUI endCal;
	private JLabel taskName;
	private JLabel descLabel;
	private JLabel rankImage;
	private JLabel statusImage;
	private static final long serialVersionUID = 1L;

	TaskViewUI() {
		super();
		initParams();
		addIndexNumber();
		addStartCalendar();
		addEndCalendar();
		addTaskNameLabel();
		addDescLabel();
		addRankImage();
		addStatusImage();
	}

	private void initParams() {
		setLayout(null);
		setBackground(StreamConstants.UI.COLOR_TASKPANEL);
	}

	/**
	 * Adds the index number label to the task view.
	 */
	private void addIndexNumber() {
		index = new JLabel();
		index.setHorizontalAlignment(SwingConstants.CENTER);
		index.setFont(StreamConstants.UI.FONT_INDEX);
		index.setBounds(StreamConstants.UI.BOUNDS_INDEX_NUM);
		add(index);
	}

	/**
	 * Adds the start calendar icon.
	 */
	private void addStartCalendar() {
		startCal = new CalendarIconUI(StreamExternals.ICON_START_CAL,
				StreamExternals.ICON_NULL_START_CAL);
		startCal.setBounds(StreamConstants.UI.BOUNDS_START_CAL);
		add(startCal);
	}

	/**
	 * Adds the end calendar icon.
	 */
	private void addEndCalendar() {
		endCal = new CalendarIconUI(StreamExternals.ICON_END_CAL,
				StreamExternals.ICON_NULL_END_CAL);
		endCal.setBounds(StreamConstants.UI.BOUNDS_END_CAL);
		add(endCal);
	}

	/**
	 * Adds the task name label to the task view.
	 */
	private void addTaskNameLabel() {
		taskName = new JLabel();
		taskName.setFont(StreamConstants.UI.FONT_TASK);
		taskName.setBounds(StreamConstants.UI.BOUNDS_TASK_NAME);
		add(taskName);
	}

	/**
	 * Adds the description label to the task view.
	 */
	private void addDescLabel() {
		descLabel = new JLabel();
		descLabel.setFont(StreamConstants.UI.FONT_DESC);
		descLabel.setBounds(StreamConstants.UI.BOUNDS_TASK_DESC);
		add(descLabel);
	}

	/**
	 * Adds the rank image to the task view.
	 */
	private void addRankImage() {
		rankImage = new JLabel();
		rankImage.setBounds(StreamConstants.UI.BOUNDS_RANK_ICON);
		add(rankImage);
	}

	/**
	 * Adds the status image to the task view.
	 */
	private void addStatusImage() {
		statusImage = new JLabel();
		statusImage.setBounds(StreamConstants.UI.BOUNDS_STATS_ICON);
		add(statusImage);
	}

	/**
	 * Hides the task from the user view. Invoked if the view object has no task
	 * assigned to it.
	 */
	void hideView() {
		setVisible(false);
	}

	/**
	 * Updates the task view according to the fields supplied by the
	 * <b>StreamTask</b> <i>task</i>, assigning it with index number <i>ind</i>.
	 * 
	 * @param ind
	 *            - the index number assigned
	 * @param task
	 *            - the <b>StreamTask</b> from which the information is obtained
	 *            from
	 */
	void updateView(Integer ind, StreamTask task) {
		Displayer.updateCalendarIcon(startCal, task, true);
		Displayer.updateCalendarIcon(endCal, task, false);
		rankImage.setIcon(Displayer.selectRankIcon(task));
		statusImage.setIcon(Displayer.selectStatusIcon(task));
		index.setText(Displayer.displayIndex(ind));
		taskName.setText(Displayer.displayName(task));
		descLabel.setText(Displayer.displayDescription(task));
		setVisible(true);
	}
}
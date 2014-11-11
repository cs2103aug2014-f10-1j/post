package ui;

import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;

import util.StreamConstants;
import util.StreamUtil;

//@author A0093874N

/**
 * <p>
 * A user-friendly calendar icon to show start date and end date or deadline,
 * whichever ones applicable.
 * </p>
 * <h3>API</h3>
 * <ul>
 * <li>StreamUICalendarIcon.hideView()</li>
 * <li>StreamUICalendarIcon.updateView(Calendar cal)</li>
 * </ul>
 * <p>
 * Refer to method documentation for details.
 * </p>
 * 
 * @version V0.5
 */
public class StreamUICalendarIcon extends JLayeredPane {

	private static final long serialVersionUID = 1L;
	ImageIcon nullCalIcon;
	ImageIcon calIcon;
	JLabel imageUsed;
	JLabel month;
	JLabel date;
	JLabel time;

	public StreamUICalendarIcon(ImageIcon cal, ImageIcon nullCal) {
		setParams(cal, nullCal);
		setBackgroundImage();
		setMonthView();
		setDateView();
		setTimeView();
	}

	private void setParams(ImageIcon cal, ImageIcon nullCal) {
		calIcon = cal;
		nullCalIcon = nullCal;
		setSize(StreamConstants.UI.HEIGHT_TASKPANEL,
				StreamConstants.UI.HEIGHT_TASKPANEL);
		setLayout(null);
	}

	private void setBackgroundImage() {
		imageUsed = new JLabel();
		imageUsed.setBounds(StreamConstants.UI.BOUNDS_CAL_ICON);
		add(imageUsed, 0, 0);
	}

	private void setMonthView() {
		month = new JLabel();
		month.setFont(StreamConstants.UI.FONT_MONTH);
		month.setForeground(StreamConstants.UI.COLOR_MONTH);
		month.setHorizontalAlignment(SwingConstants.CENTER);
		month.setBounds(StreamConstants.UI.BOUNDS_MONTH);
		add(month, 1, 0);
	}

	private void setDateView() {
		date = new JLabel();
		date.setFont(StreamConstants.UI.FONT_DATE);
		date.setHorizontalAlignment(SwingConstants.CENTER);
		date.setBounds(StreamConstants.UI.BOUNDS_DATE);
		add(date, 1, 0);
	}

	private void setTimeView() {
		time = new JLabel();
		time.setFont(StreamConstants.UI.FONT_TIME);
		time.setHorizontalAlignment(SwingConstants.CENTER);
		time.setBounds(StreamConstants.UI.BOUNDS_TIME);
		add(time);
	}

	private void updateMonthView(int mon) {
		String parsedMonth = StreamUtil.getMonthAbbrev(mon);
		month.setText(parsedMonth);
		month.setVisible(true);
	}

	private void updateDateView(Integer day) {
		date.setText(day.toString());
		date.setVisible(true);
	}

	private void updateTimeView(int hr, int min) {
		time.setText(hr + ":" + StreamUtil.addZeroToTime(min));
		time.setVisible(true);
	}

	/**
	 * Changes the calendar to a simple null display. Invoked if the task has no
	 * calendar assigned to it.
	 */
	public void hideView() {
		imageUsed.setIcon(nullCalIcon);
		month.setVisible(false);
		date.setVisible(false);
		time.setVisible(false);
	}

	/**
	 * Updates the calendar according to the fields supplied by the
	 * <b>Calendar</b> <i>cal</i>.
	 * 
	 * @param cal
	 *            - the calendar from which the information is obtained from
	 */
	public void updateView(Calendar cal) {
		imageUsed.setIcon(calIcon);
		updateMonthView(cal.get(Calendar.MONTH));
		updateDateView(cal.get(Calendar.DAY_OF_MONTH));
		updateTimeView(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
	}

}

package ui;

import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;

import util.StreamConstants;

//@author A0093874N
/**
 * A user-friendly calendar icon to show start date and end date or deadline,
 * whichever ones applicable.
 */
public class CalendarIconUI extends JLayeredPane {

	private static final long serialVersionUID = 1L;
	private ImageIcon nullCalIcon;
	private ImageIcon calIcon;
	private JLabel imageUsed;
	private JLabel month;
	private JLabel date;
	private JLabel time;

	CalendarIconUI(ImageIcon cal, ImageIcon nullCal) {
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
		String parsedMonth = Displayer.getMonthAbbrev(mon);
		month.setText(parsedMonth);
		month.setVisible(true);
	}

	private void updateDateView(Integer day) {
		date.setText(day.toString());
		date.setVisible(true);
	}

	private void updateTimeView(int hr, int min) {
		String parsedTime = Displayer.displayHour(hr, min);
		time.setText(parsedTime);
		time.setVisible(true);
	}

	/**
	 * Changes the calendar to a simple null display. Invoked if the task has no
	 * calendar assigned to it.
	 */
	void hideView() {
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
	void updateView(Calendar cal) {
		imageUsed.setIcon(calIcon);
		updateMonthView(cal.get(Calendar.MONTH));
		updateDateView(cal.get(Calendar.DAY_OF_MONTH));
		updateTimeView(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
	}

}

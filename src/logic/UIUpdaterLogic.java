package logic;

import java.util.ArrayList;

import model.StreamTask;
import ui.StreamUI;

//@author A0093874N
/**
 * Executes UI updates. In addition to handling navigation commands, this
 * component acts as a bridge from other logic components to <b>StreamUI</b>
 * whenever UI update is required.
 */
public class UIUpdaterLogic {

	private StreamUI stui;

	private UIUpdaterLogic(StreamUI ui) {
		this.stui = ui;
	}

	public static UIUpdaterLogic init(StreamUI ui) {
		return new UIUpdaterLogic(ui);
	}

	void refreshUI(ArrayList<Integer> indices, ArrayList<StreamTask> tasks,
			Boolean isReset, Boolean isSearching) {
		stui.resetAvailableTasks(indices, tasks, isReset, isSearching);
	}

	String goToFirstPage() {
		stui.goToFirstPage();
		return null;
	}

	String goToPrevPage() {
		stui.goToPrevPage();
		return null;
	}

	String goToNextPage() {
		stui.goToNextPage();
		return null;
	}

	String goToLastPage() {
		stui.goToLastPage();
		return null;
	}

	String goToPage(int page) {
		stui.goToPage(page);
		return null;
	}

	String openHelpBox() {
		stui.openHelpBox();
		return null;
	}

	void setActiveTask(StreamTask task) {
		stui.setActiveTask(task);
	}

	void displayDetails(StreamTask task) {
		stui.displayDetails(task);
	}

}

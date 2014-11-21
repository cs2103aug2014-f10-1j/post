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

	void goToFirstPage() {
		stui.goToFirstPage();
	}

	void goToPrevPage() {
		stui.goToPrevPage();
	}

	void goToNextPage() {
		stui.goToNextPage();
	}

	void goToLastPage() {
		stui.goToLastPage();
	}

	void goToPage(int page) {
		stui.goToPage(page);
	}

	void openHelpBox() {
		stui.openHelpBox();
	}

	void setActiveTask(StreamTask task) {
		stui.setActiveTask(task);
	}

	void displayDetails(StreamTask task) {
		stui.displayDetails(task);
	}

}

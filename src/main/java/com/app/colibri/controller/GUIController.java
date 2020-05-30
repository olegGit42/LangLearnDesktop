package com.app.colibri.controller;

import static com.app.colibri.controller.WordController.allWordsList;
import static com.app.colibri.controller.WordController.getRoundedTimeToMinute;
import static com.app.colibri.controller.WordController.getTimeDelta;
import static com.app.colibri.service.AppSettings.getLocaledItem;
import static com.app.colibri.view.util.ViewUtil.msgWarningCode;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.JTextField;

import com.app.colibri.model.Word;
import com.app.colibri.service.AppSettings;
import com.app.colibri.view.GUI;
import com.app.colibri.view.panels.EditPanel;

public class GUIController {

	public static boolean canWordAction() {

		if (AppSettings.appSettings.getUser().getId() == 0) {
			return true;
		}

		if (RESTController.checkConnection()) {
			if (RESTController.checkUserAuth()) {
				return true;
			} else {
				msgWarningCode("relogin_web");
			}
		} else {
			msgWarningCode("web_connention_error");
		}

		return false;

	}

	public static void repeateWords(final List<Word> repeatedWordList) {
		repeatedWordList.clear();
		final long now = System.currentTimeMillis();
		allWordsList.stream().filter(word -> now >= getRoundedTimeToMinute(word.getRegTime() + getTimeDelta(word.getBox())))
				.forEach(repeatedWordList::add);
	}

	public static void badRememberWords(final List<Word> badRememberWordList) {
		badRememberWordList.clear();
		allWordsList.stream().filter(word -> word.getRepeateIndicator() > 3 && word.getBox() < 4)
				.sorted(Comparator.comparingInt(Word::getRepeateIndicator).reversed()).limit(30)
				.forEach(badRememberWordList::add);
	}

	public static void searchWords(final List<Word> serachWordList, final String str, final boolean isTranslate,
			final boolean isRepetitionTimeOrder, final String tag) {
		serachWordList.clear();
		EditPanel.clearWordsCount();
		Filter.init(str);
		allWordsList.stream().filter(word -> Filter.isAppropriate(word, tag))
				.sorted((w1, w2) -> compareWords(w1, w2, isTranslate, isRepetitionTimeOrder)).forEach(word -> {
					EditPanel.incrementWordsCount(word);
					serachWordList.add(word);
				});
	}

	private static class Filter {
		private static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		private static final Date date = new Date();

		private static long creationDate;
		private static long repeatDate;

		private static String findStr = "";
		private static String findDateStr = "";

		public static void init(String p_findStr) {
			findStr = p_findStr == null || p_findStr.trim() == "" ? "" : p_findStr.trim().toUpperCase();
			findDateStr = findStr.replaceFirst("<", "").replaceFirst(">", "");

			try {
				if (findStr.endsWith("R")) {
					repeatDate = dateFormat.parse(findDateStr).getTime();
					creationDate = 0;
				} else {
					creationDate = dateFormat.parse(findDateStr).getTime();
					repeatDate = 0;
				}
			} catch (Exception e) {
				repeatDate = 0;
				creationDate = 0;
			}
		}

		public static boolean isAppropriate(final Word word, final String tag) {
			return (nullCondition() || wordCondition(word) || translateCondition(word) || idCondition(word) || boxCondition(word)
					|| creationDateCondition(word) || repeatDateCondition(word)) && tagCondition(word, tag);
		}

		private static boolean tagCondition(final Word word, final String tag) {
			return tag.equals(getLocaledItem("All tags")) || word.isContainTag(tag);
		}

		private static boolean nullCondition() {
			return findStr.equals("");
		}

		private static boolean wordCondition(final Word word) {
			return isNotDate() && word.getWord().toUpperCase().contains(findStr);
		}

		private static boolean translateCondition(final Word word) {
			return isNotDate() && word.getTranslate().toUpperCase().contains(findStr);
		}

		private static boolean idCondition(final Word word) {
			return isNotDate() && String.valueOf(word.getId()).equals(findStr);
		}

		private static boolean boxCondition(final Word word) {
			return isNotDate() && (findStr.startsWith("B") && ("B" + word.getBox()).equals(findStr));
		}

		private static boolean creationDateCondition(final Word word) {
			return dateCondition(creationDate, getCreationTime(word));
		}

		private static boolean repeatDateCondition(final Word word) {
			return dateCondition(repeatDate, getRepeatTime(word));
		}

		private static boolean dateCondition(final long compareDate, final long wordTime) {
			return compareDate != 0
					&& ((isEqualsDate() && compareDate == wordTime) || (findStr.startsWith("<") && wordTime < compareDate)
							|| (findStr.startsWith(">") && wordTime > compareDate));
		}

		private static boolean isEqualsDate() {
			return findStr.length() == findDateStr.length();
		}

		private static boolean isNotDate() {
			return creationDate == 0 && repeatDate == 0;
		}

		private static long getCreationTime(final Word word) {
			return getRoundedTimeToDay(word.getCreationTime());
		}

		private static long getRepeatTime(final Word word) {
			return getRoundedTimeToDay(word.getRegTime() + getTimeDelta(word.getBox()));
		}

		private static long getRoundedTimeToDay(final long time) {
			return WordController.getRoundedTime(date, time, dateFormat);
		}

	}

	private static int compareWords(Word w1, Word w2, boolean isTranslate, boolean isRepetitionTimeOrder) {

		if (isRepetitionTimeOrder) {

			if (w1.obtainRepetitionTime() < w2.obtainRepetitionTime()) {
				return -1;
			} else if (w1.obtainRepetitionTime() == w2.obtainRepetitionTime()) {
				return 0;
			} else {
				return 1;
			}

		} else if (isTranslate) {
			return fixUpperYoForCompare(w1.getTranslate().toUpperCase())
					.compareTo(fixUpperYoForCompare(w2.getTranslate().toUpperCase()));
		} else {
			return w1.getWord().toUpperCase().compareTo(w2.getWord().toUpperCase());
		}

	}

	private static String fixUpperYoForCompare(String str) {
		return str.startsWith("Ё") ? "ЕЯЯ" + str : str;
	}

	public static String getFromClipboard() {
		String result = "";

		Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		DataFlavor dataFlavor = DataFlavor.stringFlavor;

		if (systemClipboard.isDataFlavorAvailable(dataFlavor)) {
			try {
				result = ((String) systemClipboard.getData(dataFlavor)).trim();
			} catch (UnsupportedFlavorException | IOException e1) {
				e1.printStackTrace();
			}
		}

		return result;
	}

	public static void addTextFromClipboard(JTextField textField) {
		installTextFromClipboard(textField, 1);
	}

	public static void setTextFromClipboard(JTextField textField) {
		installTextFromClipboard(textField, 2);
	}

	public static void installTextFromClipboard(JTextField textField, int type) {
		final String clipboard = getFromClipboard();
		final String text = textField.getText().trim();

		if (clipboard.equals("")) {
			textField.setText(text);
		} else {
			if (type == 1) {
				textField.setText(text.equals("") ? clipboard : text.concat(", ".concat(clipboard)));
			} else {
				textField.setText(clipboard);
			}
		}
	}

	public static void updMinRepeatTime() {
		if (GUI.repeatedWordList.isEmpty()) {
			WordController.minRepeatTime = Long.MAX_VALUE;
			WordController.allWordsList.forEach(WordController::setMinRepTime);
		}
	}

}

package com.app.colibri.controller;

import static com.app.colibri.controller.WordController.allWordsList;
import static com.app.colibri.controller.WordController.getRoundedTime;
import static com.app.colibri.controller.WordController.getTimeDelta;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import javax.swing.JTextField;

import com.app.colibri.model.Word;

public class GUIController {
	public static void repeateWords(final List<Word> repeatedWordList) {
		repeatedWordList.clear();
		final long now = System.currentTimeMillis();
		allWordsList.stream().filter(word -> now > getRoundedTime(word.getRegTime() + getTimeDelta(word.getBox())))
				.forEach(repeatedWordList::add);
	}

	public static void badRememberWords(final List<Word> badRememberWordList) {
		badRememberWordList.clear();
		allWordsList.stream().filter(word -> word.getRepeateIndicator() > 3 && word.getBox() < 4)
				.sorted(Comparator.comparingInt(Word::getRepeateIndicator).reversed()).forEach(badRememberWordList::add);
	}

	public static void searchWords(final List<Word> serachWordList, final String str) {
		serachWordList.clear();
		final String strFinal = str == null || str.trim() == "" ? null : str.trim().toUpperCase();
		allWordsList.stream()
				.filter(word -> strFinal == null || word.getWord().toUpperCase().contains(strFinal)
						|| word.getTranslate().toUpperCase().contains(strFinal))
				.sorted((w1, w2) -> w1.getWord().toUpperCase().compareTo(w2.getWord().toUpperCase()))
				.forEach(serachWordList::add);
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
		final String clipboard = getFromClipboard();
		final String text = textField.getText().trim();

		if (!clipboard.equals("")) {
			textField.setText(text.equals("") ? clipboard : text.concat(", ".concat(clipboard)));
		} else {
			textField.setText(text);
		}
	}

}

package com.app.colibri.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.app.colibri.controller.WordController;

public class Box {

	public static final int MAX_BOX = 12;
	public static final List<List<Word>> boxList = new ArrayList<>();

	static {
		for (int i = 0; i <= MAX_BOX; i++) {
			boxList.add(new ArrayList<Word>());
		}
	}

	public static void fillBoxes() {
		WordController.allWordsList.stream().filter(word -> word != null)
				.sorted(Comparator.comparingLong(Word::obtainRepetitionTime)).forEach(Word::addToBox);
	}

	public static void refreshBox() {
		boxList.forEach(List::clear);
		fillBoxes();
	}

	public static List<Word> getBox(final int boxIndex) {
		return boxList.get(boxIndex);
	}

}

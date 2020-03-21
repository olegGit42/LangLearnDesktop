package com.app.colibri.model;

import java.util.ArrayList;
import java.util.List;

import com.app.colibri.controller.WordController;

public class Box {

	public static final List<List<Word>> boxList = new ArrayList<>();;

	static {
		for (int i = 0; i < 8; i++) {
			boxList.add(new ArrayList<Word>());
		}
	}

	public static void fillBoxes() {
		WordController.allWordsList.stream().filter(word -> word != null).forEach(Word::addToBox);
	}

	public static void refreshBox() {
		boxList.forEach(List::clear);
		fillBoxes();
	}

	public static List<Word> getBox(final int boxIndex) {
		return boxList.get(boxIndex);
	}

}

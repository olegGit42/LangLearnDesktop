package com.app.colibri.model;

import java.util.ArrayList;
import java.util.List;

import com.app.colibri.controller.WordController;
import com.app.colibri.model.boxstrategy.B0Strategy;
import com.app.colibri.model.boxstrategy.B1Strategy;
import com.app.colibri.model.boxstrategy.B2Strategy;
import com.app.colibri.model.boxstrategy.B3Strategy;
import com.app.colibri.model.boxstrategy.B4Strategy;
import com.app.colibri.model.boxstrategy.B5Strategy;
import com.app.colibri.model.boxstrategy.B6Strategy;
import com.app.colibri.model.boxstrategy.B7Strategy;
import com.app.colibri.model.boxstrategy.BoxStrategy;

public class Box {

	public static List<Word> b0 = new ArrayList<Word>();
	public static List<Word> b1 = new ArrayList<Word>();
	public static List<Word> b2 = new ArrayList<Word>();
	public static List<Word> b3 = new ArrayList<Word>();
	public static List<Word> b4 = new ArrayList<Word>();
	public static List<Word> b5 = new ArrayList<Word>();
	public static List<Word> b6 = new ArrayList<Word>();
	public static List<Word> b7 = new ArrayList<Word>();

	public static final BoxStrategy[] boxStrategies;

	static {
		boxStrategies = new BoxStrategy[8];
		boxStrategies[0] = new B0Strategy();
		boxStrategies[1] = new B1Strategy();
		boxStrategies[2] = new B2Strategy();
		boxStrategies[3] = new B3Strategy();
		boxStrategies[4] = new B4Strategy();
		boxStrategies[5] = new B5Strategy();
		boxStrategies[6] = new B6Strategy();
		boxStrategies[7] = new B7Strategy();
	}

	public static void fillBoxes() {
		WordController.allWordsList.stream().filter(word -> word != null)
				.forEach(word -> word.addToBox(boxStrategies[word.getBox()]));
	}

	public static void refreshBox() {
		b0.clear();
		b1.clear();
		b2.clear();
		b3.clear();
		b4.clear();
		b5.clear();
		b6.clear();
		fillBoxes();
	}
}

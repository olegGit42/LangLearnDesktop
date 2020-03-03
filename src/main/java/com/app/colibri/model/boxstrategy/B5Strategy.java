package com.app.colibri.model.boxstrategy;

import com.app.colibri.model.Box;
import com.app.colibri.model.Word;

public class B5Strategy implements BoxStrategy {

	@Override
	public void addToBox(Word word) {
		Box.b5.add(word);
	}

}

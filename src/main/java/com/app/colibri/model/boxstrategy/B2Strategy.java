package com.app.colibri.model.boxstrategy;

import com.app.colibri.model.Box;
import com.app.colibri.model.Word;

public class B2Strategy implements BoxStrategy {

	@Override
	public void addToBox(Word word) {
		Box.b2.add(word);
	}

}

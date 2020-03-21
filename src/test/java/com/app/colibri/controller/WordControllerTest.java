package com.app.colibri.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WordControllerTest {

	@Test
	public void test() {
		assertEquals(WordController.repeatPeriodArray.length, 8);
		assertEquals(WordController.timeDeltaArray.length, 8);
	}

}

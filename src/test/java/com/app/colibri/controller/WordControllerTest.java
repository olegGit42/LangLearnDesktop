package com.app.colibri.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.app.colibri.model.Box;
import com.app.colibri.service.AppRun;

public class WordControllerTest {

	@Test
	public void test() {
		@SuppressWarnings("unused")
		ApplicationContext appContext = AppRun.appContext;
		assertEquals(WordController.repeatPeriodArray.length, Box.MAX_BOX + 1);
		assertEquals(WordController.timeDeltaArray.length, Box.MAX_BOX + 1);
	}

}

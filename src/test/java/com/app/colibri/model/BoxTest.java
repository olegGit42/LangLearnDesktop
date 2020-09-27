package com.app.colibri.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BoxTest extends Box {

	@Test
	public void test() {
		assertEquals(Box.boxList.size(), Box.MAX_BOX + 1);
	}

}

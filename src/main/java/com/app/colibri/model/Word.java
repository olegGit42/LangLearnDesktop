package com.app.colibri.model;

import java.io.Serializable;

import com.app.colibri.controller.WordController;

import lombok.Data;

@Data
public class Word implements Serializable {
	private static final long serialVersionUID = -1943399776402594085L;

	private int id;
	private String word;
	private String translate;
	private long regTime;
	private long creationTime;
	private int box = 0; // 0-6
	private int repeateIndicator = 0;

	public Word() {
	}

	public void afterInitNewWord(String word, String translate) {
		final long time = System.currentTimeMillis();
		this.regTime = time;
		this.creationTime = time;
		this.id = WordController.newId.incrementAndGet();
		this.word = word;
		this.translate = translate;
		Box.getBox(0).add(this);
		WordController.allWordsList.add(this);
		WordController.setMinRepTime(this);
	}

	@Override
	public String toString() {
		String str = this.word + " " + this.translate + " " + this.regTime + " " + this.box + " " + this.repeateIndicator;
		return str;
	}

	public void inctementRepeateIndicator() {
		++repeateIndicator;
		regTime = System.currentTimeMillis();
	}

	public void addToBox() {
		Box.boxList.get(box).add(this);
	}

}

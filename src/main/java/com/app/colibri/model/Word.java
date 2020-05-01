package com.app.colibri.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	private Set<Integer> tagIdSet = new HashSet<>();

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

	public void setNewBoxAndUpdDate(int box) {
		setBox(box);
		inctementRepeateIndicator();
	}

	public void addToBox() {
		Box.boxList.get(box).add(this);
	}

	public long obtainRepetitionTime() {
		return this.getRegTime() + WordController.getTimeDelta(this.getBox());
	}

	public void putTag(String p_tag) {
		putTag(WordController.userWordRegistry.getTagRegistry().putTag(p_tag));
	}

	public void putTag(int p_tagID) {
		if (p_tagID > 0) {
			tagIdSet.add(p_tagID);
		}
	}

	public void putNewTagSet(Set<String> p_tagSet) {
		removeAllTags();
		p_tagSet.forEach(this::putTag);
	}

	public void removeTag(String p_tag) {
		removeTag(WordController.userWordRegistry.getTagRegistry().putTag(p_tag));
	}

	public void removeTag(int p_tagID) {
		tagIdSet.remove(p_tagID);
	}

	public void removeAllTags() {
		tagIdSet.clear();
	}

	public List<String> obtainTagList() {
		List<String> v_tagList = new ArrayList<>();

		for (int v_tagID : tagIdSet) {
			v_tagList.add(WordController.userWordRegistry.getTagRegistry().obtainTagById(v_tagID));
		}

		return v_tagList;
	}

	public boolean isContainTag(String p_tag) {
		return isContainTag(WordController.userWordRegistry.getTagRegistry().obtainIdByTag(p_tag));
	}

	public boolean isContainTag(int p_tagID) {
		return tagIdSet.contains(p_tagID);
	}

}

package com.app.colibri.model.tablemodel;

import static com.app.colibri.service.AppSettings.getLocaledItem;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.app.colibri.model.Word;

@SuppressWarnings("serial")
public class SearchTableModel extends AbstractTableModel {

	private List<Word> words;

	public SearchTableModel(List<Word> words) {
		this.words = words;
	}

	public Class<?> getColumnClass(int columnIndex) {
		if (words.isEmpty()) {
			return Object.class;
		}
		return getValueAt(0, columnIndex).getClass();

		// return String.class;
	}

	public int getColumnCount() {
		return 2;
	}

	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return getLocaledItem("Word");
		case 1:
			return getLocaledItem("Translate");
		}
		return "No data";
	}

	public int getRowCount() {
		return words.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {

		Word word = words.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return word.getWord();
		case 1:
			return word.getTranslate();
		case 2:
			return word;
		}
		return "";
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

}

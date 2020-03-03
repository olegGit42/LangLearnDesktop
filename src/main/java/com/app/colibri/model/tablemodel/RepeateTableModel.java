package com.app.colibri.model.tablemodel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.app.colibri.controller.WordController;
import com.app.colibri.model.Word;

public class RepeateTableModel implements TableModel {

	private Set<TableModelListener> listeners = new HashSet<TableModelListener>();

	private List<Word> words;

	public RepeateTableModel(List<Word> words) {
		this.words = words;
	}

	public void addTableModelListener(TableModelListener listener) {
		listeners.add(listener);
	}

	public Class<?> getColumnClass(int columnIndex) {
		if (words.isEmpty()) {
			return Object.class;
		}
		return getValueAt(0, columnIndex).getClass();

		// return String.class;
	}

	public int getColumnCount() {
		return 4;
	}

	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "ID";
		case 1:
			return "Word";
		case 2:
			return "Period";
		case 3:
			return "Box";
		}
		return "";
	}

	public int getRowCount() {
		return words.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Word word = words.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return word.getId(); // String.valueOf(word.getId());
		case 1:
			return word.getWord();
		case 2:
			return WordController.repeatPeriod[word.getBox()];
		case 3:
			return String.valueOf(word.getBox());
		}
		return "";
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	public void removeTableModelListener(TableModelListener listener) {
		listeners.remove(listener);
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {

	}

}

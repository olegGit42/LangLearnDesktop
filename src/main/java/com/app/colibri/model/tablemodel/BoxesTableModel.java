package com.app.colibri.model.tablemodel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.app.colibri.model.Word;

public class BoxesTableModel implements TableModel {

	private Set<TableModelListener> listeners = new HashSet<TableModelListener>();

	private List<Word> wordsList;
	private String columnName;

	public BoxesTableModel(List<Word> beans, String columnName) {
		this.wordsList = beans;
		this.columnName = columnName;
	}

	public void addTableModelListener(TableModelListener listener) {
		listeners.add(listener);
	}

	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	public int getColumnCount() {
		return 1;
	}

	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return columnName;
		}
		return columnName;
	}

	public int getRowCount() {
		return wordsList.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Word word = wordsList.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return word.getWord();
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
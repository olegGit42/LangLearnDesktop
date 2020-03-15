package com.app.colibri.model.tablemodel;

import static com.app.colibri.controller.WordController.getRoundedTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.app.colibri.controller.WordController;
import com.app.colibri.model.Word;

public class AllWordsTableModel implements TableModel {

	private Set<TableModelListener> listeners = new HashSet<TableModelListener>();

	private List<Word> words;

	public AllWordsTableModel(List<Word> words) {
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
		return 6;
	}

	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "ID";
		case 1:
			return "Word";
		case 2:
			return "Translate";
		case 3:
			return "Repeat date";
		case 4:
			return "Box";
		case 5:
			return "Period";
		}
		return "No data";
	}

	public int getRowCount() {
		return words.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {

		Word word = words.get(rowIndex);
		Date date = new Date(getRoundedTime(word.getRegTime() + WordController.getTimeDelta(word.getBox())));
		DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm"); // "dd.MM.yyyy HH:mm"
		String dateStr = dateFormat.format(date);

		switch (columnIndex) {
		case 0:
			return word.getId(); // String.valueOf(word.getId());
		case 1:
			return word.getWord();
		case 2:
			return word.getTranslate();
		case 3:
			return dateStr;
		case 4:
			return String.valueOf(word.getBox());
		case 5:
			return WordController.repeatPeriod[word.getBox()];
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

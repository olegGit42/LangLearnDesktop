package com.app.colibri.view.panels;

import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

import lombok.Getter;

@SuppressWarnings("serial")
@Getter
public class BoxScrollPane extends JScrollPane {

	private final TableModel tableModel;
	private final JTable table;

	public BoxScrollPane(TableModel tableModel) {
		super();

		this.tableModel = tableModel;

		this.table = new JTable(this.tableModel);
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.table.setAutoCreateRowSorter(true);

		this.setViewportView(table);
		this.setPreferredSize(new Dimension(797 / 7, 547));
	}

}

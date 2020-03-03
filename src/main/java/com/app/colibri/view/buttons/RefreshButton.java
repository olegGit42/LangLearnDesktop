package com.app.colibri.view.buttons;

import java.util.List;

import javax.swing.JButton;
import javax.swing.JTable;

import com.app.colibri.interfaces.FuncVoid;

@SuppressWarnings("serial")
public class RefreshButton extends JButton {

	private JTable table;

	private FuncVoid funcVoid;
	private List<JTable> tableList;

	public RefreshButton(JTable p_table) {
		super("Refresh");
		table = p_table;

		this.addActionListener(e -> table.revalidate());
	}

	public RefreshButton(FuncVoid p_funcVoid) {
		super("Refresh");
		funcVoid = p_funcVoid;

		this.addActionListener(e -> funcVoid.doIt());
	}

	public RefreshButton(FuncVoid p_funcVoid, List<JTable> p_tableList) {
		super("Refresh");
		funcVoid = p_funcVoid;
		tableList = p_tableList;

		this.addActionListener(e -> {
			funcVoid.doIt();
			tableList.forEach(JTable::revalidate);
		});
	}

}

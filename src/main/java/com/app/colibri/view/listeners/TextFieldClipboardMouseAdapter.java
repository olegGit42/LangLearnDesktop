package com.app.colibri.view.listeners;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;

import com.app.colibri.controller.GUIController;

public class TextFieldClipboardMouseAdapter extends MouseAdapter {

	private final JTextField textField;
	private final int type;

	public TextFieldClipboardMouseAdapter(JTextField textField) {
		this(textField, 1);
	}

	public TextFieldClipboardMouseAdapter(JTextField textField, int type) {
		super();
		this.textField = textField;
		this.type = type;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			GUIController.installTextFromClipboard(textField, type);
		} else if (e.getButton() == MouseEvent.BUTTON2) {
			textField.setText("");
		}
	}

}

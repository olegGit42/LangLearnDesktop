package com.app.colibri.view.listeners;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;

import com.app.colibri.controller.GUIController;

public class TextFieldClipboardMouseAdapter extends MouseAdapter {

	private final JTextField textField;

	public TextFieldClipboardMouseAdapter(JTextField textField) {
		super();
		this.textField = textField;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON2) {
			GUIController.addTextFromClipboard(textField);
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			textField.setText("");
		}
	}

}

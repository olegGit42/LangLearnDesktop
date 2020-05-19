package com.app.colibri.view.util;

import static com.app.colibri.service.AppSettings.getLocaledItem;

import javax.swing.JOptionPane;

public class ViewUtil {

	// ask messages

	public static boolean askCode(String askCode) {
		return ask(getLocaledItem(askCode));
	}

	public static boolean ask(String ask) {
		final int answer = JOptionPane.showConfirmDialog(null, ask, getLocaledItem("Question"), JOptionPane.YES_NO_OPTION);
		return (answer == JOptionPane.YES_OPTION);
	}

	// information messages

	public static void msgInfoCode(String messageCode) {
		msgInfo(getLocaledItem(messageCode));
	}

	public static void msgInfo(String message) {
		msgBase(message, "Info", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void msgWarningCode(String messageCode) {
		msgWarning(getLocaledItem(messageCode));
	}

	public static void msgWarning(String message) {
		msgBase(message, "Warning", JOptionPane.WARNING_MESSAGE);
	}

	public static void msgErrorCode(String messageCode) {
		msgError(getLocaledItem(messageCode));
	}

	public static void msgError(String message) {
		msgBase(message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	// base for information messages
	public static void msgBase(String message, String titleCode, int messageType) {
		JOptionPane.showMessageDialog(null, message, getLocaledItem(titleCode), messageType);
	}

}

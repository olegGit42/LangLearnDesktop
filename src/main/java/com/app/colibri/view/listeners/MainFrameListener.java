package com.app.colibri.view.listeners;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import com.app.colibri.controller.WordController;

public class MainFrameListener implements WindowListener {
	public MainFrameListener() {
	}

	@Override
	public void windowClosing(WindowEvent event) {
		WordController.serializeUserDataRegistryWithBackup();
		System.exit(0);
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}
}

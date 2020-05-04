package com.app.colibri.service;

import java.io.File;

import javax.swing.SwingUtilities;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.app.colibri.controller.WordController;
import com.app.colibri.model.Box;
import com.app.colibri.view.GUI;

public class AppRun {

	public static ApplicationContext appContext;

	static {
		File userDataDir = new File("UserData");
		if (!userDataDir.isDirectory()) {
			userDataDir.mkdir();
		}
		appContext = new ClassPathXmlApplicationContext("appContext.xml");
		AppSettings.appSettings = appContext.getBean("appSettings", AppSettings.class);
		MainLocaleManager.mainLocaleManager = appContext.getBean("mainLocaleManager", MainLocaleManager.class);
	}

	public static void main(String[] args) {
		init();
		SwingUtilities.invokeLater(GUI::new);
	}

	public static void init() {
		WordController.unserializeAllWordsMain();
		Box.fillBoxes();
		/*
		 * try { // Set System L&F
		 * UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch
		 * (Exception e) { }
		 */
	}

}

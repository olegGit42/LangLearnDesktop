package com.app.colibri.controller;

import javax.swing.SwingUtilities;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.app.colibri.model.Box;
import com.app.colibri.view.GUI;

public class AppRun {

	public static ApplicationContext appContext;

	static {
		appContext = new ClassPathXmlApplicationContext("appContext.xml");
		appContext.getBean("allWords");
	}

	public static void main(String[] args) {
		init();
		SwingUtilities.invokeLater(GUI::new);
	}

	public static void init() {
		WordController.serializeAllWordsCopy();
		Box.fillBoxes();
		/*
		 * try { // Set System L&F
		 * UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch
		 * (Exception e) { }
		 */
	}

}

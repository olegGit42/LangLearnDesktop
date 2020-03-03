package com.app.colibri.controller;

import java.util.List;

import javax.swing.SwingUtilities;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.app.colibri.model.Box;
import com.app.colibri.model.Word;
import com.app.colibri.view.GUI;

@SuppressWarnings("unchecked")
public class AppRun {
	public static ApplicationContext appContext;
	static {
		appContext = new ClassPathXmlApplicationContext("appContext.xml");
		WordController.allWordsList = (List<Word>) AppRun.appContext.getBean("allWords");
	}

	public static void main(String[] args) {
		init();
		SwingUtilities.invokeLater(GUI::new);
	}

	public static void init() {
		WordController.serializeAllWordsToFile("wordsCopy.bin");
		Box.fillBoxes();
		/*
		 * try { // Set System L&F
		 * UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch
		 * (Exception e) { }
		 */
	}
}

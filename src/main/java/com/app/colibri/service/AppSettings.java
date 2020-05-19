package com.app.colibri.service;

import java.util.Locale;

import javax.swing.JFrame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.app.colibri.controller.WordController;
import com.app.colibri.model.User;
import com.app.colibri.registry.UserDataRegistry;
import com.app.colibri.view.GUI;
import com.app.colibri.view.LoginFrame;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Component
@Data
public class AppSettings {

	public static AppSettings appSettings;
	public static final String KEY = "WgeD3PXESApMqc2h";
	public static final String APP_URL = "http://localhost:8080/ColibriWeb/";
	public static final ObjectMapper jsonMapper = new ObjectMapper();

	@Autowired
	private MessageSource localeSource;
	@Autowired
	@Qualifier(value = "defaultUser")
	private User user;
	private Locale appLocale = Locale.forLanguageTag("EN");

	public AppSettings() {
	}

	public String obtainLocaleSourceItem(String code) {
		try {
			return localeSource.getMessage(code, null, appLocale);
		} catch (Exception e) {
			return "";
		}
	}

	public void installLocale(String locale) {
		try {
			appLocale = Locale.forLanguageTag(locale);
		} catch (Exception e) {
		}
	}

	public static void reloadMainFrame() {
		reloadMainFrameBase();
		LoginFrame.appInit();
	}

	public static void reloadMainFrame(UserDataRegistry userDataRegistry) {
		reloadMainFrameBase();
		LoginFrame.appInit(userDataRegistry);
	}

	private static void reloadMainFrameBase() {
		// Close mainFrame for reload it
		if (GUI.mainFrame != null) {
			GUI.mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			GUI.mainFrame.dispose();
			GUI.mainFrame = null;
		}

		MainLocaleManager.removeAllItems();
		WordController.minRepeatTime = Long.MAX_VALUE;
	}

	public static String getLocaledItem(String code) {
		return appSettings.obtainLocaleSourceItem(code);
	}

	public static void changeLocale(String locale) {
		appSettings.installLocale(locale);
		try {
			WordController.userDataRegistry.setAppLocale(locale);
		} catch (Exception e) {
		}
		MainLocaleManager.changeLocaleStatic();
	}

}

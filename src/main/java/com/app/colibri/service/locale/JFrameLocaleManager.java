package com.app.colibri.service.locale;

import javax.swing.JFrame;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.app.colibri.service.AppSettings;

@Component
@Qualifier(value = "localeManager")
public class JFrameLocaleManager extends ALocaleManager<JFrame> {

	@Override
	public void changeComponentLocale(JFrame component, String localedString, int index) {
		switch (index) {
		case 0:
			component.setTitle(localedString + " - " + AppSettings.appSettings.getUser().getUserName());
			break;

		default:
			break;
		}
	}

}

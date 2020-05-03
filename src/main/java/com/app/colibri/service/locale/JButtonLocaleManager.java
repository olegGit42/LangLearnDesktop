package com.app.colibri.service.locale;

import javax.swing.JButton;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier(value = "localeManager")
public class JButtonLocaleManager extends ALocaleManager<JButton> {

	@Override
	public void changeComponentLocale(JButton component, String localedString, int index) {
		switch (index) {
		case 0:
			component.setText(localedString);
			break;

		case 1:
			component.setToolTipText(localedString);
			break;

		default:
			break;
		}
	}

}

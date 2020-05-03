package com.app.colibri.service.locale;

import javax.swing.JToggleButton;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier(value = "localeManager")
public class JToggleButtonLocaleManager extends ALocaleManager<JToggleButton> {

	@Override
	public void changeComponentLocale(JToggleButton component, String localedString, int index) {
		switch (index) {
		case 0:
			if (!component.isSelected()) {
				component.setText(localedString);
			}
			break;

		case 1:
			if (component.isSelected()) {
				component.setText(localedString);
			}
			break;

		case 2:
			if (!component.isSelected()) {
				component.setToolTipText(localedString);
			}
			break;

		case 3:
			if (component.isSelected()) {
				component.setToolTipText(localedString);
			}
			break;

		default:
			break;
		}
	}

}

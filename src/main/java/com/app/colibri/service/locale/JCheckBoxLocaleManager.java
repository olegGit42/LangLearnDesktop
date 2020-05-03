package com.app.colibri.service.locale;

import javax.swing.JCheckBox;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier(value = "localeManager")
public class JCheckBoxLocaleManager extends ALocaleManager<JCheckBox> {

	@Override
	public void changeComponentLocale(JCheckBox component, String localedString, int index) {
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
		}
	}

}

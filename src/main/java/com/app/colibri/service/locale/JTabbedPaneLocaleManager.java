package com.app.colibri.service.locale;

import javax.swing.JTabbedPane;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier(value = "localeManager")
public class JTabbedPaneLocaleManager extends ALocaleManager<JTabbedPane> {

	@Override
	public void changeComponentLocale(JTabbedPane component, String localedString, int index) {
		component.setTitleAt(index, localedString);
	}

}

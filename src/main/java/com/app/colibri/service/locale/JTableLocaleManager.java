package com.app.colibri.service.locale;

import javax.swing.JTable;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier(value = "localeManager")
public class JTableLocaleManager extends ALocaleManager<JTable> {

	@Override
	public void changeComponentLocale(JTable component, String localedString, int index) {
		component.getColumnModel().getColumn(index).setHeaderValue(localedString);
	}

}

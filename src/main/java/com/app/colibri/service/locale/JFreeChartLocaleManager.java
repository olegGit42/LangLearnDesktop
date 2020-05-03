package com.app.colibri.service.locale;

import org.jfree.chart.JFreeChart;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier(value = "localeManager")
public class JFreeChartLocaleManager extends ALocaleManager<JFreeChart> {

	@Override
	public void changeComponentLocale(JFreeChart component, String localedString, int index) {
		switch (index) {
		case 0:
			component.getCategoryPlot().getRangeAxis().setLabel(localedString);
			break;

		default:
			break;
		}
	}

}

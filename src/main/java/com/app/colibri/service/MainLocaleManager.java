package com.app.colibri.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.app.colibri.interfaces.ILocaleManager;

import lombok.Data;

@Component
@Qualifier(value = "mainLocaleManager")
@Data
@SuppressWarnings("rawtypes")
public class MainLocaleManager implements ILocaleManager<Object> {

	public static MainLocaleManager mainLocaleManager;

	@Autowired
	@Qualifier(value = "localeManager")
	private Set<ILocaleManager> localeManagerSet;

	@Override
	public void changeLocale() {
		localeManagerSet.forEach(ILocaleManager::changeLocale);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addTrackedComponent(Object component, String... codeArray) {
		if (component != null) {
			localeManagerSet.stream().filter(lm -> lm.checkComponentClass(component))
					.forEach(lm -> lm.addTrackedComponent(component, codeArray));
		}
	}

	@Override
	public boolean checkComponentClass(Object component) {
		return true;
	}

	public static void changeLocaleStatic() {
		mainLocaleManager.changeLocale();
	}

	public static void addTrackedItem(Object component, String... codeArray) {
		mainLocaleManager.addTrackedComponent(component, codeArray);
	}

}

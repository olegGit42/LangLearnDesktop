package com.app.colibri.service.locale;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import com.app.colibri.interfaces.ILocaleManager;
import com.app.colibri.service.AppSettings;

import lombok.Data;

@Data
public abstract class ALocaleManager<T> implements ILocaleManager<T> {

	private Map<T, String[]> componentMap = new HashMap<>();
	private final Class<T> classT;

	@SuppressWarnings("unchecked")
	public ALocaleManager() {
		this.classT = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	@Override
	public void changeLocale() {
		componentMap.keySet().forEach(this::changeComponentLocale);
	}

	@Override
	public void addTrackedComponent(T component, String... codeArray) {
		if (!(component == null || codeArray == null || codeArray.length == 0)) {
			componentMap.put(component, codeArray);
		}
	}

	@Override
	public boolean checkComponentClass(Object component) {
		try {
			return classT.isAssignableFrom(component.getClass());
		} catch (Exception e) {
			return false;
		}
	}

	private void changeComponentLocale(T component) {
		String[] codeArray = componentMap.get(component);

		for (int i = 0; i < codeArray.length; i++) {
			String localedString = AppSettings.getLocaledItem(codeArray[i]);
			localedString = localedString == null ? "" : localedString.trim();

			if (!localedString.equals("")) {
				changeComponentLocale(component, localedString, i);
			}
		}
	}

	public abstract void changeComponentLocale(T component, String localedString, int index);

}

package com.app.colibri.interfaces;

public interface ILocaleManager<T> {

	public void changeLocale();

	public void addTrackedComponent(T component, String... codeArray);

	public boolean checkComponentClass(Object component);

}

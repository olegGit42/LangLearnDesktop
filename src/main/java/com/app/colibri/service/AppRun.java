package com.app.colibri.service;

import static com.app.colibri.service.AppSettings.jsonMapper;

import java.io.File;
import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.app.colibri.model.User;
import com.app.colibri.registry.UserDataRegistry;
import com.app.colibri.service.crypt.CryptoException;
import com.app.colibri.service.crypt.CryptoUtils;
import com.app.colibri.view.LoginFrame;

public class AppRun {

	public static ApplicationContext appContext;

	static {
		File userDataDir = new File("UserData");
		if (!userDataDir.isDirectory()) {
			userDataDir.mkdir();
		}
		appContext = new ClassPathXmlApplicationContext("appContext.xml");
		AppSettings.appSettings = appContext.getBean("appSettings", AppSettings.class);
		MainLocaleManager.mainLocaleManager = appContext.getBean("mainLocaleManager", MainLocaleManager.class);
	}

	public static void main(String[] args) {
		start();
	}

	public static void start() {

		File autoEnterUserEncryptedFile = new File("User.encrypted");
		User currentUser = AppSettings.appSettings.getUser();

		if (autoEnterUserEncryptedFile.exists()) {

			try {
				String jsonUser = CryptoUtils.decrypt(AppSettings.KEY, autoEnterUserEncryptedFile);
				currentUser = jsonMapper.readValue(jsonUser, User.class);
			} catch (IOException | CryptoException e) {
				e.printStackTrace();
			}

			AppSettings.appSettings.setUser(currentUser);

			if (currentUser.isAutoEnter()) {

				if (currentUser.getUserName().equals(User.GUEST)) {
					LoginFrame.appInit();
				} else {
					try {
						String path = "UserData/" + currentUser.getUserName() + "/Data.encrypted";
						String jsonData = CryptoUtils.decrypt(AppSettings.KEY, new File(path));
						UserDataRegistry userDataRegistryForCheck = jsonMapper.readValue(jsonData, UserDataRegistry.class);

						if (currentUser.getUserName().equals(userDataRegistryForCheck.getUserName())
								&& currentUser.getUserPasswordHash().equals(userDataRegistryForCheck.getUserPasswordHash())
								&& userDataRegistryForCheck.isAutoEnter()) {

							LoginFrame.appInit();
						} else {
							LoginFrame.launch(LoginFrame.State.LOGIN);
						}

					} catch (CryptoException | IOException e) {
						e.printStackTrace();
						LoginFrame.launch(LoginFrame.State.LOGIN);
					}
				}

			} else {
				if (currentUser.getUserName().equals(User.GUEST)) {
					LoginFrame.appInit();
				} else {
					LoginFrame.launch(LoginFrame.State.LOGIN);
				}
			}

		} else {

			try {
				String jsonUser = jsonMapper.writeValueAsString(currentUser);

				try {
					CryptoUtils.encrypt(AppSettings.KEY, jsonUser, autoEnterUserEncryptedFile);
				} catch (CryptoException ex) {
					System.err.println(ex.getMessage());
					ex.printStackTrace();
				}

				LoginFrame.appInit();

			} catch (Exception e) {
				e.printStackTrace();
				LoginFrame.launch(LoginFrame.State.LOGIN);
			}
		}
	}

}

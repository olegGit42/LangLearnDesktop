package com.app.colibri.service;

import static com.app.colibri.service.AppSettings.jsonMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
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
		// testGet();
		// testPost();

		start();
	}

	public static void testPost() {

		try {

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost postRequest = new HttpPost("http://localhost:8080/ColibriWeb/setstring");

			String one = "один";

			String jsonString = jsonMapper.writeValueAsString(new String[] { one, "two" });

			System.out.println(jsonString);

			StringEntity input = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
			input.setContentType("application/json");
			postRequest.setEntity(input);

			HttpResponse response = httpClient.execute(postRequest);

			if (response.getStatusLine().getStatusCode() != 201) {
				// throw new RuntimeException("Failed : HTTP error code : " +
				// response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void testGet() {

		try {

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet getRequest = new HttpGet("http://localhost:8080/ColibriWeb/getstring/testget");
			getRequest.addHeader("accept", "application/json");

			HttpResponse response = httpClient.execute(getRequest);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

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

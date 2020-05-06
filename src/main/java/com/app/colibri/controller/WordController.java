package com.app.colibri.controller;

import static com.app.colibri.service.AppSettings.getLocaledItem;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import com.app.colibri.model.User;
import com.app.colibri.model.Word;
import com.app.colibri.registry.UserDataRegistry;
import com.app.colibri.service.AppRun;
import com.app.colibri.service.AppSettings;
import com.app.colibri.service.crypt.CryptoException;
import com.app.colibri.service.crypt.CryptoUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WordController {
	public static List<Word> allWordsList;
	public static final AtomicInteger newId = new AtomicInteger();
	public static final String[] repeatPeriodArray;
	public static final long[] timeDeltaArray;
	public static final String[] boxPeriod;

	public static final long minute_ms = 60_000L;
	public static final long hour_ms = minute_ms * 60;
	public static final long day_ms = hour_ms * 24;
	public static final long week_ms = day_ms * 7;
	public static final long month_ms = day_ms * 30;
	public static final long month_6_ms = month_ms * 6;

	public static long minRepeatTime = Long.MAX_VALUE;
	public static long minTime;
	public static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	public static final Date date = new Date();

	public static UserDataRegistry userDataRegistry;

	static {
		final long day_delta = hour_ms * 6;

		repeatPeriodArray = new String[8];
		repeatPeriodArray[0] = "period_box_0";
		repeatPeriodArray[1] = "period_box_1";
		repeatPeriodArray[2] = "period_box_2";
		repeatPeriodArray[3] = "period_box_3";
		repeatPeriodArray[4] = "period_box_4";
		repeatPeriodArray[5] = "period_box_5";
		repeatPeriodArray[6] = "period_box_6";
		repeatPeriodArray[7] = "period_box_7";

		timeDeltaArray = new long[8];
		timeDeltaArray[0] = minute_ms * 2;
		timeDeltaArray[1] = minute_ms * 25;
		timeDeltaArray[2] = day_ms - day_delta;
		timeDeltaArray[3] = day_ms * 3 - day_delta;
		timeDeltaArray[4] = week_ms - day_delta;
		timeDeltaArray[5] = week_ms * 2 - day_delta;
		timeDeltaArray[6] = month_ms - day_delta;
		timeDeltaArray[7] = month_6_ms - day_delta;

		boxPeriod = new String[WordController.repeatPeriodArray.length];
		for (int i = 0; i < boxPeriod.length; i++) {
			boxPeriod[i] = getBoxInfo(i);
		}
	}

	public static void unserializeAllWordsMain() {
		unserializeAllWordsFromFile("UserData/" + AppSettings.appSettings.getUser().getUserName() + "/Data.encrypted");
	}

	public static void serializeAllWordsMain() {
		serializeAllWordsToFile(AppSettings.appSettings.getUser().getUserName());
	}

	private static void unserializeAllWordsFromFile(String path) {
		userDataRegistry = AppRun.appContext.getBean("userDataRegistry", UserDataRegistry.class);

		if (path.endsWith(".encrypted")) {
			ObjectMapper mapper = new ObjectMapper();

			try {
				String jsonData = CryptoUtils.decrypt(AppSettings.KEY, new File(path));
				UserDataRegistry userDataRegistryForCheck = mapper.readValue(jsonData, UserDataRegistry.class);

				User user = AppSettings.appSettings.getUser();
				if (user.getUserName().equals(userDataRegistryForCheck.getUserName())
						&& (user.getUserPasswordHash().equals(userDataRegistryForCheck.getUserPasswordHash())
								|| userDataRegistryForCheck.getUserName().equals(User.GUEST))) {

					userDataRegistry = userDataRegistryForCheck;
					userDataRegistry.getTagRegistry().restoreTagIdMap();
					AppSettings.appSettings.setAppLocale(Locale.forLanguageTag(userDataRegistry.getAppLocale()));

				} else {
					AppSettings.appSettings.setUser(AppRun.appContext.getBean("defaultUser", User.class));
				}

			} catch (Exception e) {
				System.err.println("File " + path + " not found");
			}
		}

		allWordsList = userDataRegistry.getAllUserWordsList();
		allWordsList.forEach(WordController::setMinRepTime);
		newId.set(userDataRegistry.getMaxWordID());
	}

	private static void serializeAllWordsToFile(String userName) {

		ObjectMapper mapper = new ObjectMapper();

		try {
			// for dev branch, comment out line below on release
			// mapper.writeValue(new File("Data.json"), userDataRegistry);

			String currentUserDataDirPath = "UserData/" + userName;

			File curentUserDataDir = new File(currentUserDataDirPath);
			if (!curentUserDataDir.isDirectory()) {
				curentUserDataDir.mkdir();
			}

			File dataEncryptedFile = new File(currentUserDataDirPath + "/Data.encrypted");
			File userEncryptedFile = new File(currentUserDataDirPath + "/User.encrypted");
			File autoEnterUserEncryptedFile = new File("User.encrypted");

			User currentUser = AppSettings.appSettings.getUser();
			if (currentUser.getUserName().equals(User.GUEST)) {
				currentUser.setAutoEnter(true);
			}

			userDataRegistry.setMaxWordID(newId.get());
			userDataRegistry.setUserName(currentUser.getUserName());
			userDataRegistry.setUserPasswordHash(currentUser.getUserPasswordHash());
			userDataRegistry.setAutoEnter(currentUser.isAutoEnter());

			currentUser.setAutoEnter(false);
			String jsonUser = mapper.writeValueAsString(currentUser);
			currentUser.setAutoEnter(userDataRegistry.isAutoEnter());
			String jsonAutoEnterUser = mapper.writeValueAsString(currentUser);

			String jsonData = mapper.writeValueAsString(userDataRegistry);

			try {
				CryptoUtils.encrypt(AppSettings.KEY, jsonData, dataEncryptedFile);
				CryptoUtils.encrypt(AppSettings.KEY, jsonUser, userEncryptedFile);
				if (currentUser.isAutoEnter()) {
					CryptoUtils.encrypt(AppSettings.KEY, jsonAutoEnterUser, autoEnterUserEncryptedFile);
				} else if (autoEnterUserEncryptedFile.exists()) {
					autoEnterUserEncryptedFile.delete();
				}
			} catch (CryptoException ex) {
				System.err.println(ex.getMessage());
				ex.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void createNewWordGUI(String word, String translate) {
		Word newWord = AppRun.appContext.getBean("word", Word.class);
		newWord.afterInitNewWord(word, translate);
	}

	public static long getTimeDelta(final int box) {
		if (0 <= box && box < timeDeltaArray.length) {
			return timeDeltaArray[box];
		} else {
			return 0L;
		}
	}

	public static String getRepeatPeriod(final int box) {
		if (0 <= box && box < repeatPeriodArray.length) {
			return getLocaledItem(repeatPeriodArray[box]);
		} else {
			return "";
		}
	}

	public static void setMinRepTime(final Word word) {
		final long timeDelta = getTimeDelta(word.getBox());

		if (timeDelta != 0L) {
			final long repTime = getRoundedTimeToMinute(word.getRegTime() + timeDelta);

			if (repTime < minRepeatTime) {
				minRepeatTime = repTime;
			}
		}
	}

	public static long getRoundedTimeToMinute(final long time) {
		return getRoundedTime(date, time, dateFormat);
	}

	public static long getRoundedTime(final Date date, final long time, final DateFormat dateFormat) {
		try {
			date.setTime(time);
			return dateFormat.parse(dateFormat.format(date)).getTime();
		} catch (Exception e) {
			return time;
		}
	}

	public static String getBoxInfo(Word word) {
		return getBoxInfo(word.getBox());
	}

	public static String getBoxInfo(int box) {
		return String.valueOf(box) + " | " + getLocaledItem(repeatPeriodArray[box]);
	}

}

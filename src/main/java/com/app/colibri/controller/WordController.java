package com.app.colibri.controller;

import static com.app.colibri.service.AppSettings.getLocaledItem;
import static com.app.colibri.service.AppSettings.jsonMapper;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
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

public class WordController {
	public static List<Word> allWordsList;
	public static final AtomicInteger maxWordId = new AtomicInteger();
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

	public static void serializeUserDataRegistry() {
		serializeUserDataRegistry(false);
	}

	public static void serializeUserDataRegistryWithBackup() {
		serializeUserDataRegistry(true);
	}

	public static UserDataRegistry unserializeUserDataRegistry() {
		final String path = "UserData/" + AppSettings.appSettings.getUser().getUserName() + "/Data.encrypted";

		UserDataRegistry userDataRegistry = AppRun.appContext.getBean("userDataRegistry", UserDataRegistry.class);

		if (path.endsWith(".encrypted")) {
			try {
				String jsonData = CryptoUtils.decrypt(AppSettings.KEY, new File(path));
				UserDataRegistry userDataRegistryForCheck = jsonMapper.readValue(jsonData, UserDataRegistry.class);

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

		return userDataRegistry;
	}

	public static void loadUserData(UserDataRegistry p_userDataRegistry) {
		userDataRegistry = p_userDataRegistry;
		allWordsList = userDataRegistry.getAllUserWordsList();
		allWordsList.forEach(WordController::setMinRepTime);
		maxWordId.set(userDataRegistry.getMaxWordID());
	}

	private static void serializeUserDataRegistry(boolean backup) {
		try {
			User currentUser = AppSettings.appSettings.getUser();

			String currentUserDataDirPath = "UserData/" + currentUser.getUserName();

			File curentUserDataDir = new File(currentUserDataDirPath);
			if (!curentUserDataDir.isDirectory()) {
				curentUserDataDir.mkdir();
			}

			File dataEncryptedFile = new File(currentUserDataDirPath + "/Data.encrypted");
			File userEncryptedFile = new File(currentUserDataDirPath + "/User.encrypted");
			File autoEnterUserEncryptedFile = new File("User.encrypted");

			if (currentUser.getUserName().equals(User.GUEST)) {
				currentUser.setAutoEnter(true);
			}

			userDataRegistry.setMaxWordID(maxWordId.get());
			userDataRegistry.setUserName(currentUser.getUserName());
			userDataRegistry.setUserPasswordHash(currentUser.getUserPasswordHash());
			userDataRegistry.setAutoEnter(currentUser.isAutoEnter());
			userDataRegistry.setUserId(currentUser.getId());

			currentUser.setAutoEnter(false);
			String jsonUser = jsonMapper.writeValueAsString(currentUser);
			currentUser.setAutoEnter(userDataRegistry.isAutoEnter());
			String jsonAutoEnterUser = jsonMapper.writeValueAsString(currentUser);

			String jsonData = jsonMapper.writeValueAsString(userDataRegistry);

			// for dev branch, comment out two lines below on release
			// mapper.writeValue(new File("Data.json"), userDataRegistry);
			// mapper.writeValue(new File("User.json"), currentUser);

			try {
				CryptoUtils.encrypt(AppSettings.KEY, jsonData, dataEncryptedFile);
				CryptoUtils.encrypt(AppSettings.KEY, jsonUser, userEncryptedFile);

				if (currentUser.isAutoEnter())
					CryptoUtils.encrypt(AppSettings.KEY, jsonAutoEnterUser, autoEnterUserEncryptedFile);
				else if (autoEnterUserEncryptedFile.exists())
					autoEnterUserEncryptedFile.delete();

				if (backup) {
					String currentUserDataDirBackupPath = currentUserDataDirPath + "/Backup";

					File curentUserDataDirBackup = new File(currentUserDataDirBackupPath);
					if (!curentUserDataDirBackup.isDirectory()) {
						curentUserDataDirBackup.mkdir();
					}

					Arrays.asList(curentUserDataDirBackup.listFiles()).stream().sorted(Comparator.reverseOrder()).skip(8)
							.forEach(File::delete);

					Date date = new Date(System.currentTimeMillis());
					DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
					final String dateTime = dateFormat.format(date);

					File dataEncryptedFileBackup = new File(currentUserDataDirBackupPath + "/" + dateTime + "-Data.encrypted");
					File userEncryptedFileBackup = new File(currentUserDataDirBackupPath + "/" + dateTime + "-User.encrypted");

					CryptoUtils.encrypt(AppSettings.KEY, jsonData, dataEncryptedFileBackup);
					CryptoUtils.encrypt(AppSettings.KEY, jsonUser, userEncryptedFileBackup);
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

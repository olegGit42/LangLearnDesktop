package com.app.colibri.controller;

import static com.app.colibri.service.AppSettings.getLocaledItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import com.app.colibri.model.Word;
import com.app.colibri.registry.TagRegistry;
import com.app.colibri.registry.UserDataRegistry;
import com.app.colibri.service.AppRun;
import com.app.colibri.service.AppSettings;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WordController {
	private static int maxId;

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
		unserializeAllWordsFromFile("words.json"); // main
		// unserializeAllWordsFromFile("words.bin"); // for compatibility
	}

	public static void serializeAllWordsMain() {
		serializeAllWordsToFile("words.json"); // main
		// serializeAllWordsToFile("words.bin"); // for compatibility
	}

	public static void serializeAllWordsCopy() {
		serializeAllWordsToFile("wordsCopy.json"); // main
		// serializeAllWordsToFile("wordsCopy.bin"); // for compatibility
	}

	@SuppressWarnings("unchecked")
	private static void unserializeAllWordsFromFile(String path) {
		userDataRegistry = AppRun.appContext.getBean("userDataRegistry", UserDataRegistry.class);

		if (path.endsWith(".bin")) {
			try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(path))) {
				userDataRegistry.setAllUserWordsList((List<Word>) objectInputStream.readObject());
			} catch (Exception e) {
				System.err.println("File " + path + " not found");
			}
		} else if (path.endsWith(".json")) {
			ObjectMapper mapper = new ObjectMapper();

			// JSON file to Java object
			try {
				userDataRegistry = mapper.readValue(new File(path), UserDataRegistry.class);
				if (userDataRegistry.getTagRegistry() == null) { // for compatibility
					userDataRegistry.setTagRegistry(AppRun.appContext.getBean("tagRegistry", TagRegistry.class));
				}
				userDataRegistry.getTagRegistry().restoreTagIdMap();
				AppSettings.appSettings.setAppLocale(Locale.forLanguageTag(userDataRegistry.getAppLocale()));
			} catch (Exception e) {
				System.err.println("File " + path + " not found");
			}
		}

		allWordsList = userDataRegistry.getAllUserWordsList();

		maxId = 0;

		allWordsList.forEach(word -> {
			maxId = word.getId() > maxId ? word.getId() : maxId;
			setMinRepTime(word);
		});

		newId.set(maxId);
	}

	private static void serializeAllWordsToFile(String path) {
		if (path.endsWith(".bin")) {
			try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(path))) {
				objectOutputStream.writeObject(allWordsList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (path.endsWith(".json")) {
			ObjectMapper mapper = new ObjectMapper();

			// Java object to JSON file
			try {
				mapper.writeValue(new File(path), userDataRegistry);
			} catch (Exception e) {
				e.printStackTrace();
			}
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

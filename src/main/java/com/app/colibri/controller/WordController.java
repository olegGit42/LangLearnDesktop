package com.app.colibri.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.app.colibri.model.Word;
import com.app.colibri.registry.UserWordRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WordController {
	public static int maxId;

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

	public static UserWordRegistry userWordRegistry;

	static {
		final long day_delta = hour_ms * 6;

		repeatPeriodArray = new String[8];
		repeatPeriodArray[0] = "2 min";
		repeatPeriodArray[1] = "25 min";
		repeatPeriodArray[2] = "day";
		repeatPeriodArray[3] = "3 days";
		repeatPeriodArray[4] = "week";
		repeatPeriodArray[5] = "2 weeks";
		repeatPeriodArray[6] = "month";
		repeatPeriodArray[7] = "Archive";

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

	public static List<Word> unserializeAllWordsMain() {
		// return unserializeAllWordsFromFile("words.json"); // main
		return unserializeAllWordsFromFile("words.bin"); // for compatibility
	}

	public static void serializeAllWordsMain() {
		serializeAllWordsToFile("words.json"); // main
		serializeAllWordsToFile("words.bin"); // for compatibility
	}

	public static void serializeAllWordsCopy() {
		serializeAllWordsToFile("wordsCopy.json"); // main
		serializeAllWordsToFile("wordsCopy.bin"); // for compatibility
	}

	@SuppressWarnings("unchecked")
	private static List<Word> unserializeAllWordsFromFile(String path) {
		List<Word> allWordsList = new ArrayList<>();
		WordController.userWordRegistry = new UserWordRegistry();
		WordController.userWordRegistry.setAllUserWordsList(allWordsList);

		if (path.endsWith(".bin")) {
			try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(path))) {
				WordController.userWordRegistry.setAllUserWordsList((List<Word>) objectInputStream.readObject());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (path.endsWith(".json")) {
			ObjectMapper mapper = new ObjectMapper();

			// JSON file to Java object
			try {
				WordController.userWordRegistry = mapper.readValue(new File(path), UserWordRegistry.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		allWordsList = WordController.userWordRegistry.getAllUserWordsList();
		WordController.allWordsList = allWordsList;

		maxId = 0;
		if (allWordsList.size() > 0) {
			allWordsList.forEach(word -> {
				maxId = word.getId() > maxId ? word.getId() : maxId;
				WordController.setMinRepTime(word);
			});
		}
		newId.set(maxId);

		return allWordsList;
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
				mapper.writeValue(new File(path), userWordRegistry);
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
			return repeatPeriodArray[box];
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
		return String.valueOf(box) + " | " + WordController.repeatPeriodArray[box];
	}

}

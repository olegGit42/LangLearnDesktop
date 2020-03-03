package com.app.colibri.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.app.colibri.model.Word;

public class WordController {
	public static int maxId;

	public static List<Word> allWordsList;
	public static AtomicInteger newId = new AtomicInteger();
	public static String[] repeatPeriod;

	public static long minute_ms = 60_000L;
	public static long hour_ms = minute_ms * 60;
	public static long day_ms = hour_ms * 24;
	public static long week_ms = day_ms * 7;
	public static long month_ms = day_ms * 30;
	public static long month_6_ms = month_ms * 6;

	public static long minRepeateTime = Long.MAX_VALUE;

	static {
		repeatPeriod = new String[8];
		repeatPeriod[0] = "2 min";
		repeatPeriod[1] = "20 min";
		repeatPeriod[2] = "hour";
		repeatPeriod[3] = "day";
		repeatPeriod[4] = "week";
		repeatPeriod[5] = "month";
		repeatPeriod[6] = "6 month";
		repeatPeriod[7] = "Archive";
	}

	@SuppressWarnings("unchecked")
	public static List<Word> unserializeAllWordsFromFile(String path) {
		List<Word> allWordsList = new ArrayList<>();

		try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(path))) {
			allWordsList = (List<Word>) objectInputStream.readObject();
		} catch (Exception e) {
			System.err.println("Ошибка в методе unserializeAllWordsFromFile(String path : " + path + ") класса "
					+ WordController.class.getName());
			e.printStackTrace();
		}

		maxId = 0;
		if (allWordsList.size() > 0) {
			allWordsList.forEach(word -> {
				maxId = word.getId() > maxId ? word.getId() : maxId;
				WordController.setMinRepTime(word);
				restoreCreationTime(word);
			});
		}
		newId.set(maxId);

		return allWordsList;
	}

	public static void serializeAllWordsToFile(String path) {
		try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(path))) {
			objectOutputStream.writeObject(allWordsList);
		} catch (Exception e) {
			System.err.println("Ошибка в методе serializeAllWordsToFile(String path : " + path + ") класса "
					+ WordController.class.getName());
			e.printStackTrace();
		}
	}

	public static void createNewWordGUI(String word, String translate) {
		Word newWord = AppRun.appContext.getBean("word", Word.class);
		newWord.afterInitNewWord(word, translate);
	}

	public static long getTimeDelta(final int box) {
		switch (box) {
		case 0:
			return WordController.minute_ms * 2;
		case 1:
			return WordController.minute_ms * 20;
		case 2:
			return WordController.hour_ms;
		case 3:
			return WordController.hour_ms * 18;
		case 4:
			return WordController.week_ms - (WordController.day_ms / 2);
		case 5:
			return WordController.month_ms - (WordController.day_ms / 2);
		case 6:
			return WordController.month_6_ms - (WordController.day_ms / 2);
		case 7:
			return WordController.month_6_ms * 2 - (WordController.day_ms / 2);
		default:
			return 0L;
		}
	}

	public static void setMinRepTime(final Word word) {
		final long timeDelta = getTimeDelta(word.getBox());

		if (timeDelta != 0L) {
			final long repTime = getRoundedTime(word.getRegTime() + timeDelta);

			if (repTime < minRepeateTime) {
				minRepeateTime = repTime;
			}
		}
	}

	public static long getRoundedTime(final long time) {
		return (time / 100_000) * 100_000;
	}

	public static void restoreCreationTime(final Word word) {
		if (word.getCreationTime() == 0) {
			long sum = 0;
			for (int i = 0; i < word.getBox(); i++) {
				sum += getTimeDelta(i);
			}

			word.setCreationTime(word.getRegTime() - sum);
			// System.out.println(new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new
			// Date(word.getCreationTime())));
		}
	}

}

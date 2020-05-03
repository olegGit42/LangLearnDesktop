package com.app.colibri.service;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class NotificationSound {

	public static void beep() {
		new Thread(() -> createTone(500, 20)).start();
	}

	private static void createTone(int Hertz, int volume) {
		float rate = 5000;
		byte[] buf;
		AudioFormat audioF;

		buf = new byte[1];
		audioF = new AudioFormat(rate, 8, 1, true, false);

		try { /** Exception is thrown when line cannot be opened */
			SourceDataLine sourceDL = AudioSystem.getSourceDataLine(audioF);
			sourceDL.open(audioF);
			sourceDL.start();

			for (int i = 0; i < rate; i++) {
				double angle = (i / rate) * Hertz * 2.0 * Math.PI;
				buf[0] = (byte) (Math.sin(angle) * volume);
				sourceDL.write(buf, 0, 1);
			}

			sourceDL.drain();
			sourceDL.stop();
			sourceDL.close();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

}
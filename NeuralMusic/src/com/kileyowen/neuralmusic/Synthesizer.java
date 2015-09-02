package com.kileyowen.neuralmusic;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Synthesizer {

	public Synthesizer() {
		AudioFormat af = new AudioFormat(44100, 16, 2, true, false);
		try {
			SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
			sdl.open();
			sdl.start();
			ByteBuffer bb = ByteBuffer.allocate(4);
			for (int i = 0; i < af.getFrameRate(); i++) {
				
				float frequency = 1;
				short testSound = (short)((Math.sin(i * frequency / af.getFrameRate() * Math.PI * 2)) * Short.MAX_VALUE / 2 + Short.MAX_VALUE / 2);
				System.out.println(testSound + " " + Short.MAX_VALUE);
				bb.putShort(0, (short)(testSound)); // left
				bb.putShort(2, (short)(testSound)); // right
				sdl.write(bb.array(), 0, 4);
			}
			sdl.drain();
			sdl.close();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Synthesizer();
	}

}

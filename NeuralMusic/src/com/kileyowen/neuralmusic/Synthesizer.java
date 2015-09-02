package com.kileyowen.neuralmusic;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Synthesizer {
	private AudioFormat af;
	private SourceDataLine sdl;
	private ByteBuffer bb;
	private final int AUDIO_RANGE;

	public Synthesizer() {
		af = new AudioFormat(44100, 16, 2, true, false);
		AUDIO_RANGE = (int) (Math.pow(2, af.getSampleSizeInBits()) - 1);
		try {
			sdl = AudioSystem.getSourceDataLine(af);
			sdl.open();
			sdl.start();
			bb = ByteBuffer.allocate(4);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void play(double sample) {
		sample = Math.min(Math.max(sample, -1.0), 1.0);
		short sampleShort = (short) (sample * AUDIO_RANGE);
		for (int byteIndex = 0; byteIndex < af.getChannels(); byteIndex++) {
			bb.put(byteIndex * 2 + 0, (byte) (sampleShort >> 0));
			bb.put(byteIndex * 2 + 1, (byte) (sampleShort >> 8));
		}
		sdl.write(bb.array(), 0, bb.capacity());
	}
	
	public void play(double[] samples){
		for(int sampleIndex = 0;sampleIndex < samples.length;sampleIndex++){
			play(samples[sampleIndex]);
		}
	}
	
	public void playNeural(double sampleNeural){
		play(sampleNeural * 2 - 1);
	}
	
	public void playNeural(double[] samplesNeural){
		for(int sampleIndex = 0;sampleIndex < samplesNeural.length;sampleIndex++){
			playNeural(samplesNeural[sampleIndex]);
		}
	}

	public void stop() {
		sdl.drain();
		sdl.close();
	}

	public static void main(String[] args) {
		Synthesizer synth = new Synthesizer();
		
		synth.stop();
	}
}
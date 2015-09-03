package com.kileyowen.neuralmusic;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Synthesizer {
	private AudioFormat af;
	private SourceDataLine sdl;
	private ByteBuffer bb;
	private final short AUDIO_RANGE;
	private double minFreq, maxFreq;
	private double maxDur;
	private double minDur;

	public Synthesizer(double minFreq, double maxFreq) {
		this.minFreq = minFreq;
		this.maxFreq = maxFreq;
		af = new AudioFormat(44100, 16, 2, true, false);
		this.maxDur = af.getFrameRate();
		this.minDur = af.getFrameRate() / 100;
		AUDIO_RANGE = Short.MAX_VALUE;
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

	public void play(double[] samples) {
		for (int sampleIndex = 0; sampleIndex < samples.length; sampleIndex++) {
			play(samples[sampleIndex]);
		}
	}

	public void playNote(double frequencySample, double duration) {
		double frequency = frequencySample * (maxFreq - minFreq) + minFreq;
		for (int sampleIndex = 0; sampleIndex < duration; sampleIndex++) {
			play(Math.sin(sampleIndex * frequency / af.getFrameRate() * 2
					* Math.PI));
		}
	}

	public void playNote(double[] frequencySamples, double[] durations) {
		for (int sampleIndex = 0; sampleIndex < frequencySamples.length; sampleIndex++) {
			playNote(frequencySamples[sampleIndex], durations[sampleIndex]);
		}
	}

	public void playNotesNeural(List<Double> neuralOutput) {
		double[] freqs = new double[neuralOutput.size() / 2];
		double[] durs = freqs.clone();
		for (int i = 0; i < freqs.length; i++) {
			freqs[i] = neuralToSample(neuralOutput.get(i * 2));
			durs[i] = neuralToDur(neuralOutput.get(i * 2 + 1));
		}
		playNote(freqs, durs);
	}

	public double neuralToDur(double neural) {
		return neural * (maxDur - minDur) + minDur;
	}

	public double neuralToSample(double neural) {
		return neural * 2 - 1;
	}

	public double[] neuralToSample(double[] neural) {
		double[] copy = new double[neural.length];
		for (int sampleIndex = 0; sampleIndex < copy.length; sampleIndex++) {
			copy[sampleIndex] = neuralToSample(neural[sampleIndex]);
		}
		return copy;
	}

	public void stop() {
		sdl.drain();
		sdl.close();
	}

	public static void main(String[] args) {
		int noteCount = 1, numLayers = 3;
		Scanner scan = new Scanner(System.in);
		Synthesizer synth = new Synthesizer(20, 4400);
		NeuralRecorder recorder = new NeuralRecorder("assets/" + noteCount
				+ "/", "neural.dat");
		NeuralNetwork network;
		if ((network = recorder.readNeural()) == null) {
			network = new NeuralNetwork(2, (int) (noteCount * 2), numLayers, -1, 1);
		}
		List<Double> input = new ArrayList<Double>();
		input.add(.5);
		input.add(1d);
		do {
			List<Double> result = network.fire(input);
			synth.playNotesNeural(result);
			recorder.writeNeural(network);
			System.out.println("Enter 0 to exit");
		} while (scan.nextInt() != 0);
		synth.stop();
		scan.close();
	}
}
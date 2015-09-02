package com.kileyowen.neuralmusic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NeuralRecorder {
	private File file;

	@SuppressWarnings("unused")
	private static final int NUMBER_OF_INPUTS = 0, LAYERS_OF_NEURONS = 1, NEURONS_IN_LAYER = 2, WEIGHTS_IN_NEURON = 3,
			NEURON_WEIGHTS = 4;

	public NeuralRecorder(String folder, String fileName) {
		file = new File(folder);
		file.mkdirs();
		file = new File(folder + fileName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean writeNeural(NeuralNetwork network) {
		try {
			DataOutputStream writer = new DataOutputStream(new FileOutputStream(file));
			int layers = network.getLayerCount();
			writer.writeInt(network.getNumInputs());
			writer.writeInt(layers);
			for (int layerIndex = 0; layerIndex < layers; layerIndex++) {
				NeuralLayer layer = network.getLayer(layerIndex);
				int neurons = layer.getNeuronCount();
				writer.writeInt(neurons);
				for (int neuronIndex = 0; neuronIndex < neurons; neuronIndex++) {
					Neuron neuron = layer.getNeuron(neuronIndex);
					int weights = neuron.getWeightCount();
					writer.writeInt(weights);
					List<Double> weightList = neuron.getAllWeights();
					for (int weightIndex = 0; weightIndex < weights; weightIndex++) {
						writer.writeDouble(weightList.get(weightIndex));
					}
				}
			}
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public NeuralNetwork readNeural() {
		try {
			DataInputStream reader = new DataInputStream(new FileInputStream(file));
			int numInputs = reader.readInt();
			int lastNeuronCount = numInputs;
			int layerCount = reader.readInt();
			List<NeuralLayer> layers = new ArrayList<NeuralLayer>();
			for (int layerIndex = 0; layerIndex < layerCount; layerIndex++) {
				int neuronCount = reader.readInt();
				List<Neuron> neurons = new ArrayList<Neuron>();
				for (int neuronIndex = 0; neuronIndex < neuronCount; neuronIndex++) {
					int weightCount = reader.readInt();
					List<Double> weights = new ArrayList<Double>();
					for (int weightIndex = 0; weightIndex < weightCount; weightIndex++) {
						weights.add(reader.readDouble());
					}
					neurons.add(new Neuron(lastNeuronCount, weights));
				}
				layers.add(new NeuralLayer(neurons));
				lastNeuronCount = neuronCount;
			}
			reader.close();
			return new NeuralNetwork(numInputs, layers);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}

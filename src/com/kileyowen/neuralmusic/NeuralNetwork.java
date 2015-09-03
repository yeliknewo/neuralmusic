package com.kileyowen.neuralmusic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NeuralNetwork {
	private List<NeuralLayer> layers;
	public static Random rand;
	private int numInputs;
	private int numOutputs;

	public NeuralNetwork(int numInputs, int numOutputs, int numLayers,
			double minWeight, double maxWeight) {
		this.numInputs = numInputs;
		this.numOutputs = numOutputs;
		rand = new Random();
		layers = new ArrayList<NeuralLayer>();
		double layerMod = ((double) numInputs - (double) numOutputs)
				/ (double) numLayers, layerSize = numInputs, currentInputs = numInputs;
		while (layers.size() < numLayers) {
			layerSize -= layerMod;
			layers.add(new NeuralLayer((int) Math.round(currentInputs),
					(int) Math.round(layerSize), minWeight, maxWeight));
			currentInputs = layerSize;
		}
	}

	public NeuralNetwork(int numInputs, List<NeuralLayer> layers) {
		this.numInputs = numInputs;
		this.layers = layers;
	}
	
	public NeuralNetwork clone() {
		NeuralNetwork copy = cloneWeightless();
		copy.setAllNeuronWeights(getAllNeuronWeights());
		return copy;
	}

	public NeuralNetwork cloneWeightless() {
		NeuralNetwork copy = new NeuralNetwork(numInputs, numOutputs,
				getLayerCount(), 0, 0);
		return copy;
	}

	public List<Double> fire(List<Double> input) {
		for (int i = 0; i < layers.size(); i++) {
			input = layers.get(i).fire(input);
		}
		return input;
	}

	public List<Double> getAllNeuronWeights() {
		List<Double> weights = new ArrayList<Double>();
		for (int i = 0; i < layers.size(); i++) {
			weights.addAll(layers.get(i).getAllNeuronWeights());
		}
		return weights;
	}

	public void setAllNeuronWeights(List<Double> list) {
		int weightCount = 0;
		for (int i = 0; i < layers.size(); i++) {
			NeuralLayer layer = layers.get(i);
			int layerWeightCount = layer.getWeightCount();
			layer.setAllNeuronWeights(list.subList(weightCount, weightCount
					+ layerWeightCount));
			weightCount += layerWeightCount;
		}
	}

	public int getWeightCount() {
		int weightCount = 0;
		for (int i = 0; i < layers.size(); i++) {
			weightCount += layers.get(i).getWeightCount();
		}
		return weightCount;
	}

	public int getLayerCount() {
		return layers.size();
	}

	public NeuralLayer getLayer(int layerIndex) {
		return layers.get(layerIndex);
	}

	public int getNumInputs() {
		return numInputs;
	}
}

package com.kileyowen.neuralmusic;

import java.util.ArrayList;
import java.util.List;

public class NeuralLayer {

	private List<Neuron> neurons;

	public NeuralLayer(int numInputs, int numNeurons, double minWeight, double maxWeight) {
		neurons = new ArrayList<Neuron>();
		while (neurons.size() < numNeurons) {
			neurons.add(new Neuron(numInputs, minWeight, maxWeight));
		}
	}

	public NeuralLayer(List<Neuron> neurons) {
		this.neurons = neurons;
	}

	public List<Double> fire(List<Double> input) {
		List<Double> activations = new ArrayList<Double>();
		for (int i = 0; i < neurons.size(); i++) {
			activations.add(neurons.get(i).fire(input));
		}
		return activations;
	}

	public List<Double> getAllNeuronWeights() {
		List<Double> weights = new ArrayList<Double>();
		for (int i = 0; i < neurons.size(); i++) {
			weights.addAll(neurons.get(i).getAllWeights());
		}
		return weights;
	}

	public void setAllNeuronWeights(List<Double> list) {
		int weightCount = 0;
		for (int i = 0; i < neurons.size(); i++) {
			Neuron neuron = neurons.get(i);
			int neuronWeightCount = neuron.getWeightCount();
			neuron.setAllWeights(list.subList(weightCount, weightCount + neuronWeightCount));
			weightCount += neuronWeightCount;
		}
	}

	public int getWeightCount() {
		int weightCount = 0;
		for (int i = 0; i < neurons.size(); i++) {
			weightCount += neurons.get(i).getWeightCount();
		}
		return weightCount;
	}

	public int getNeuronCount() {
		return neurons.size();
	}

	public Neuron getNeuron(int neuronIndex) {
		return neurons.get(neuronIndex);
	}
}

package com.jeromewagener.network;

import java.util.Map;
import java.util.Objects;

public class Neuron {
    String label = "";
    NeuronType neuronType;

    // All except output layers
    Map<Neuron, Double> links;

    // Only for Input
    public Double value = 0.0;

    public Neuron(String label, NeuronType neuronType, Map<Neuron, Double> links) {
        this.label = label;
        this.neuronType = neuronType;
        this.links = links;
    }

    public void calculateWeightedSum(Double bias, Map<Neuron, Double> links) {
        double weightedSum = 0;

        for (Map.Entry<Neuron, Double> link : links.entrySet()) {
            weightedSum += link.getKey().value * link.getValue();
        }

        weightedSum -= bias;

        this.value = sigmoid(weightedSum);
        //System.out.println(" >> " + this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Neuron neuron = (Neuron) o;
        return Objects.equals(label, neuron.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }

    private static double sigmoid(double x)
    {
        return 1 / (1 + Math.exp(-x));
    }
}

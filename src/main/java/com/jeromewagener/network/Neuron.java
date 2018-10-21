package com.jeromewagener.network;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Objects;

public class Neuron {
    String label;

    /* Activation bias for the neuron used during the weighted sum calculation. Not used by input neurons */
    Float bias = null;

    DecimalFormat df = new DecimalFormat("#.0000");

    // Not used by output neurons
    Map<Neuron, Float> links;

    // Used by all neuron types. For inputs, the value is the input (e.g. the average pixel value of an input image)
    // For hidden layer and output neurons, the value is the result of the weighted sum calculation
    Float value = 0.0f;

    Neuron(String label, Map<Neuron, Float> links, Float bias) {
        this.label = label;
        this.links = links;
        this.bias = bias;
    }

    void calculateWeightedSum(Map<Neuron, Float> links) {
        float weightedSum = 0;

        for (Map.Entry<Neuron, Float> link : links.entrySet()) {
            weightedSum += link.getKey().value * link.getValue();
        }

        weightedSum -= bias;

        this.value = sigmoid(weightedSum);
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

    public static float sigmoid(float x) {
        return (float) (1 / (1 + Math.exp(-x)));
    }
}
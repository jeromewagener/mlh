package com.jeromewagener.network;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

public class Genetics {
    public static Network breed(String networkName, Network network1, Network network2) throws IOException {
        Network child = new Network(networkName, null);

        // Make a copy of the first network's neurons
        StringBuilder newNetwork = new StringBuilder();
        String[] network1AsString = network1.printNetwork(false).split("\n");
        String[] network2AsString = network2.printNetwork(false).split("\n");

        int min = (new Random()).nextInt(network1AsString.length);
        int max = (new Random()).nextInt(network1AsString.length);
        if (min>max) {
            int tmp = max;
            max = min;
            min = tmp;
        }

        for (int i=0; i<min; i++) {
            newNetwork.append(network1AsString[i]).append("\n");
        }
        for (int i=min; i<max; i++) {
            newNetwork.append(network2AsString[i]).append("\n");
        }
        for (int i=max; i<network1AsString.length; i++) {
            newNetwork.append(network1AsString[i]).append("\n");
        }

        child.initializeFromString(newNetwork.toString());

        return child;
    }

    public static void mutate(Network network) {
        double mutationFactor = ((new Random()).nextDouble() - 0.5d) / 10.0d;

        for (Map.Entry<String, Neuron> neuron : network.getNeurons().entrySet()) {
            neuron.getValue().value = (neuron.getValue().value - mutationFactor);
            network.setHiddenLayerBias((network.getHiddenLayerBias() - mutationFactor));
            network.setOutputLayerBias((network.getOutputLayerBias() - mutationFactor));

            for (Map.Entry<Neuron, Double> entry : neuron.getValue().links.entrySet()) {
                entry.setValue(entry.getValue() - mutationFactor);
            }
        }
    }
}

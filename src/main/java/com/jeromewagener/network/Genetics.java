package com.jeromewagener.network;

import java.io.IOException;
import java.util.*;

public class Genetics {
    private static Network breed(String networkName, Network network1, Network network2) throws IOException {
        Network child = new Network(networkName);

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

    private static void mutate(Network network) {
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

    public static void evolve(int generation, ArrayList<Network> population, Random random) throws IOException {
        HashSet<Network> popSet = new HashSet<>(population);
        population.clear();
        population.addAll(popSet);

        // order population
        Collections.sort(population);

        if (generation == 1 || generation % 10 == 0) {
            for (Network network : population) {
                System.out.println(network.getName() + " >> Success Rate: " + network.getSuccessRate() + "% >> Avg. Certainty: " + network.getCertainty());
            }
            System.out.println();
        }

        // drop all low performers
        int populationSize = population.size();
        for (int i=10; i<populationSize; i++) {
            population.remove(population.size()-1);
        }

        // add fresh blood
        for (int i=0; i<10; i++) {
            population.add(new Network("G'"+ generation + "-" + i, random));
        }

        // breed
        for (int i=0; i<10; i+=2) {
            Network network = Genetics.breed("G"+ generation + "-" + i, population.get(i), population.get(i+1));
            Genetics.mutate(network);
            population.add(network);
        }
        for (int i=10; i<20; i++) {
            Network network = Genetics.breed("G"+ generation + "-" + i, population.get(i-10), population.get(i));
            Genetics.mutate(network);
            population.add(network);
        }
    }
}

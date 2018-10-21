package com.jeromewagener.network;

import com.jeromewagener.TrainerThread;

import java.io.IOException;
import java.util.*;

public class Genetics {
    private static Network breed(String networkName, Network network1, Network network2) throws IOException {
        Network child = new Network(networkName);

        // Make a copy of the first network's neurons
        StringBuilder newNetwork = new StringBuilder();
        String[] network1AsString = network1.printNetwork(false).split("\n");
        String[] network2AsString = network2.printNetwork(false).split("\n");

        int min = -1;
        int max = -1;
        do {
            min = (new Random()).nextInt(network1AsString.length);
            max = (new Random()).nextInt(network1AsString.length);
        } while (min == max);

        if (min>max) {
            int tmp = max;
            max = min;
            min = tmp;
        }

        //System.err.println("network1 " + network1.printNetwork(false).hashCode() + " network2 " + network2.printNetwork(false).hashCode());
        //System.err.println("max " + max + " min " + min);

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
        /*double mutationFactor = ((new Random()).nextDouble() - 0.5d) / 10.0d;

        for (Map.Entry<String, Neuron> neuron : network.getNeurons().entrySet()) {
            neuron.getValue().value = (neuron.getValue().value - mutationFactor);
            network.setHiddenLayerBias((network.getHiddenLayerBias() - mutationFactor));
            network.setOutputLayerBias((network.getOutputLayerBias() - mutationFactor));

            for (Map.Entry<Neuron, Double> entry : neuron.getValue().links.entrySet()) {
                entry.setValue(entry.getValue() - mutationFactor);
            }
        }*/

        // TODO
        Random generator = new Random();
        Object[] values = network.getNeurons().keySet().toArray();
        Object randomKey = values[generator.nextInt(values.length)];

        if (generator.nextBoolean() && network.getNeurons().get(randomKey).bias != null) {
            float biasFactor = ((new Random()).nextFloat() - 0.5f) / 10f;
            network.getNeurons().get(randomKey).bias += biasFactor;
        } else if (network.getNeurons().get(randomKey).links != null) {
            //network.getNeurons().get(randomKey).value *= adaptionFactor;

            float adaptionFactor = ((new Random()).nextFloat() - 0.5f) / 10f;
            Map<Neuron, Float> links = network.getNeurons().get(randomKey).links;
            Object[] linkKeys = links.keySet().toArray();
            Object randomLinkKey = linkKeys[generator.nextInt(linkKeys.length)];

            float newValue = links.get(randomLinkKey).floatValue() + adaptionFactor;
            if (newValue > 1) {
                newValue = 1.0f;
            } else if (newValue < 0) {
                newValue = 0.0f;
            }

            links.put((Neuron) randomLinkKey, newValue);
        }
        /*if (links != null) {
            for (Map.Entry<Neuron, Float> entry : links.entrySet()) {
                entry.setValue(entry.getValue() * ((new Random()).nextFloat() - 0.5f) / 10f);
            }
        }*/
    }

    public static void evolve(int generation, ArrayList<Network> population, Random random) throws IOException {


        // order population

        int halfPopulationSize = Math.round(TrainerThread.MAX_POPULATION_SIZE / 2.0f);
        int quarterPopulationSize = Math.round(TrainerThread.MAX_POPULATION_SIZE / 4.0f);

        // drop all low performers
        int populationSize = population.size();
        for (int i = halfPopulationSize; i<populationSize; i++) {
            population.remove(population.size()-1);
        }

        // add fresh blood
        // for (int i=0; i<quarterPopulationSize; i++) {
        //     population.add(new Network("G'"+ generation + "-Fresh-" + i, random));
        // }

        // breed
        for (int i=0; i<6; i++) {
            Network network = Genetics.breed("G"+ generation + "-Breed-" + i, population.get(random.nextInt(population.size())), population.get(random.nextInt(population.size())));
            Genetics.mutate(network);
            population.add(network);
        }
//        for (int i=1; i<6; i++) {
//            Network network = Genetics.breed("G"+ generation + "-" + i, population.get(i-10), population.get(i));
//            Genetics.mutate(network);
//            population.add(network);
//        }

//        HashSet<Network> popSet = new HashSet<>(population);
//        population.clear();
//        population.addAll(popSet);
    }
}

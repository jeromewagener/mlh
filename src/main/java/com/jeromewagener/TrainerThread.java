package com.jeromewagener;

import com.jeromewagener.network.Genetics;
import com.jeromewagener.network.Network;
import com.jeromewagener.util.Evaluator;
import com.jeromewagener.util.ImageCompressor;
import com.jeromewagener.util.TrainingData;
import lombok.Getter;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

@Getter
public class TrainerThread extends Thread {

    public static final int MAX_POPULATION_SIZE = 12;
    static int MAX_GENERATIONS_COUNT = 2000;

    private String winnerNetwork;
    private ArrayList<Network> population;

    public TrainerThread(String name) {
        super(name);
    }

    public TrainerThread(String name, ArrayList<String> population) {
        super(name);

        this.population = new ArrayList<>();
        for (int i=0; i<population.size(); i++) {
            Network network = new Network(name + "-NN-" + i);
            network.initializeFromString(population.get(i));
            this.population.add(network);
        }
    }

    @Override
    public void run() {
        Random random = new SecureRandom();

        TrainingData trainingData = new TrainingData();
        trainingData.load();

        // Create a random population if we do not have a population to start with
        if (population == null) {
            population = new ArrayList<>();

            for (int i = 0; i < MAX_POPULATION_SIZE; i++) {
                Network network = new Network("NN-" + i, random);
                population.add(network);
            }
        }

        // Let evolution do its magic
        for (int generation = 1; generation <= MAX_GENERATIONS_COUNT; generation++) {


            for (Network network : population) {
                int successCounter = 0;
                float successCertainty = 0.0f;

                for (Map.Entry<String, Integer> entry : trainingData.get().entrySet()) {
                    Evaluator evaluator = new Evaluator();
                    try {
                        evaluator.evaluate(
                                entry.getKey(),
                                entry.getValue(),
                                network);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (evaluator.isEvaluatedAsCorrect()) {
                        successCounter++;
                        successCertainty += evaluator.getCertainty();
                    }
                }

                network.setSuccessRate((successCounter / (trainingData.get().size() * 1f)) * 100f);
                network.setCertainty(successCertainty);
            }

            Collections.sort(population);
            //Collections.reverse(population);

            try {
                ImageCompressor imageCompressor = new ImageCompressor(false);
                population.get(0).calculate(imageCompressor.compress(trainingData.get().keySet().iterator().next()));
                population.get(0).printNetwork(true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //if (generation == 1 || generation % 10 == 0 || generation == MAX_GENERATIONS_COUNT) {
                System.out.println("--------------------------");
                System.out.println(" >> Current Generation: " + generation);
                System.out.println("--------------------------");

                for (Network network : population) {
                    try {
                        System.out.println(network.getName() + " >> Success Rate: " + network.getSuccessRate() + "% >> Avg. Certainty: " + network.getCertainty() + "% >> HashCode: " + network.printNetwork(false).hashCode());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println();
            //}

            try {
                Genetics.evolve(generation, population, random);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // At the end, we sort by best to worst network from the latest population and we print the best network to a file
        Collections.sort(population);
        //Collections.reverse(population);
        try {
            winnerNetwork = population.get(0).printNetwork(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

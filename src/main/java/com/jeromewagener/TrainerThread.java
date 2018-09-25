package com.jeromewagener;

import com.jeromewagener.network.Genetics;
import com.jeromewagener.network.Network;
import com.jeromewagener.util.Evaluator;
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

    static int MAX_GENERATIONS_COUNT = 50;

    private String winnerNetwork;
    private ArrayList<Network> population;

    public TrainerThread(String name) {
        super(name);
    }

    public TrainerThread(String name, ArrayList<Network> population) {
        super(name);
        this.population = population;

    }

    @Override
    public void run() {
        Random random = new SecureRandom();

        TrainingData trainingData = new TrainingData();
        trainingData.load();

        // Create a random population if we do not have a population to start with
        if (population == null) {
            population = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                Network network = new Network("NN-" + i, random);
                population.add(network);
            }
        }

        // Let evolution do its magic
        for (int generation = 1; generation <= MAX_GENERATIONS_COUNT; generation++) {
            if (generation == 1 || generation % 10 == 0 || generation == MAX_GENERATIONS_COUNT) {
                System.out.println("--------------------------");
                System.out.println(" >> Start Generation: " + generation);
                System.out.println("--------------------------");
            }

            for (Network network : population) {
                int successCounter = 0;
                double successCertainty = 0.0d;

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

                network.setSuccessRate((successCounter / (trainingData.get().size() * 1d)) * 100d);
                network.setCertainty(successCertainty / (trainingData.get().size() * 1d));
            }

            try {
                Genetics.evolve(generation, population, random);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // At the end, we sort by best to worst network from the latest population and we print the best network to a file
        Collections.sort(population);
        try {
            winnerNetwork = population.get(0).printNetwork(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

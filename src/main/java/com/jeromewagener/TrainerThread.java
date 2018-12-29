package com.jeromewagener;

import com.jeromewagener.network.Evaluator;
import com.jeromewagener.network.Genetics;
import com.jeromewagener.network.Network;
import com.jeromewagener.util.TrainingData;
import lombok.Getter;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Logger;

@Getter
public class TrainerThread extends Thread {
    Logger LOGGER;

    private String winnerNetwork;
    private ArrayList<Network> population;
    private long startTime;
    private TrainingData trainingData;

    TrainerThread(Logger LOGGER, TrainingData trainingData, String name) {
        super(name);
        this.trainingData = trainingData;
        this.LOGGER = LOGGER;
    }

    TrainerThread(Logger LOGGER, TrainingData trainingData, String name, ArrayList<String> population) {
        super(name);
        this.trainingData = trainingData;
        this.LOGGER = LOGGER;

        this.population = new ArrayList<>();
        for (int i=0; i<population.size(); i++) {
            Network network = new Network(name + "-NN-" + i);
            network.initializeFromString(population.get(i));
            this.population.add(network);
        }
    }

    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        Random random = new SecureRandom();

        // Create a random population if we do not have a population to start with
        if (population == null) {
            population = new ArrayList<>();

            for (int i = 0; i < Runner.MAX_POPULATION_SIZE; i++) {
                Network network = new Network("NN-" + i, random);
                population.add(network);
            }
        }

        // Let evolution do its magic
        for (int generation = 1; generation <= Runner.MAX_GENERATIONS_COUNT; generation++) {


            for (Network network : population) {
                int successCounter = 0;
                float meanSquaredError = 0.0f;

                for (TrainingData.Structure entry : trainingData.get()) {
                    Evaluator evaluator = new Evaluator();
                    evaluator.evaluate(entry.getInputVector(), entry.getExpectedOutputNeuron(), network);

                    if (evaluator.isEvaluatedAsCorrect()) {
                        successCounter++;
                        meanSquaredError += evaluator.getMeanSquaredError();
                    }
                }

                network.setSuccessRate((successCounter / (trainingData.get().size() * 1f)) * 100f);
                network.setMeanSquaredError(meanSquaredError);

                try {
                    LOGGER.info("Intermediate logging" +
                            " >> Name: " + network.getName() + "%" +
                            " >> Success Rate: " + network.getSuccessRate() + "%" +
                            " >> Mean squared error: " + network.getMeanSquaredError() +
                            " >> HashCode: " + network.printNetwork(false).hashCode());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Collections.sort(population);

            /*try {
                population.get(0).calculate(TrainingData);
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            try {
                Genetics.evolve(getName(), generation, population, random);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // At the end, we sort by best to worst network from the latest population and we print the best network to a file
        Collections.sort(population);
        //Collections.reverse(population);
        try {
            winnerNetwork = population.get(0).printNetwork(false);
            LOGGER.info(Runner.MAX_GENERATIONS_COUNT + " generations with a population of " + Runner.MAX_POPULATION_SIZE + " executed in " + (System.currentTimeMillis() - startTime) + "ms" +
                    " >> Winner: " + population.get(0).getName() +
                    " >> Success Rate: " + population.get(0).getSuccessRate() + "%" +
                    " >> Mean squared error: " + population.get(0).getMeanSquaredError() +
                    " >> HashCode: " + population.get(0).printNetwork(false).hashCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

import com.jeromewagener.util.Evaluator;
import com.jeromewagener.network.Genetics;
import com.jeromewagener.network.Network;
import com.jeromewagener.util.TrainingData;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;

public class Trainer {
    private static final int MAX_GENERATIONS_COUNT = 50;

    public static void main(String[] argv) throws IOException {
        Random random = new SecureRandom();

        TrainingData trainingData = new TrainingData();
        trainingData.load();

        // Create a random population
        ArrayList<Network> population = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Network network = new Network("NN-" + i, random);
            population.add(network);
        }

        // Let evolution do its magic
        for (int generation = 1; generation <= MAX_GENERATIONS_COUNT; generation++) {
            if (generation == 1 || generation % 10 == 0 || generation == MAX_GENERATIONS_COUNT) {
                System.out.println("--------------------------");
                System.out.println("Start Generation: " + generation);
                System.out.println("--------------------------");
            }

            for (Network network : population) {
                int successCounter = 0;
                double successCertainty = 0.0d;

                for (Map.Entry<String, Integer> entry : trainingData.get().entrySet()) {
                    Evaluator evaluator = new Evaluator();
                    evaluator.evaluate(
                            entry.getKey(),
                            entry.getValue(),
                            network);

                    if (evaluator.isEvaluatedAsCorrect()) {
                        successCounter++;
                        successCertainty += evaluator.getCertainty();
                    }
                }

                network.setSuccessRate((successCounter / (trainingData.get().size() * 1d)) * 100d);
                network.setCertainty(successCertainty / (trainingData.get().size() * 1d));
            }

            evaluate(generation, population, random);
        }

        Collections.sort(population);
        population.get(0).printNetwork(true);
    }

    private static void evaluate(int generation, ArrayList<Network> population, Random random) throws IOException {
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

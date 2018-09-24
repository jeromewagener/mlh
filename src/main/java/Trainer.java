import com.jeromewagener.util.Evaluator;
import com.jeromewagener.network.Genetics;
import com.jeromewagener.network.Network;
import com.jeromewagener.util.TrainingData;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;

public class Trainer {
    public static int MAX_GENERATIONS_COUNT = 50;

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
                System.out.println(" >> Start Generation: " + generation);
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

            Genetics.evolve(generation, population, random);
        }

        // At the end, we sort by best to worst network from the latest population and we print the best network to a file
        Collections.sort(population);
        population.get(0).printNetwork(true);
    }
}

import com.jeromewagener.network.Evaluator;
import com.jeromewagener.network.Genetics;
import com.jeromewagener.network.Network;
import com.jeromewagener.util.TrainingData;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;

public class Launcher {
    private static final int IMAGE_LOOPS = 1;

    public static void main(String[] argv) throws IOException {
        Random random = new SecureRandom();
        TrainingData trainingData = new TrainingData();
        trainingData.load();

        ArrayList<Network> population = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Network network = new Network("NN-" + i, random);
//            com.jeromewagener.network.Network network = new com.jeromewagener.network.Network("from-file",null);
//            network.readFile("/home/jerome/nn/nn-31p-hundred-images-training.txt");
            population.add(network);
        }

        for (int generation = 1; generation<2000; generation++) {
            if (generation == 1 || generation % 10 == 0) {
                System.out.println("--------------------------");
                System.out.println("Start Generation: " + generation);
                System.out.println("--------------------------");
            }

            for (Network network : population) {
                long start = System.currentTimeMillis();

                int successCounter = 0;
                double successCertainty = 0.0d;

                int i = 1;
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

//                    Thread.sleep(5000);
//
//                    evaluator.getFrame().setVisible(false);
//                    evaluator.getFrame().dispose();

                    if (i == IMAGE_LOOPS) {
                        break;
                    }
                    i++;
                }

                network.setSuccessRate((successCounter / (IMAGE_LOOPS * 1d)) * 100d);
                network.setCertainty(successCertainty / (IMAGE_LOOPS * 1d));

//                System.out.println();
//                System.out.println("com.jeromewagener.network.Network: " + network.name);
//                System.out.println("Duration in seconds: " + (System.currentTimeMillis() - start) / 1000.d);
//                System.out.println("Success Rate: " + String.valueOf(successCounter / (handwrittenNumbersDataSet.entrySet().size() * 1d) * 100) + "% (" + successCounter + "/" + handwrittenNumbersDataSet.entrySet().size() + ")");
//                System.out.println("Success Certainty: " + String.valueOf(successCertainty / (handwrittenNumbersDataSet.entrySet().size() * 1d) * 100));
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
            //population.get(0).visualizeNetwork();
            //System.out.println(" >> Hidden Layer Bias: " + population.get(0).getHiddenLayerBias());
            //System.out.println(" >> Output Layer Bias: " + population.get(0).getOutputLayerBias());
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

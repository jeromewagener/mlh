package com.jeromewagener;

import com.jeromewagener.util.HandwrittenNumbersTrainingData;

import java.io.IOException;

public class Runner {
    // Training Data
    public static final String TRAINING_DATA_PATH = "/home/jerome/mlh/mlh/src/main/resources/0022_AT3M/";
    //public static final String TRAINING_DATA_PATH = "/home/jerome/mlh/mlh/src/main/resources/nn-data.csv";

    // Core parameters
    public static final int INPUT_NEURONS_COUNT = 100;
    public static final int HIDDEN_LAYER_NEURONS_COUNT = 10;
    public static final int OUTPUT_NEURONS_COUNT = 10;
    public static final int MAX_POPULATION_SIZE = 200;
    public static final int MAX_GENERATIONS_COUNT = 100;
    // Only for MultiThreadedTrainer
    public static final int NUMBER_OF_THREADS = 4;
    public static final int MAX_ROUNDS = 15;

    public static void main(String[] argv) throws IOException, InterruptedException {
        MultiThreadedTrainer multiThreadedTrainer = new MultiThreadedTrainer();
        multiThreadedTrainer.run(new HandwrittenNumbersTrainingData());
    }
}

package com.jeromewagener;

import com.jeromewagener.network.Network;
import com.jeromewagener.util.TrainingData;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SingleThreadedTrainer {
    private static final Logger LOGGER = Logger.getLogger("MyLog");

    public static void main(String[] argv) throws IOException, InterruptedException {
        FileHandler fileHandler = new FileHandler(System.getProperty("user.home") + "/running.log");
        LOGGER.addHandler(fileHandler);
        LOGGER.setUseParentHandlers(false);
        SimpleFormatter formatter = new SimpleFormatter();
        fileHandler.setFormatter(formatter);

        TrainingData trainingData = new TrainingData();
        trainingData.load();

        TrainerThread trainerThread = new TrainerThread(LOGGER, trainingData, "SingleThreadedTrainer");
        trainerThread.start();
        trainerThread.join();

        Network winner = new Network("Winner");
        winner.initializeFromString(trainerThread.getWinnerNetwork());
        winner.printNetwork(true);
    }
}

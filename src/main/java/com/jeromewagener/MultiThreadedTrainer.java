package com.jeromewagener;

import com.jeromewagener.network.Network;
import com.jeromewagener.util.TrainingData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MultiThreadedTrainer {
    private static final Logger LOGGER = Logger.getLogger("MyLog");
    private static final int NUMBER_OF_THREADS = 4;
    private static final int MAX_ROUNDS = 15;

    public void run(TrainingData trainingData) throws IOException, InterruptedException {
        FileHandler fileHandler = new FileHandler(System.getProperty("user.home") + "/running.log");
        LOGGER.addHandler(fileHandler);
        LOGGER.setUseParentHandlers(false);
        SimpleFormatter formatter = new SimpleFormatter();
        fileHandler.setFormatter(formatter);
        trainingData.load();

        ArrayList<TrainerThread> trainerThreads = new ArrayList<>();
        ArrayList<String> winnerPopulation = new ArrayList<>();

        for (int round=1; round<MAX_ROUNDS; round++) {
            LOGGER.info("Round " + round + " of " + MAX_ROUNDS + " running with " + NUMBER_OF_THREADS + " threads");

            // remove old threads if there are any as we will run into an
            // IllegalThreadStateException when trying to restart them
            trainerThreads.clear();

            for (int i = 0; i <= NUMBER_OF_THREADS; i++) {
                if (round == 1) {
                    trainerThreads.add(new TrainerThread(LOGGER, trainingData, "Thread" + i));
                } else {
                    trainerThreads.add(new TrainerThread(LOGGER, trainingData, "Thread" + i, winnerPopulation));
                }

            }

            for (int i = 0; i < NUMBER_OF_THREADS; i++) {
                trainerThreads.get(i).start();
            }

            for (int i = 0; i < NUMBER_OF_THREADS; i++) {
                trainerThreads.get(i).join();
            }

            winnerPopulation.clear();

            for (int i = 0; i < NUMBER_OF_THREADS; i++) {
                winnerPopulation.add(trainerThreads.get(i).getWinnerNetwork());
            }

            File f = new File(System.getProperty("user.home") + "/stop");
            if (f.exists()) {
                LOGGER.warning("Exiting prematurely due to stop file detected on disk");
                break;
            }
        }

        ArrayList<Network> bestNetworks = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            Network winner = new Network("Winner-" + i);
            winner.initializeFromString(winnerPopulation.get(i));
            bestNetworks.add(winner);
        }

        Collections.sort(bestNetworks);
        bestNetworks.get(0).printNetwork(true);
    }
}

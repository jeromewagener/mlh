package com.jeromewagener;

import com.jeromewagener.network.Network;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MultiThreadedTrainer {

    public static final int NUMBER_OF_THREADS = 8;

    public static void main(String[] argv) throws IOException, InterruptedException {
        ArrayList<TrainerThread> trainerThreads = new ArrayList<>();
        ArrayList<String> winnerPopulation = new ArrayList<>();

        for (int gen=0; gen<10; gen++) {

            // remove old threads if there are any as we will run into an
            // IllegalThreadStateException when trying to restart them
            trainerThreads.clear();

            for (int i = 0; i < NUMBER_OF_THREADS; i++) {
                if (gen == 0) {
                    trainerThreads.add(new TrainerThread("T" + i));
                } else {
                    trainerThreads.add(new TrainerThread("T" + i, winnerPopulation));
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

            File f = new File("/home/jerome/stop-nn");
            if (f.exists()) {
                System.out.println("Exiting prematurely due to stop file detected on disk");
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
        Collections.reverse(bestNetworks);
        bestNetworks.get(0).printNetwork(true);
    }
}

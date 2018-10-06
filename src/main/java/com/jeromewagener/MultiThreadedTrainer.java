package com.jeromewagener;

import com.jeromewagener.network.Network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MultiThreadedTrainer {

    public static void main(String[] argv) throws IOException, InterruptedException {
        ArrayList<TrainerThread> trainerThreads = new ArrayList<>();
        ArrayList<String> winnerPopulation = new ArrayList<>();

        for (int gen=0; gen<5; gen++) {

            // remove old threads if there are any as we will run into an
            // IllegalThreadStateException when trying to restart them
            trainerThreads.clear();

            for (int i = 0; i < 4; i++) {
                if (gen == 0) {
                    trainerThreads.add(new TrainerThread("T" + i));
                } else {
                    trainerThreads.add(new TrainerThread("T" + i, winnerPopulation));
                }

            }

            for (int i = 0; i < 4; i++) {
                trainerThreads.get(i).start();
            }

            for (int i = 0; i < 4; i++) {
                trainerThreads.get(i).join();
            }

            winnerPopulation.clear();

            for (int i = 0; i < 4; i++) {
                winnerPopulation.add(trainerThreads.get(i).getWinnerNetwork());
            }
        }

        ArrayList<Network> bestNetworks = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Network winner = new Network("Winner-" + i);
            winner.initializeFromString(winnerPopulation.get(i));
            bestNetworks.add(winner);
        }

        Collections.sort(bestNetworks);
        bestNetworks.get(0).printNetwork(true);
    }
}

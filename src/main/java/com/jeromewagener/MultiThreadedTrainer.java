package com.jeromewagener;

import com.jeromewagener.network.Network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MultiThreadedTrainer {

    public static void main(String[] argv) throws IOException, InterruptedException {
        ArrayList<TrainerThread> trainerThreads = new ArrayList<>();
        ArrayList<Network> winnerPopulation = new ArrayList<>();

        for (int gen=0; gen<10; gen++) {

            for (int i = 0; i < 20; i++) {
                if (gen == 0) {
                    trainerThreads.add(new TrainerThread("T" + i));
                } else {
                    trainerThreads.add(new TrainerThread("T" + i, winnerPopulation));
                }

            }

            for (int i = 0; i < 20; i++) {
                trainerThreads.get(i).start();
            }

            for (int i = 0; i < 20; i++) {
                trainerThreads.get(i).join();
            }

            winnerPopulation.clear();

            for (int i = 0; i < 20; i++) {
                Network network = new Network("G" + gen + "T" + i);
                network.initializeFromString(trainerThreads.get(i).getWinnerNetwork());
                winnerPopulation.add(network);
            }
        }

        Collections.sort(winnerPopulation);
        winnerPopulation.get(0).printNetwork(true);
    }
}

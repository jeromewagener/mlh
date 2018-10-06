package com.jeromewagener;

import com.jeromewagener.network.Network;

import java.io.IOException;

public class SingleThreadedTrainer {

    public static void main(String[] argv) throws IOException, InterruptedException {
        TrainerThread trainerThread = new TrainerThread("SingleThreadedTrainer");
        trainerThread.start();
        trainerThread.join();

        Network winner = new Network("Winner");
        winner.initializeFromString(trainerThread.getWinnerNetwork());
        winner.printNetwork(true);
    }
}

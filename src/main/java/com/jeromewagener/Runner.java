package com.jeromewagener;

import com.jeromewagener.util.AboveTwoPointFiveTrainingData;

import java.io.IOException;

public class Runner {
    public static void main(String[] argv) throws IOException, InterruptedException {
        SingleThreadedTrainer singleThreadedTrainer = new SingleThreadedTrainer();
        singleThreadedTrainer.run(new AboveTwoPointFiveTrainingData());
    }
}

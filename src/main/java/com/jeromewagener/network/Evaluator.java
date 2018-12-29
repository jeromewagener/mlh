package com.jeromewagener.network;

import lombok.Getter;

@Getter
public class Evaluator {
    private boolean evaluatedAsCorrect;
    private float meanSquaredError;

    public void evaluate(float[] inputVector, String expectedWinner, Network network) {
        if (network != null) {
            Network.Output networkOutput = network.calculate(inputVector);
            evaluatedAsCorrect = networkOutput.getWinnerNeuron().equals(expectedWinner);
            meanSquaredError = networkOutput.getMeanSquaredError();
        }
    }
}

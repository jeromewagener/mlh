package com.jeromewagener.network;

import com.jeromewagener.util.ImageCompressor;
import lombok.Getter;

import java.io.IOException;

@Getter
public class Evaluator {
    private ImageCompressor imageCompressor = new ImageCompressor(false);
    private boolean evaluatedAsCorrect;
    private double certainty;

    public void evaluate(String fileName, int realValue, Network network) throws IOException {
        double[] inputVector = imageCompressor.compress(fileName);

        if (network != null) {
            NetworkOutput networkOutput = network.calculate(inputVector);
            evaluatedAsCorrect = networkOutput.getDetectedNumber() == realValue;
            certainty = networkOutput.getCertainty();
        }
    }
}

package com.jeromewagener.util;

import com.jeromewagener.network.Network;
import lombok.Getter;

import java.io.IOException;

@Getter
public class Evaluator {
    private ImageCompressor imageCompressor = new ImageCompressor(false);
    private boolean evaluatedAsCorrect;
    private float certainty;

    public void evaluate(String fileName, int realValue, Network network) throws IOException {
        float[] inputVector = imageCompressor.compress(fileName);

        if (network != null) {
            Network.Output networkOutput = network.calculate(inputVector);
            evaluatedAsCorrect = networkOutput.getDetectedNumber() == realValue;
            certainty = networkOutput.getCertainty();
        }
    }
}

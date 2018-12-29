package com.jeromewagener.util;

import com.jeromewagener.Runner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HandwrittenNumbersTrainingData extends TrainingData {

    private ArrayList<Structure> handwrittenNumbersDataSet = new ArrayList<>();
    private ImageCompressor imageCompressor = new ImageCompressor(false);

    public void load() throws IOException {
        File rootDataSetLocation = new File(Runner.TRAINING_DATA_PATH);
        for (File numbersDirectory : Objects.requireNonNull(rootDataSetLocation.listFiles(), "the numbers directory must not be empty")) {
            if (numbersDirectory.isDirectory()) {
                for (File numberSubDirectory : Objects.requireNonNull(numbersDirectory.listFiles(), "the numbers sub directory must not be empty")) {
                    if (numberSubDirectory.getName().endsWith(".png")) {
                        Structure structure = new Structure();
                        structure.setInputVector(imageCompressor.compress(numberSubDirectory.getAbsolutePath()));
                        structure.setExpectedOutputNeuron("O" + Integer.valueOf(numbersDirectory.getName()));
                        handwrittenNumbersDataSet.add(structure);
                    }
                }
            }
        }
    }

    public List<Structure> get() {
        return handwrittenNumbersDataSet;
    }
}

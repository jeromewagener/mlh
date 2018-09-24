package com.jeromewagener.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TrainingData {

    private static final String TRAINING_IMAGES_PATH = "/home/jerome/code/mlh/src/main/resources/0022_AT3M/";
    private Map<String, Integer> handwrittenNumbersDataSet = new HashMap<>();

    public void load() {
        File rootDataSetLocation = new File(TRAINING_IMAGES_PATH);
        for (File numbersDirectory : Objects.requireNonNull(rootDataSetLocation.listFiles(), "the numbers directory must not be empty")) {
            if (numbersDirectory.isDirectory()) {
                for (File numberSubDirectory : Objects.requireNonNull(numbersDirectory.listFiles(), "the numbers sub directory must not be empty")) {
                    if (numberSubDirectory.getName().endsWith(".png")) {
                        handwrittenNumbersDataSet.put(
                                numberSubDirectory.getAbsolutePath(),
                                Integer.valueOf(numbersDirectory.getName()));
                    }
                }
            }
        }
    }

    public Map<String, Integer> get() {
        return handwrittenNumbersDataSet;
    }
}

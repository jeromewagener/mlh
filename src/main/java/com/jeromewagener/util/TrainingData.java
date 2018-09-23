package com.jeromewagener.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TrainingData {

    public static final String TRAINING_IMAGES_PATH = "/home/jerome/code/mlh/src/main/resources/0022_AT3M/";
    private Map<String, Integer> handwrittenNumbersDataSet = new HashMap<String, Integer>();

    public void load() {
        File rootDataSetLocation = new File(TRAINING_IMAGES_PATH);
            for (File numbersDirectory : rootDataSetLocation.listFiles()) {
            if (numbersDirectory.isDirectory()) {
                for (File numberSubDirectory : numbersDirectory.listFiles()) {
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

package com.jeromewagener.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AboveTwoPointFiveTrainingData extends TrainingData {

    private static final String TRAINING_DATA_PATH = "/home/jerome/mlh/mlh/src/main/resources/nn-data.csv";
    private ArrayList<Structure> dataSet = new ArrayList<>();

    public void load() {
        try (BufferedReader br = new BufferedReader(new FileReader(TRAINING_DATA_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] lineArray = line.split(",");
                TrainingData.Structure structure = new TrainingData.Structure();
                float[] inputVector = new float[5];
                inputVector[0] = Float.valueOf(lineArray[0]);
                inputVector[1] = Float.valueOf(lineArray[1]);
                inputVector[2] = Float.valueOf(lineArray[2]);
                inputVector[3] = Float.valueOf(lineArray[3]);
                inputVector[4] = Float.valueOf(lineArray[4]);

                structure.setInputVector(inputVector);

                if ("1".equals(lineArray[6])) {
                    structure.setExpectedOutputNeuron("O0");
                } else if ("1".equals(lineArray[7])) {
                    structure.setExpectedOutputNeuron("O1");
                }

                dataSet.add(structure);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public List<Structure> get() {
        return dataSet;
    }
}

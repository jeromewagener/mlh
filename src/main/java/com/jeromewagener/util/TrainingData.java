package com.jeromewagener.util;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.List;

public abstract class TrainingData {

    public abstract void load() throws IOException;
    public abstract List<Structure> get();

    @Setter
    @Getter
    public static class Structure {
        private float[] inputVector;
        private String expectedOutputNeuron;
    }
}

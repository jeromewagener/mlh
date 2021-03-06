package com.jeromewagener.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jeromewagener.Runner;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

@Getter
@Setter
public class Network implements Comparable<Network>{
    /** The name of the network. Helps identifying from which generation the network comes from or to identify it when loaded from a file */
    private String name;

    /** All neurons are stored in one big hash-map for easy and fast access */
    private Map<String, Neuron> neurons = new HashMap<>();

    private Float successRate = 0.0f;
    private Float meanSquaredError = 0.0f;
    private DecimalFormat decimalFormat = new DecimalFormat("0.000");

    /** Create a new network without any neurons or anything else.
     * Typically used when the network information is loaded from a file or string
     * @param name the name used for the network */
    public Network(String name) {
        this.name = name;
    }

    /** Create a new network and initializes it with random values
     * @param name the name used for the network
     * @param random the random number generator to be used during the initialization */
    public Network(String name, Random random) {
        this.name = name;

        // add output neurons
        for (int i=0; i<Runner.OUTPUT_NEURONS_COUNT; i++) {
            neurons.put("O" + i, new Neuron( "O" + i, null, random.nextFloat()*10));
        }

        // add hidden layer neurons
        for (int i=0; i<Runner.HIDDEN_LAYER_NEURONS_COUNT; i++) {
            Map<Neuron, Float> links = new HashMap<>();
            for (int outputIndex=0; outputIndex<Runner.OUTPUT_NEURONS_COUNT; outputIndex++) {
                links.put(neurons.get("O" + outputIndex), random.nextFloat());
            }

            neurons.put("H" + i, new Neuron( "H" + i, links, random.nextFloat()*10));
        }

        // add input neurons
        for (int i = 0; i< Runner.INPUT_NEURONS_COUNT; i++) {
            Map<Neuron, Float> links = new HashMap<>();
            for (int hlIndex=0; hlIndex<Runner.HIDDEN_LAYER_NEURONS_COUNT; hlIndex++) {
                links.put(neurons.get("H" + hlIndex), random.nextFloat());
            }

            neurons.put("I" +i, new Neuron( "I" + i, links, null));
        }
    }

    public Output calculate(float[] inputVector) {
        List<Neuron> outputNeurons = new ArrayList<>();
        for (int i=0; i<inputVector.length; i++) {
            neurons.get("I" + i).value = inputVector[i];

            if (i < Runner.OUTPUT_NEURONS_COUNT) {
                outputNeurons.add(neurons.get("O" + i));
            }
        }

        calculateHiddenLayerValues();
        calculateOutputLayerValues();

        // first find the best
        Neuron winnerNeuron = outputNeurons.get(0);
        for (Neuron outputNeuron : outputNeurons) {
            if (winnerNeuron.value < outputNeuron.value) {
                winnerNeuron = outputNeuron;
            }
        }

        // now calculate the meanSquaredError. Meaning, a good network only highlights the winner and not the other neurons
        float meanSquaredError = 0.0f;
        for (Neuron outputNeuron : outputNeurons) {
            if (outputNeuron == winnerNeuron) {
                meanSquaredError += Math.pow(outputNeuron.value - 1.0, 2);
            } else {
                meanSquaredError += Math.pow(outputNeuron.value, 2);
            }
        }

        Output networkOutput = new Output();
        networkOutput.setWinnerNeuron(winnerNeuron.label);
        networkOutput.setMeanSquaredError(meanSquaredError);
        return networkOutput;
    }

    private void calculateHiddenLayerValues() {
        for (int hlIndex=0; hlIndex<Runner.HIDDEN_LAYER_NEURONS_COUNT; hlIndex++) {
            float weightedSum = 0;
            Neuron hidden = neurons.get("H" + hlIndex);

            for (int inputIndex=0; inputIndex<Runner.INPUT_NEURONS_COUNT; inputIndex++) {
                Neuron input = neurons.get("I" + inputIndex);
                weightedSum += input.value * input.links.get(hidden);
            }

            weightedSum -= hidden.bias;
            hidden.value = Neuron.sigmoid(weightedSum);
        }
    }

    private void calculateOutputLayerValues() {
        for (int outputIndex=0; outputIndex<Runner.OUTPUT_NEURONS_COUNT; outputIndex++) {
            float weightedSum = 0;
            Neuron output = neurons.get("O" + outputIndex);

            for (int hlIndex=0; hlIndex<Runner.HIDDEN_LAYER_NEURONS_COUNT; hlIndex++) {
                Neuron hidden = neurons.get("H" + hlIndex);
                weightedSum += hidden.value * hidden.links.get(output);
            }

            weightedSum -= output.bias;
            output.value = Neuron.sigmoid(weightedSum);
        }
    }

    public String printNetwork(boolean writeToFile) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("let network = {\n");

        int neuronCounter = 0;
        for (Neuron neuron : neurons.values()) {
            stringBuilder.append("\"" + neuron.label + "\" : " + "{");

            stringBuilder.append("\"value\" : " + decimalFormat.format(neuron.value));

            if (neuron.bias != null) {
                stringBuilder.append(", \"bias\" : " + decimalFormat.format(neuron.bias));
            }

            if (neuron.links != null) {
                stringBuilder.append(", \"links\" : {");
                int linkCounter = 0;
                for (Map.Entry<Neuron, Float> entry : neuron.links.entrySet()) {
                    stringBuilder.append("\"" + entry.getKey().label + "\"").append(":").append(decimalFormat.format(entry.getValue()));

                    if (linkCounter<neuron.links.size()-1) {
                        stringBuilder.append(",");
                    }

                    linkCounter++;
                }
                stringBuilder.append("}");
            }

            stringBuilder.append("}");

            if (neuronCounter<neurons.size()-1) {
                stringBuilder.append(",");
            }

            stringBuilder.append("\n");

            neuronCounter++;
        }

        stringBuilder.append("}");

        if (writeToFile) {
            try (PrintWriter out = new PrintWriter(System.getProperty("user.home") + "/NetworkData.js")) {
                out.println(stringBuilder.toString());
            }
        }

        return stringBuilder.toString();
    }

    public void initializeFromString(String text) {
        String networkJson = text.replace("let network = ", "");
        JsonObject jsonObject = new JsonParser().parse(networkJson).getAsJsonObject();

        // Read and create output neurons first as they will be needed for the links from the hidden layer neurons
        for (int i=0; i<Runner.OUTPUT_NEURONS_COUNT; i++) {
            neurons.put("O" + i, new Neuron("O" + i, null, jsonObject.get("O" + i).getAsJsonObject().get("bias").getAsFloat()));
        }

        // Read and create hidden layer neurons second and link to the output neurons using the link values from the json
        for (int i=0; i<Runner.HIDDEN_LAYER_NEURONS_COUNT; i++) {
            Map<Neuron, Float> links = new HashMap<>();
            neurons.put("H" + i, new Neuron("H" + i, links, jsonObject.get("H" + i).getAsJsonObject().get("bias").getAsFloat()));

            for (int j=0; j<Runner.OUTPUT_NEURONS_COUNT; j++) {
                links.put(neurons.get("O" + j), jsonObject.get("H" + i).getAsJsonObject().get("links").getAsJsonObject().get("O" + j).getAsFloat());
            }
        }

        // Read and create input neurons last and link to the hidden layer neurons using the link values from the json
        for (int i=0; i<Runner.INPUT_NEURONS_COUNT; i++) {
            Map<Neuron, Float> links = new HashMap<>();
            neurons.put("I" + i, new Neuron("I" + i, links, null));

            for (int j=0; j<Runner.HIDDEN_LAYER_NEURONS_COUNT; j++) {
                links.put(neurons.get("H" + j), jsonObject.get("I" + i).getAsJsonObject().get("links").getAsJsonObject().get("H" + j).getAsFloat());
            }
        }

    }

    public void initializeFromFilePath(String filePath) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(filePath));
        initializeFromString(new String(encoded, StandardCharsets.UTF_8));
    }

    @Override
    public int compareTo(Network o) {
        int successComparison = o.successRate.compareTo(this.successRate);

        if (successComparison != 0) {
            return successComparison;
        } else {
            return o.meanSquaredError.compareTo(this.meanSquaredError) * -1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Network) {
            Network other = (Network) o;
            return other.successRate.equals(this.successRate) && other.meanSquaredError.equals(this.meanSquaredError);

        } else {
            return false;
        }
    }


    @Override
    public int hashCode() {
        return successRate.hashCode() + meanSquaredError.hashCode();
    }

    @Getter
    @Setter
    public class Output {
        private String winnerNeuron;
        private float meanSquaredError;
    }
}


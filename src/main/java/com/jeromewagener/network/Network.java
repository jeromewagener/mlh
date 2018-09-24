package com.jeromewagener.network;

import com.jeromewagener.util.ImageCompressor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Getter
@Setter
public class Network implements Comparable<Network>{
    /** Total number of input neurons. Each input neuron represents one pixel of the scale down handwritten number image */
    private static final int INPUT_NEURONS_COUNT = ImageCompressor.IMAGE_WIDTH * ImageCompressor.IMAGE_HEIGHT;
    /** Total number of hidden layer neurons. Value determined by playing around... as this is more art than science */
    private static final int HIDDEN_LAYER_NEURONS_COUNT = 10;
    /** Total number of output neurons. As the output is supposed to be a number between 0 and 9 it makes sense to have 10 output neurons. */
    private static final int OUTPUT_NEURONS_COUNT = 10;

    /** The name of the network. Helps identifying from which generation the network comes from or to identify it when loaded from a file */
    private String name;

    /** All neurons are stored in one big hash-map for easy and fast access */
    private Map<String, Neuron> neurons = new HashMap<>();

    private double hiddenLayerBias = 0.0d;
    private double outputLayerBias = 0.0d;
    private Double successRate = 0.0d;
    private Double certainty = 0.0d;
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
        for (int i=0; i<OUTPUT_NEURONS_COUNT; i++) {
            neurons.put("O-" + i, new Neuron( "O-" + i, Neuron.Type.OUTPUT,null));
        }

        // add hidden layer neurons
        for (int i=0; i<HIDDEN_LAYER_NEURONS_COUNT; i++) {
            Map<Neuron, Double> links = new HashMap<>();
            for (int outputIndex=0; outputIndex<OUTPUT_NEURONS_COUNT; outputIndex++) {
                links.put(neurons.get("O-" + outputIndex), random.nextDouble());
            }

            neurons.put("HL-" + i, new Neuron( "HL-" + i, Neuron.Type.HIDDEN, links));
        }

        // add input neurons
        for (int i = 0; i<INPUT_NEURONS_COUNT; i++) {
            Map<Neuron, Double> links = new HashMap<>();
            for (int hlIndex=0; hlIndex<HIDDEN_LAYER_NEURONS_COUNT; hlIndex++) {
                links.put(neurons.get("HL-" + hlIndex), random.nextDouble());
            }

            neurons.put("IP-" +i, new Neuron( "IP-" + i, Neuron.Type.INPUT, links));
        }

        outputLayerBias = random.nextDouble()*10;
        hiddenLayerBias = random.nextDouble()*10;
    }

    public Output calculate(double[] inputVector) {
        for (int i=0; i<inputVector.length; i++) {
            neurons.get("IP-" + i).value = inputVector[i];
        }

        calculateHiddenLayerValues();
        calculateOutputLayerValues();

        // Detect brightest output neuron which indicated which number between 0 and 9 the NN thinks is shown in the image
        Neuron max = null;
        for (int i=0; i<10; i++) {
            //System.out.println(" -> " + neurons.get("O-" + i).label + " - " + neurons.get("O-" + i).value);

            if (max == null || max.value < neurons.get("O-" + i).value) {
                max = neurons.get("O-" + i);
            }
        }

        Output networkOutput = new Output();
        networkOutput.setDetectedNumber(Integer.valueOf(max.label.split("-")[1]));
        networkOutput.setCertainty(max.value);
        return networkOutput;
    }

    private void calculateHiddenLayerValues() {
        Map<Neuron, Double> weightedSumLinks = new HashMap<>();
        for (int hlIndex=0; hlIndex<HIDDEN_LAYER_NEURONS_COUNT; hlIndex++) {
            for (int inputIndex=0; inputIndex<INPUT_NEURONS_COUNT; inputIndex++) {
                weightedSumLinks.put(neurons.get("IP-" + inputIndex), neurons.get("IP-" + inputIndex).links.get(neurons.get("HL-" + hlIndex)));
            }

            neurons.get("HL-" + hlIndex).calculateWeightedSum(hiddenLayerBias, weightedSumLinks);
        }
    }

    private void calculateOutputLayerValues() {
        Map<Neuron, Double> weightedSumLinks = new HashMap<>();
        for (int outputIndex=0; outputIndex<10; outputIndex++) {
            for (int hlIndex=0; hlIndex<HIDDEN_LAYER_NEURONS_COUNT; hlIndex++) {
                weightedSumLinks.put(neurons.get("HL-" + hlIndex), neurons.get("HL-" + hlIndex).links.get(neurons.get("O-" + outputIndex)));
            }

            neurons.get("O-" + outputIndex).calculateWeightedSum(outputLayerBias, weightedSumLinks);
        }
    }

    public String printNetwork(boolean writeToFile) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("hiddenLayerBias=").append(hiddenLayerBias).append("\n");
        stringBuilder.append("outputLayerBias=").append(outputLayerBias).append("\n");

        for (Neuron neuron : neurons.values()) {

            stringBuilder.append(neuron.label).append(";").append(neuron.type).append(";");

            if (neuron.links != null) {
                for (Map.Entry<Neuron, Double> entry : neuron.links.entrySet()) {
                    stringBuilder.append(entry.getKey().label).append("#").append(decimalFormat.format(entry.getValue())).append("/");
                }
                stringBuilder.append("\n");
            } else {
                stringBuilder.append("\n");
            }
        }

        if (writeToFile) {
            try (PrintWriter out = new PrintWriter("/home/jerome/nn/nn-" + System.currentTimeMillis() + ".txt")) {
                out.println(stringBuilder.toString());
            }
        }

        return stringBuilder.toString();
    }

    void initializeFromString(String text) {
        String[] lines = text.split("\n");

        for (String line : lines) {
            if (line.startsWith("hiddenLayerBias")) {
                hiddenLayerBias = Double.valueOf(line.split("=")[1]);
                continue;
            } else if (line.startsWith("outputLayerBias")) {
                outputLayerBias = Double.valueOf(line.split("=")[1]);
                continue;
            }

            String[] neuronComponents = line.split(";");

            if (neuronComponents.length < 2) {
                break;
            }

            Neuron neuron = new Neuron(neuronComponents[0], Neuron.Type.valueOf(neuronComponents[1]), new HashMap<>());
            neurons.put(neuronComponents[0], neuron);
        }

        for (String line : lines) {
            String[] neuronComponents = line.split(";");

            if (neuronComponents.length == 3) {
                String[] allLinkBundles = neuronComponents[2].split("/");
                for (String linkBundle : allLinkBundles) {
                    String[] linkComponents = linkBundle.split("#");
                    neurons.get(neuronComponents[0]).links.put(neurons.get(linkComponents[0]), Double.valueOf(linkComponents[1]));
                }
            }
        }
    }

    public void initializeFromFilePath(String filePath) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(filePath));
        initializeFromString(new String(encoded, StandardCharsets.UTF_8));
    }

    @Override
    public int compareTo(Network o) {
        int successRateComparison = o.successRate.compareTo(this.successRate);
        if (successRateComparison == 0) {
            return o.certainty.compareTo(this.certainty);
        }
        return successRateComparison;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Network) {
            Network other = (Network) o;

            if (other.successRate.equals(this.successRate)) {
                return other.certainty.equals(this.certainty);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    @Override
    public int hashCode() {
        return successRate.hashCode() + certainty.hashCode();
    }

    @Getter
    @Setter
    public class Output {
        private int detectedNumber;
        private double certainty;
    }
}


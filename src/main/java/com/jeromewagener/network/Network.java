package com.jeromewagener.network;

import lombok.Getter;
import lombok.Setter;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

import java.io.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Getter
@Setter
public class Network implements Comparable<Network>{
    private String name;
    private Map<String, Neuron> neurons = new HashMap<>();
    private double hiddenLayerBias = 0.0d;
    private double outputLayerBias = 0.0d;

    private Double successRate = 0.0d;
    private Double certainty = 0.0d;
    private static Graph graph = null;
    private DecimalFormat df2 = new DecimalFormat("#.##");

    public Network(String name, Random random) throws IOException {
        this.name = name;
        if (random == null) {
            return;
        }

        // TODO check this https://stackoverflow.com/questions/2480650/role-of-bias-in-neural-networks

        // add output neurons
        for (int i=0; i<10; i++) {
            neurons.put("O-" + i, new Neuron( "O-" + i, NeuronType.OUTPUT,null));
        }

        // add hidden layer neurons
        for (int i=0; i<4; i++) {
            Map<Neuron, Double> links = new HashMap<>();
            for (int outputIndex=0; outputIndex<10; outputIndex++) {
                links.put(neurons.get("O-" + outputIndex), random.nextDouble());
            }

            neurons.put("HL-" + i, new Neuron( "HL-" + i, NeuronType.HIDDEN, links));
        }

        // add input neurons
        for (int i=0; i<25; i++) {
            Map<Neuron, Double> links = new HashMap<>();
            for (int hlIndex=0; hlIndex<4; hlIndex++) {
                links.put(neurons.get("HL-" + hlIndex), random.nextDouble());
            }

            neurons.put("IP-" +i, new Neuron( "IP-" + i, NeuronType.INPUT, links));
        }

        outputLayerBias = random.nextDouble()*10;
        hiddenLayerBias = random.nextDouble()*10;
    }

    public NetworkOutput calculate(double[] inputVector) throws IOException {
        for (int i=0; i<inputVector.length; i++) {
            neurons.get("IP-" + i).value = inputVector[i];
        }

        calculateHiddenLayerValues();
        calculateOutputLayerValues();

        //printNetwork();

        Neuron max = null;
        for (int i=0; i<10; i++) {
            //System.out.println(" -> " + neurons.get("O-" + i).label + " - " + neurons.get("O-" + i).value);

            if (max == null || max.value < neurons.get("O-" + i).value) {
                max = neurons.get("O-" + i);
            }
        }

        NetworkOutput networkOutput = new NetworkOutput();
        networkOutput.detectedNumber = Integer.valueOf(max.label.split("-")[1]);
        networkOutput.certainty = max.value;
        return networkOutput;
    }

    private void calculateHiddenLayerValues() {
        Map<Neuron, Double> weightedSumLinks = new HashMap<>();
        for (int hlIndex=0; hlIndex<4; hlIndex++) {
            for (int inputIndex=0; inputIndex<25; inputIndex++) {
                weightedSumLinks.put(neurons.get("IP-" + inputIndex), neurons.get("IP-" + inputIndex).links.get(neurons.get("HL-" + hlIndex)));
            }

            neurons.get("HL-" + hlIndex).calculateWeightedSum(hiddenLayerBias, weightedSumLinks);

//            System.out.println("HL-" + hlIndex + " --> " + neurons.get("HL-" + hlIndex).value);
//            System.out.println("HLb-" + hlIndex + " --> " + hiddenLayerBias);
        }
    }

    private void calculateOutputLayerValues() {
        Map<Neuron, Double> weightedSumLinks = new HashMap<>();
        for (int outputIndex=0; outputIndex<10; outputIndex++) {
            for (int hlIndex=0; hlIndex<4; hlIndex++) {
                weightedSumLinks.put(neurons.get("HL-" + hlIndex), neurons.get("HL-" + hlIndex).links.get(neurons.get("O-" + outputIndex)));
            }

            neurons.get("O-" + outputIndex).calculateWeightedSum(outputLayerBias, weightedSumLinks);

//            System.out.println("O-" + outputIndex + " --> " + neurons.get("O-" + outputIndex).value);
//            System.out.println("Ob-" + outputIndex + " --> " + outputLayerBias);
        }
    }

    public String printNetwork(boolean writeToFile) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("hiddenLayerBias=" + hiddenLayerBias + "\n");
        stringBuilder.append("outputLayerBias=" + outputLayerBias + "\n");

        for (Neuron neuron : neurons.values()) {

            stringBuilder.append(neuron.label + ";" + neuron.neuronType + ";");

            if (neuron.links != null) {
                for (Map.Entry<Neuron, Double> entry : neuron.links.entrySet()) {
                    stringBuilder.append(entry.getKey().label + "#" + entry.getValue() + "/");
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

        //System.out.println(stringBuilder);
        return stringBuilder.toString();
    }

    public void visualizeNetwork() {

        Map<String, Node> nodes = new HashMap<>();

        String styleSheet = "edge { shape: line; fill-color: #ccc; arrow-size: 6px, 4px; }";

        if (graph == null) {
            graph = new SingleGraph(name);
            graph.addAttribute("ui.stylesheet", styleSheet);
            Viewer viewer = graph.display();
            viewer.disableAutoLayout();
        } else {
            graph.clear();
            graph.addAttribute("ui.stylesheet", styleSheet);
        }

        int inputCounter = 0;
        int hiddenCounter = 0;
        int outputCounter = 0;

        for (Neuron neuron : neurons.values()) {

            //stringBuilder.append(neuron.label + ";" + neuron.neuronType + ";" + neuron.bias + ";");

            Node node = graph.addNode(neuron.label);
            node.addAttribute("ui.label", neuron.label);

            if (NeuronType.INPUT.equals(neuron.neuronType)) {
                node.setAttribute("x", 0);
                node.setAttribute("y", inputCounter);
                inputCounter += 20;
            } else if (NeuronType.HIDDEN.equals(neuron.neuronType)) {
                if (neuron.label.startsWith("HL-0")) {
                    node.setAttribute("x", 100);
                    node.setAttribute("y", 700);
                } else if (neuron.label.startsWith("HL-1")) {
                    node.setAttribute("x", 300);
                    node.setAttribute("y", 500);
                } else if (neuron.label.startsWith("HL-2")) {
                    node.setAttribute("x", 500);
                    node.setAttribute("y", 300);
                } else if (neuron.label.startsWith("HL-3")) {
                    node.setAttribute("x", 700);
                    node.setAttribute("y", 100);
                }
            } else if (NeuronType.OUTPUT.equals(neuron.neuronType)) {
                node.setAttribute("x", 800);
                node.setAttribute("y", outputCounter);
                outputCounter += 50;

            } else {
                System.out.println("This should not happen");
            }

            nodes.put(neuron.label, node);

            /*if (neuron.links != null) {
                for (Map.Entry<com.jeromewagener.network.Neuron, Double> entry : neuron.links.entrySet()) {
                    stringBuilder.append(entry.getKey().label + "#" + entry.getValue() + "/");
                }
                stringBuilder.append("\n");
            } else {
                stringBuilder.append("\n");
            }*/
        }


        for (Neuron neuron : neurons.values()) {
            if (neuron.links != null) {
                for (Map.Entry<Neuron, Double> entry : neuron.links.entrySet()) {
                    Edge edge = graph.addEdge(nodes.get(neuron.label) + "-" + nodes.get(entry.getKey().label), nodes.get(neuron.label), nodes.get(entry.getKey().label), true);
                    edge.addAttribute("ui.label", String.valueOf(df2.format(entry.getValue())));
                }
            }
        }

//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        //graph.display();

        /*graph.addNode("A" );
        graph.addNode("B" );
        graph.addNode("C" );
        graph.addEdge("AB", "A", "B");
        graph.addEdge("BC", "B", "C");
        graph.addEdge("CA", "C", "A");

        for (Node node : graph) {
            node.addAttribute("ui.label", node.getId());
        }

        for (Edge edge : graph.getEachEdge()) {
            edge.addAttribute("ui.label", edge.getId());
        }*/


    }

//    public static void main(String[] argv) throws IOException {
//        //while (true) {
//            //com.jeromewagener.network.Network network = new com.jeromewagener.network.Network("rand", new Random());
//
//            //com.jeromewagener.network.Network network = new com.jeromewagener.network.Network("from-file",null);
//            //network.readFile("/home/jerome/nn/nn-22-i25-hundred-images.txt");
//            //network.readFile("/home/jerome/nn/nn-35p-twenty-image-training.txt");
//            com.jeromewagener.network.Network network = new com.jeromewagener.network.Network("from-file",null);
//            network.readFile("/home/jerome/nn/nn-1524327261428.txt");
//
//            //network.visualizeNetwork();
//            //network.printNetwork(false);
//
//            Map<String, Integer> handwrittenNumbersDataSet = new HashMap<String, Integer>();
//            File rootDataSetLocation = new File("/home/jerome/Code/mlh/src/main/resources/0022_AT3M/");
//            for (File numbersDirectory : rootDataSetLocation.listFiles()) {
//                if (numbersDirectory.isDirectory()) {
//                    for (File numberSubDirectory : numbersDirectory.listFiles()) {
//                        if (numberSubDirectory.getName().endsWith(".png")) {
//                            handwrittenNumbersDataSet.put(
//                                    numberSubDirectory.getAbsolutePath(),
//                                    Integer.valueOf(numbersDirectory.getName()));
//                        }
//                    }
//                }
//            }
//
//            //handwrittenNumbersDataSet.size()
//            int IMAGE_LOOPS = handwrittenNumbersDataSet.size();
//
//            double successCounter = 0;
//            double successCertainty = 0.0d;
//            int i = 1;
//            for (Map.Entry<String, Integer> entry : handwrittenNumbersDataSet.entrySet()) {
//                com.jeromewagener.network.Evaluator evaluator = new com.jeromewagener.network.Evaluator(entry.getKey(), entry.getValue(), network);
//
//                if (evaluator.evaluatedAsCorrect) {
//                    successCounter++;
//                    successCertainty += evaluator.certainty;
//                }
//
//                if (i == IMAGE_LOOPS) {
//                    break;
//                }
//                i++;
//            }
//
//            network.successRate = (successCounter / (IMAGE_LOOPS * 1d)) * 100d;
//            network.certainty = successCertainty / (IMAGE_LOOPS * 1d);
//
//            System.out.println(network.name + " >> Success Rate: " + network.successRate + "% >> Avg. Certainty: " + network.certainty);
//        //}
//    }

    public void initializeFromString(String text) throws IOException {
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

            Neuron neuron = new Neuron(neuronComponents[0], NeuronType.valueOf(neuronComponents[1]), new HashMap<>());
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

    public void readFile(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader (file));
        String line;

        try {
            while((line = reader.readLine()) != null) {
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

                Neuron neuron = new Neuron(neuronComponents[0], NeuronType.valueOf(neuronComponents[1]), new HashMap<>());
                neurons.put(neuronComponents[0], neuron);
            }
        } finally {
            reader.close();
        }

        BufferedReader linkReader = new BufferedReader(new FileReader (file));

        try {
            while((line = linkReader.readLine()) != null) {
                String[] neuronComponents = line.split(";");

                if (neuronComponents.length == 3) {
                    String[] allLinkBundles = neuronComponents[2].split("/");
                    for (String linkBundle : allLinkBundles) {
                        String[] linkComponents = linkBundle.split("#");

                        neurons.get(neuronComponents[0]).links.put(neurons.get(linkComponents[0]), Double.valueOf(linkComponents[1]));
                    }
                }
            }
        } finally {
            linkReader.close();
        }
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
}

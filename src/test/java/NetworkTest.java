import com.jeromewagener.network.Network;
import com.jeromewagener.util.HandwrittenNumbersTrainingData;
import com.jeromewagener.util.ImageCompressor;
import com.jeromewagener.util.TrainingData;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;

public class NetworkTest {
    @Test
    public void verifySuccessRate() throws IOException {
        TrainingData trainingData = new HandwrittenNumbersTrainingData();
        trainingData.load();

        Network network = new Network("from-file");
        network.initializeFromFilePath("/home/jerome/code/mlh/src/test/resources/NetworkData-0.001.js");

        ImageCompressor imageCompressor = new ImageCompressor(false);

        int successCounter = 0;
        //for (Map.Entry<String, Integer> entry : trainingData.get().entrySet()) {
        //    if (network.calculate(imageCompressor.compress(entry.getKey())).getDetectedNumber() == entry.getValue()) {
        //        successCounter++;
        //    }
        //}

        //float successRate = successCounter * 1.0f / trainingData.get().size();
        //assertEquals(0.10135135f, successRate, 0.0f);
    }

    @Test
    public void checkDistribution() throws IOException {
        TrainingData trainingData = new HandwrittenNumbersTrainingData();
        trainingData.load();

        Network network = new Network("from-file");
        network.initializeFromFilePath("/home/jerome/code/mlh/src/test/resources/NetworkData-0.001.js");

        ImageCompressor imageCompressor = new ImageCompressor(false);

        HashMap<Integer, Integer> successCounterMap = new HashMap<>();
        successCounterMap.put(0, 0);
        successCounterMap.put(1, 0);
        successCounterMap.put(2, 0);
        successCounterMap.put(3, 0);
        successCounterMap.put(4, 0);
        successCounterMap.put(5, 0);
        successCounterMap.put(6, 0);
        successCounterMap.put(7, 0);
        successCounterMap.put(8, 0);
        successCounterMap.put(9, 0);

        //for (Map.Entry<String, Integer> entry : trainingData.get().entrySet()) {
        //    if (network.calculate(imageCompressor.compress(entry.getKey())).getDetectedNumber() == entry.getValue()) {
        //        successCounterMap.put(entry.getValue(), (successCounterMap.get(entry.getValue()))+1);
        //    }
        //}

        //System.out.println(successCounterMap);
    }

    @Test
    public void checkCalculations() throws IOException {
        TrainingData trainingData = new HandwrittenNumbersTrainingData();
        trainingData.load();

        Network network = new Network("from-file");
        network.initializeFromFilePath("/home/jerome/code/mlh/src/test/resources/NetworkData-0.001.js");

        ImageCompressor imageCompressor = new ImageCompressor(true);

        //Map.Entry<String, Integer> firstTrainingSample = trainingData.get().entrySet().iterator().next();
        //network.calculate(imageCompressor.compress(firstTrainingSample.getKey()));
    }

    @Test
    public void testJsonGeneration() throws IOException {
        Network network = new Network("testNetwork", new SecureRandom());

        TrainingData trainingData = new HandwrittenNumbersTrainingData();
        trainingData.load();

        ImageCompressor imageCompressor = new ImageCompressor(true);
        //network.calculate(imageCompressor.compress(trainingData.get().keySet().iterator().next()));

        network.printNetwork(true);
    }

    @Test
    public void readJsonGeneration() throws IOException {
        Network network = new Network("testNetwork");
        network.initializeFromFilePath("/home/jerome/code/mlh/src/main/resources/visualization/NetworkData.js");
        network.printNetwork(true);

        /*TrainingData trainingData = new TrainingData();
        trainingData.load();

        ImageCompressor imageCompressor = new ImageCompressor(true);
        network.calculate(imageCompressor.compress(trainingData.get().keySet().iterator().next()));

        network.printNetwork(true);*/
    }

    @Test
    public void manualTesting() throws IOException {
        Network network = new Network("from-file");
        network.initializeFromFilePath("/home/jerome/mlh/mlh/src/test/resources/NetworkData-Above-2-5-100percent.js");

        TrainingData.Structure structure = new TrainingData.Structure();
        structure.setInputVector(new float[]{0.0f, 0.0f, 0.0f, 0.0f, 4.0f});

        Network.Output networkOutput = network.calculate(structure.getInputVector());

        System.out.println(networkOutput.getWinnerNeuron());
        System.out.println(networkOutput.getMeanSquaredError());
    }

    @Test
    public void automatedTesting() throws IOException {
        Network network = new Network("from-file");
        network.initializeFromFilePath("/home/jerome/mlh/mlh/src/test/resources/NetworkData-Abvove-2-5-98-1percent.js");

        try (BufferedReader br = new BufferedReader(new FileReader("/home/jerome/mlh/mlh/src/main/resources/nn-test-data.csv"))) {
            String line = "";
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

                Network.Output networkOutput = network.calculate(structure.getInputVector());

                if (!networkOutput.getWinnerNeuron().equals(structure.getExpectedOutputNeuron())) {
                    System.out.println("Network   (above 2.5) : " + "O0".equals(networkOutput.getWinnerNeuron()));
                    System.out.println("Test Data (above 2.5) : " + "O0".equals(structure.getExpectedOutputNeuron()));
                    System.out.println("Input Vector : " + Arrays.toString(inputVector));
                    float total = 0.0f;
                    for (int i=0; i<5; i++) {
                        total += inputVector[i];
                    }
                    System.out.println("Total : " + total);
                    System.out.println("MSE   : " + network.getMeanSquaredError());
                    System.out.println("----");
                }

            }
        }
    }
}
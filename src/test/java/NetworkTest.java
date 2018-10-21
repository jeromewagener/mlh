import com.jeromewagener.network.Network;
import com.jeromewagener.util.ImageCompressor;
import com.jeromewagener.util.TrainingData;
import org.junit.Test;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class NetworkTest {
    @Test
    public void verifySuccessRate() throws IOException {
        TrainingData trainingData = new TrainingData();
        trainingData.load();

        Network network = new Network("from-file");
        network.initializeFromFilePath("/home/jerome/code/mlh/src/test/resources/NetworkData-0.001.js");

        ImageCompressor imageCompressor = new ImageCompressor(false);

        int successCounter = 0;
        for (Map.Entry<String, Integer> entry : trainingData.get().entrySet()) {
            if (network.calculate(imageCompressor.compress(entry.getKey())).getDetectedNumber() == entry.getValue()) {
                successCounter++;
            }
        }

        float successRate = successCounter * 1.0f / trainingData.get().size();
        assertEquals(0.10135135f, successRate, 0.0f);
    }

    @Test
    public void checkDistribution() throws IOException {
        TrainingData trainingData = new TrainingData();
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

        for (Map.Entry<String, Integer> entry : trainingData.get().entrySet()) {
            if (network.calculate(imageCompressor.compress(entry.getKey())).getDetectedNumber() == entry.getValue()) {
                successCounterMap.put(entry.getValue(), (successCounterMap.get(entry.getValue()))+1);
            }
        }

        System.out.println(successCounterMap);
    }

    @Test
    public void checkCalculations() throws IOException {
        TrainingData trainingData = new TrainingData();
        trainingData.load();

        Network network = new Network("from-file");
        network.initializeFromFilePath("/home/jerome/code/mlh/src/test/resources/NetworkData-0.001.js");

        ImageCompressor imageCompressor = new ImageCompressor(true);

        Map.Entry<String, Integer> firstTrainingSample = trainingData.get().entrySet().iterator().next();
        network.calculate(imageCompressor.compress(firstTrainingSample.getKey()));
    }

    @Test
    public void testJsonGeneration() throws IOException {
        Network network = new Network("testNetwork", new SecureRandom());

        TrainingData trainingData = new TrainingData();
        trainingData.load();

        ImageCompressor imageCompressor = new ImageCompressor(true);
        network.calculate(imageCompressor.compress(trainingData.get().keySet().iterator().next()));

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
}

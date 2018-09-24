import com.jeromewagener.network.Network;
import com.jeromewagener.util.ImageCompressor;
import com.jeromewagener.util.TrainingData;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class NetworkTest {
    @Test
    public void verifySuccessRate() throws IOException {
        TrainingData trainingData = new TrainingData();
        trainingData.load();

        Network network = new Network("from-file");
        network.initializeFromFilePath("/home/jerome/code/mlh/src/test/resources/nn-1537732798767.txt");

        ImageCompressor imageCompressor = new ImageCompressor(false);

        int successCounter = 0;
        for (Map.Entry<String, Integer> entry : trainingData.get().entrySet()) {
            if (network.calculate(imageCompressor.compress(entry.getKey())).getDetectedNumber() == entry.getValue()) {
                successCounter++;
            }
        }

        double successRate = successCounter * 1.0d / trainingData.get().size();
        assertEquals(0.17905405405405407, successRate, 0.0);
    }
}

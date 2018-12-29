import com.jeromewagener.network.Evaluator;
import com.jeromewagener.util.HandwrittenNumbersTrainingData;
import com.jeromewagener.util.ImageCompressor;
import com.jeromewagener.util.TrainingData;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;

public class TrainingDataTest {

    @Test
    public void loadTrainingData() throws IOException {
        int exceptionCount = 0;

        TrainingData trainingData = new HandwrittenNumbersTrainingData();
        trainingData.load();

        Evaluator evaluator = new Evaluator();

        assertEquals(0, exceptionCount);
    }

    @Test
    public void checkTrainingDataDistribution() {
        TrainingData trainingData = new HandwrittenNumbersTrainingData();

        HashMap<Integer, Integer> distribution = new HashMap<>();


        System.out.println(distribution);
    }

    @Test
    public void readAndCompressKnownImageAndTestAgainstKnownCompression() throws IOException {
        TrainingData trainingData = new HandwrittenNumbersTrainingData();
        trainingData.load();

        float[] zero = new float[] {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0039215686274509665f, 0.015686274509803977f, 0.0039215686274509665f, 0.0f, 0.0f,
                0.22352941176470587f, 0.0f, 0.196078431372549f, 0.0f, 0.039215686274509776f, 0.015686274509803977f, 0.007843137254901933f, 0.0f, 0.0f, 0.0f,
                0.0117647058823529f, 0.04705882352941182f, 0.0f, 0.0f, 0.6470588235294117f, 0.019607843137254943f, 0.0f, 0.0f, 0.015686274509803977f, 0.0f,
                0.0f, 0.0f, 0.32156862745098036f, 0.0f, 0.0f, 0.07843137254901966f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.596078431372549f, 0.0f, 0.5490196078431373f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.015686274509803977f, 0.02352941176470591f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.019607843137254943f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};

        ImageCompressor compressor = new ImageCompressor(true);
        Assert.assertArrayEquals(compressor.compress("/home/jerome/code/mlh/src/main/resources/0022_AT3M/0/number-53.png"), zero, 0.00001f);
    }
}

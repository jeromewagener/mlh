import com.jeromewagener.util.Evaluator;
import com.jeromewagener.util.ImageCompressor;
import com.jeromewagener.util.TrainingData;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class TrainingDataTest {

    @Test
    public void loadTrainingData() {
        int exceptionCount = 0;

        TrainingData trainingData = new TrainingData();
        trainingData.load();

        Evaluator evaluator = new Evaluator();

        for (Map.Entry<String, Integer> entry : trainingData.get().entrySet()) {
            try {

                evaluator.evaluate(entry.getKey(), entry.getValue(), null);

            } catch (IOException e) {
                exceptionCount++;
            }
        }

        assertEquals(0, exceptionCount);
    }

    @Test
    public void readAndCompressKnownImageAndTestAgainstKnownCompression() throws IOException {
        TrainingData trainingData = new TrainingData();
        trainingData.load();

        double[] zero = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0039215686274509665, 0.015686274509803977, 0.0039215686274509665, 0.0, 0.0,
                0.22352941176470587, 0.0, 0.196078431372549, 0.0, 0.039215686274509776, 0.015686274509803977, 0.007843137254901933, 0.0, 0.0, 0.0,
                0.0117647058823529, 0.04705882352941182, 0.0, 0.0, 0.6470588235294117, 0.019607843137254943, 0.0, 0.0, 0.015686274509803977, 0.0,
                0.0, 0.0, 0.32156862745098036, 0.0, 0.0, 0.07843137254901966, 0.0, 0.0, 0.0, 0.0,
                0.596078431372549, 0.0, 0.5490196078431373, 0.0, 0.0, 0.0, 0.0, 0.0, 0.015686274509803977, 0.02352941176470591,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.019607843137254943, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

        ImageCompressor compressor = new ImageCompressor(true);
        Assert.assertArrayEquals(compressor.compress("/home/jerome/code/mlh/src/main/resources/0022_AT3M/0/number-53.png"), zero, 0.0);
    }
}

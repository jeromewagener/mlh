import com.jeromewagener.network.Neuron;
import org.junit.Test;

import java.util.Random;

import static junit.framework.TestCase.assertEquals;

public class MathTest {
    @Test
    public void testSigmoid() {
        // See https://en.wikipedia.org/wiki/Sigmoid_function#/media/File:Logistic-curve.svg
        assertEquals(1.0d, Neuron.sigmoid(1000), 0d);
        assertEquals(0.0d, Neuron.sigmoid(-1000), 0d);
        assertEquals(0.5d, Neuron.sigmoid(0), 0d);
    }
}

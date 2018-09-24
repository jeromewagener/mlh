import org.junit.Test;

import java.io.IOException;

public class TrainingTest {
    @Test
    public void runTraining() throws IOException {
        Trainer.MAX_GENERATIONS_COUNT = 10;
        Trainer.main(new String[] {});
    }
}

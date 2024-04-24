package smartwake.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class AlarmTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Alarm getAlarmSample1() {
        return new Alarm().id("id1").sound("sound1").label("label1").snoozeDuration(1);
    }

    public static Alarm getAlarmSample2() {
        return new Alarm().id("id2").sound("sound2").label("label2").snoozeDuration(2);
    }

    public static Alarm getAlarmRandomSampleGenerator() {
        return new Alarm()
            .id(UUID.randomUUID().toString())
            .sound(UUID.randomUUID().toString())
            .label(UUID.randomUUID().toString())
            .snoozeDuration(intCount.incrementAndGet());
    }
}

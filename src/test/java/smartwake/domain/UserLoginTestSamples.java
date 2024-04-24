package smartwake.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserLoginTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UserLogin getUserLoginSample1() {
        return new UserLogin().id(1L).username("username1").password("password1");
    }

    public static UserLogin getUserLoginSample2() {
        return new UserLogin().id(2L).username("username2").password("password2");
    }

    public static UserLogin getUserLoginRandomSampleGenerator() {
        return new UserLogin()
            .id(longCount.incrementAndGet())
            .username(UUID.randomUUID().toString())
            .password(UUID.randomUUID().toString());
    }
}

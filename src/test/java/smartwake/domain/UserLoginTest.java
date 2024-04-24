package smartwake.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static smartwake.domain.AlarmTestSamples.*;
import static smartwake.domain.UserLoginTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import smartwake.web.rest.TestUtil;

class UserLoginTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserLogin.class);
        UserLogin userLogin1 = getUserLoginSample1();
        UserLogin userLogin2 = new UserLogin();
        assertThat(userLogin1).isNotEqualTo(userLogin2);

        userLogin2.setId(userLogin1.getId());
        assertThat(userLogin1).isEqualTo(userLogin2);

        userLogin2 = getUserLoginSample2();
        assertThat(userLogin1).isNotEqualTo(userLogin2);
    }

    @Test
    void usernameTest() throws Exception {
        UserLogin userLogin = getUserLoginRandomSampleGenerator();
        Alarm alarmBack = getAlarmRandomSampleGenerator();

        userLogin.addUsername(alarmBack);
        assertThat(userLogin.getUsernames()).containsOnly(alarmBack);
        assertThat(alarmBack.getUserLogin()).isEqualTo(userLogin);

        userLogin.removeUsername(alarmBack);
        assertThat(userLogin.getUsernames()).doesNotContain(alarmBack);
        assertThat(alarmBack.getUserLogin()).isNull();

        userLogin.usernames(new HashSet<>(Set.of(alarmBack)));
        assertThat(userLogin.getUsernames()).containsOnly(alarmBack);
        assertThat(alarmBack.getUserLogin()).isEqualTo(userLogin);

        userLogin.setUsernames(new HashSet<>());
        assertThat(userLogin.getUsernames()).doesNotContain(alarmBack);
        assertThat(alarmBack.getUserLogin()).isNull();
    }
}

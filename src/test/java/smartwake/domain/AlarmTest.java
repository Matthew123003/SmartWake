package smartwake.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static smartwake.domain.AlarmTestSamples.*;
import static smartwake.domain.UserLoginTestSamples.*;

import org.junit.jupiter.api.Test;
import smartwake.web.rest.TestUtil;

class AlarmTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Alarm.class);
        Alarm alarm1 = getAlarmSample1();
        Alarm alarm2 = new Alarm();
        assertThat(alarm1).isNotEqualTo(alarm2);

        alarm2.setId(alarm1.getId());
        assertThat(alarm1).isEqualTo(alarm2);

        alarm2 = getAlarmSample2();
        assertThat(alarm1).isNotEqualTo(alarm2);
    }

    @Test
    void userLoginTest() throws Exception {
        Alarm alarm = getAlarmRandomSampleGenerator();
        UserLogin userLoginBack = getUserLoginRandomSampleGenerator();

        alarm.setUserLogin(userLoginBack);
        assertThat(alarm.getUserLogin()).isEqualTo(userLoginBack);

        alarm.userLogin(null);
        assertThat(alarm.getUserLogin()).isNull();
    }
}

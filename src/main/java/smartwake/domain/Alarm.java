package smartwake.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Alarm.
 */
@Entity
@Table(name = "alarm")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Alarm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private String id;

    @Column(name = "time")
    private ZonedDateTime time;

    @Column(name = "alarm_time")
    private ZonedDateTime alarmTime;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "sound")
    private String sound;

    @Column(name = "label")
    private String label;

    @Column(name = "repeat_days")
    private LocalDate repeatDays;

    @Column(name = "snooze_enabled")
    private Boolean snoozeEnabled;

    @Column(name = "snooze_duration")
    private Integer snoozeDuration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "usernames" }, allowSetters = true)
    private UserLogin userLogin;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Alarm id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ZonedDateTime getTime() {
        return this.time;
    }

    public Alarm time(ZonedDateTime time) {
        this.setTime(time);
        return this;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }

    public ZonedDateTime getAlarmTime() {
        return this.alarmTime;
    }

    public Alarm alarmTime(ZonedDateTime alarmTime) {
        this.setAlarmTime(alarmTime);
        return this;
    }

    public void setAlarmTime(ZonedDateTime alarmTime) {
        this.alarmTime = alarmTime;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public Alarm enabled(Boolean enabled) {
        this.setEnabled(enabled);
        return this;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getSound() {
        return this.sound;
    }

    public Alarm sound(String sound) {
        this.setSound(sound);
        return this;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getLabel() {
        return this.label;
    }

    public Alarm label(String label) {
        this.setLabel(label);
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public LocalDate getRepeatDays() {
        return this.repeatDays;
    }

    public Alarm repeatDays(LocalDate repeatDays) {
        this.setRepeatDays(repeatDays);
        return this;
    }

    public void setRepeatDays(LocalDate repeatDays) {
        this.repeatDays = repeatDays;
    }

    public Boolean getSnoozeEnabled() {
        return this.snoozeEnabled;
    }

    public Alarm snoozeEnabled(Boolean snoozeEnabled) {
        this.setSnoozeEnabled(snoozeEnabled);
        return this;
    }

    public void setSnoozeEnabled(Boolean snoozeEnabled) {
        this.snoozeEnabled = snoozeEnabled;
    }

    public Integer getSnoozeDuration() {
        return this.snoozeDuration;
    }

    public Alarm snoozeDuration(Integer snoozeDuration) {
        this.setSnoozeDuration(snoozeDuration);
        return this;
    }

    public void setSnoozeDuration(Integer snoozeDuration) {
        this.snoozeDuration = snoozeDuration;
    }

    public UserLogin getUserLogin() {
        return this.userLogin;
    }

    public void setUserLogin(UserLogin userLogin) {
        this.userLogin = userLogin;
    }

    public Alarm userLogin(UserLogin userLogin) {
        this.setUserLogin(userLogin);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Alarm)) {
            return false;
        }
        return getId() != null && getId().equals(((Alarm) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Alarm{" +
            "id=" + getId() +
            ", time='" + getTime() + "'" +
            ", alarmTime='" + getAlarmTime() + "'" +
            ", enabled='" + getEnabled() + "'" +
            ", sound='" + getSound() + "'" +
            ", label='" + getLabel() + "'" +
            ", repeatDays='" + getRepeatDays() + "'" +
            ", snoozeEnabled='" + getSnoozeEnabled() + "'" +
            ", snoozeDuration=" + getSnoozeDuration() +
            "}";
    }
}

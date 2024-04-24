package smartwake.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static smartwake.domain.AlarmAsserts.*;
import static smartwake.web.rest.TestUtil.createUpdateProxyForBean;
import static smartwake.web.rest.TestUtil.sameInstant;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import smartwake.IntegrationTest;
import smartwake.domain.Alarm;
import smartwake.repository.AlarmRepository;

/**
 * Integration tests for the {@link AlarmResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AlarmResourceIT {

    private static final ZonedDateTime DEFAULT_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_ALARM_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ALARM_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Boolean DEFAULT_ENABLED = false;
    private static final Boolean UPDATED_ENABLED = true;

    private static final String DEFAULT_SOUND = "AAAAAAAAAA";
    private static final String UPDATED_SOUND = "BBBBBBBBBB";

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_REPEAT_DAYS = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_REPEAT_DAYS = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_SNOOZE_ENABLED = false;
    private static final Boolean UPDATED_SNOOZE_ENABLED = true;

    private static final Integer DEFAULT_SNOOZE_DURATION = 1;
    private static final Integer UPDATED_SNOOZE_DURATION = 2;

    private static final String ENTITY_API_URL = "/api/alarms";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAlarmMockMvc;

    private Alarm alarm;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Alarm createEntity(EntityManager em) {
        Alarm alarm = new Alarm()
            .time(DEFAULT_TIME)
            .alarmTime(DEFAULT_ALARM_TIME)
            .enabled(DEFAULT_ENABLED)
            .sound(DEFAULT_SOUND)
            .label(DEFAULT_LABEL)
            .repeatDays(DEFAULT_REPEAT_DAYS)
            .snoozeEnabled(DEFAULT_SNOOZE_ENABLED)
            .snoozeDuration(DEFAULT_SNOOZE_DURATION);
        return alarm;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Alarm createUpdatedEntity(EntityManager em) {
        Alarm alarm = new Alarm()
            .time(UPDATED_TIME)
            .alarmTime(UPDATED_ALARM_TIME)
            .enabled(UPDATED_ENABLED)
            .sound(UPDATED_SOUND)
            .label(UPDATED_LABEL)
            .repeatDays(UPDATED_REPEAT_DAYS)
            .snoozeEnabled(UPDATED_SNOOZE_ENABLED)
            .snoozeDuration(UPDATED_SNOOZE_DURATION);
        return alarm;
    }

    @BeforeEach
    public void initTest() {
        alarm = createEntity(em);
    }

    @Test
    @Transactional
    void createAlarm() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Alarm
        var returnedAlarm = om.readValue(
            restAlarmMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alarm)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Alarm.class
        );

        // Validate the Alarm in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAlarmUpdatableFieldsEquals(returnedAlarm, getPersistedAlarm(returnedAlarm));
    }

    @Test
    @Transactional
    void createAlarmWithExistingId() throws Exception {
        // Create the Alarm with an existing ID
        alarm.setId("existing_id");

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAlarmMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alarm)))
            .andExpect(status().isBadRequest());

        // Validate the Alarm in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAlarms() throws Exception {
        // Initialize the database
        alarmRepository.saveAndFlush(alarm);

        // Get all the alarmList
        restAlarmMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(alarm.getId())))
            .andExpect(jsonPath("$.[*].time").value(hasItem(sameInstant(DEFAULT_TIME))))
            .andExpect(jsonPath("$.[*].alarmTime").value(hasItem(sameInstant(DEFAULT_ALARM_TIME))))
            .andExpect(jsonPath("$.[*].enabled").value(hasItem(DEFAULT_ENABLED.booleanValue())))
            .andExpect(jsonPath("$.[*].sound").value(hasItem(DEFAULT_SOUND)))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].repeatDays").value(hasItem(DEFAULT_REPEAT_DAYS.toString())))
            .andExpect(jsonPath("$.[*].snoozeEnabled").value(hasItem(DEFAULT_SNOOZE_ENABLED.booleanValue())))
            .andExpect(jsonPath("$.[*].snoozeDuration").value(hasItem(DEFAULT_SNOOZE_DURATION)));
    }

    @Test
    @Transactional
    void getAlarm() throws Exception {
        // Initialize the database
        alarmRepository.saveAndFlush(alarm);

        // Get the alarm
        restAlarmMockMvc
            .perform(get(ENTITY_API_URL_ID, alarm.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(alarm.getId()))
            .andExpect(jsonPath("$.time").value(sameInstant(DEFAULT_TIME)))
            .andExpect(jsonPath("$.alarmTime").value(sameInstant(DEFAULT_ALARM_TIME)))
            .andExpect(jsonPath("$.enabled").value(DEFAULT_ENABLED.booleanValue()))
            .andExpect(jsonPath("$.sound").value(DEFAULT_SOUND))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
            .andExpect(jsonPath("$.repeatDays").value(DEFAULT_REPEAT_DAYS.toString()))
            .andExpect(jsonPath("$.snoozeEnabled").value(DEFAULT_SNOOZE_ENABLED.booleanValue()))
            .andExpect(jsonPath("$.snoozeDuration").value(DEFAULT_SNOOZE_DURATION));
    }

    @Test
    @Transactional
    void getNonExistingAlarm() throws Exception {
        // Get the alarm
        restAlarmMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAlarm() throws Exception {
        // Initialize the database
        alarmRepository.saveAndFlush(alarm);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alarm
        Alarm updatedAlarm = alarmRepository.findById(alarm.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAlarm are not directly saved in db
        em.detach(updatedAlarm);
        updatedAlarm
            .time(UPDATED_TIME)
            .alarmTime(UPDATED_ALARM_TIME)
            .enabled(UPDATED_ENABLED)
            .sound(UPDATED_SOUND)
            .label(UPDATED_LABEL)
            .repeatDays(UPDATED_REPEAT_DAYS)
            .snoozeEnabled(UPDATED_SNOOZE_ENABLED)
            .snoozeDuration(UPDATED_SNOOZE_DURATION);

        restAlarmMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAlarm.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedAlarm))
            )
            .andExpect(status().isOk());

        // Validate the Alarm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAlarmToMatchAllProperties(updatedAlarm);
    }

    @Test
    @Transactional
    void putNonExistingAlarm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alarm.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAlarmMockMvc
            .perform(put(ENTITY_API_URL_ID, alarm.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alarm)))
            .andExpect(status().isBadRequest());

        // Validate the Alarm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAlarm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alarm.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlarmMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(alarm))
            )
            .andExpect(status().isBadRequest());

        // Validate the Alarm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAlarm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alarm.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlarmMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alarm)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Alarm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAlarmWithPatch() throws Exception {
        // Initialize the database
        alarmRepository.saveAndFlush(alarm);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alarm using partial update
        Alarm partialUpdatedAlarm = new Alarm();
        partialUpdatedAlarm.setId(alarm.getId());

        partialUpdatedAlarm
            .time(UPDATED_TIME)
            .enabled(UPDATED_ENABLED)
            .sound(UPDATED_SOUND)
            .repeatDays(UPDATED_REPEAT_DAYS)
            .snoozeEnabled(UPDATED_SNOOZE_ENABLED);

        restAlarmMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAlarm.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAlarm))
            )
            .andExpect(status().isOk());

        // Validate the Alarm in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAlarmUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAlarm, alarm), getPersistedAlarm(alarm));
    }

    @Test
    @Transactional
    void fullUpdateAlarmWithPatch() throws Exception {
        // Initialize the database
        alarmRepository.saveAndFlush(alarm);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alarm using partial update
        Alarm partialUpdatedAlarm = new Alarm();
        partialUpdatedAlarm.setId(alarm.getId());

        partialUpdatedAlarm
            .time(UPDATED_TIME)
            .alarmTime(UPDATED_ALARM_TIME)
            .enabled(UPDATED_ENABLED)
            .sound(UPDATED_SOUND)
            .label(UPDATED_LABEL)
            .repeatDays(UPDATED_REPEAT_DAYS)
            .snoozeEnabled(UPDATED_SNOOZE_ENABLED)
            .snoozeDuration(UPDATED_SNOOZE_DURATION);

        restAlarmMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAlarm.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAlarm))
            )
            .andExpect(status().isOk());

        // Validate the Alarm in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAlarmUpdatableFieldsEquals(partialUpdatedAlarm, getPersistedAlarm(partialUpdatedAlarm));
    }

    @Test
    @Transactional
    void patchNonExistingAlarm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alarm.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAlarmMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, alarm.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(alarm))
            )
            .andExpect(status().isBadRequest());

        // Validate the Alarm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAlarm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alarm.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlarmMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(alarm))
            )
            .andExpect(status().isBadRequest());

        // Validate the Alarm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAlarm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alarm.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlarmMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(alarm)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Alarm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAlarm() throws Exception {
        // Initialize the database
        alarmRepository.saveAndFlush(alarm);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the alarm
        restAlarmMockMvc
            .perform(delete(ENTITY_API_URL_ID, alarm.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return alarmRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Alarm getPersistedAlarm(Alarm alarm) {
        return alarmRepository.findById(alarm.getId()).orElseThrow();
    }

    protected void assertPersistedAlarmToMatchAllProperties(Alarm expectedAlarm) {
        assertAlarmAllPropertiesEquals(expectedAlarm, getPersistedAlarm(expectedAlarm));
    }

    protected void assertPersistedAlarmToMatchUpdatableProperties(Alarm expectedAlarm) {
        assertAlarmAllUpdatablePropertiesEquals(expectedAlarm, getPersistedAlarm(expectedAlarm));
    }
}

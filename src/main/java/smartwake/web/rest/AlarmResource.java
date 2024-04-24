package smartwake.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import smartwake.domain.Alarm;
import smartwake.repository.AlarmRepository;
import smartwake.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link smartwake.domain.Alarm}.
 */
@RestController
@RequestMapping("/api/alarms")
@Transactional
public class AlarmResource {

    private final Logger log = LoggerFactory.getLogger(AlarmResource.class);

    private static final String ENTITY_NAME = "alarm";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AlarmRepository alarmRepository;

    public AlarmResource(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    /**
     * {@code POST  /alarms} : Create a new alarm.
     *
     * @param alarm the alarm to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new alarm, or with status {@code 400 (Bad Request)} if the alarm has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Alarm> createAlarm(@RequestBody Alarm alarm) throws URISyntaxException {
        log.debug("REST request to save Alarm : {}", alarm);
        if (alarm.getId() != null) {
            throw new BadRequestAlertException("A new alarm cannot already have an ID", ENTITY_NAME, "idexists");
        }
        alarm = alarmRepository.save(alarm);
        return ResponseEntity.created(new URI("/api/alarms/" + alarm.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, alarm.getId()))
            .body(alarm);
    }

    /**
     * {@code PUT  /alarms/:id} : Updates an existing alarm.
     *
     * @param id the id of the alarm to save.
     * @param alarm the alarm to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated alarm,
     * or with status {@code 400 (Bad Request)} if the alarm is not valid,
     * or with status {@code 500 (Internal Server Error)} if the alarm couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Alarm> updateAlarm(@PathVariable(value = "id", required = false) final String id, @RequestBody Alarm alarm)
        throws URISyntaxException {
        log.debug("REST request to update Alarm : {}, {}", id, alarm);
        if (alarm.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, alarm.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!alarmRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        alarm = alarmRepository.save(alarm);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, alarm.getId()))
            .body(alarm);
    }

    /**
     * {@code PATCH  /alarms/:id} : Partial updates given fields of an existing alarm, field will ignore if it is null
     *
     * @param id the id of the alarm to save.
     * @param alarm the alarm to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated alarm,
     * or with status {@code 400 (Bad Request)} if the alarm is not valid,
     * or with status {@code 404 (Not Found)} if the alarm is not found,
     * or with status {@code 500 (Internal Server Error)} if the alarm couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Alarm> partialUpdateAlarm(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Alarm alarm
    ) throws URISyntaxException {
        log.debug("REST request to partial update Alarm partially : {}, {}", id, alarm);
        if (alarm.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, alarm.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!alarmRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Alarm> result = alarmRepository
            .findById(alarm.getId())
            .map(existingAlarm -> {
                if (alarm.getTime() != null) {
                    existingAlarm.setTime(alarm.getTime());
                }
                if (alarm.getAlarmTime() != null) {
                    existingAlarm.setAlarmTime(alarm.getAlarmTime());
                }
                if (alarm.getEnabled() != null) {
                    existingAlarm.setEnabled(alarm.getEnabled());
                }
                if (alarm.getSound() != null) {
                    existingAlarm.setSound(alarm.getSound());
                }
                if (alarm.getLabel() != null) {
                    existingAlarm.setLabel(alarm.getLabel());
                }
                if (alarm.getRepeatDays() != null) {
                    existingAlarm.setRepeatDays(alarm.getRepeatDays());
                }
                if (alarm.getSnoozeEnabled() != null) {
                    existingAlarm.setSnoozeEnabled(alarm.getSnoozeEnabled());
                }
                if (alarm.getSnoozeDuration() != null) {
                    existingAlarm.setSnoozeDuration(alarm.getSnoozeDuration());
                }

                return existingAlarm;
            })
            .map(alarmRepository::save);

        return ResponseUtil.wrapOrNotFound(result, HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, alarm.getId()));
    }

    /**
     * {@code GET  /alarms} : get all the alarms.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of alarms in body.
     */
    @GetMapping("")
    public List<Alarm> getAllAlarms() {
        log.debug("REST request to get all Alarms");
        return alarmRepository.findAll();
    }

    /**
     * {@code GET  /alarms/:id} : get the "id" alarm.
     *
     * @param id the id of the alarm to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the alarm, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Alarm> getAlarm(@PathVariable("id") String id) {
        log.debug("REST request to get Alarm : {}", id);
        Optional<Alarm> alarm = alarmRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(alarm);
    }

    /**
     * {@code DELETE  /alarms/:id} : delete the "id" alarm.
     *
     * @param id the id of the alarm to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlarm(@PathVariable("id") String id) {
        log.debug("REST request to delete Alarm : {}", id);
        alarmRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
    }
}

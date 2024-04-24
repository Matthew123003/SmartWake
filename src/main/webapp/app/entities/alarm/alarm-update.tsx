import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IUserLogin } from 'app/shared/model/user-login.model';
import { getEntities as getUserLogins } from 'app/entities/user-login/user-login.reducer';
import { IAlarm } from 'app/shared/model/alarm.model';
import { getEntity, updateEntity, createEntity, reset } from './alarm.reducer';

export const AlarmUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const userLogins = useAppSelector(state => state.userLogin.entities);
  const alarmEntity = useAppSelector(state => state.alarm.entity);
  const loading = useAppSelector(state => state.alarm.loading);
  const updating = useAppSelector(state => state.alarm.updating);
  const updateSuccess = useAppSelector(state => state.alarm.updateSuccess);

  const handleClose = () => {
    navigate('/alarm');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUserLogins({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  // eslint-disable-next-line complexity
  const saveEntity = values => {
    values.time = convertDateTimeToServer(values.time);
    values.alarmTime = convertDateTimeToServer(values.alarmTime);
    if (values.snoozeDuration !== undefined && typeof values.snoozeDuration !== 'number') {
      values.snoozeDuration = Number(values.snoozeDuration);
    }

    const entity = {
      ...alarmEntity,
      ...values,
      userLogin: userLogins.find(it => it.id.toString() === values.userLogin?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          time: displayDefaultDateTime(),
          alarmTime: displayDefaultDateTime(),
        }
      : {
          ...alarmEntity,
          time: convertDateTimeFromServer(alarmEntity.time),
          alarmTime: convertDateTimeFromServer(alarmEntity.alarmTime),
          userLogin: alarmEntity?.userLogin?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="smartWakeApp.alarm.home.createOrEditLabel" data-cy="AlarmCreateUpdateHeading">
            Create or edit a Alarm
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="alarm-id" label="Id" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Time"
                id="alarm-time"
                name="time"
                data-cy="time"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label="Alarm Time"
                id="alarm-alarmTime"
                name="alarmTime"
                data-cy="alarmTime"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField label="Enabled" id="alarm-enabled" name="enabled" data-cy="enabled" check type="checkbox" />
              <ValidatedField label="Sound" id="alarm-sound" name="sound" data-cy="sound" type="text" />
              <ValidatedField label="Label" id="alarm-label" name="label" data-cy="label" type="text" />
              <ValidatedField label="Repeat Days" id="alarm-repeatDays" name="repeatDays" data-cy="repeatDays" type="date" />
              <ValidatedField
                label="Snooze Enabled"
                id="alarm-snoozeEnabled"
                name="snoozeEnabled"
                data-cy="snoozeEnabled"
                check
                type="checkbox"
              />
              <ValidatedField
                label="Snooze Duration"
                id="alarm-snoozeDuration"
                name="snoozeDuration"
                data-cy="snoozeDuration"
                type="text"
              />
              <ValidatedField id="alarm-userLogin" name="userLogin" data-cy="userLogin" label="User Login" type="select">
                <option value="" key="0" />
                {userLogins
                  ? userLogins.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/alarm" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default AlarmUpdate;

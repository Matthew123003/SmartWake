import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './alarm.reducer';

export const AlarmDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const alarmEntity = useAppSelector(state => state.alarm.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="alarmDetailsHeading">Alarm</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">Id</span>
          </dt>
          <dd>{alarmEntity.id}</dd>
          <dt>
            <span id="time">Time</span>
          </dt>
          <dd>{alarmEntity.time ? <TextFormat value={alarmEntity.time} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="alarmTime">Alarm Time</span>
          </dt>
          <dd>{alarmEntity.alarmTime ? <TextFormat value={alarmEntity.alarmTime} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="enabled">Enabled</span>
          </dt>
          <dd>{alarmEntity.enabled ? 'true' : 'false'}</dd>
          <dt>
            <span id="sound">Sound</span>
          </dt>
          <dd>{alarmEntity.sound}</dd>
          <dt>
            <span id="label">Label</span>
          </dt>
          <dd>{alarmEntity.label}</dd>
          <dt>
            <span id="repeatDays">Repeat Days</span>
          </dt>
          <dd>
            {alarmEntity.repeatDays ? <TextFormat value={alarmEntity.repeatDays} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="snoozeEnabled">Snooze Enabled</span>
          </dt>
          <dd>{alarmEntity.snoozeEnabled ? 'true' : 'false'}</dd>
          <dt>
            <span id="snoozeDuration">Snooze Duration</span>
          </dt>
          <dd>{alarmEntity.snoozeDuration}</dd>
          <dt>User Login</dt>
          <dd>{alarmEntity.userLogin ? alarmEntity.userLogin.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/alarm" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/alarm/${alarmEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default AlarmDetail;

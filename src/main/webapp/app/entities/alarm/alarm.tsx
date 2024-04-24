import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, TextFormat, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortUp, faSortDown } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, SORT } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './alarm.reducer';

export const Alarm = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const alarmList = useAppSelector(state => state.alarm.entities);
  const loading = useAppSelector(state => state.alarm.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    } else {
      return order === ASC ? faSortUp : faSortDown;
    }
  };

  return (
    <div>
      <h2 id="alarm-heading" data-cy="AlarmHeading">
        Alarms
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Link to="/alarm/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Alarm
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {alarmList && alarmList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  Id <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('time')}>
                  Time <FontAwesomeIcon icon={getSortIconByFieldName('time')} />
                </th>
                <th className="hand" onClick={sort('alarmTime')}>
                  Alarm Time <FontAwesomeIcon icon={getSortIconByFieldName('alarmTime')} />
                </th>
                <th className="hand" onClick={sort('enabled')}>
                  Enabled <FontAwesomeIcon icon={getSortIconByFieldName('enabled')} />
                </th>
                <th className="hand" onClick={sort('sound')}>
                  Sound <FontAwesomeIcon icon={getSortIconByFieldName('sound')} />
                </th>
                <th className="hand" onClick={sort('label')}>
                  Label <FontAwesomeIcon icon={getSortIconByFieldName('label')} />
                </th>
                <th className="hand" onClick={sort('repeatDays')}>
                  Repeat Days <FontAwesomeIcon icon={getSortIconByFieldName('repeatDays')} />
                </th>
                <th className="hand" onClick={sort('snoozeEnabled')}>
                  Snooze Enabled <FontAwesomeIcon icon={getSortIconByFieldName('snoozeEnabled')} />
                </th>
                <th className="hand" onClick={sort('snoozeDuration')}>
                  Snooze Duration <FontAwesomeIcon icon={getSortIconByFieldName('snoozeDuration')} />
                </th>
                <th>
                  User Login <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {alarmList.map((alarm, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/alarm/${alarm.id}`} color="link" size="sm">
                      {alarm.id}
                    </Button>
                  </td>
                  <td>{alarm.time ? <TextFormat type="date" value={alarm.time} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{alarm.alarmTime ? <TextFormat type="date" value={alarm.alarmTime} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{alarm.enabled ? 'true' : 'false'}</td>
                  <td>{alarm.sound}</td>
                  <td>{alarm.label}</td>
                  <td>{alarm.repeatDays ? <TextFormat type="date" value={alarm.repeatDays} format={APP_LOCAL_DATE_FORMAT} /> : null}</td>
                  <td>{alarm.snoozeEnabled ? 'true' : 'false'}</td>
                  <td>{alarm.snoozeDuration}</td>
                  <td>{alarm.userLogin ? <Link to={`/user-login/${alarm.userLogin.id}`}>{alarm.userLogin.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/alarm/${alarm.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button tag={Link} to={`/alarm/${alarm.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/alarm/${alarm.id}/delete`)}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">No Alarms found</div>
        )}
      </div>
    </div>
  );
};

export default Alarm;

import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './user-login.reducer';

export const UserLoginDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const userLoginEntity = useAppSelector(state => state.userLogin.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="userLoginDetailsHeading">User Login</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{userLoginEntity.id}</dd>
          <dt>
            <span id="username">Username</span>
          </dt>
          <dd>{userLoginEntity.username}</dd>
          <dt>
            <span id="password">Password</span>
          </dt>
          <dd>{userLoginEntity.password}</dd>
        </dl>
        <Button tag={Link} to="/user-login" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/user-login/${userLoginEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default UserLoginDetail;

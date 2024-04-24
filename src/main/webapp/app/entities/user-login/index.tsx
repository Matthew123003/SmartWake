import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import UserLogin from './user-login';
import UserLoginDetail from './user-login-detail';
import UserLoginUpdate from './user-login-update';
import UserLoginDeleteDialog from './user-login-delete-dialog';

const UserLoginRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<UserLogin />} />
    <Route path="new" element={<UserLoginUpdate />} />
    <Route path=":id">
      <Route index element={<UserLoginDetail />} />
      <Route path="edit" element={<UserLoginUpdate />} />
      <Route path="delete" element={<UserLoginDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default UserLoginRoutes;

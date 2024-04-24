import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Alarm from './alarm';
import AlarmDetail from './alarm-detail';
import AlarmUpdate from './alarm-update';
import AlarmDeleteDialog from './alarm-delete-dialog';

const AlarmRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Alarm />} />
    <Route path="new" element={<AlarmUpdate />} />
    <Route path=":id">
      <Route index element={<AlarmDetail />} />
      <Route path="edit" element={<AlarmUpdate />} />
      <Route path="delete" element={<AlarmDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AlarmRoutes;

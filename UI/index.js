import 'babel-polyfill';
import React from 'react';
import { Router, Route, IndexRedirect, IndexRoute, useRouterHistory } from 'react-router';
import { createHashHistory } from 'history';
import { render } from 'react-dom';
import { Provider } from 'react-redux';
import configureStore from 'store/configureStore';
// top component - layout
import App from 'components/App';
// route handlers
import SourceProfile from 'components/SourceProfile';
import OALDProfile from 'components/OALDProfile';
import PortfolioProfile from 'components/PortfolioProfile';
import TemplateProfile from 'components/TemplateProfile';
import LoginPage from 'components/LoginPage';
import ExceptionManagement from 'components/ExceptionManagement';
import DocumentFieldProfile from 'components/DocumentFieldProfile';
import FieldDefinition from 'components/FieldDefinition';
import IngestionConfiguration from 'components/IngestionConfiguration';
import IngestionReprocessing from 'components/IngestionReprocessing';
import Report from 'components/Report';
import About from 'components/About';
import Error from 'components/Error';

const store = configureStore();
const appHistory = useRouterHistory(createHashHistory)({ queryKey: false });

function checkAuth(nextState, replace) {
  const { loggedIn } = store.getState().auth;
  if (nextState.location.pathname !== '/login') {
    if (!loggedIn) {
      replace('/login');
    }
  } else {
    // If the user is already logged in, forward them to the homepage
    if (loggedIn) {
      replace('media-profile');
    }
  }
}

// bootstrap
render(
  <Provider store={store}>
    <Router history={appHistory}>
      <Route path="/" name="Home" component={App}>
        <IndexRedirect to="/login"/>
        <Route path="login" name="Login" component={LoginPage} onEnter={checkAuth} />
        <Route path="/media-profile" name="Media Profile">
          <IndexRedirect to="/media-profile/oald-profile"/>
          <Route path="source-profile" name="Source Profile" component={SourceProfile} onEnter={checkAuth} />
          <Route path="oald-profile" name="OALD Profile" component={OALDProfile} onEnter={checkAuth} />
          <Route path="portfolio-profile" name="Portfolio Profile" component={PortfolioProfile} onEnter={checkAuth} />
          <Route path="field-definition" name="Field Definition Profile" component={FieldDefinition} onEnter={checkAuth} />
          <Route path="document-field-profile" name="Document Field Profile" component={DocumentFieldProfile} onEnter={checkAuth} />
          <Route path="template-profile" name="Template Profile" component={TemplateProfile} onEnter={checkAuth} />
        </Route>
        <Route path="exception-management" name="Exception Management" component={ExceptionManagement} onEnter={checkAuth} />
        <Route path="/administration" name="Administration">
          <Route path="ingestion-configuration" name="Ingestion Configuration" component={IngestionConfiguration} onEnter={checkAuth} />
          <Route path="ingestion-reprocessing" name="Ingestion Reprocessing" component={IngestionReprocessing} onEnter={checkAuth} />
        </Route>
        <Route path="report" name="Report" component={Report} onEnter={checkAuth} />
        <Route path="about" name="About" component={About} onEnter={checkAuth} />
        <Route path="*" name="Page not found" component={Error} onEnter={checkAuth} />
      </Route>
    </Router>
  </Provider>,
  document.getElementById('app')
);

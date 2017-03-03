import { hashHistory } from 'react-router';
import toastr from 'toastr';
import baseToastrConfig from 'lib/baseToastrConfig';
import { 
  setAuth, 
  setLastActivity, 
  SET_LASTACTIVITY, 
  SET_AUTHENTICATION,
} from 'actions/auth';

export default store => next => action => {
  if (action.type !== SET_LASTACTIVITY && action.type !== SET_AUTHENTICATION) {
    const { lastActivity } = store.getState().auth;
    if ( Math.floor((new Date() - lastActivity)/60000) < 30 ) {
      next(setLastActivity(new Date()));
      next(action);
    } else {
      localStorage.clear('token');
      localStorage.clear('user');
      next(setAuth(false));
      toastr.options = baseToastrConfig();
      toastr.error('Session expired');
      hashHistory.push('/login');
    }
  } else {
    next(action);
  }
}

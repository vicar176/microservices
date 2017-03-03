import { hashHistory } from 'react-router';
import toastr from 'toastr';
import baseToastrConfig from 'lib/baseToastrConfig';
import { cancelTemplate } from 'actions/template';
import { cancelOALDProfile } from 'actions/oald';

export const SET_AUTHENTICATION = 'SET_AUTHENTICATION';
export const SET_LASTACTIVITY = 'SET_LASTACTIVITY';
export const SENDING_REQUEST = 'SENDING_REQUEST';

export function login(res) {
  return (dispatch) => {
    const currentUser = { id: res.user.id, firstName: res.user.profile.firstName.trim(), lastName: res.user.profile.lastName.trim() };
    localStorage.setItem('token', res.session.token);
    localStorage.setItem('user', JSON.stringify(currentUser));
    dispatch(setLastActivity(new Date()));
    dispatch(setAuth(true, currentUser));
    toastr.options = baseToastrConfig();
    toastr.success(`Hi ${currentUser.firstName} ${currentUser.lastName}`);
    hashHistory.push('media-profile/oald-profile');
  }
}

export function logout() {
  return (dispatch) => {
    localStorage.clear('token');
    localStorage.clear('user');
    dispatch(setAuth(false));
    dispatch(cancelTemplate());
    dispatch(cancelOALDProfile());
    hashHistory.push('/login');
  }
}

export function lastActivity () {
  return (dispatch) => {
    dispatch({ type: 'ACTIVITY'});
  }
}

export function setAuth(isLoggedIn, user) {
  return { type: SET_AUTHENTICATION, isLoggedIn, user };
}

export function setLastActivity(lastActivity) {
  return { type: SET_LASTACTIVITY, lastActivity };
}

export function sendingRequest(sending) {
  return { type: SENDING_REQUEST, sending };
}

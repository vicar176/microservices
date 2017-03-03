import {
  SET_AUTHENTICATION,
  SENDING_REQUEST,
  SET_LASTACTIVITY,
} from 'actions/auth';

const env = process.env.NODE_ENV;
// The initial application state
const initialState = {
  currentlySending: false,
  user: JSON.parse(localStorage.getItem('user')),
  loggedIn: !!localStorage.getItem('token'),
  lastActivity: new Date()
};

if (env === 'local') {
  initialState.user = {
    id: 0,
    firstName: 'devUser',
    lastName: ''
  }
  initialState.loggedIn = true;
}

export default function auth(state = initialState, action) {
  switch (action.type) {
    case SET_AUTHENTICATION:
      return Object.assign({}, state, { loggedIn: action.isLoggedIn, user: action.user });
    case SENDING_REQUEST:
      return Object.assign({}, state, { currentlySending: action.sending });
    case SET_LASTACTIVITY:
      return Object.assign({}, state, { lastActivity: action.lastActivity });
    default:
      return state;
  }
}

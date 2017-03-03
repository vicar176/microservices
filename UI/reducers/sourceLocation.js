import {
  IS_FETCHING,
  SET_PROFILE_LOG
} from 'actions/sourceLocation';

const initialState = {
  profileLogs: [],
  isFetching: false
};

export default function viewProfiles(state = initialState, action) {

  switch (action.type) {

    case IS_FETCHING:
      return Object.assign({}, state, { isFetching: action.isFetching });

    case SET_PROFILE_LOG:
      return Object.assign({}, state, { profileLogs: action.profileLogs });

    default:
      return state;

  }

}

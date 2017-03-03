import {
  IS_FETCHING,
  IS_NAME_AVAILABLE,
  IS_FILE_ZIPPED,
  SET_PROFILE_NAME,
  SET_ACCESS_TYPE,
  SET_HOST_LOCATION,
  SET_HOST_PORT,
  SET_USERNAME,
  SET_PASSWORD,
  SET_FILE_LOCATION,
  SET_ZIP_PASSWORD,
  PRELOAD_PROFILE,
  CANCEL_PROFILE
} from 'actions/sourceProfile';

const initialState = {
  profileName: '',
  accessType: '',
  hostLocation: '',
  hostPort: '',
  username: '',
  password: '',
  fileLocation: '',
  zipPassword: '',
  zipFile: [],
  profileLogs: [],
  isNameAvailable: true,
  isFetching: false,
  isFileZipped: '0'
};

export default function viewProfiles(state = initialState, action) {

  switch (action.type) {

    case IS_FETCHING:
      return Object.assign({}, state, { isFetching: action.isFetching });

    case IS_NAME_AVAILABLE:
      return Object.assign({}, state, { isNameAvailable: action.isNameAvailable });

    case IS_FILE_ZIPPED:
      return Object.assign({}, state, { isFileZipped: action.isFileZipped });

    case SET_PROFILE_NAME:
      return Object.assign({}, state, { profileName: action.profileName });

    case SET_ACCESS_TYPE:
      return Object.assign({}, state, { accessType: action.accessType });

    case SET_HOST_LOCATION:
      return Object.assign({}, state, { hostLocation: action.hostLocation });

    case SET_HOST_PORT:
      return Object.assign({}, state, { hostPort: action.hostPort });

    case SET_USERNAME:
      return Object.assign({}, state, { username: action.username });

    case SET_PASSWORD:
      return Object.assign({}, state, { password: action.password });

    case SET_FILE_LOCATION:
      return Object.assign({}, state, { fileLocation: action.fileLocation });

    case SET_ZIP_PASSWORD:
      return Object.assign({}, state, { zipPassword: action.zipPassword });

    case PRELOAD_PROFILE:
      return Object.assign({}, state, action.profile);

    // case SET_CURRENT_ROWS:
    //   return Object.assign({}, state, action.currentRows);

    case CANCEL_PROFILE:
      return Object.assign({}, initialState);

    default:
      return state;

  }

}

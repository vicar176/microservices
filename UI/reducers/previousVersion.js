import {
  ENABLE_PREVIOUS_VERSION,
  SELECT_PREVIOUS_VERSION,
  SELECT_PREVIOUS_VERSION_NUMBER,
  IS_FETCHING_PREVIOUS_VERSION,
  RECEIVE_PREVIOUS_VERSION_LIST,
  // CANCEL_PREVIOUS_MODE,
  RESET_PREVIOUS_VERSION
} from 'actions/previousVersion';

const initialState = {
  isProfileEnable: false,
  selectedPreviousVersion: {},
  selectedPreviousVersionNumber: '',
  isFetchingPreviousVersion: false,
  versionsList: [],
};

export default function previousVersion(state = initialState, action) {

  switch (action.type) {
    case ENABLE_PREVIOUS_VERSION:
      return Object.assign({}, state, { isProfileEnable: action.isEnabled });

    case SELECT_PREVIOUS_VERSION:
      return Object.assign({}, state, { selectedPreviousVersion: action.prevVersion });

    case SELECT_PREVIOUS_VERSION_NUMBER:
      return Object.assign({}, state, { selectedPreviousVersionNumber: action.prevVersionNumber });

    case IS_FETCHING_PREVIOUS_VERSION:
      return Object.assign({}, state, { isFetchingPreviousVersion: action.isFetching });

    case RECEIVE_PREVIOUS_VERSION_LIST:
      return Object.assign({}, state, { versionsList: action.versionsList });

    // case CANCEL_PREVIOUS_MODE:
    //   return Object.assign({}, state, { selectedPreviousVersion: initialState.selectedPreviousVersion });

    case RESET_PREVIOUS_VERSION:
      return Object.assign({}, initialState);

    // case PREVIOUS_VERSIONS:
    //   return Object.assign({}, state, { previousVersions: action.previousVersions });

    default:
      return state;
  }
}

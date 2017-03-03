import {
  SELECT_TAB,
  IS_FORM_VALID,
  SAVE_PROFILE_DATA,
  CLEAN_PROFILE_DATA,
  IS_EDIT_MODE,
  IS_INGESTION_SHOUTDOWN,
  IS_FETCHING_INGESTION_STATE,
  LAST_URL_HISTORY
} from 'actions/common';

const initialState = {
  selectedTab: '',
  isFormValid: true,
  preloadedProfileData: null,
  isEditMode: false,
  isIngestionShoutDown: true,
  isFetchingIngestionState: false,
  lastUrlHistory: ""
};

export default function common (state = initialState, action) {

  switch (action.type) {

    case SELECT_TAB:
      return Object.assign({}, state, { selectedTab: action.currTab });

    case IS_FORM_VALID:
      return Object.assign({}, state, { isFormValid: action.isFormValid });

    case SAVE_PROFILE_DATA:
      return Object.assign({}, state, { preloadedProfileData: action.preloadedProfileData });

    case CLEAN_PROFILE_DATA:
      return Object.assign({}, state, { preloadedProfileData: initialState.preloadedProfileData });

    case IS_EDIT_MODE:
      return Object.assign({}, state, { isEditMode: action.isEditMode });

    case IS_FETCHING_INGESTION_STATE:
      return Object.assign({}, state, { isFetchingIngestionState: action.isFetchingIngestionState });

    case IS_INGESTION_SHOUTDOWN:
      return Object.assign({}, state, { isIngestionShoutDown: action.isIngestionShoutDown });

    case LAST_URL_HISTORY:
      return Object.assign({}, state, { lastUrlHistory: action.value });

    default:
      return state;
  }

}

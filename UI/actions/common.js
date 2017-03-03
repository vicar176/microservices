import fetch from 'isomorphic-fetch';
import toastr from 'toastr';
import config from 'config';
import baseToastrConfig from 'lib/baseToastrConfig';

export const SELECT_TAB = 'SELECT_TAB';
export const IS_FORM_VALID = 'IS_FORM_VALID';
export const SAVE_PROFILE_DATA = 'SAVE_PROFILE_DATA';
export const CLEAN_PROFILE_DATA = 'CLEAN_PROFILE_DATA';
export const IS_EDIT_MODE = 'IS_EDIT_MODE';
export const FETCH_INGESTION_DOCUMENTS = 'FETCH_INGESTION_DOCUMENTS';
export const IS_FETCHING_INGESTION_STATE = 'IS_FETCHING_INGESTION_STATE';
export const IS_INGESTION_SHOUTDOWN = 'IS_INGESTION_SHOUTDOWN';
export const LAST_URL_HISTORY = 'LAST_URL_HISTORY';

const { ingestionState } = config;

// Utils
function showToastrOnError(message) {
  toastr.options = baseToastrConfig();
  toastr.error(message);
}

export function selectTab (currTab) {
  return {
    type: SELECT_TAB,
    currTab
  }
}

export function isFormValid (isFormValid) {
  return {
    type: IS_FORM_VALID,
    isFormValid
  }
}

export function cleanProfileData () {
  return {
    type: CLEAN_PROFILE_DATA
  }
}

export function saveProfileData (preloadedProfileData) {
  return {
    type: SAVE_PROFILE_DATA,
    preloadedProfileData
  }
}

export function isEditMode (isEditMode) {
  return {
    type: IS_EDIT_MODE,
    isEditMode
  }
}

function isFetchingIngestionState (isFetchingIngestionState) {
  return {
    type: IS_FETCHING_INGESTION_STATE,
    isFetchingIngestionState
  }
}

export function isIngestionShoutDown (isIngestionShoutDown) {
  return {
    type: IS_INGESTION_SHOUTDOWN,
    isIngestionShoutDown
  }
}

export function fetchIngestionState() {
  return async dispatch => {
    try {
      dispatch(isFetchingIngestionState(true));
      const noCache = Math.random();
      const response = await (await fetch(`${ingestionState}workflow-shutdown?noCache=${noCache}`)).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
        dispatch(isIngestionShoutDown(response.data.shutdownState));
        return response.data;
      }
      showToastrOnError(response.error.message);
    } catch (error) {
      showToastrOnError('Service unavailable');
    } finally {
      dispatch(isFetchingIngestionState(false));
    }
  }
}

export function updateIngestionState (state) {
  return async (dispatch, getState) => {
    try {
      dispatch(isFetchingIngestionState(true));
      const { firstName, lastName } = getState().auth.user;
      const response = await (await fetch(`${ingestionState}/workflow-shutdown`, {
        method: 'put',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          shutdownState: state,
          updatedBy: firstName +' '+ lastName,
        })
      })).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
        // dispatch(isIngestionShoutDown(false));
        toastr.success('Document ingestion has been turned ' + (state ? 'off' : 'on'));
        return response.data;
      }
      // dispatch(saveTemplateError());
      showToastrOnError(response.error.message);
    } catch (error) {
      showToastrOnError('Service unavailable');
    } finally {
      dispatch(isFetchingIngestionState(false));
    }
  }
}

export function lastUrlHistory (value) {
  return {
    type: LAST_URL_HISTORY,
    value
  }
}

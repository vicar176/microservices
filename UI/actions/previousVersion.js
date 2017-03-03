import fetch from 'isomorphic-fetch';
import config from 'config';
import toastr from 'toastr';
import baseToastrConfig from 'lib/baseToastrConfig';

export const ENABLE_PREVIOUS_VERSION = 'ENABLE_PREVIOUS_VERSION';
export const SELECT_PREVIOUS_VERSION = 'SELECT_PREVIOUS_VERSION';
export const SELECT_PREVIOUS_VERSION_NUMBER = 'SELECT_PREVIOUS_VERSION_NUMBER';
export const IS_FETCHING_PREVIOUS_VERSION = 'IS_FETCHING_PREVIOUS_VERSION';
export const RECEIVE_PREVIOUS_VERSION_LIST = 'RECEIVE_PREVIOUS_VERSION_LIST';
// export const CANCEL_PREVIOUS_MODE = 'CANCEL_PREVIOUS_MODE';
export const RESET_PREVIOUS_VERSION = 'RESET_PREVIOUS_VERSION';
export const FETCH_PROFILE_VERSION = 'FETCH_PROFILE_VERSION';

const { profileService } = config;

function showToastrOnError(message) {
  toastr.options = baseToastrConfig();
  toastr.error(message);
}

export function resetPreviousVersion () {
  return {
    type: RESET_PREVIOUS_VERSION
  }
}

export function enablePreviousVersion (isEnabled) {
  return {
    type: ENABLE_PREVIOUS_VERSION,
    isEnabled
  }
}

export function selectPreviousVersion (prevVersion) {
  return {
    type: SELECT_PREVIOUS_VERSION,
    prevVersion
  }
}

export function selectPreviousVersionNumber (prevVersionNumber) {
  return {
    type: SELECT_PREVIOUS_VERSION_NUMBER,
    prevVersionNumber
  }
}

function isFetchingPreviousVersion (isFetching) {
  return {
    type: IS_FETCHING_PREVIOUS_VERSION,
    isFetching
  }
}

function receiveVersionsList (versionsList) {
  return {
    type: RECEIVE_PREVIOUS_VERSION_LIST,
    versionsList
  }
}

// export function cancelPreviousMode () {
//   return {
//     type: CANCEL_PREVIOUS_MODE
//   }
// }

export function fetchVersion (query, version = '') {
  return async dispatch => {
    try {
      // /media-profiles/profile-type/{id}/versions/{version}
      dispatch(isFetchingPreviousVersion(true));
      const response = await (await fetch(`${profileService}${query}/versions/${version}`)).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
        if (version) {
          return response.data;
        }
        dispatch(receiveVersionsList(response.data));
      } else {
        showToastrOnError("Error Searching for Versions");
      }
    } catch (error) {
      showToastrOnError(response.error.message);
    } finally {
      dispatch(isFetchingPreviousVersion(false));
    }
  }
}

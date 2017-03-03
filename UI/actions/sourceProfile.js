export const IS_FETCHING = 'IS_FETCHING';
export const IS_NAME_AVAILABLE = 'IS_NAME_AVAILABLE';
export const IS_FILE_ZIPPED = 'IS_FILE_ZIPPED';
export const SET_PROFILE_NAME = 'SET_PROFILE_NAME';
export const SET_ACCESS_TYPE = 'SET_ACCESS_TYPE';
export const SET_HOST_LOCATION = 'SET_HOST_LOCATION';
export const SET_HOST_PORT = 'SET_HOST_PORT';
export const SET_USERNAME = 'SET_USERNAME';
export const SET_PASSWORD = 'SET_PASSWORD';
export const SET_FILE_LOCATION = 'SET_FILE_LOCATION';
export const SET_ZIP_PASSWORD = 'SET_ZIP_PASSWORD';
export const PRELOAD_PROFILE = 'PRELOAD_PROFILE';
// export const SET_CURRENT_ROWS = 'SET_CURRENT_ROWS';
export const CANCEL_PROFILE = 'CANCEL_PROFILE';

export function isFetching (isFetching) {
  return {
    type: IS_FETCHING,
    isFetching
  }
}

export function isNameAvailable (isNameAvailable) {
  return {
    type: IS_NAME_AVAILABLE,
    isNameAvailable
  }
}

export function setProfileName (profileName) {
  return {
    type: SET_PROFILE_NAME,
    profileName
  }
}

export function setAccessType (accessType) {
  return {
    type: SET_ACCESS_TYPE,
    accessType
  }
}

export function setHostLocation (hostLocation) {
  return {
    type: SET_HOST_LOCATION,
    hostLocation
  }
}

export function setHostPort (hostPort) {
  return {
    type: SET_HOST_PORT,
    hostPort
  }
}

export function setUsername (username) {
  return {
    type: SET_USERNAME,
    username
  }
}

export function setPassword (password) {
  return {
    type: SET_PASSWORD,
    password
  }
}

export function setFileLocation (fileLocation) {
  return {
    type: SET_FILE_LOCATION,
    fileLocation
  }
}

export function checkFileIsZippped (isFileZipped) {
  return {
    type: IS_FILE_ZIPPED,
    isFileZipped
  }
}

export function setZipPassword (zipPassword) {
  return {
    type: SET_ZIP_PASSWORD,
    zipPassword
  }
}

// export function setCurrentRows (currentRows) {
//   return {
//     type: SET_CURRENT_ROWS,
//     currentRows
//   }
// }

export function cancelProfile () {
  return { type: CANCEL_PROFILE }
}

export function preloadProfile (profile) {
  return dispatch => {
    dispatch(isNameAvailable(true));
    dispatch(setProfileName(profile.profileName));
    dispatch(setAccessType(profile.accessType.toLowerCase()));
    dispatch(setHostLocation(profile.hostLocation));
  }
}

export function fetchPortfolioName (profileName) {
  return dispatch => {
    dispatch(isFetching(true));
    const takenNames = ['Citibank', 'Synchorny', 'Capital One', 'Barclays Bank'];
    const isAvailable = takenNames.indexOf(profileName) === -1;
    dispatch(isFetching(false));
    dispatch(isNameAvailable(isAvailable));
    dispatch(setProfileName(profileName));
  }
}

// export function fetchViewProfilesRows (query) {
//   return dispatch => {
//     dispatch(isFetching(true));
//     const rows = [];
//     for (let i = 0; i < query.itemsPerPage; i++) {
//       rows.push({
//         profileName: ['Barclays Bank FTP', 'Capital One Media', 'Capital One STMT', 'Citibank FTP', 'Synchorny FTP'][Math.floor((Math.random() * 5))],
//         accessType: 'FTP',
//         hostLocation: ['ftp.barclaybankmedia.com', 'ftp.capitalone.com/mcm/media', 'ftp.capitalone.com/mcm/stmt', 'ftp.citibank.mcm.com', 'ftp.synchrony.mcm.com'][Math.floor((Math.random() * 5))],
//         version: ['Version 1', 'Version 2', 'Version 3', 'Version 4'][Math.floor((Math.random() * 4))],
//         configId: i
//       });
//     }
//     rows.totalItems = 50;
//     dispatch(isFetching(false));
//     dispatch(setCurrentRows(rows));
//   }
// }

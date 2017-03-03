export const IS_FETCHING = 'IS_FETCHING';
export const SET_PROFILE_LOG = 'SET_PROFILE_LOG';

export function isFetching (isFetching) {
  return {
    type: IS_FETCHING,
    isFetching
  }
}

export function setProfileLogs (profileLogs) {
  return {
    type: SET_PROFILE_LOG,
    profileLogs
  }
}

export function fetchProfileLog (rowId) {
  return dispatch => {
    dispatch(isFetching(true));
    const numberOfErrors = Math.floor(Math.random() * 20) + 1;
    const errorsRows = [];
    for (let i = 0; i < numberOfErrors; i++) {
      errorsRows.push({
        'profileId': i,
        'errorDate': ['05/01/201614:30:55', '05/02/2016 06:30:55', '08/08/2016 06:56:55'][Math.floor((Math.random() * 3))],
        'errorCode': [421, 533, 404, 503][Math.floor((Math.random() * 4))],
        'errorDetail': ['Service not available, closing control connection', 'Not authorized to make the connection', 'Can\'t open data connection', 'Cannnot extract contents from zip file. Zip file password incorrect', 'System could not copy files to destination, insufficient space'][Math.floor((Math.random() * 5))],
        'executionType': ['Scheduled', 'Manual (username)'][Math.floor((Math.random() * 2))]
      });
    }
    setTimeout(() => {
      dispatch(isFetching(false));
      dispatch(setProfileLogs(errorsRows));
    }, 2000);
  }
}

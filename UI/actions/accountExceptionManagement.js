// import fetch from 'isomorphic-fetch';

export const SELECT_REVIEW_METHOD = 'SELECT_REVIEW_METHOD';
export const ACTIVE_VARIFICATION = 'ACTIVE_VARIFICATION';
export const REQUEST_ACCOUNT_DATA = 'REQUEST_ACCOUNT_DATA';
export const ERROR_REQUEST_ACCOUNT_DATA = 'ERROR_REQUEST_ACCOUNT_DATA';
export const RECEIVE_ACCOUNT_DATA = 'RECEIVE_ACCOUNT_DATA';
export const EDITING_MODE_ACTIVE = 'EDITING_MODE_ACTIVE';

// Review method state
export function selectReviewMethod (reviewMethod) {
  return {
    type: SELECT_REVIEW_METHOD,
    reviewMethod
  }
}

// Set an individual account to verified or unverified using checkboxes
export function activeVerification (verificationState) {
  return {
    type: ACTIVE_VARIFICATION,
    verificationState
  }
}

// Set an individual account to verified or unverified using checkboxes
export function editAccountField (editedField) {
  return {
    type: EDITING_MODE_ACTIVE,
    editedField
  }
}

// @TODO:JSRef
// Those request utilities should be moved to a global place where they can be reused

// Fetch state for loading request phase
function requestAccountData () {
  return {
    type: REQUEST_ACCOUNT_DATA
  }
}

// Fetch data error
// function errorRequestAccountData () {
//   return {
//     type: ERROR_REQUEST_ACCOUNT_DATA
//   }
// }

// Fetch ready state
function receiveAccountData (accountsData) {
  return {
    type: RECEIVE_ACCOUNT_DATA,
    accountsData
  }
}

// Fetch task for getting all account detail from server
export function fetchAccountDetail (accountsData) {
  return dispatch => {
    dispatch(requestAccountData());
    setTimeout(()=> {
      dispatch(receiveAccountData(accountsData));
    }, 1000);

    // @TODO:JSRef
    // Fetch functionality to be used once we have the endpoint.
    //
    // fetch(accountDataUrl)
    //   .then(res => res.json())
    //   .then(json => {
    //     if(json.status >= 400) {
    //       dispatch(errorRequestAccountData());
    //       throw new Error("Bad response from server");
    //       return;
    //     }
    //     dispatch(receiveAccountData());
    //     return json;
    //   })
    //   .catch((err) => {
    //     dispatch(errorRequestAccountData());
    //   });
  }
}

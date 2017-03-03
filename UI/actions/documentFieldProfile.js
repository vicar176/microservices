import fetch from 'isomorphic-fetch';
import toastr from 'toastr';
import config from 'config';
import baseToastrConfig from 'lib/baseToastrConfig';

// Actions constants for view
export const IS_FETCHING_DOCUMENTS = 'IS_FETCHING_DOCUMENTS';
export const RECEIVE_DOCUMENTS = 'RECEIVE_DOCUMENTS';
export const SELECT_DOCUMENTS = 'SELECT_DOCUMENTS';
export const IS_FETCHING_FIELD_DEFINITIONS = 'IS_FETCHING_FIELD_DEFINITIONS';
export const RECEIVE_FIELD_DEFINITIONS = 'RECEIVE_FIELD_DEFINITIONS';
export const IS_SAVING_DOCUMENT_FIELD = 'IS_SAVING_DOCUMENT_FIELD';
export const CANCEL_DOCUMENT_PROFILE = 'CANCEL_DOCUMENT_PROFILE';
export const CLEAN_DOCUMENT_PROFILE = 'CLEAN_DOCUMENT_PROFILE';
export const CLEAN_DOCUMENT_FIELD = 'CLEAN_DOCUMENT_FIELD';

// Actions constant for field profile data
export const SET_ACTIVE_STATE = 'SET_ACTIVE_STATE';
export const SET_ACCOUNT_VERIFICATION = 'SET_ACCOUNT_VERIFICATION';
export const SET_FIELD_DEFINITIONS = 'SET_FIELD_DEFINITIONS';
export const ADD_FIELD_DEFINITION = 'ADD_FIELD_DEFINITION';
export const REMOVE_FIELD_DEFINITION = 'REMOVE_FIELD_DEFINITION';
export const UPDATE_FIELD_DEFINITION = 'UPDATE_FIELD_DEFINITION';
export const UPDATE_REQUIREMENT_LEVEL = 'UPDATE_REQUIREMENT_LEVEL';

// Map endpoints paths
const { profileService, portfolioService } = config;
const fieldDefinition = `${profileService}document-fields/field-definitions`;
const documentFieldService = `${profileService}document-fields/document-field-definitions`;

// Utils
function showToastrOnError(message) {
  toastr.options = baseToastrConfig();
  toastr.error(message);
}

// Field Profile component
export function selectDocument (selectedDocument) {
  return {
    type: SELECT_DOCUMENTS,
    selectedDocument
  }
}

export function isFetchingDocuments (isFetching) {
  return {
    type: IS_FETCHING_DOCUMENTS,
    isFetching
  }
}

export function receiveDocuments (receivedDocuments) {
  return {
    type: RECEIVE_DOCUMENTS,
    receivedDocuments
  }
}

export function activeChange (value) {
  return {
    type: ACTIVE_CHANGE,
    value
  }
}

export function fetchDocuments (docType) {
  return async dispatch => {
    try {
      dispatch(isFetchingDocuments(true));
      const noCache = Math.random();
      const id = docType ? `code=${docType}&` : '';
      const endpoint = docType ? documentFieldService : `${portfolioService}document-types`;
      // /media-profiles/document-fields/document-field-definitions/{id}/version/{version}
      const response = await (await fetch(`${endpoint}?${id}noCache=${noCache}`)).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
        // if (response.data.length < 1) {
        //   showToastrOnError('No Document Profiles Defined');
        // }
        if (docType) {
          dispatch(receiveDocuments(response.data));
        }
        // console.log('fetchDocuments:: ', response);
        return response;
      }
      showToastrOnError(response.error.message);
    } catch (error) {
      showToastrOnError('Service unavailable');
    } finally {
      dispatch(isFetchingDocuments(false));
    }
  }
}

// Field Definitions dropdown
export function isFetchingFieldDefinitions (isFetching) {
  return {
    type: IS_FETCHING_FIELD_DEFINITIONS,
    isFetching
  }
}

export function receiveFieldDefinitions (receivedFieldDefinitions) {
  return {
    type: RECEIVE_FIELD_DEFINITIONS,
    receivedFieldDefinitions
  }
}

/*
 * ATTENTION: This fetch needs refactoring from BE,
 * at this time is returning innecesary data causing performance issues.
 * Recommended structure for dropdown: [{ id: XX, value: YY }, { id: XXX, value: YYY }]
*/
export function fetchFieldDefinitions () {
  return async dispatch => {
    try {
      dispatch(isFetchingFieldDefinitions(true));
      const noCache = Math.random();
      // /media-profiles/document-fields/document-field-definitions/{id}/version/{version}
      const response = await (await fetch(`${fieldDefinition}?noCache=${noCache}`)).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
        if (response.data.length < 1) {
          showToastrOnError('No Field Definitions defined');
        }
        dispatch(receiveFieldDefinitions(response.data));
        return response.data;
      }
      showToastrOnError(response.error.message);
    } catch (error) {
      showToastrOnError('Service unavailable');
    } finally {
      dispatch(isFetchingFieldDefinitions(false));
    }
  }
}

// Field Profile data
export function setDocumentFieldProfile (profile) {
  return dispatch => {
    if (profile) {
      dispatch(setFieldDefinitions(profile.fieldDefinitions));
      dispatch(setAccountVerification(profile.accountVerification));
      dispatch(setActiveState(profile.active));
    }
  }
}

export function setFieldDefinitions (fieldDefinitions) {
  return {
    type: SET_FIELD_DEFINITIONS,
    fieldDefinitions
  }
}

export function addFieldDefinition (fieldDefinition) {
  return {
    type: ADD_FIELD_DEFINITION,
    fieldDefinition
  }
}

export function removeFieldDefinition (fieldName) {
  return {
    type: REMOVE_FIELD_DEFINITION,
    fieldName
  }
}

export function updateFieldDefinition (oldFieldName, newFieldObj) {
  return {
    type: UPDATE_FIELD_DEFINITION,
    oldFieldName,
    newFieldObj
  }
}

export function updateRequirementLevel (fieldName, isRequired) {
  return {
    type: UPDATE_REQUIREMENT_LEVEL,
    required: isRequired === 'true' ? true : false,
    fieldName
  }
}

export function setAccountVerification (accountVerification) {
  return {
    type: SET_ACCOUNT_VERIFICATION,
    accountVerification
  }
}

export function setActiveState (activeState) {
  return {
    type: SET_ACTIVE_STATE,
    activeState
  }
}

export function cancelDocumentFieldProfile (currProfile) {
  return {
    type: CANCEL_DOCUMENT_PROFILE,
    currProfile
  }
}

export function cleanDocumentFieldProfile () {
  return {
    type: CLEAN_DOCUMENT_PROFILE
  }
}

export function cleanDocumentField () {
  return {
    type: CLEAN_DOCUMENT_FIELD
  }
}

export function isSavingDocumentFieldProfile (isFetching) {
  return {
    type: IS_SAVING_DOCUMENT_FIELD,
    isFetching
  }
}

export function saveDocumentFieldProfile (profileId) {
  return async (dispatch, getState) => {
    try {
      dispatch(isSavingDocumentFieldProfile(true));
      const { firstName, lastName } = getState().auth.user;
      const { active, accountVerification, fieldDefinitions } = getState().documentFieldProfile;
      const { selectedDocument } = getState().documentField;
      // /media-profiles/document-fields/field-definitions/
      const response = await (await fetch(`${documentFieldService}/${profileId || ''}`, {
        method: 'put',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          id: profileId,
          active,
          fieldDefinitions,
          accountVerification,
          documentType: {
            id: selectedDocument.value,
            code: selectedDocument.id
          },
          updatedBy: firstName +' '+ lastName,
        })
      })).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
        // dispatch(cancelDocumentFieldProfile());
        toastr.success(profileId ? 'Document Field Profile Updated' : 'Document Field Profile Saved');
        return response.error;
      }
      showToastrOnError(response.error.message);
    } catch (error) {
      showToastrOnError('Service unavailable');
    } finally {
      dispatch(isSavingDocumentFieldProfile(false));
    }
  }
}

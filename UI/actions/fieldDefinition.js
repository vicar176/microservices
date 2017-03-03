import fetch from 'isomorphic-fetch';
import toastr from 'toastr';
import config from 'config';
import queryString from 'query-string';
import baseToastrConfig from 'lib/baseToastrConfig';

export const IS_FETCHING_FIELD_DEFINITIONS = 'IS_FETCHING_FIELD_DEFINITIONS';
export const IS_FETCHING_FIELD_MAPPING_OPTIONS = 'IS_FETCHING_FIELD_MAPPING_OPTIONS';
export const RECEIVE_FIELDDEFINITIONS = 'RECEIVE_FIELDDEFINITIONS';
export const RECEIVE_FIELD_MAPPING_OPTIONS = 'RECEIVE_FIELD_MAPPING_OPTIONS';
export const SET_FIELD_DEFINITION_ID = 'SET_FIELD_DEFINITION_ID';
export const FIELD_NAME_CHANGE = 'FIELD_NAME_CHANGE';
export const FIELD_DESCRIPTION_CHANGE = 'FIELD_DESCRIPTION_CHANGE';
export const FIELD_TYPE_CHANGE = 'FIELD_TYPE_CHANGE';
export const ACCOUNT_MAPPING_CHANGE = 'ACCOUNT_MAPPING_CHANGE';
export const ACTIVE_CHANGE = 'ACTIVE_CHANGE';
export const ENCRYPT_CHANGE = 'ENCRYPT_CHANGE';
export const FIELD_MAPPING_CHANGE = 'FIELD_MAPPING_CHANGE';
export const ADD_FIELD_MAPPING = 'ADD_FIELD_MAPPING';
export const REMOVE_ALL_FIELD_MAPPING = 'REMOVE_ALL_FIELD_MAPPING';
export const ADD_MAP = 'ADD_MAP';
export const REMOVE_MAP = 'REMOVE_MAP';
export const UPDATED_FIELD = 'UPDATED_FIELD';
export const IS_SAVING_FIELDDEFINITIONS = 'IS_SAVING_FIELDDEFINITIONS';
export const CANCEL_NEW_FIELD = 'CANCEL_NEW_FIELD';

const { profileService } = config;

const fieldDefinition = `${profileService}document-fields/field-definitions`;

function showToastrOnError(message) {
  toastr.options = baseToastrConfig();
  toastr.error(message);
}

export function fetchFieldMappingOptions () {
  return async dispatch => {
    try {
      dispatch(isFetchingFieldMappingOptions(true));
      const noCache = Math.random();
      const response = await (await fetch(`https://phxiodpsgwd01.internal.mcmcg.com:7505/accounts/fields?noCache=${noCache}`)).json();
      dispatch(isFetchingFieldMappingOptions(false));
      const code = response.error.code;
      if (code === 200 || code === 201) {
        dispatch(receiveFieldMappingOptions(response.data));
      } else {
        showToastrOnError(response.error.message);
      }
    } catch (error) {
      dispatch(isFetchingFieldMappingOptions(false));
      showToastrOnError('Service unavailable');
    }
  }
}

export function isFetchingFieldMappingOptions (isFetching) {
  return {
    type: IS_FETCHING_FIELD_MAPPING_OPTIONS,
    isFetching
  }
}

function receiveFieldMappingOptions (fieldMappingOptions) {
  return {
    type: RECEIVE_FIELD_MAPPING_OPTIONS,
    fieldMappingOptions
  }
}

export function isFetchingFieldDefinitions (isFetching) {
  return {
    type: IS_FETCHING_FIELD_DEFINITIONS,
    isFetching
  }
}

function receiveFieldDefinitions (fieldDefinitions) {
  return {
    type: RECEIVE_FIELDDEFINITIONS,
    fieldDefinitions
  }
}

export function setFieldDefinitionId (fieldId) {
  return {
    type: SET_FIELD_DEFINITION_ID,
    fieldId
  }
}

export function fetchFieldDefinitionList (queryString) {
  return async dispatch => {
    try {
      dispatch(isFetchingFieldDefinitions(true));
      const noCache = Math.random();
      const fieldListQuery = (queryString.indexOf('filter') > -1) ? `&${queryString}` : `filter=fieldName="@@"&${queryString}`;
      // console.log('fetchFieldDefinitionList:: ', fieldListQuery);
      const response = await (await fetch(`${fieldDefinition}?${fieldListQuery}&noCache=${noCache}`)).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
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

export function fieldNameChange (value) {
  return {
    type: FIELD_NAME_CHANGE,
    value
  }
}

export function fieldDescriptionChange (value) {
  return {
    type: FIELD_DESCRIPTION_CHANGE,
    value
  }
}

export function fieldTypeChange (value) {
  return {
    type: FIELD_TYPE_CHANGE,
    value
  }
}

export function accountMappingChange (value) {
  return {
    type: ACCOUNT_MAPPING_CHANGE,
    value
  }
}

export function activeChange (value) {
  return {
    type: ACTIVE_CHANGE,
    value
  }
}

export function encryptChange (value) {
  return {
    type: ENCRYPT_CHANGE,
    value
  }
}

export function fieldMappingChange (fieldMappingId, mappingTo) {
  return {
    type: FIELD_MAPPING_CHANGE,
    fieldMappingId,
    mappingTo
  }
}

export function removeAllFieldMapping () {
  return {
    type: REMOVE_ALL_FIELD_MAPPING
  }
}

export function addMap (joinType) {
  return {
    type: ADD_MAP,
    joinType
  }
}

export function removeMap (fieldMappingId) {
  return {
    type: REMOVE_MAP,
    fieldMappingId
  }
}

export function isSavingFieldDefinitions (isUpdating) {
  return {
    type: IS_SAVING_FIELDDEFINITIONS,
    isUpdating
  }
}

export function saveField () {
  return async (dispatch, getState) => {
    try {
      dispatch(isSavingFieldDefinitions(true));
      const { firstName, lastName } = getState().auth.user;
      const { id, fieldName, fieldType, fieldDescription, databaseMapping, operators, active, encrypt } = getState().fieldDefinitionProfile;
      const versionNumer = getState().previousVersion.versionsList[0] + 1;
      // /media-profiles/document-fields/field-definitions/
      const response = await (await fetch(`${fieldDefinition}/${id}`, {
        method: 'put',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          id,
          fieldName,
          fieldType,
          fieldDescription,
          databaseMapping,
          operators,
          active,
          encrypt,
          updatedBy: firstName +' '+ lastName
        })
      })).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
        dispatch(cancelNewField());
        toastr.success(id ? `Version ${versionNumer} for "${fieldName}" has been created` : 'New document field created');
      } else {
        // dispatch(saveTemplateError());
        showToastrOnError(response.error.message);
      }
    } catch (error) {
      showToastrOnError('Service unavailable');
    } finally {
      dispatch(isSavingFieldDefinitions(false));
    }
  }
}

export function cancelNewField() {
  return {
    type: CANCEL_NEW_FIELD
  }
}

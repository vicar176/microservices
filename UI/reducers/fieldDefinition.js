import {
  IS_FETCHING_FIELD_DEFINITIONS,
  IS_FETCHING_FIELD_MAPPING_OPTIONS,
  RECEIVE_FIELDDEFINITIONS,
  RECEIVE_FIELD_MAPPING_OPTIONS,
  IS_SAVING_FIELDDEFINITIONS
} from 'actions/fieldDefinition';

// The initial application state
const initialState = {
  isSavingFieldDefinitions: false,
  fieldDefinitions: [],
  isFetchingFieldDefinitions: false,
  isFetchingFieldMappingOptions: false,
  fieldMappingOptions: [],
};

export default function fieldDefinition(state = initialState, action) {
  switch (action.type) {
    case IS_FETCHING_FIELD_DEFINITIONS:
      return Object.assign({}, state, { isFetchingFieldDefinitions: action.isFetching });

    case IS_FETCHING_FIELD_MAPPING_OPTIONS:
      return Object.assign({}, state, { isFetchingFieldMappingOptions: action.isFetching });

    case IS_SAVING_FIELDDEFINITIONS:
      return Object.assign({}, state, { isSavingFieldDefinitions: action.isSaving });

    case RECEIVE_FIELDDEFINITIONS:
      return Object.assign({}, state, { fieldDefinitions: action.fieldDefinitions });

    case RECEIVE_FIELD_MAPPING_OPTIONS:
      return Object.assign({}, state, { fieldMappingOptions: action.fieldMappingOptions });

    default:
      return state;
  }
}

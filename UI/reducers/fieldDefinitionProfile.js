import {
  SET_FIELD_DEFINITION_ID,
  FIELD_NAME_CHANGE,
  FIELD_DESCRIPTION_CHANGE,
  FIELD_TYPE_CHANGE,
  ACCOUNT_MAPPING_CHANGE,
  ACTIVE_CHANGE,
  ENCRYPT_CHANGE,
  REMOVE_ALL_FIELD_MAPPING,
  FIELD_MAPPING_CHANGE,
  ADD_MAP,
  REMOVE_MAP,
  UPDATED_FIELD,
  CANCEL_NEW_FIELD,
} from 'actions/fieldDefinition';

// The initial application state
const initialState = {
  id: '',
  fieldName: '',
  fieldDescription: '',
  fieldType: '',
  active: true,
  encrypt: false,
  accountMapping: false,
  databaseMapping: [],
  operators: []
};

export default function fieldDefinitionProfile(state = initialState, action) {
  switch (action.type) {
    case SET_FIELD_DEFINITION_ID:
      return Object.assign({}, state, { id: action.fieldId });

    case FIELD_NAME_CHANGE:
      return Object.assign({}, state, { fieldName: action.value });

    case FIELD_TYPE_CHANGE:
      return Object.assign({}, state, { fieldType: action.value });

    case FIELD_DESCRIPTION_CHANGE:
      return Object.assign({}, state, { fieldDescription: action.value });

    case ACCOUNT_MAPPING_CHANGE:
      return Object.assign({}, state, { accountMapping: action.value });

    case ACTIVE_CHANGE:
      return Object.assign({}, state, { active: action.value });

    case ENCRYPT_CHANGE:
      return Object.assign({}, state, { encrypt: action.value });

    case REMOVE_ALL_FIELD_MAPPING:
      return Object.assign({}, state, { databaseMapping: []});

    case FIELD_MAPPING_CHANGE: {
      const fieldMappingIndex = state.databaseMapping.findIndex(fieldMap => fieldMap.id === action.fieldMappingId);
      const currentFieldMapping = state.databaseMapping[fieldMappingIndex];
      const newFieldMapping = Object.assign({}, currentFieldMapping, {mappingTo: action.mappingTo.columnName, type: action.mappingTo.dataType});
      return Object.assign({}, state, {databaseMapping: [...state.databaseMapping.slice(0, fieldMappingIndex), newFieldMapping, ...state.databaseMapping.slice(fieldMappingIndex + 1)]});
    }

    case ADD_MAP:
      return Object.assign({}, state, { databaseMapping: [...state.databaseMapping, {id: Math.random(), mappingTo: 0, type: '', joinType: action.joinType}]});

    case REMOVE_MAP: {
      const fieldMappingIndex = state.databaseMapping.findIndex(fieldMap => fieldMap.id === action.fieldMappingId);
      return Object.assign({}, state, { databaseMapping: [...state.databaseMapping.slice(0, fieldMappingIndex), ...state.databaseMapping.slice(fieldMappingIndex + 1)]});
    }

    case CANCEL_NEW_FIELD:
      return Object.assign({}, initialState);

    default:
      return state;
  }
}

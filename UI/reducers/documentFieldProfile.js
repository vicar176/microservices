import {
  SET_ACTIVE_STATE,
  SET_ACCOUNT_VERIFICATION,
  SET_FIELD_DEFINITIONS,
  ADD_FIELD_DEFINITION,
  REMOVE_FIELD_DEFINITION,
  UPDATE_FIELD_DEFINITION,
  UPDATE_REQUIREMENT_LEVEL,
  CANCEL_DOCUMENT_PROFILE,
  CLEAN_DOCUMENT_PROFILE
} from 'actions/documentFieldProfile';

const initialState = {
  active: false,
  accountVerification: false,
  fieldDefinitions: [],
  fieldTemplate: {
    required: false,
    verification: false,
    fieldDefinition: {
      id: null,
      fieldDescription: "",
      fieldName: "",
      fieldType: ""
    }
  }
};

export default function documentFieldProfile(state = initialState, action) {
  switch (action.type) {

    // Document field profile options:
    case SET_ACTIVE_STATE:
      return Object.assign({}, state, { active: action.activeState });

    case SET_ACCOUNT_VERIFICATION:
      return Object.assign({}, state, { accountVerification: action.accountVerification });

    // Field definition grid:
    case SET_FIELD_DEFINITIONS:
      return Object.assign({}, state, { fieldDefinitions: action.fieldDefinitions });

    case ADD_FIELD_DEFINITION:
      return Object.assign({}, state, {
        fieldDefinitions: [ initialState.fieldTemplate, ...state.fieldDefinitions ]
      });

    case REMOVE_FIELD_DEFINITION: {
      const fieldIndex = state.fieldDefinitions.findIndex(field => field.fieldDefinition.fieldName === action.fieldName);
      if (fieldIndex < 0) {
        return state;
      }
      return Object.assign({}, state, {
        fieldDefinitions: [
          ...state.fieldDefinitions.slice(0, fieldIndex),
          ...state.fieldDefinitions.slice(fieldIndex + 1)
        ]
      });
    }

    case UPDATE_FIELD_DEFINITION: {
      const fieldIndex = state.fieldDefinitions.findIndex(field => field.fieldDefinition.fieldName === action.oldFieldName);
      const newFieldDefinition = Object.assign({}, initialState.fieldTemplate, { fieldDefinition: action.newFieldObj });
      return Object.assign({}, state, {
        fieldDefinitions: [
          ...state.fieldDefinitions.slice(0, fieldIndex),
          newFieldDefinition,
          ...state.fieldDefinitions.slice(fieldIndex + 1)
        ]
      });
    }

    case UPDATE_REQUIREMENT_LEVEL: {
      const fieldIndex = state.fieldDefinitions.findIndex(field => field.fieldDefinition.fieldName === action.fieldName);
      const updatedFieldDefinition = Object.assign({}, state.fieldDefinitions[fieldIndex], { required: action.required });
      return Object.assign({}, state, {
        fieldDefinitions: [
          ...state.fieldDefinitions.slice(0, fieldIndex),
          updatedFieldDefinition,
          ...state.fieldDefinitions.slice(fieldIndex + 1)
        ]
      });
    }

    case CANCEL_DOCUMENT_PROFILE:
      return Object.assign({}, initialState, action.currProfile);

    case CLEAN_DOCUMENT_PROFILE:
      return Object.assign({},initialState);

    default:
      return state;

  }
}

import {
  IS_FETCHING_DOCUMENTS,
  RECEIVE_DOCUMENTS,
  SELECT_DOCUMENTS,
  IS_FETCHING_FIELD_DEFINITIONS,
  RECEIVE_FIELD_DEFINITIONS,
  IS_SAVING_DOCUMENT_FIELD,
  CLEAN_DOCUMENT_FIELD
} from 'actions/documentFieldProfile';

const initialState = {
  isFetchingDocuments: false,
  receivedDocuments: [],
  selectedDocument: null,
  isFetchingFieldDefinitions: false,
  receivedFieldDefinitions: [],
  isSavingDocumentProfile: false
};

export default function documentField(state = initialState, action) {
  switch (action.type) {

    // Document Profiles
    case IS_FETCHING_DOCUMENTS:
      return Object.assign({}, state, { isFetchingDocuments: action.isFetching });

    case RECEIVE_DOCUMENTS:
      return Object.assign({}, state, { receivedDocuments: action.receivedDocuments });

    case SELECT_DOCUMENTS:
      return Object.assign({}, state, { selectedDocument: action.selectedDocument });

    case IS_SAVING_DOCUMENT_FIELD:
      return Object.assign({}, state, { isSavingDocumentProfile: action.isFetching });

    case CLEAN_DOCUMENT_FIELD:
      return Object.assign({}, initialState, { receivedFieldDefinitions: state.receivedFieldDefinitions });

    // Field Definitions
    case IS_FETCHING_FIELD_DEFINITIONS:
      return Object.assign({}, state, { isFetchingFieldDefinitions: action.isFetching });

    case RECEIVE_FIELD_DEFINITIONS:
      return Object.assign({}, state, { receivedFieldDefinitions: action.receivedFieldDefinitions });

    default:
      return state;
  }
}

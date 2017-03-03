import {
  SELECT_REVIEW_METHOD,
  SELECT_DOCUMENT_LIST,
  ADD_DOCUMENT_LIST,
  IS_FETCHING_DOCUMENT_LIST,
  IS_FETCHING_DOCUMENTS,
  IS_FETCHING_DOCUMENT,
  RECEIVE_DOCUMENT_LIST,
  RECEIVE_DOCUMENTS,
  CLEAR_DOCUMENTS,
  SET_CURRENT_PAGE,
  SET_TOTAL_ITEMS,
  SET_ITEMS_PER_PAGE,
  NO_DOCUMENTS,
  UPDATE_DOCUMENT_FIELD
} from 'actions/oaldExceptionManagement';

const initialState = {
  selectedReviewMethod: 'exceptionList',
  documentList: [],
  documents: [],
  isFetchingDocumentList: false,
  selectedDocumentList: [],
  isFetchingDocuments: false,
  currentPage: 0,
  totalItems: 0,
  dropdownItems: '25,50,100',
  itemsPerPage: 1,
  areDocuments: false,
  isFetchingDocument: false
};

export default function oaldExceptionManagement(state = initialState, action) {

  switch (action.type) {
    case SET_CURRENT_PAGE:
      return Object.assign({}, state, {
        currentPage: action.newPage
      });

    case SELECT_REVIEW_METHOD:
      return Object.assign({}, state, {
        selectedReviewMethod: action.reviewMethod
      });

    case SELECT_DOCUMENT_LIST:
      return Object.assign({}, state, {
        selectedDocumentList: action.selectedDocumentList ? action.selectedDocumentList : []
      });

    case ADD_DOCUMENT_LIST:
      return Object.assign({}, state, {
        selectedDocumentList: state.selectedDocumentList.concat(action.newDocumentList)
      });

    case IS_FETCHING_DOCUMENT_LIST:
      return Object.assign({}, state, {
        isFetchingDocumentList: action.isFetching
      });

    case IS_FETCHING_DOCUMENTS:
      return Object.assign({}, state, {
        isFetchingDocuments: action.isFetching
      });

    case IS_FETCHING_DOCUMENT:
      return Object.assign({}, state, {
        isFetchingDocument: action.isFetchingDocument
      });

    case RECEIVE_DOCUMENT_LIST:
      return Object.assign({}, state, {
        documentList: action.documentList
      });

    case RECEIVE_DOCUMENTS:
      return Object.assign({}, state, {
        documents: action.documents
      });

    case SET_TOTAL_ITEMS:
      return Object.assign({}, state, {
        totalItems: action.totalItems
      });

    case SET_ITEMS_PER_PAGE:
      return Object.assign({}, state, {
        itemsPerPage: action.itemsPerPage
      });

    case CLEAR_DOCUMENTS:
      return Object.assign({}, state, {
        documents: [],
        selectedDocumentList: [],
        currentPage: 0
      });

    case NO_DOCUMENTS:
      return Object.assign({}, state, {
        noDocuments: action.noDocuments
      });

    case UPDATE_DOCUMENT_FIELD: {
      const currDocument = state.documents[action.documentIndex];
      const fieldIndex = currDocument.dataElements.findIndex(field => field.fieldDefinition.fieldName === action.fieldName);
      const updatedField = Object.assign({}, currDocument.dataElements[fieldIndex], { value: action.newValue, fieldEdited: true });
      const updatedDocument = Object.assign({}, currDocument, {
        dataElements: [
          ...currDocument.dataElements.slice(0, fieldIndex),
          updatedField,
          ...currDocument.dataElements.slice(fieldIndex + 1)
        ]
      });
      return Object.assign({}, state, {
        documents: [
          ...state.documents.slice(0, action.documentIndex),
          updatedDocument,
          ...state.documents.slice(action.documentIndex + 1)
        ]
      });
    }

    default:
      return state;
  }

}

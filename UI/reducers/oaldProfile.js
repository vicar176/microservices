import {
  RECEIVE_OALD_PROFILE,
  REMOVE_DOC_TYPES,
  ADD_DOC_TYPES,
  RECEIVE_PORTFOLIO_PROFILE,
  ENABLE_PORTFOLIO,
  CANCEL_OALD_PROFILE,
  SELECT_LENDER,
  SELECT_PORTFOLIO,
  REQUEST_PORTFOLIO,
} from 'actions/oald';

const initialState = {
  id: null,
  documentTypes: []
};

let oaldLevelProfile = null;

export default function oaldProfile(state = initialState, action) {

  switch (action.type) {

    case REMOVE_DOC_TYPES:
      return Object.assign({}, state, {
        documentTypes: removeDocTypes(state.documentTypes.slice(), action.docTypes)
      });

    case ADD_DOC_TYPES:
      return Object.assign({}, state, {
        documentTypes: addDocTypes(state.documentTypes.slice(), action.docTypes)
      });

    case RECEIVE_OALD_PROFILE:
    case RECEIVE_PORTFOLIO_PROFILE:
      if (!action.profile) {
        return Object.assign({}, initialState);
      }
      return Object.assign({}, state, {id: action.profile.id, documentTypes: action.profile.documentTypes});

    case ENABLE_PORTFOLIO:
      if (!action.enabled) {
        return Object.assign({}, oaldLevelProfile);
      }
      oaldLevelProfile = state;
      return Object.assign({}, state);

    case SELECT_LENDER:
    case CANCEL_OALD_PROFILE:
    case SELECT_PORTFOLIO:
    case REQUEST_PORTFOLIO:
      return Object.assign({}, state, {
        id: null,
        documentTypes: []
      });

    default:
      return state;

  }

}

function removeDocTypes (documentTypes, docTypes) {
  const ids = docTypes.map(doc => doc.id);
  return documentTypes.filter(doc => ids.indexOf(doc.id) === -1);
}

function addDocTypes (documentTypes, docTypes) {
  docTypes.forEach( doc => {
    if (!documentTypes.find( document => document.id === doc.id)) {
      documentTypes.push(doc);
    }
  });
  return [...documentTypes];
}

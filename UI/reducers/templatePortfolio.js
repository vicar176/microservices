// import docTypes from 'json!data/docTypes.json';

import {
  RECEIVE_DOCTYPES,
  RECEIVE_SELLERS,
  RECEIVE_LENDERS,
  RECEIVE_LENDER_AFFINITIES,
} from 'actions/template';

const initialState = {
  docTypes: {},
  sellers: [],
  originalLenders: [],
  lenderAffinities: []
}

export default function templatePortfolio (state = initialState, action) {

  switch (action.type) {

    case RECEIVE_DOCTYPES:
      return Object.assign({}, state, {
        docTypes: action.docTypes
      });

    case RECEIVE_SELLERS:
      return Object.assign({}, state, {
        sellers: action.sellers
      });

    case RECEIVE_LENDERS:
      return Object.assign({}, state, {
        originalLenders: action.lenders
      });

    case RECEIVE_LENDER_AFFINITIES:
      return Object.assign({}, state, {
        lenderAffinities: action.affinities
      });

    default:
      return state;
  }
}

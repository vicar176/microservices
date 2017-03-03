import {
  RECEIVE_PRODUCT_GROUPS,
  RECEIVE_DOC_TYPES,
  RECEIVE_PORTFOLIO,
  REQUEST_PORTFOLIO,
  CANCEL_OALD_PROFILE,
  ENABLE_PORTFOLIO,
} from 'actions/oald';

const initialState = {
  productGroups: [],
  docTypes: [],
  originalLenders: [],
};

export default function oaldPortfolio(state = initialState, action) {

  switch (action.type) {

    case RECEIVE_PRODUCT_GROUPS:
      return Object.assign({}, state, {
        productGroups: action.groups
      });

    case RECEIVE_DOC_TYPES:
      return Object.assign({}, state, {
        docTypes: action.docTypes
      });

    case RECEIVE_PORTFOLIO:
      return Object.assign({}, state, {
        originalLenders: action.lenders
      });

    case REQUEST_PORTFOLIO:
      if (action.isFetchingPortfolio) {
        return Object.assign({}, state, { originalLenders: [] });
      }
      return state;

    case ENABLE_PORTFOLIO:
      return Object.assign({}, state, { originalLenders: [] });

    case CANCEL_OALD_PROFILE:
      return Object.assign({}, state, { originalLenders: [] });

    default:
      return state;

  }

}

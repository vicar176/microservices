import {
  IS_FETCHING_PRODUCT_GROUPS,
  IS_FETCHING_DOC_TYPES,
  SELECT_PRODUCT_GROUP,
  SELECT_PORTFOLIO,
  IS_FETCHING_OALD_PROFILE,
  ENABLE_PORTFOLIO,
  SAVING_OALD_PROFILE,
  CLEAN_ERROR_SAVING_OALD_PROFILE,
  ERROR_SAVING_OALD_PROFILE,
  SAVED_OALD_PROFILE,
  SELECT_LENDER,
  CANCEL_OALD_PROFILE,
  REQUEST_PORTFOLIO,
  SELECT_PREVIOUS_VERSION,
  IS_FETCHING_PREVIOUS_VERSION,
  RECEIVE_PREVIOUS_VERSION,
  CANCEL_PREVIOUS_MODE,
  IS_FETCHING_FILTER_GRID
} from 'actions/oald';

const initialState = {
  isFetchingProfile: false,
  isSavingProfile: false,
  isFetchingProductGroups: false,
  selectedProductGroup: null,
  isFetchingDocTypes: false,
  isPortfolioEnabled: false,
  selectedPortfolio: null,
  isFetchingPortfolio: false,
  selectedLender: null,
  isFetchingLenders: false,
  isFetchingFilterGrid: true,
  errorOnSave: false
};

export default function oald(state = initialState, action) {

  switch (action.type) {

    case IS_FETCHING_PRODUCT_GROUPS:
      return Object.assign({}, state, { isFetchingProductGroups: action.isFetching });

    case SELECT_PRODUCT_GROUP:
      return Object.assign({}, state, { selectedProductGroup: action.group });

    case IS_FETCHING_OALD_PROFILE:
      return Object.assign({}, state, { isFetchingProfile: action.isFetching });

    case IS_FETCHING_DOC_TYPES:
      return Object.assign({}, state, { isFetchingDocTypes: action.isFetching });

    case ENABLE_PORTFOLIO:
      if (!action.enabled) {
        return Object.assign({}, state, {
          isPortfolioEnabled: action.enabled,
          selectedPortfolio: null
        });
      }
      return Object.assign({}, state, { isPortfolioEnabled: action.enabled });

    case SAVING_OALD_PROFILE:
      return Object.assign({}, state, { isSavingProfile: true });

    case CLEAN_ERROR_SAVING_OALD_PROFILE:
      return Object.assign({}, state, { errorOnSave: false });

    case ERROR_SAVING_OALD_PROFILE:
      return Object.assign({}, state, {
        isSavingProfile: false,
        errorOnSave: true
      });

    case SAVED_OALD_PROFILE:
      return Object.assign({}, state, { isSavingProfile: false });

    case SELECT_PORTFOLIO:
      return Object.assign({}, state, {
        selectedPortfolio: action.portfolio,
        selectedLender: null
      });

    case SELECT_LENDER:
      return Object.assign({}, state, { selectedLender: action.lender });

    case CANCEL_OALD_PROFILE:
      return Object.assign({}, initialState);

    case REQUEST_PORTFOLIO:
      if (action.isFetching) {
        return Object.assign({}, state, {
          isFetchingPortfolio: action.isFetching,
          selectedPortfolio: null,
          selectedLender: null
        });
      }
      return Object.assign({}, state, { isFetchingPortfolio: action.isFetchingPortfolio });

    case IS_FETCHING_FILTER_GRID:
      return Object.assign({}, state, { isFetchingFilterGrid: action.isFetching });

    default:
      return state;
  }

}

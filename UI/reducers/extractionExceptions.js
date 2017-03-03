import {
  SELECT_PORTFOLIOS_ID,
  CLEAN_PORTFOLIOS_ID,
  CLEAN_PORTFOLIO_DETAIL,
  CLEAN_FAILED_DOCUMENTS,
  RECEIVE_PORTFOLIOS_ID,
  RECEIVE_PORTFOLIO_SUMMARY,
  RECEIVE_FOUND_TEMPLATES,
  RECEIVE_NOT_FOUND_TEMPLATES,
  RECEIVE_FAILED_DOCUMENTS,
  SET_PORTFOLIO_ACTIVE_TAB,
  IS_PORTFOLIO_SELECTED,
  IS_FETCHING_PORTFOLIOS_ID,
  IS_FETCHING_PORTFOLIO_SUMMARY,
  IS_FETCHING_FOUND_TEMPLATES,
  IS_FETCHING_NOT_FOUND_TEMPLATES,
  IS_FETCHING_FAILED_DOCS,
  TEMPLATE_FOR_REPROCESS
} from 'actions/extractionExceptions';

const initialState = {
  receivedPortfoliosId: [],
  selectedPortfoliosId: null,
  portfolioSummaryList: [],
  failedDocList: {},
  activePortfolioTabId: null,
  isPortfolioSelected: false,
  isFetchingPortfoliosId: false,
  isFetchingPortfolioSummary: false,
  isFetchingFoundTemplates: false,
  isFetchingNotFoundTemplates: false,
  isFetchingFailedDocs: false,
  templateForReprocess: {},
  templatesFound: null,
  templatesNotFound: null
};

export default function extractionExceptions(state = initialState, action) {
  switch (action.type) {

    case RECEIVE_PORTFOLIOS_ID:
      return Object.assign({}, state, {
        receivedPortfoliosId: action.receivedPortfoliosId
      });

    case SELECT_PORTFOLIOS_ID:
      return Object.assign({}, state, {
        selectedPortfoliosId: action.selectedPortfoliosId
      });

    case CLEAN_PORTFOLIOS_ID:
      return Object.assign({}, initialState);

    case CLEAN_PORTFOLIO_DETAIL:
      return Object.assign({}, state, {
        templatesFound: null,
        templatesNotFound: null
      });

    case CLEAN_FAILED_DOCUMENTS:
      return Object.assign({}, state, {
        failedDocList: initialState.failedDocList
      });

    case RECEIVE_PORTFOLIO_SUMMARY:
      return Object.assign({}, state, {
        portfolioSummaryList: action.portfolioSummaryList
      });

    case RECEIVE_FOUND_TEMPLATES:
      return Object.assign({}, state, {
        templatesFound: action.templates
      });

    case RECEIVE_NOT_FOUND_TEMPLATES:
      return Object.assign({}, state, {
        templatesNotFound: action.templates
      });

    case RECEIVE_FAILED_DOCUMENTS:
      return Object.assign({}, state, {
        failedDocList: action.failedDocList
      });

    case SET_PORTFOLIO_ACTIVE_TAB:
      return Object.assign({}, state, {
        activePortfolioTabId: action.activePortfolioTabId
      });

    case IS_PORTFOLIO_SELECTED:
      return Object.assign({}, state, {
        isPortfolioSelected: action.isPortfolioSelected
      });

    case IS_FETCHING_PORTFOLIOS_ID:
      return Object.assign({}, state, {
        isFetchingPortfoliosId: action.isFetching
      });

    case IS_FETCHING_PORTFOLIO_SUMMARY:
      return Object.assign({}, state, {
        isFetchingPortfolioSummary: action.isFetching
      });

    case IS_FETCHING_FOUND_TEMPLATES:
      return Object.assign({}, state, {
        isFetchingFoundTemplates: action.isFetching
      });

    case IS_FETCHING_NOT_FOUND_TEMPLATES:
      return Object.assign({}, state, {
        isFetchingNotFoundTemplates: action.isFetching
      });

    case IS_FETCHING_FAILED_DOCS:
      return Object.assign({}, state, {
        isFetchingFailedDocs: action.isFetchingFailedDocs
      });

    case TEMPLATE_FOR_REPROCESS:
      return Object.assign({}, state, {
        templateForReprocess: action.templateForReprocess
      });

    default:
      return state;
  }
}

import {
  REPORT_NAME_CHANGE,
  REPORT_DESCRIPTION_CHANGE,
  REPORT_URL_CHANGE,
  REPORT_CATEGORY_CHANGE,
  REPORT_SUB_CATEGORY_CHANGE,
} from 'actions/report';

// The initial application state
const initialState = {
  reportName: '',
  reportDescription: '',
  reportUrl: '',
  reportCategory: '',
  reportSubCategory: '',
};

export default function reportProfile(state = initialState, action) {
  switch (action.type) {
    case REPORT_NAME_CHANGE:
      return Object.assign({}, state, { reportName: action.value });

    case REPORT_DESCRIPTION_CHANGE:
      return Object.assign({}, state, { reportDescription: action.value });

    case REPORT_URL_CHANGE:
      return Object.assign({}, state, { reportUrl: action.value });

    case REPORT_CATEGORY_CHANGE:
      return Object.assign({}, state, { reportCategory: action.value });

    case REPORT_SUB_CATEGORY_CHANGE:
      return Object.assign({}, state, { reportSubCategory: action.value });

    default:
      return state;
  }
}

import {
  IS_FETCHING_REPORTS,
  IS_SAVING_REPORT,
  ADD_CATEGORY,
  ADD_SUBCATEGORY,
  CATEGORY_CHANGE,
  SUBCATEGORY_CHANGE,
  CATEGORY_REMOVE,
  SUBCATEGORY_REMOVE,
  NEW_CATEGORY_NAME_CHANGE,
  NEW_SUBCATEGORY_NAME_CHANGE
} from 'actions/report';

// The initial application state
const initialState = {
  isSavingReport: false,
  reportCategories: [],
  reportSubcategories: [],
  isFetchingReports: false,
  newCatgoryName: '',
  newSubcatgoryName: '',
};

export default function report(state = initialState, action) {
  switch (action.type) {
    case IS_FETCHING_REPORTS:
      return Object.assign({}, state, { isFetchingReports: action.isFetching });

    case IS_SAVING_REPORT:
      return Object.assign({}, state, { isSavingReport: action.isSaving });

    case NEW_CATEGORY_NAME_CHANGE:
      return Object.assign({}, state, { newCatgoryName: action.value });

    case NEW_SUBCATEGORY_NAME_CHANGE:
      return Object.assign({}, state, { newSubcatgoryName: action.value });

    case ADD_CATEGORY:
      return Object.assign({}, state, { reportCategories: [...state.reportCategories, {id: Math.random(), name: action.value}]});

    case ADD_SUBCATEGORY:
      return Object.assign({}, state, { reportSubcategories: [...state.reportSubcategories, {id: Math.random(), name: action.value}]});

    case CATEGORY_REMOVE: {
      const categoryIndex = state.reportCategories.findIndex(category => category.id === action.id);
      return Object.assign({}, state, { reportCategories: [...state.reportCategories.slice(0, categoryIndex), ...state.reportCategories.slice(categoryIndex + 1)]});
    }

    case SUBCATEGORY_REMOVE: {
      const subcategoryIndex = state.reportSubcategories.findIndex(subcategory => subcategory.id === action.id);
      return Object.assign({}, state, { reportSubcategories: [...state.reportSubcategories.slice(0, subcategoryIndex), ...state.reportSubcategories.slice(subcategoryIndex + 1)]});
    }

    default:
      return state;
  }
}

import {
  UPDATE_ITEMS_PER_PAGE,
  SET_CURRENT_PAGE,
  SET_TOTAL_PAGES,
  SET_TOTAL_ITEMS,
  SET_COLUMNS_HEADER,
  SET_CURRENT_ROWS,
  CLEAN_PROFILE_GRID,
  IS_FILTER_ACTIVE,
  IS_FETCHING_PAGE,
  SET_FILTER,
  CLEAN_FILTER,
  CLEAN_FILTER_COLUMN,
  SORT_COLUMN
} from 'actions/viewProfiles';

const initialState = {
  isFilterActive: false,
  dropdownItems: '25,50,100',
  itemsPerPage: 25,
  totalPages: 0,
  totalItems: 0,
  currentPageNumber: 0,
  currentRows: [],
  pages: [],
  columns: [],
  filters: {},
  sorts: ''
};

export default function viewProfiles(state = initialState, action) {

  switch (action.type) {
    case IS_FILTER_ACTIVE:
      return Object.assign({}, state, { isFilterActive: action.isFilterActive });

    case CLEAN_PROFILE_GRID:
      return Object.assign({}, initialState);

    case UPDATE_ITEMS_PER_PAGE:
      return Object.assign({}, state, { itemsPerPage: action.itemsPerPage });

    case SET_CURRENT_PAGE:
      return Object.assign({}, state, { currentPageNumber: action.currentPageNumber });

    case SET_TOTAL_PAGES:
      return Object.assign({}, state, { totalPages: action.totalPages });

    case SET_TOTAL_ITEMS:
      return Object.assign({}, state, { totalItems: action.totalItems });

    case SET_COLUMNS_HEADER:
      return Object.assign({}, state, { columns: action.columns });

    case SET_CURRENT_ROWS:
      return Object.assign({}, state, { currentRows: action.currentRows });

    case SET_FILTER: {
      const filters = Object.assign({}, state.filters, action.filters);
      return Object.assign({}, state, { filters: filters });
    }

    case CLEAN_FILTER_COLUMN: {
      return Object.assign({}, state, {
        filters: Object.keys(state.filters).reduce((result, key) => {
          if (key !== action.column) {
            result[key] = state.filters[key];
          }
          return result;
        }, {})
      });
    }

    case CLEAN_FILTER:
      return Object.assign({}, state, { filters: initialState.filters });

    case SORT_COLUMN:
      return Object.assign({}, state, { sorts: action.sorts });

    default:
      return state;
  }

}

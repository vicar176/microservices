export const CLEAN_PROFILE_GRID = 'CLEAN_PROFILE_GRID';
export const UPDATE_ITEMS_PER_PAGE = 'UPDATE_ITEMS_PER_PAGE';
export const SET_CURRENT_PAGE = 'SET_CURRENT_PAGE';
export const SET_TOTAL_PAGES = 'SET_TOTAL_PAGES';
export const SET_TOTAL_ITEMS = 'SET_TOTAL_ITEMS';
export const SET_COLUMNS_HEADER = 'SET_COLUMNS_HEADER';
export const SET_CURRENT_ROWS = 'SET_CURRENT_ROWS';
export const IS_FILTER_ACTIVE = 'IS_FILTER_ACTIVE';
export const IS_FETCHING_PAGE = 'IS_FETCHING_PAGE';
export const SET_FILTER = 'SET_FILTER';
export const CLEAN_FILTER = 'CLEAN_FILTER';
export const SORT_COLUMN = 'SORT_COLUMN';
export const CLEAN_FILTER_COLUMN = 'CLEAN_FILTER_COLUMN';

export function cleanProfileGrid () {
  return {
    type: CLEAN_PROFILE_GRID
  }
}

export function updateItemsPerPage (itemsPerPage) {
  return {
    type: UPDATE_ITEMS_PER_PAGE,
    itemsPerPage
  }
}

export function setCurrentPageNumber (currentPageNumber) {
  return {
    type: SET_CURRENT_PAGE,
    currentPageNumber
  }
}

export function setTotalPages (totalPages) {
  return {
    type: SET_TOTAL_PAGES,
    totalPages
  }
}

export function setTotalItems (totalItems) {
  return {
    type: SET_TOTAL_ITEMS,
    totalItems
  }
}

export function setColumnsHeader (columns) {
  return {
    type: SET_COLUMNS_HEADER,
    columns
  }
}

export function toggleFilter (isFilterActive) {
  return {
    type: IS_FILTER_ACTIVE,
    isFilterActive
  }
}

export function isFetchingPage () {
  return {
    type: IS_FETCHING_PAGE,
    isFetchingPage
  }
}

export function setCurrentRows (currentRows) {
  return {
    type: SET_CURRENT_ROWS,
    currentRows
  }
}

export function setFilter (filters) {
  return {
    type: SET_FILTER,
    filters
  }
}

export function cleanFilterColumn (column) {
  return {
    type: CLEAN_FILTER_COLUMN,
    column
  }
}

export function sortColumn (sorts) {
  return {
    type: SORT_COLUMN,
    sorts
  }
}

export function cleanFilter () {
  return {
    type: CLEAN_FILTER
  }
}

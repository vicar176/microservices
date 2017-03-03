export const REPORT_NAME_CHANGE = 'REPORT_NAME_CHANGE';
export const REPORT_DESCRIPTION_CHANGE = 'REPORT_DESCRIPTION_CHANGE';
export const REPORT_URL_CHANGE = 'REPORT_URL_CHANGE';
export const REPORT_CATEGORY_CHANGE = 'REPORT_CATEGORY_CHANGE';
export const REPORT_SUB_CATEGORY_CHANGE = 'REPORT_SUB_CATEGORY_CHANGE';
export const IS_FETCHING_REPORTS = 'IS_FETCHING_REPORTS';
export const IS_SAVING_REPORT = 'IS_SAVING_REPORT';
export const ADD_CATEGORY = 'ADD_CATEGORY';
export const ADD_SUBCATEGORY = 'ADD_SUBCATEGORY';
export const CATEGORY_CHANGE = 'CATEGORY_CHANGE';
export const SUBCATEGORY_CHANGE = 'SUBCATEGORY_CHANGE';
export const CATEGORY_REMOVE = 'CATEGORY_REMOVE';
export const SUBCATEGORY_REMOVE = 'SUBCATEGORY_REMOVE';
export const NEW_CATEGORY_NAME_CHANGE = 'NEW_CATEGORY_NAME_CHANGE';
export const NEW_SUBCATEGORY_NAME_CHANGE = 'NEW_SUBCATEGORY_NAME_CHANGE';

export function isFetchingReports (isFetching) {
  return {
    type: IS_FETCHING_REPORTS,
    isFetching
  }
}

export function isSavingReport (isSaving) {
  return {
    type: IS_SAVING_REPORT,
    isSaving
  }
}

export function reportNameChange (value) {
  return {
    type: REPORT_NAME_CHANGE,
    value
  }
}

export function reportDescriptionChange (value) {
  return {
    type: REPORT_DESCRIPTION_CHANGE,
    value
  }
}

export function reportUrlChange (value) {
  return {
    type: REPORT_URL_CHANGE,
    value
  }
}

export function reportCategoryChange (value) {
  return {
    type: REPORT_CATEGORY_CHANGE,
    value
  }
}

export function reportSubCategoryChange (value) {
  return {
    type: REPORT_SUB_CATEGORY_CHANGE,
    value
  }
}

export function addCategory (value) {
  return {
    type: ADD_CATEGORY,
    value
  }
}

export function addSubCategory (value) {
  return {
    type: ADD_SUBCATEGORY,
    value
  }
}

export function categoryChange (categoryID, value) {
  return {
    type: CATEGORY_CHANGE,
    categoryID,
    value
  }
}

export function subcategoryChange (subcategoryID, value) {
  return {
    type: SUBCATEGORY_CHANGE,
    subcategoryID,
    value
  }
}

export function categoryRemove (id) {
  return {
    type: CATEGORY_REMOVE,
    id
  }
}

export function subcategoryRemove (id) {
  return {
    type: SUBCATEGORY_REMOVE,
    id
  }
}

export function newCategoryNameChange (value) {
  return {
    type: NEW_CATEGORY_NAME_CHANGE,
    value
  }
}

export function newSubcategoryNameChange (value) {
  return {
    type: NEW_SUBCATEGORY_NAME_CHANGE,
    value
  }
}

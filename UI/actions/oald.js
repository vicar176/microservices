import fetch from 'isomorphic-fetch';
import toastr from 'toastr';
import baseToastrConfig from 'lib/baseToastrConfig';
import config from 'config';

export const IS_FETCHING_PRODUCT_GROUPS = 'IS_FETCHING_PRODUCT_GROUPS';
export const RECEIVE_PRODUCT_GROUPS = 'RECEIVE_PRODUCT_GROUPS';
export const SELECT_PRODUCT_GROUP = 'SELECT_PRODUCT_GROUP';
export const IS_FETCHING_OALD_PROFILE = 'IS_FETCHING_OALD_PROFILE';
export const RECEIVE_OALD_PROFILE = 'RECEIVE_OALD_PROFILE';
export const IS_FETCHING_DOC_TYPES = 'IS_FETCHING_DOC_TYPES';
export const RECEIVE_DOC_TYPES = 'RECEIVE_DOC_TYPES';
export const ENABLE_PORTFOLIO = 'ENABLE_PORTFOLIO';
export const SELECT_PORTFOLIO = 'SELECT_PORTFOLIO';
export const SELECT_LENDER = 'SELECT_LENDER';
export const REQUEST_PORTFOLIO = 'REQUEST_PORTFOLIO';
export const RECEIVE_PORTFOLIO = 'RECEIVE_PORTFOLIO';
export const ADD_DOC_TYPES = 'ADD_DOC_TYPES';
export const REMOVE_DOC_TYPES = 'REMOVE_DOC_TYPES';
export const SAVING_OALD_PROFILE = 'SAVING_OALD_PROFILE';
export const ERROR_SAVING_OALD_PROFILE = 'ERROR_SAVING_OALD_PROFILE';
export const CLEAN_ERROR_SAVING_OALD_PROFILE = 'CLEAN_ERROR_SAVING_OALD_PROFILE';
export const SAVED_OALD_PROFILE = 'SAVED_OALD_PROFILE';
export const CANCEL_OALD_PROFILE = 'CANCEL_OALD_PROFILE';
export const RECEIVE_PORTFOLIO_PROFILE = 'RECEIVE_PORTFOLIO_PROFILE';
export const IS_FETCHING_FILTER_GRID = 'IS_FETCHING_FILTER_GRID';

const { profileService, portfolioService } = config;

const oaldService = `${profileService}oald-profiles`;

function showToastrOnError(message) {
  toastr.options = baseToastrConfig();
  toastr.error(message);
}

function showToastrOnSuccess(message) {
  toastr.options = baseToastrConfig();
  toastr.success(message);
}

function showToastrOnWarning(message) {
  toastr.options = baseToastrConfig();
  toastr.warning(message);
}

function isFetchingProductGroups (isFetching) {
  return {
    type: IS_FETCHING_PRODUCT_GROUPS,
    isFetching
  }
}

function receiveProductGroups (groups) {
  return {
    type: RECEIVE_PRODUCT_GROUPS,
    groups
  }
}

export function fetchProductGroups () {
  return async dispatch => {
    try {
      dispatch(isFetchingProductGroups(true));
      const noCache = Math.random();
      const response = await (await fetch(`${portfolioService}product-groups?noCache=${noCache}`)).json();
      dispatch(isFetchingProductGroups(false));
      const code = response.error.code;
      if (code === 200 || code === 201) {
        dispatch(receiveProductGroups(response.data));
      } else {
        showToastrOnError(response.error.message);
      }
    } catch (error) {
      dispatch(isFetchingProductGroups(false));
      showToastrOnError('Service unavailable');
    }
  }
}

export function selectProductGroup (group) {
  return {
    type: SELECT_PRODUCT_GROUP,
    group
  }
}

function isFetchingOALDProfile (isFetching) {
  return {
    type: IS_FETCHING_OALD_PROFILE,
    isFetching
  }
}

export function receiveOALDProfile (profile) {
  return {
    type: RECEIVE_OALD_PROFILE,
    profile: profile
  }
}

export function fetchOALDProfile (groupCode) {
  return async dispatch => {
    try {
      dispatch(isFetchingOALDProfile(true));
      const noCache = Math.random();
      const response = await (await fetch(`${oaldService}?productGroup.code=${groupCode}&noCache=${noCache}`)).json();
      dispatch(isFetchingOALDProfile(false));
      const code = response.error.code;
      if (code === 200 || code === 201) {
        dispatch(receiveOALDProfile(response.data));
      } else {
        showToastrOnError(response.error.message);
      }
      return response.data;
    } catch (error) {
      dispatch(isFetchingOALDProfile(false));
      showToastrOnError('Service unavailable');
    }
  }
}

function isFetchingDocTypes (isFetching) {
  return {
    type: IS_FETCHING_DOC_TYPES,
    isFetching
  }
}

function receiveDocTypes (docTypes) {
  return {
    type: RECEIVE_DOC_TYPES,
    docTypes: docTypes
  }
}

export function fetchDocTypes () {
  return async dispatch => {
    try {
      dispatch(isFetchingDocTypes(true));
      const noCache = Math.random();
      const response = await (await fetch(`${portfolioService}document-types?noCache=${noCache}`)).json();
      dispatch(isFetchingDocTypes(false));
      const code = response.error.code;
      if (code === 200 || code === 201) {
        dispatch(receiveDocTypes(response.data));
      } else {
        showToastrOnError(response.error.message);
      }
    } catch (error) {
      dispatch(isFetchingDocTypes(false));
      showToastrOnError('Service unavailable');
    }
  }
}

export function enablePortfolio (isEnabled) {
  return {
    type: ENABLE_PORTFOLIO,
    enabled: isEnabled
  }
}

export function removeDocTypes (docTypes) {
  return {
    type: REMOVE_DOC_TYPES,
    docTypes: docTypes
  }
}

export function addDocTypes (docTypes) {
  return {
    type: ADD_DOC_TYPES,
    docTypes: docTypes
  }
}

function savingOALDProfile() {
  return {
    type: SAVING_OALD_PROFILE
  }
}

function savedOALDProfile () {
  return {
    type: SAVED_OALD_PROFILE
  }
}

function cleanErrorSavingOALDProfile () {
  return {
    type: CLEAN_ERROR_SAVING_OALD_PROFILE
  }
}

function errorSavingOALDProfile () {
  return {
    type: ERROR_SAVING_OALD_PROFILE
  }
}

export function saveOALDProfile () {
  return async (dispatch, getState) => {
    try {
      const { id, documentTypes } = getState().oaldProfile;

      if (!documentTypes.length) {
        showToastrOnError('You must associate a Document Type');
        return;
      }

      const method = id ? 'PUT' : 'POST';
      const pid = id || '';
      const {
        selectedProductGroup,
        isPortfolioEnabled,
        selectedPortfolio,
        selectedLender,
      } = getState().oald;

      // we have to build the profile
      const profile = {
        documentTypes: documentTypes,
        productGroup: selectedProductGroup
      };

      const { firstName, lastName } = getState().auth.user;

      if (id) {
        profile.id = id;
      }

      profile.updatedBy = firstName +' '+ lastName;

      if (isPortfolioEnabled) {
        profile.portfolio = {id: selectedPortfolio};
        profile.originalLender = selectedLender;
      } else {
        profile.portfolio = null;
        profile.originalLender = null;
      }
      dispatch(cleanErrorSavingOALDProfile());
      dispatch(savingOALDProfile());
      const response = await (await fetch(`${oaldService}/${pid}`, {
        method: method,
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(profile)
      })).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
        showToastrOnSuccess(method === 'PUT' ? 'Product group updated' : 'Product group saved');
        dispatch(savedOALDProfile());
        dispatch(cancelOALDProfile());
      } else {
        dispatch(errorSavingOALDProfile());
        showToastrOnError(response.error.message);
      }
    } catch (error) {
      dispatch(errorSavingOALDProfile());
      showToastrOnError('Service unavailable');
    }
  }
}

export function selectPortfolio (portfolio) {
  return {
    type: SELECT_PORTFOLIO,
    portfolio
  }
}

function isFetchingPortfolio (isFetching) {
  return {
    type: REQUEST_PORTFOLIO,
    isFetching
  }
}

export function receivePortfolioLenders (lenders) {
  return {
    type: RECEIVE_PORTFOLIO,
    lenders: lenders
  }
}

export function fetchPortfolioLenders (id) {
  return async dispatch => {
    try {
      dispatch(isFetchingPortfolio(true));
      const noCache = Math.random();
      const response = await (await fetch(`${portfolioService}portfolios/${id}/originalLenders?noCache=${noCache}`)).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
        if (response.data && response.data.length) {
          dispatch(isFetchingPortfolio(false));
          dispatch(selectPortfolio(id));
          dispatch(receivePortfolioLenders(response.data));
        } else {
          dispatch(isFetchingPortfolio(false));
          dispatch(receivePortfolioLenders([]));
          showToastrOnWarning(`No Original Lenders found for Portfolio ID ${id}`);
        }
      } else {
        showToastrOnError('Invalid Portfolio');
        dispatch(isFetchingPortfolio(false));
      }
    } catch (error) {
      showToastrOnError('Service unavailable');
    }
  }
}

export function selectLender (name) {
  return (dispatch, getState) => {
    const { originalLenders } = getState().oaldPortfolio;
    const lender = originalLenders.find(lender => lender.name === name);
    dispatch({
      type: SELECT_LENDER,
      lender: lender
    });
  }
}

function receivePortfolioProfile (profile) {
  return {
    type: RECEIVE_PORTFOLIO_PROFILE,
    profile: profile
  }
}

export function fetchPortofolioProfile () {
  return async (dispatch, getState) => {
    try {
      const { selectedPortfolio, selectedLender, selectedProductGroup } = getState().oald;
      const noCache = Math.random();
      const response = await (await fetch(`${oaldService}?productGroup.code=${selectedProductGroup.code}&portfolio.id=${selectedPortfolio}&originalLender.name=${selectedLender.name}&noCache=${noCache}`)).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
        dispatch(receivePortfolioProfile(response.data));
      } else {
        showToastrOnError(response.error.message);
      }
      return response.data;
    } catch (error) {
      showToastrOnError('Service unavailable');
    }
  }
}

export function cancelOALDProfile () {
  return {
    type: CANCEL_OALD_PROFILE
  }
}

export function isFetchingFilterGrid (isFetching) {
  return {
    type: IS_FETCHING_FILTER_GRID,
    isFetching
  }
}

export function fetchFilterGrid (queryString) {
  return async dispatch => {
    try {
      dispatch(isFetchingFilterGrid(true));
      const noCache = Math.random();
      const response = await (await fetch(`${oaldService}?${queryString}&noCache=${noCache}`)).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
        return response.data;
      }
      showToastrOnError(response.error.message);
    } catch (error) {
      showToastrOnError('Service unavailable');
    } finally {
      dispatch(isFetchingFilterGrid(false));
    }
  }
}

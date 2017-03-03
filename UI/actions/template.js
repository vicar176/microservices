// import docTypesData from 'json!data/docTypes.json';
import { find, sortBy, where, first } from 'lodash';
import queryString from 'query-string';
import fetch from 'isomorphic-fetch';
import toastr from 'toastr';
import baseToastrConfig from 'lib/baseToastrConfig';
import config from 'config';
import { cleanProfileData } from 'actions/common';

export const REQUEST_DOCTYPES = 'REQUEST_DOCTYPES';
export const RECEIVE_DOCTYPES = 'RECEIVE_DOCTYPES';
export const SELECT_DOCTYPE = 'SELECT_DOCTYPE';
export const REQUEST_SELLERS = 'REQUEST_SELLERS';
export const ERROR_REQUEST_SELLERS = 'ERROR_REQUEST_SELLERS';
export const RECEIVE_SELLERS = 'RECEIVE_SELLERS';
export const SELECT_TEMPLATE_SELLER = 'SELECT_TEMPLATE_SELLER';
export const SELECT_TEMPLATE_LENDER = 'SELECT_TEMPLATE_LENDER';
export const REQUEST_LENDERS = 'REQUEST_LENDERS';
export const ERROR_REQUEST_LENDERS = 'ERROR_REQUEST_LENDERS';
export const RECEIVE_LENDERS = 'RECEIVE_LENDERS';
export const REQUEST_LENDER_AFFINITIES = 'REQUEST_LENDER_AFFINITIES';
export const ERROR_REQUEST_LENDER_AFFINITIES = 'ERROR_REQUEST_LENDER_AFFINITIES';
export const RECEIVE_LENDER_AFFINITIES = 'RECEIVE_LENDER_AFFINITIES';
export const ADD_TEMPLATE_AFFINITIES = 'ADD_TEMPLATE_AFFINITIES';
export const SET_TEMPLATE_WIDTH = 'SET_TEMPLATE_WIDTH';
export const SELECT_ZOOM_LEVEL = 'SELECT_ZOOM_LEVEL';
export const SELECT_TEMPLATE_NAME = 'SELECT_TEMPLATE_NAME';
export const SELECT_TEMPLATE_FILE = 'SELECT_TEMPLATE_FILE';
export const CANCEL_TEMPLATE = 'CANCEL_TEMPLATE';
export const REQUEST_TEMPLATE_PROFILE = 'REQUEST_TEMPLATE_PROFILE';
export const ERROR_REQUEST_TEMPLATE_PROFILE = 'ERROR_REQUEST_TEMPLATE_PROFILE';
export const RECEIVE_TEMPLATE_PROFILE = 'RECEIVE_TEMPLATE_PROFILE';
export const SAVING_TEMPLATE_PROFILE = 'SAVING_TEMPLATE_PROFILE';
export const SAVED_TEMPLATE_PROFILE = 'SAVED_TEMPLATE_PROFILE';
export const ADD_LENDER_AFFINITIES = 'ADD_LENDER_AFFINITIES';
export const REMOVE_LENDER_AFFINITIES = 'REMOVE_LENDER_AFFINITIES';
export const RESET_LENDER_AFFINITIES = 'RESET_LENDER_AFFINITIES';
export const CREATE_NEW_TEMPLATE = 'CREATE_NEW_TEMPLATE';
export const SET_TOTAL_PAGES = 'SET_TOTAL_PAGES';
export const SELECT_PAGE = 'SELECT_PAGE';
export const SHOW_MAPPING = 'SHOW_MAPPING';
export const SAVING_TEMPLATE_ERROR = 'SAVING_TEMPLATE_ERROR';
export const SELECTED_DOCTYPE_LENDER_SELLER_AFFINITIES = 'SELECTED_DOCTYPE_LENDER_SELLER_AFFINITIES';
export const IS_FETCHING_FILTER_GRID = 'IS_FETCHING_FILTER_GRID';
export const SAVE_SAMPLE_TEMPLATE_NAME = 'SAVE_SAMPLE_TEMPLATE_NAME';
export const CLEAN_SAMPLE_TEMPLATE_NAME = 'CLEAN_SAMPLE_TEMPLATE_NAME';

const { profileService, portfolioService, utilitiesService } = config;
const templateService = `${profileService}template-mapping-profiles`;

// Utils
function showToastrOnError(message) {
  toastr.options = baseToastrConfig();
  toastr.error(message);
}

export function selectedDocTypeLenderSellerAffinities () {
  return {
    type: SELECTED_DOCTYPE_LENDER_SELLER_AFFINITIES
  }
}

export function requestDocType () {
  return {
    type: REQUEST_DOCTYPES
  }
}

export function errorRequestDocType () {
  return {
    type: ERROR_REQUEST_DOCTYPE
  }
}

export function receiveDocTypes (docTypes) {
  return {
    type: RECEIVE_DOCTYPES,
    docTypes
  }
}

export function fetchDocType () {
  return async dispatch => {
    try {
      dispatch(requestDocType());
      const noCache = Math.random();
      // /media-profiles/document-fields/document-field-definitions
      const response = await (await fetch(`${profileService}document-fields/document-field-definitions?noCache=${noCache}`)).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
        if (response.data.length < 1) {
          showToastrOnError('No Document types defined');
        }
        dispatch(receiveDocTypes(response.data));
      } else {
        // dispatch(errorRequestDocType());
        showToastrOnError(response.error.message);
      }
    } catch (error) {
      dispatch(errorRequestDocType());
      showToastrOnError('Service unavailable');
    }
  }
}

export function selectTemplateDocType (docTypeId) {
  return (dispatch, getState) => {
    const { docTypes } = getState().templatePortfolio;
    const selectedDocType = first(docTypes.filter(type => type.documentType.id === Number(docTypeId)));
    dispatch({
      type: SELECT_DOCTYPE,
      docType: selectedDocType || null
    });
  }
}

function requestSellers () {
  return {
    type: REQUEST_SELLERS
  }
}

function errorRequestSellers () {
  return {
    type: ERROR_REQUEST_SELLERS
  }
}

function receiveSellers (sellers) {
  return {
    type: RECEIVE_SELLERS,
    sellers: sortBy(sellers, 'name')
  }
}

export function fetchSellers () {
  return async dispatch => {
    try {
      dispatch(requestSellers());
      const noCache = Math.random();
      const response = await (await fetch(`${portfolioService}sellers?noCache=${noCache}`)).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
        dispatch(receiveSellers(response.data));
      } else {
        dispatch(errorRequestSellers());
        showToastrOnError(response.error.message);
      }
    } catch (error) {
      dispatch(errorRequestSellers());
      showToastrOnError('Service unavailable');
    }
  }
}

export function selectTemplateSeller (seller) {
  return {
    type: SELECT_TEMPLATE_SELLER,
    seller: seller
  }
}

function requestLenders () {
  return {
    type: REQUEST_LENDERS
  }
}

function errorRequestLenders () {
  return {
    type: ERROR_REQUEST_LENDERS
  }
}

function receiveLenders (lenders) {
  return {
    type: RECEIVE_LENDERS,
    lenders: sortBy(lenders, 'name')
  }
}

export function fetchLenders (sellerId) {
  return async dispatch => {
    try {
      dispatch(requestLenders());
      const noCache = Math.random();
      const response = await (await fetch(`${portfolioService}sellers/${sellerId}/originalLenders?noCache=${noCache}`)).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
        dispatch(receiveLenders(response.data));
      } else {
        dispatch(errorRequestLenders());
        showToastrOnError(response.error.message);
      }
    } catch (error) {
      dispatch(errorRequestLenders());
      showToastrOnError('Service unavailable');
    }
  }
}

export function selectTemplateLender(lender) {
  return {
    type: SELECT_TEMPLATE_LENDER,
    lender: lender
  }
}

function requestAffinities () {
  return {
    type: REQUEST_LENDER_AFFINITIES
  }
}

function errorRequestAffinities () {
  return {
    type: ERROR_REQUEST_LENDER_AFFINITIES
  }
}

function receiveAffinities (affinities) {
  return {
    type: RECEIVE_LENDER_AFFINITIES,
    affinities: affinities
  }
}

export function fetchLenderAffinities (name) {
  return async dispatch => {
    try {
      dispatch(requestAffinities());
      const noCache = Math.random();
      const response = await (await fetch(`${portfolioService}originalLenders/affinities?originalLenders.name=${name}&noCache=${noCache}`)).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
        const affinities = response.data;
        dispatch(receiveAffinities(affinities));
        if (!affinities.length) {
          dispatch(selectAddAffinities('no', false));
        }
        return affinities;
      }
      dispatch(errorRequestAffinities());
      showToastrOnError(response.error.message);
    } catch (error) {
      dispatch(errorRequestAffinities());
      showToastrOnError('Service unavailable');
    }
  }
}

export function selectAddAffinities (value) {
  return dispatch => {
    dispatch({
      type: ADD_TEMPLATE_AFFINITIES,
      value: value
    });
  }
}

export function setTemplateWidth (templateWidth, wrapperWidth) {
  return {
    type: SET_TEMPLATE_WIDTH,
    templateWidth: templateWidth,
    wrapperWidth: wrapperWidth
  }
}

export function selectZoomLevel (zoomLevel) {
  return {
    type: SELECT_ZOOM_LEVEL,
    zoomLevel: zoomLevel
  }
}

export function selectTemplateName (name) {
  return {
    type: SELECT_TEMPLATE_NAME,
    name: name
  }
}

export function selectTemplateFile (file) {
  return {
    type: SELECT_TEMPLATE_FILE,
    file: file
  }
}

export function cancelTemplate () {
  return {
    type: CANCEL_TEMPLATE
  }
}

function requestTemplateProfile (isFetching) {
  return {
    type: REQUEST_TEMPLATE_PROFILE,
    isFetching
  }
}

function errorRequestTemplateProfile () {
  return {
    type: ERROR_REQUEST_TEMPLATE_PROFILE,
  }
}

export function receiveTemplateProfile (profile) {
  return (dispatch, getState) => {
    if (profile) {
      dispatch({
        type: RECEIVE_TEMPLATE_PROFILE,
        profile: profile
      });
      if (profile.templates) {
        const { addAffinities } = getState().template;
        // if (addAffinities === 'no') {
        const { name, sampleFileName, totalPages, affinities } = first(profile.templates);
        dispatch(selectTemplateName(name));
        dispatch(selectTemplateFile(sampleFileName));
        dispatch(setTotalPages(totalPages));
        // }
        if (addAffinities === 'yes') {
          dispatch(addLenderAffinities(affinities));
        }
      } else {
        dispatch(createNewTemplate(true));
      }
    } else {
      // we have to build it
      const {
        selectedDocType,
        selectedSeller,
        selectedLender,
      } = getState().template;

      const profile = {
        documentType: {
          id: selectedDocType.documentType.id,
          code: selectedDocType.documentType.code
        },
        seller: selectedSeller,
        originalLender: selectedLender,
        templates: null
      }
      // console.log('receiveTemplateProfile::', profile);
      dispatch({
        type: RECEIVE_TEMPLATE_PROFILE,
        profile: profile
      });
      dispatch(createNewTemplate(true));
    }
  }
}

export function fetchTemplateProfile () {
  return async (dispatch, getState) => {
    try {
      const { selectedDocType, selectedSeller, selectedLender, addAffinities } = getState().template;
      const query = queryString.stringify({
        'documentType.code': selectedDocType.documentType.code,
        'seller.id': selectedSeller.id,
        'originalLender.name': selectedLender.name,
        'affinities': !!(addAffinities === 'yes'),
        'noCache': Math.random()
      });
      dispatch(requestTemplateProfile(true));
      const response = await (await fetch(`${templateService}?${query}`)).json();
      // console.log('fetchTemplateProfile::', response);
      // TEMP - If you want to load template on localhost uncomment this TEMP section
      // const responseTemp = Object.assign({}, response.data.templates[0], {
      //   originalLender: response.originalLender,
      //   seller: response.seller,
      //   documentType: response.documentType,
      // });
      // response.data = [responseTemp];
      // console.log('fetchTemplateProfile::', response, responseTemp);
      // TEMP

      const templates = response.data;
      let profile = null;
      if (templates) {
        const templateRef = first(templates);
        profile = {
          documentType: templateRef.documentType,
          seller: templateRef.seller,
          originalLender: templateRef.originalLender,
          templates: templates
        };
      }
      const code = response.error.code;
      if (code === 200 || code === 201) {
        dispatch(receiveTemplateProfile(profile));
        return templates;
      }
      dispatch(errorRequestTemplateProfile());
      showToastrOnError(response.error.message);
    } catch (error) {
      dispatch(errorRequestTemplateProfile());
      showToastrOnError('Service unavailable');
    }
  }
}

export function sendSampleTemplate (file) {
  return (dispatch, getState) => {
    const {
      selectedDocType,
      selectedSeller,
      selectedLender,
    } = getState().template;
    const data = new FormData();
    data.append('file', file);
    const query = queryString.stringify({
      'fileName': file.name,
      'documentType.code': selectedDocType.documentType.code,
      'seller.id': selectedSeller.id,
      'originalLender.name': selectedLender.name
    });
    const xhr = new XMLHttpRequest();
    xhr.addEventListener('readystatechange', () => {
      if (xhr.readyState === 4) {
        const json = JSON.parse(xhr.responseText);
        const code = json.error.code;
        if (code === 200 || code === 201) {
          dispatch(selectTemplateFile(json.data.fileName));
          dispatch(setTotalPages(json.data.totalPages));
        } else {
          showToastrOnError('File format not supported');
        }
      }
    });
    xhr.open('POST', `${utilitiesService}sample-files?${query}`);
    xhr.send(data);
  }
}

export function setTotalPages (pages) {
  return {
    type: SET_TOTAL_PAGES,
    pages: pages
  }
}

export function savingTemplate() {
  return {
    type: SAVING_TEMPLATE_PROFILE
  }
}

export function savedTemplate() {
  return {
    type: SAVED_TEMPLATE_PROFILE
  }
}

export function saveTemplateError() {
  return {
    type: SAVING_TEMPLATE_ERROR
  }
}

export function saveTemplate () {
  return async (dispatch, getState) => {
    try {
      const {
        selectedTemplateName,
        selectedTemplateFile,
        associatedAffinities,
        totalPages,
        addAffinities,
      } = getState().template;
      if (addAffinities === 'yes' && !associatedAffinities.length) {
        showToastrOnError('You must associate at least one affinity');
        return;
      }
      const areasRef = where(getState().templateAreasRef, {mapped: true});
      if (!areasRef.length) {
        showToastrOnError('You must select at least one reference area');
        return;
      }
      const areas = getState().templateAreas;
      const { firstName, lastName } = getState().auth.user;
      const { documentType, seller, originalLender, templates } = getState().templateProfile;
      const json = {
        documentType: documentType,
        originalLender: originalLender,
        seller: seller,
        updatedBy: firstName +' '+ lastName,
        // template: {
        totalPages: totalPages,
        name: selectedTemplateName,
        sampleFileName: selectedTemplateFile,
        affinities: associatedAffinities.length ? associatedAffinities : null,
        referenceAreas: areasRef.map( area => {
          const { value, fieldType, coords, pageNumber } = area;
          const { x, y, w, h } = coords;
          return {
            value: value,
            zoneArea: [x, y, w, h],
            fieldType: fieldType,
            pageNumber: pageNumber,
          }
        }),
        // zoneMappings: areas.map( area => {
        zoneMappings: where(areas, {mapped: true}).map( area => {
          const { coords, pageNumber, required } = area;
          const { fieldDescription, fieldName, fieldType } = area.fieldDefinition;
          const { x=0, y=0, w=0, h=0 } = (coords || {});
          // const isZoneDefined = x && y && w && h;
          return {
            fieldDefinition: {
              fieldDescription: fieldDescription,
              fieldName: fieldName,
              fieldType: fieldType,
              fieldRequired: required,
            },
            fieldFormat: '',
            zoneArea: [x, y, w, h],
            pageNumber: pageNumber,
            // zoneArea: isZoneDefined ? [x, y, w, h] : [],
            // pageNumber: isZoneDefined ? pageNumber : -1,
          }
        })
      }
      // };
      // are we modifying an existing template ?
      // console.warn('saveTemplate:: ', json);
      const template = find(templates, {name: selectedTemplateName});
      let id = ''
      if (template) {
        id = json.id = template.id;
      }
      dispatch(savingTemplate());
      const response = await (await fetch(`${templateService}/${id}`, {
        method: id ? 'put': 'post',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(json)})).json();
      const code = response.error.code;
      // console.log('SaveTemplate::', json, response.data, id, template);
      if (code === 200 || code === 201) {
        dispatch(savedTemplate());
        toastr.clear();
        toastr.success(id ? 'Template updated' : 'Template saved');
        dispatch(cancelTemplate());
        dispatch(cleanProfileData());
        $('a[href="#viewProfiles"]').tab('show');
      } else {
        dispatch(saveTemplateError());
        showToastrOnError(response.error.message);
      }
    } catch (error) {
      showToastrOnError('Service unavailable');
    }
  }
}

export function addLenderAffinities(affinities) {
  return {
    type: ADD_LENDER_AFFINITIES,
    affinities: affinities
  }
}

export function removeLenderAffinities(affinities) {
  return {
    type: REMOVE_LENDER_AFFINITIES,
    affinities: affinities
  }
}

export function createNewTemplate (value) {
  return {
    type: CREATE_NEW_TEMPLATE,
    value: value
  }
}

export function resetLenderAffinities () {
  return {
    type: RESET_LENDER_AFFINITIES
  }
}

export function selectPage (page) {
  return {
    type: SELECT_PAGE,
    page: page
  }
}

export function showMapping (value) {
  return {
    type: SHOW_MAPPING,
    visible: value
  }
}

// View Profiles endpoints
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
      const response = await (await fetch(`${templateService}?${queryString}&noCache=${noCache}`)).json();
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

export function saveSampleTemplateName (sampleName) {
  return {
    type: SAVE_SAMPLE_TEMPLATE_NAME,
    sampleName
  }
}

export function cleanSampleTemplateName () {
  return {
    type: CLEAN_SAMPLE_TEMPLATE_NAME
  }
}

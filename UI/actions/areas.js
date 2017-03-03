import fetch from 'isomorphic-fetch';
import { find, debounce } from 'lodash';
import queryString from 'query-string';
import toastr from 'toastr';
import baseToastrConfig from 'lib/baseToastrConfig';
import config from 'config';

export const SET_AREAS = 'SET_AREAS';
export const SELECT_AREA = 'SELECT_AREA';
export const SELECT_ZONE = 'SELECT_ZONE';
export const CHANGE_VALIDATION = 'CHANGE_VALIDATION';
export const REQUEST_AREA_TEXT = 'REQUEST_AREA_TEXT';
export const RECEIVE_AREA_TEXT = 'RECEIVE_AREA_TEXT';
export const RESET_AREA = 'RESET_AREA';
export const SET_COORD_VALUE = 'SET_COORD_VALUE';

const extractionService = `${config.utilitiesService}zone-data-extraction/extract-zone-text`;

toastr.options = baseToastrConfig();

export function setAreas (areas) {
  return {
    type: SET_AREAS,
    areas: areas,
  }
}

function requesAreaText () {
  return {
    type: REQUEST_AREA_TEXT,
  }
}

function receiveAreaText (text) {
  return {
    type: RECEIVE_AREA_TEXT,
    text: text,
  }
}

export const fetchAreaText = debounce(_fetchAreaText, 250, {
  leading: true,
});

function _fetchAreaText () {
  return (dispatch, getState) => {
    const areasZone = getState().templateAreas;
    const areasRef = getState().templateAreasRef;
    let area = find(areasZone, {selected: true});
    area = area || find(areasRef, {selected: true});
    if (!area) {
      toastr.warning('No area selected');
      return
    }
    const { selectedPage, selectedTemplateFile } = getState().template;
    const { fieldDefinition, pageNumber, coords: {x, y, w, h} } = area;
    const areaSlot = fieldDefinition ? fieldDefinition.fieldType : area.fieldType;
    const query = queryString.stringify({
      'page-number': pageNumber || selectedPage,
      'type': 'text',
      'x': x,
      'y': y,
      'w': w,
      'h': h,
      'source': selectedTemplateFile,
      'validation': areaSlot,
    });
    dispatch(requesAreaText());
    fetch(`${extractionService}?${query}`)
      .then(res => res.json())
      .then(json => {
        const code = json.error.code;
        if (code === 200 || code === 201) {
          dispatch(receiveAreaText(json.data.extractedText));
        } else {
          dispatch(receiveAreaText('Error Fetching'));
          toastr.error(json.error.message);
        }
      }).catch((error) => {
        dispatch(receiveAreaText(error));
        toastr.error(error.toString());
      });
  }
}

export function resetArea () {
  return {
    type: RESET_AREA,
  }
}

export function selectArea (id) {
  return {
    type: SELECT_AREA,
    id: id,
  }
}

export function selectZone (coords, page) {
  return {
    type: SELECT_ZONE,
    coords: coords,
    page: page,
  }
}

export function setCoordValue(coord, value) {
  return {
    type: SET_COORD_VALUE,
    coord: coord,
    value: value,
  }
}

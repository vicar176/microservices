export const ADD_AREA_REF = 'ADD_AREA_REF';
export const SELECT_AREA_REF = 'SELECT_AREA_REF';
export const SET_AREAS_REF = 'SET_AREAS_REF';
export const DELETE_AREA_REF = 'DELETE_AREA_REF';

export function addAreaRef() {
  return {
    type: ADD_AREA_REF
  }
}

export function setAreasRef(areas) {
  return {
    type: SET_AREAS_REF,
    areas: areas
  }
}

export function selectAreaRef(id) {
  return {
    type: SELECT_AREA_REF,
    id: id
  }
}

export function deleteAreaRef(id) {
  return {
    type: DELETE_AREA_REF,
    id: id
  }
}
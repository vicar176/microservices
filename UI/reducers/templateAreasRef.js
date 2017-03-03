import {
  SELECT_AREA,
  SELECT_ZONE,
  REQUEST_AREA_TEXT,
  RECEIVE_AREA_TEXT,
  RESET_AREA,
  SET_COORD_VALUE,
} from 'actions/areas';

import {
  ADD_AREA_REF,
  SELECT_AREA_REF,
  SET_AREAS_REF,
  DELETE_AREA_REF,
} from 'actions/areasRef';

import {
  CANCEL_TEMPLATE,
  SELECT_TEMPLATE_FILE,
} from 'actions/template';

let areaID = 0;
let nextState = null;

export default function templateAreasRef(state = [], action) {

  switch (action.type) {

    case SET_AREAS_REF:
      areaID = 0;
      return action.areas.map( area => {
        const { value, fieldType, pageNumber, zoneArea } = area;
        const [x, y, w, h] = zoneArea;
        return {
          id: areaID++,
          value: value,
          fieldType: fieldType,
          pageNumber: pageNumber,
          coords: {
            x: x,
            y: y,
            w: w,
            h: h
          },
          mapped: true,
          selected: false,
          isFetching: false,
          areaType: 'reference'
        }
      });

    case ADD_AREA_REF:
      nextState = state.map( area => {
        return Object.assign({}, area, { selected: false });
      });

      nextState.push({
        id: areaID++,
        value: '',
        fieldType: 'alphanumeric',
        pageNumber: 1,
        coords: null,
        selected: true,
        mapped: false,
        isFetching: false,
        areaType: 'reference',
      });

      return nextState;

    case SELECT_AREA_REF:
      return state.map( area => {
        return area.id === action.id ?
          Object.assign({}, area, { selected: true }) :
          Object.assign({}, area, { selected: false });
      });

    case SELECT_AREA:
      return state.map( area => {
        return Object.assign({}, area, { selected: false });
      });

    case SELECT_ZONE:
      return state.map( area => {
        return area.selected ?
          Object.assign({}, area, {
            coords: action.coords,
            mapped: true,
            pageNumber: Number(action.page)
          }) :
          area;
      });

    case REQUEST_AREA_TEXT:
      return state.map( area => {
        return area.selected ?
          Object.assign({}, area, { isFetching: true }) :
          area;
      });

    case RECEIVE_AREA_TEXT:
      return state.map( area => {
        return area.selected ?
          Object.assign({}, area, { isFetching: false, value: action.text }) :
          area;
      });

    case SET_COORD_VALUE:
      return state.map( area => {
        return area.selected ?
          Object.assign({}, area, {
            coords: Object.assign({}, area.coords, {
              [action.coord]: action.value,
            }),
          }) :
          area;
      });

    case RESET_AREA:
      return state.map( area => {
        return area.selected ?
          Object.assign({}, area, { coords: null, mapped: false, value: '' }) : 
          area;
      });

    case DELETE_AREA_REF:
      areaID = 0;
      return state.filter( area => area.id !== action.id)
        .map( area => Object.assign(area, {id: areaID++}));

    case CANCEL_TEMPLATE:
    case SELECT_TEMPLATE_FILE:
      areaID = 0;
      return [];

    default:
      return state;

  }

}

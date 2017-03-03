import {
  SET_AREAS,
  REQUEST_AREA_TEXT,
  RECEIVE_AREA_TEXT,
  SELECT_AREA,
  SELECT_ZONE,
  RESET_AREA,
  SET_COORD_VALUE,
} from 'actions/areas';

import {
  SELECT_AREA_REF,
  ADD_AREA_REF,
} from 'actions/areasRef';

import {
  CANCEL_TEMPLATE,
  SELECT_TEMPLATE_FILE,
} from 'actions/template';

const initialState = [];

export default function templateAreas(state = initialState, action) {

  switch (action.type) {

    case SET_AREAS:
      return action.areas.map( (area, i) => {
        return Object.assign(area, {
          id: i,
          isFetching: false,
          selected: false,
          text: '',
          pageNumber: area.pageNumber || 1,
          areaType: 'selection',
        });
      }).slice();

    case SELECT_AREA:
      return state.map( area => {
        return area.id === action.id ?
          Object.assign({}, area, { selected: true }) :
          Object.assign({}, area, { selected: false });
      });

    case SELECT_AREA_REF:
    case ADD_AREA_REF:
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
          Object.assign({}, area, { isFetching: false, text: action.text }) :
          area;
      });

    case RESET_AREA:
      return state.map( area => {
        return area.selected ?
          Object.assign({}, area, { coords: null, mapped: false, text: '' }) : 
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

    case SELECT_TEMPLATE_FILE:
    case CANCEL_TEMPLATE:
      return [];

    default:
      return state;

  }

}

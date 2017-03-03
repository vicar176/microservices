import {
  RECEIVE_TEMPLATE_PROFILE,
} from 'actions/template';

export default function templateProfile (state = {}, action) {

  switch (action.type) {

    case RECEIVE_TEMPLATE_PROFILE:
      return Object.assign({}, state, action.profile);

    default: 
      return state;
  }
}
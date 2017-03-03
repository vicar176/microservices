import { createStore, applyMiddleware } from 'redux';
import thunkMiddleware from 'redux-thunk';
import createLogger from 'redux-logger';
import rootReducer from 'reducers';
import userSession from './middleware/userSession';

const env = process.env.NODE_ENV;

const middlewares = [thunkMiddleware];

if (env !== 'local') {
  middlewares.push(userSession);
}

if (env === 'development' || env === 'local') {
  middlewares.push(createLogger());
}

const createStoreWithMiddleware = applyMiddleware(...middlewares)(createStore);

export default function configureStore(initialState) {
  return createStoreWithMiddleware(rootReducer, initialState);
}

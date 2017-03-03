import {
  IS_FETCHING_INGESTION_REPROCESSING,
  LENGHT_OF_DOCUMENTS_FOR_REPROCESSING,
  QUERY_FILTER
} from 'actions/ingestionReprocessing';

const initialState = {
  isFetchingIngestionReprocessing: false,
  lenghtOfDocumentsForReprocessing: 0,
  queryFilter: ""
};


export default function ingestionReprocessing(state = initialState, action) {

  switch (action.type) {

    case IS_FETCHING_INGESTION_REPROCESSING:
      return Object.assign({}, state, { isFetchingIngestionReprocessing: action.value });

    case LENGHT_OF_DOCUMENTS_FOR_REPROCESSING:
      return Object.assign({}, state, { lenghtOfDocumentsForReprocessing: action.value });

    case QUERY_FILTER:
      return Object.assign({}, state, { queryFilter: action.value });

    default:
      return state;

  }

}

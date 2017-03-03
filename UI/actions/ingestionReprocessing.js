import fetch from 'isomorphic-fetch';
import config from 'config';
import toastr from 'toastr';
import baseToastrConfig from 'lib/baseToastrConfig';

const { ingestionState, ingestionService } = config;

export const IS_FETCHING_INGESTION_REPROCESSING = 'IS_FETCHING_INGESTION_REPROCESSING';
export const INGESTION_REPROCESSING = 'INGESTION_REPROCESSING';
export const QUERY_FILTER = 'QUERY_FILTER';
export const LENGHT_OF_DOCUMENTS_FOR_REPROCESSING = 'LENGHT_OF_DOCUMENTS_FOR_REPROCESSING';

export function isFetchingIngestionReprocessing (value) {
  return {
    type: IS_FETCHING_INGESTION_REPROCESSING,
    value
  }
}

export function setQueryFilter (value) {
  return {
    type: QUERY_FILTER,
    value
  }
}

export function setLenghtOfDocumentsFoReprocessing (value) {
  return {
    type: LENGHT_OF_DOCUMENTS_FOR_REPROCESSING,
    value
  }
}

export function fetchIngestionReprocessingList (query) {
  return async dispatch => {
    try {
      dispatch(isFetchingIngestionReprocessing(true));
      const apiQuery = query.replace(/"@|@"/gi,"");
      dispatch(setQueryFilter(apiQuery));
      const response = await (await fetch(`${ingestionState}ingestion-steps/failed?${apiQuery}`)).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
        if (apiQuery === "page=1&size=25") {
          dispatch(setLenghtOfDocumentsFoReprocessing(response.data.totalItems));
        }
        return response.data;
      }
      showToastrOnError(response.error.message);
    } catch (error) {
      showToastrOnError('Service unavailable');
    } finally {
      dispatch(isFetchingIngestionReprocessing(false))
    }
  }
}

export function reprocessDocuments (type = "all") {
  return async (dispatch,getState) => {
    try {
      const { firstName, lastName } = getState().auth.user;
      const { queryFilter } = getState().ingestionReprocessing;
      const updatedByUrl = `updatedBy=${firstName + lastName}`;

      let url = `${ingestionService}documents/failed?${updatedByUrl}`;
      if (type === "filtered") {
        url = `${ingestionService}documents/failed?${updatedByUrl}&${queryFilter}`
      }

      const response = await (await fetch(url, {
        method: 'POST'
      })).json();

      const code = response.error.code;
      if (code === 200 || code === 201) {
        toastr.success( type === "filtered" ? "Reprocess Filtered Documents Started" : "Reprocess for All Documents Started");
      } else {
        toastr.error(response.error.message);
      }
    } catch (error) {
      showToastrOnError('Service unavailable');
    }

  }
}

function showToastrOnError(message) {
  toastr.options = baseToastrConfig();
  toastr.error(message);
}

// export function fetchFieldDefinitionList (queryString) {
//   return async dispatch => {
//     try {
//       dispatch(isFetchingFieldDefinitions(true));

//       const fieldListQuery = (queryString.indexOf('filter') > -1) ? `&${queryString}` : `filter=fieldName="@@"&${queryString}`;
//       // console.log('fetchFieldDefinitionList:: ', fieldListQuery);
//       const response = await (await fetch(`${fieldDefinition}?${fieldListQuery}&noCache=${noCache}`)).json();
//       const code = response.error.code;
//       if (code === 200 || code === 201) {
//         return response.data;
//       }
//       showToastrOnError(response.error.message);
//     } catch (error) {
//       showToastrOnError('Service unavailable');
//     } finally {
//       dispatch(isFetchingFieldDefinitions(false));
//     }
//   }
// }

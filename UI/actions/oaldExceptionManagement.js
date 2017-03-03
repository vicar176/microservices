import toastr from 'toastr';
import baseToastrConfig from 'lib/baseToastrConfig';
import config from 'config';
import moment from 'moment';

export const SET_CURRENT_PAGE = 'SET_CURRENT_PAGE';
export const SET_ITEMS_PER_PAGE = 'SET_ITEMS_PER_PAGE';
export const SET_TOTAL_ITEMS = 'SET_TOTAL_ITEMS';
export const SELECT_REVIEW_METHOD = 'SELECT_REVIEW_METHOD';
export const SELECT_DOCUMENT_LIST = 'SELECT_DOCUMENT_LIST';
export const IS_FETCHING_DOCUMENT_LIST = 'IS_FETCHING_DOCUMENT_LIST';
export const IS_FETCHING_DOCUMENTS = 'IS_FETCHING_DOCUMENTS';
export const IS_FETCHING_DOCUMENT = 'IS_FETCHING_DOCUMENT';
export const RECEIVE_DOCUMENT_LIST = 'RECEIVE_DOCUMENT_LIST';
export const RECEIVE_DOCUMENTS = 'RECEIVE_DOCUMENTS';
export const CLEAR_DOCUMENTS = 'CLEAR_DOCUMENTS';
export const ADD_DOCUMENT_LIST = 'ADD_DOCUMENT_LIST';
export const NO_DOCUMENTS = 'NO_DOCUMENTS';
export const UPDATE_DOCUMENT_FIELD = 'UPDATE_DOCUMENT_FIELD';

// Config
const {
  mediaMetadata,
  utilitiesService,
  ingestionService
} = config;

const metaDatasService = `${mediaMetadata}metadatas/`;
const imageBaseUrl = `${utilitiesService}s3/extraction/`;
const documentExtractedService = `${metaDatasService}`;

// Test data:
import oaldExceptionData from "json!data/oaldExceptionsByList.json";

export function setCurrentPage(newPage) {
  return {
    type: SET_CURRENT_PAGE,
    newPage
  }
}

export function selectReviewMethod(reviewMethod) {
  return {
    type: SELECT_REVIEW_METHOD,
    reviewMethod
  }
}

export function selectDocumentList(selectedDocumentList) {
  return {
    type: SELECT_DOCUMENT_LIST,
    selectedDocumentList
  }
}

export function addDocumentList(newDocumentList) {
  return {
    type: ADD_DOCUMENT_LIST,
    newDocumentList
  }
}

export function isFetchingDocumentList(isFetching) {
  return {
    type: IS_FETCHING_DOCUMENT_LIST,
    isFetching
  }
}

function receiveDocumentList(documentList) {
  return {
    type: RECEIVE_DOCUMENT_LIST,
    documentList
  }
}

export function fetchDocumentsList() {
  return async (dispatch,getState) => {
    try {
      dispatch(isFetchingDocumentList(true));
      dispatch(isFetchingDocument(false));
      const {
        currentPage,
        selectedReviewMethod
      } = getState().oaldExceptionManagement;

      if (selectedReviewMethod === 'byPortfolio') {
        // /media-metadata/metadatas/portfolios?filter=documentStatus=%22extracted%22&page=2&size=2
        const response = await (await fetch(`${documentExtractedService}portfolios?filter=documentStatus="validated"&page=${currentPage +1}&size=15`)).json();
        const code = response.error.code;

        const items = [];
        response.data.items.forEach((item) => {
          if (item !== 0) {
            items.push({
              value: item
            });
          }
        });

        if (code === 200 || code === 201) {
          dispatch(receiveDocumentList(items));
          dispatch(isFetchingDocumentList(false));
        } else {
          showToastrOnError(response.error.message);
        }
      } else {
        // /media-metadata/metadatas/portfolios?filter=documentIdList=%22extracted%22
        const response = await (await fetch(`${documentExtractedService}?documentStatus=validated`)).json();
        const code = response.error.code;

        const items = [];
        response.data.items.forEach((item) => {
          if (item.documentId !== 0 ) {
            items.push({
              value: item.documentId
            });
          }
        });

        if (code === 200 || code === 201) {
          dispatch(receiveDocumentList(items));
          dispatch(isFetchingDocumentList(false));
        } else {
          showToastrOnError(response.error.message);
        }
      }
    } catch (error) {
      showToastrOnError('Service Unavailable');
    } finally {
      dispatch(isFetchingDocumentList(false));
    }
  }
}

export function isFetchingDocuments(isFetching) {
  return {
    type: IS_FETCHING_DOCUMENTS,
    isFetching
  }
}

export function isFetchingDocument(isFetchingDocument) {
  return {
    type: IS_FETCHING_DOCUMENT,
    isFetchingDocument
  }
}

export function noDocuments(noDocuments) {
  return {
    type: NO_DOCUMENTS,
    noDocuments
  }
}

export function clearDocuments() {
  return {
    type: CLEAR_DOCUMENTS,
  }
}

export function receiveDocuments(documents) {
  return {
    type: RECEIVE_DOCUMENTS,
    documents
  }
}

export function setItemsPerPage(itemsPerPage) {
  return {
    type: SET_ITEMS_PER_PAGE,
    itemsPerPage
  }
}

export function setTotalItems(totalItems) {
  return {
    type: SET_TOTAL_ITEMS,
    totalItems
  }
}

export function fetchDocuments() {
  return async (dispatch,getState) => {
    try {
      dispatch(isFetchingDocuments(true));
      dispatch(noDocuments(false));

      const {
        currentPage,
        itemsPerPage,
        selectedReviewMethod,
        selectedDocumentList
      } = getState().oaldExceptionManagement;

      const documents = [];
      const queryIds = selectedDocumentList.join(',');
      let query = `${documentExtractedService}?filter=documentStatus="validated"`;
      let response = "";
      let code = "";
      let items = "";

      switch (selectedReviewMethod) {
        case 'byPortfolio': {
          // ?filter=documentStatus="validated"&cportfolioNumber=[PORTFOLIO_ID]&page=1&size=1
          query += `|portfolioNumberList=${queryIds}`;
          break;
        }
        case 'manualList': {
          // ?filter=documentStatus="validated"&documentIdList=[LIST_OF_IDS]&page=1&size=1
          query += `|documentIdList=${queryIds}`;
          break;
        }
        default:
      }

      response = await (await fetch(`${query}&page=${currentPage+1}&size=${itemsPerPage}`)).json();
      // response = oaldExceptionData; // Test Data
      code = response.error.code;
      items = response.data.items;

      if (code === 200 || code === 201) {
        dispatch(receiveDocuments([]));

        if (!items.length) {
          showToastrOnError("There are no items to show right now, please try later.");
          return;
        }

        for (let i = 0; i < response.data.items.length; i++) {
          documents.push({
            documentId: items[i].document.documentName.documentId,
            accountNumber: items[i].accountNumber,
            dataElements: items[i].dataElements || []
          });
          let name = '';
          if (items[i].extraction && items[i].extraction.hasOwnProperty("templateMappingProfile")) {
            name = items[i].extraction.templateMappingProfile.name;
          }
          documents[i].extraction = {
            template: {
              name: name
            }
          };

          // snippet
          const documentNameStringForUrl = items[i].document.documentNameString.split(".")[0];

          for (let j = 0; j < documents[i].dataElements.length; j++) {
            documents[i].dataElements[j].snipet = `${imageBaseUrl}objects?key=${documentNameStringForUrl}/${documents[i].dataElements[j].snipet}`;
          }

          // merge the remaining properties of the object
          // documents[i].autoValidation.autoValidated = true;
          documents[i] = Object.assign(items[i], documents[i]);

          function sortFields(a, b) {
            if (a.fieldDefinition.fieldName < b.fieldDefinition.fieldName) {
              return -1;
            }
            if (a.fieldDefinition.fieldName > b.fieldDefinition.fieldName) {
              return 1;
            }
            return 0;
          }
          documents[i].dataElements = items[i].dataElements.sort(sortFields);
        }
        // console.warn('fetchDocuments::documents ', documents);
        dispatch(setTotalItems(response.data.totalItems));
        dispatch(receiveDocuments(documents));
      } else {
        showToastrOnError(response.error.message);
      }
      if (!documents.length) {
        dispatch(noDocuments(true));
      }
    } catch (error) {
      showToastrOnError(error);
    } finally {
      dispatch(isFetchingDocuments(false));
    }
  }
}

export function updateDocument(document) {
  return async (dispatch,getState) => {
    try {
      dispatch(isFetchingDocument(document.documentId));
      delete document._class;
      const { firstName, lastName } = getState().auth.user;
      const user = firstName +' '+ lastName;
      document.manualValidation = {
        "manualValidated": true,
        "manualValidatedDate": moment(),
        "manualValidatedBy": user
      };

      const response = await (await fetch(`${documentExtractedService}${document.documentId}/manual-validation`, {
        method: 'put',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(document)
      })).json();

      const code = response.error.code;
      if (code === 200 || code === 201) {
        const responseWorkflow = await (await fetch(`${ingestionService}documents/${document.documentId}?documentName=${document.document.documentNameString}&updatedBy=${user}`, {
          method: 'post',
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(document)
        })).json();

        const code = responseWorkflow.error.code;
        if (code === 200 || code === 201) {
          showToastrOnSuccess("Document Updated");
        } else {
          showToastrOnError(responseWorkflow.error.message);
        }
      } else {
        showToastrOnError(response.error.message);
      }
    } catch (error) {
      showToastrOnError('Service Unavailable');
    } finally {
      const { selectedReviewMethod } = getState().oaldExceptionManagement;
      dispatch(isFetchingDocument(false));
      dispatch(fetchDocuments({method: selectedReviewMethod}));
    }
  }
}

export function updateDocumentField(documentIndex, fieldName, newValue) {
  return {
    type: UPDATE_DOCUMENT_FIELD,
    documentIndex,
    fieldName,
    newValue
  }
}

function showToastrOnError(message) {
  toastr.options = baseToastrConfig();
  toastr.error(message);
}

function showToastrOnSuccess(message) {
  const config = baseToastrConfig();
  config.showDuration = 100;
  toastr.options = config;
  toastr.success(message);
}

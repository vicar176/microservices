// import queryString from 'query-string';
import fetch from 'isomorphic-fetch';
import toastr from 'toastr';
import baseToastrConfig from 'lib/baseToastrConfig';
import config from 'config';

export const SELECT_PORTFOLIOS_ID = 'SELECT_PORTFOLIOS_ID';
export const CLEAN_PORTFOLIOS_ID = 'CLEAN_PORTFOLIOS_ID';
export const CLEAN_PORTFOLIO_DETAIL = 'CLEAN_PORTFOLIO_DETAIL';
export const CLEAN_FAILED_DOCUMENTS = 'CLEAN_FAILED_DOCUMENTS';
export const RECEIVE_PORTFOLIOS_ID = 'RECEIVE_PORTFOLIOS_ID';
export const RECEIVE_FOUND_TEMPLATES = 'RECEIVE_FOUND_TEMPLATES';
export const RECEIVE_NOT_FOUND_TEMPLATES = 'RECEIVE_NOT_FOUND_TEMPLATES';
export const RECEIVE_PORTFOLIO_SUMMARY = 'RECEIVE_PORTFOLIO_SUMMARY';
export const RECEIVE_FAILED_DOCUMENTS = 'RECEIVE_FAILED_DOCUMENTS';
export const SET_PORTFOLIO_ACTIVE_TAB = 'SET_PORTFOLIO_ACTIVE_TAB';
export const IS_PORTFOLIO_SELECTED = 'IS_PORTFOLIO_SELECTED';
export const IS_FETCHING_PORTFOLIOS_ID = 'IS_FETCHING_PORTFOLIOS_ID';
export const IS_FETCHING_PORTFOLIO_SUMMARY = 'IS_FETCHING_PORTFOLIO_SUMMARY';
export const IS_FETCHING_FOUND_TEMPLATES = 'IS_FETCHING_FOUND_TEMPLATES';
export const IS_FETCHING_NOT_FOUND_TEMPLATES = 'IS_FETCHING_NOT_FOUND_TEMPLATES';
export const IS_FETCHING_FAILED_DOCS = 'IS_FETCHING_FAILED_DOCS';
export const TEMPLATE_FOR_REPROCESS = 'TEMPLATE_FOR_REPROCESS';

const {
  mediaMetadata,
  ingestionService
} = config;
const portfoliosService = `${mediaMetadata}metadatas/portfolios`;
const documentExtractedService = `filter=documentStatusList="extracted","reprocess","templateNotFound"`;

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

export function selectPortfoliosId(selectedPortfoliosId) {
  return {
    type: SELECT_PORTFOLIOS_ID,
    selectedPortfoliosId
  }
}

export function templateForReprocess(templateForReprocess) {
  return {
    type: TEMPLATE_FOR_REPROCESS,
    templateForReprocess
  }
}

export function cleanPortfoliosId() {
  return {
    type: CLEAN_PORTFOLIOS_ID
  }
}

export function cleanPortfolioDetail() {
  return {
    type: CLEAN_PORTFOLIO_DETAIL
  }
}

export function cleanFailedDocuments() {
  return {
    type: CLEAN_FAILED_DOCUMENTS
  }
}

export function receivePortfoliosId(receivedPortfoliosId) {
  return {
    type: RECEIVE_PORTFOLIOS_ID,
    receivedPortfoliosId
  }
}

export function receivePortfolioSummary(portfolioSummaryList) {
  return {
    type: RECEIVE_PORTFOLIO_SUMMARY,
    portfolioSummaryList
  }
}

export function receiveFoundTemplates(templates) {
  return {
    type: RECEIVE_FOUND_TEMPLATES,
    templates
  }
}

export function receiveNotFoundTemplates(templates) {
  return {
    type: RECEIVE_NOT_FOUND_TEMPLATES,
    templates
  }
}

export function receiveFailedDocs(failedDocList) {
  return {
    type: RECEIVE_FAILED_DOCUMENTS,
    failedDocList
  }
}

export function setPortfolioActiveTab(activePortfolioTabId) {
  return {
    type: SET_PORTFOLIO_ACTIVE_TAB,
    activePortfolioTabId
  }
}

export function isPortfolioSelected(isPortfolioSelected) {
  return {
    type: IS_PORTFOLIO_SELECTED,
    isPortfolioSelected
  }
}

export function isFetchingPortfoliosId(isFetching) {
  return {
    type: IS_FETCHING_PORTFOLIOS_ID,
    isFetching
  }
}

export function isFetchingPortfolioSummary(isFetching) {
  return {
    type: IS_FETCHING_PORTFOLIO_SUMMARY,
    isFetching
  }
}

export function isFetchingFoundTemplates(isFetching) {
  return {
    type: IS_FETCHING_FOUND_TEMPLATES,
    isFetching
  }
}

export function isFetchingNotFoundTemplates(isFetching) {
  return {
    type: IS_FETCHING_NOT_FOUND_TEMPLATES,
    isFetching
  }
}

export function isFetchingFailedDocs(isFetchingFailedDocs) {
  return {
    type: IS_FETCHING_FAILED_DOCS,
    isFetchingFailedDocs
  }
}

export function fetchPortfoliosId() {
  return async dispatch => {
    try {
      dispatch(isFetchingPortfoliosId(true));
      const noCache = Math.random();
      // /media-metadata/metadatas/portfolios?filter=documentStatus="extracted"&showSummary=false
      const portfoliosIds = [];
      const response = await (await fetch(`${portfoliosService}?${documentExtractedService}&showSummary=false&noCache=${noCache}`)).json();
      const code = response.error.code;
      response.data.items.forEach(id => {
        if (id !== 0 ) {
          portfoliosIds.push({
            id: id
          });
        }
      });
      if (code === 200 || code === 201) {
        dispatch(receivePortfoliosId(portfoliosIds));
      } else {
        showToastrOnError(response.error.message);
      }
    } catch (error) {
      showToastrOnError('Service unavailable');
    } finally {
      dispatch(isFetchingPortfoliosId(false));
    }
  }
}

export function fetchPortfolioSumary(portfoliosId) {
  return async dispatch => {
    try {
      dispatch(receivePortfolioSummary([]));
      dispatch(isFetchingPortfolioSummary(true));
      const noCache = Math.random();
      const portfoliosList = portfoliosId.join();
      // /media-metadata/metadatas/portfolios?filter=documentStatus="extracted"&showSummary=true
      const response = await (await fetch(`${portfoliosService}?${documentExtractedService}|portfolioNumberList=${portfoliosList}&showSummary=true&noCache=${noCache}`)).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
        // console.log('fetchPortfolioSumary:: ', response.data.items);
        dispatch(receivePortfolioSummary(response.data.items));
      } else {
        showToastrOnError(response.error.message);
      }
    } catch (error) {
      showToastrOnError('Service unavailable');
    } finally {
      dispatch(isFetchingPortfolioSummary(false));
    }
  }
}

export function fetchPortfolioDetail(requestedParams) {
  return async dispatch => {
    const { portfolioId, templateType, queryString } = requestedParams;
    try {
      if (templateType === 'templatesFound') {
        dispatch(isFetchingFoundTemplates(true));
      } else {
        dispatch(isFetchingNotFoundTemplates(true));
      }
      // /media-metadata/metadatas/portfolios/1024/{templateType}?filter=documentStatusList="extracted","reprocess","templateNotFound"|{filterQuery}
      const noCache = Math.random();
      const exceptionQuery = (queryString.indexOf('filter') > -1) ? queryString.replace('filter=', '|') : `&${queryString}`;

      const response = await (await fetch(`${portfoliosService}/${portfolioId}/${templateType}?${documentExtractedService}${exceptionQuery}&noCache=${noCache}`)).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
        if (templateType === 'templatesFound') {
          // response.data.totalItems = 3;
          // response.data.items = [
          //   {
          //     "templateId": "e2afb09b-5211-4708-a93a-a3bcbe75fdcf",
          //     "templateName": "STMT_SHERMAN ORIGINATOR III_CREDIT ONE BANK, N.A.",
          //     "version": 2,
          //     "updatedBy": "devUser ",
          //     "updateDate": "2016-08-26T15:31:44Z",
          //     "lastRun": "2016-08-26T16:15:05Z",
          //     "documentsFailed": 3,
          //     "reprocess": false
          //   },
          //   {
          //     "templateId": "e2afb09b-5211-4708-a93a-a3bcbe75fdcf",
          //     "templateName": "STMT_SHERMAN ORIGINATOR III_CREDIT ONE BANK, N.A.",
          //     "version": 2,
          //     "updatedBy": "devUser ",
          //     "updateDate": "2016-08-26T15:31:44Z",
          //     "lastRun": "2016-08-26T16:15:05Z",
          //     "documentsFailed": 3,
          //     "reprocess": false
          //   },
          //   {
          //     "templateId": "e2afb09b-5211-4708-a93a-a3bcbe75fdcf",
          //     "templateName": "STMT_SHERMAN ORIGINATOR III_CREDIT ONE BANK, N.A.",
          //     "version": 2,
          //     "updatedBy": "devUser ",
          //     "updateDate": "2016-08-26T15:31:44Z",
          //     "lastRun": "2016-08-26T16:15:05Z",
          //     "documentsFailed": 3,
          //     "reprocess": false
          //   }
          // ]
          dispatch(receiveFoundTemplates(response.data));
        } else {
          // response.data.totalItems = 3;
          // response.data.items = [
          //   {
          //     "documentType": "gbltr",
          //     "originalLender": "AMANA",
          //     "documentsFailed": 15
          //   },
          //   {
          //     "documentType": "gbltr",
          //     "originalLender": "AMANA",
          //     "documentsFailed": 15
          //   },
          //   {
          //     "documentType": "gbltr",
          //     "originalLender": "AMANA",
          //     "documentsFailed": 15
          //   }
          // ]
          dispatch(receiveNotFoundTemplates(response.data));
        }
        return response.data;
      }
      showToastrOnError(response.error.message);
    } catch (error) {
      showToastrOnError('Service unavailable');
    } finally {
      if (templateType === 'templatesFound') {
        dispatch(isFetchingFoundTemplates(false));
      } else {
        dispatch(isFetchingNotFoundTemplates(false));
      }
    }
  }
}

export function fetchFailedDocs(query) {
  // query = {template: "Menards_ChgOff4", portfolio: "1025"}
  return async dispatch => {
    try {
      dispatch(isFetchingFailedDocs(true));
      const queryFilter = !query.template ? `|originalDocumentType.code="${query.type}"|originalLenderName="${query.lender}"` : '';
      const noCache = Math.random();
      const templateId = query.template ? '.id="' + query.type + '"' : '=IS NULL';
      // /media-metadata/metadatas/portfolios/1024/documents?filter=documentStatus="extracted"|extraction.templateMappingProfile=IS NULL
      const response = await (await fetch(`${portfoliosService}/${query.portfolio}/documents?${documentExtractedService}${queryFilter}|extraction.templateMappingProfile${templateId}&noCache=${noCache}`)).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
        dispatch(receiveFailedDocs(response.data));
      } else {
        showToastrOnError(response.error.message);
      }
    } catch (error) {
      showToastrOnError('Service unavailable');
    } finally {
      dispatch(isFetchingFailedDocs(false));
    }
  }
}

export function reprocessTemplate(template, portfolioId) {
  return async (dispatch,getState) => {
    try {
      dispatch(cleanPortfolioDetail());
      const noCache = Math.random();
      const { firstName, lastName } = getState().auth.user;
      const user = firstName +' '+ lastName;
      let templateType = `documents?templateName=${template.templateName}&documentStatus=extracted&updatedBy=${template.updatedBy}`;
      if (template && !template.templateName) {
        templateType = `documentTypes?documentTypeCode=${template.documentType.code}&originalLenderName=${template.originalLender}&updatedBy=${user}`;
      }

      const response = await (await fetch(`${ingestionService}portfolios/${portfolioId}/${templateType}&noCache=${noCache}`, {
        method: 'post',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        }
      })).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
        if (template.templateName) {
          showToastrOnSuccess(`Reprocess for template ${template.templateName} started`);
        } else {
          showToastrOnSuccess(`Reprocess for not found template of Document Type ${template.documentType.code} started`);
        }

        return;
      }
    } catch (error) {
      showToastrOnError(response.error.message);
    }
  }
}

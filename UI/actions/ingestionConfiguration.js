import fetch from 'isomorphic-fetch';
import toastr from 'toastr';
import config from 'config';
import baseToastrConfig from 'lib/baseToastrConfig';
const {
    mediaMetadata,
    profileService,
    utilitiesService,
    ingestionState,
    ingestionService,
    portfolioService,
    ingestionStatus
} = config;

export const SET_STATUS = 'SET_STATUS';
export const SET_BATCHES = 'SET_BATCHES';
export const SET_SUMMARY = 'SET_SUMMARY';
export const SET_ENVIRONMENT = 'SET_ENVIRONMENT';
export const SET_DOCUMENT_TO_PROCESS = 'SET_DOCUMENT_TO_PROCESS';
export const SET_CREATE_SNIPPETS = 'SET_CREATE_SNIPPETS';
export const SET_PDF_TAGGING = 'SET_PDF_TAGGING';
export const SET_BATCH_SIZE = 'SET_BATCH_SIZE';
export const SET_WORKFLOW_THREADS = 'SET_WORKFLOW_THREADS';
export const SET_DOCUMENT_PROCESSOR_CRON = 'SET_DOCUMENT_PROCESSOR_CRON';
export const SET_STOP_BATCH_RUN = 'SET_STOP_BATCH_RUN';
export const IS_SAVING_CONFIGURATION = 'IS_SAVING_CONFIGURATION';
export const IS_STARTING_METRICS = 'IS_STARTING_METRICS';
export const SET_NODES = 'SET_NODES';
export const SET_CORES = 'SET_CORES';
export const SET_COUCHBASE_IOPS = 'SET_COUCHBASE_IOPS';
export const IS_FETCHING_STATUS = 'IS_FETCHING_STATUS';
export const IS_FETCHING_BATCHES = 'IS_FETCHING_BATCHES';
export const IS_FETCHING_SUMMARY = 'IS_FETCHING_SUMMARY';
export const SET_DOCUMENT_PROCESSOR_STATUS = 'SET_DOCUMENT_PROCESSOR_STATUS';
export const SET_ACTIVITIES_STATUS = 'SET_ACTIVITIES_STATUS';
export const SET_WORKER_CPU_MEDIAN = "SET_WORKER_CPU_MEDIAN";
export const SET_COUCHBASE_CPU_MAX = "SET_COUCHBASE_CPU_MAX";
export const SET_AURORA_CPU_MAX = "SET_AURORA_CPU_MAX";
export const SET_TOMCAT_RAM_PERCENT = "SET_TOMCAT_RAM_PERCENT";
export const SET_LEVEL = "SET_LEVEL";

export function setStatus (value) {
  return {
    type: SET_STATUS,
    value
  }
}

export function setBatches (value) {
  return {
    type: SET_BATCHES,
    value
  }
}

export function setSummary (value) {
  return {
    type: SET_SUMMARY,
    value
  }
}

export function setEnvironment (value) {
  return {
    type: SET_ENVIRONMENT,
    value
  }
}

export function setDocumentToProcess (value) {
  return {
    type: SET_DOCUMENT_TO_PROCESS,
    value
  }
}

export function setCreateSnippets (value) {
  return {
    type: SET_CREATE_SNIPPETS,
    value
  }
}

export function setPdfTagging (value) {
  return {
    type: SET_PDF_TAGGING,
    value
  }
}

export function setBatchSize (value) {
  return {
    type: SET_BATCH_SIZE,
    value
  }
}


export function setDocumentProcessorCron (value) {
  return {
    type: SET_DOCUMENT_PROCESSOR_CRON,
    value
  }
}

export function setWorkFlowThreads (value) {
  return {
    type: SET_WORKFLOW_THREADS,
    value
  }
}

export function setStopBatchRun (value) {
  return {
    type: SET_STOP_BATCH_RUN,
    value
  }
}

export function setNodes (value) {
  return {
    type: SET_NODES,
    value
  }
}

export function setCores (value) {
  return {
    type: SET_CORES,
    value
  }
}

export function setCouchbaseIops (value) {
  return {
    type: SET_COUCHBASE_IOPS,
    value
  }
}

export function setDocumentProcessorStatus (value) {
  return {
    type: SET_DOCUMENT_PROCESSOR_STATUS,
    value
  }
}

export function setWorkerCPUMedian (value) {
  return {
    type: SET_WORKER_CPU_MEDIAN,
    value
  }
}

export function setCouchbaseCPUMax (value) {
  return {
    type: SET_COUCHBASE_CPU_MAX,
    value
  }
}

export function setAuroraCPUMax (value) {
  return {
    type: SET_AURORA_CPU_MAX,
    value
  }
}

export function setTomcatRAMPercent (value) {
  return {
    type: SET_TOMCAT_RAM_PERCENT,
    value
  }
}

export function setLevel (value) {
  return {
    type: SET_LEVEL,
    value
  }
}

export function isSavingConfiguration (value) {
  return {
    type: IS_SAVING_CONFIGURATION,
    value
  }
}

export function isStartingIngestion (value) {
  return {
    type: IS_STARTING_INGESTION,
    value
  }
}

export function isStartingMetrics (value) {
  return {
    type: IS_STARTING_METRICS,
    value
  }
}

export function isFetchingStatus (value) {
  return {
    type: IS_FETCHING_STATUS,
    value
  }
}

export function isFetchingBatches (value) {
  return {
    type: IS_FETCHING_BATCHES,
    value
  }
}

export function isFetchingSummary (value) {
  return {
    type: IS_FETCHING_SUMMARY,
    value
  }
}

export function setActivityStatus (value) {
  return {
    type: SET_ACTIVITIES_STATUS,
    value
  }
}

export function processIngestion () {
  return async (dispatch,getState) => {
    try {
      dispatch(isSavingConfiguration(true));
      const { firstName, lastName } = getState().auth.user;
      const { batchSize, documentProcessorCron, totalDocuments, workFlowThreads, nodes, cores, couchBaseIOPS, createSnippets, pdfTagging } = getState().ingestionConfiguration;

      const response = await (await fetch(`${ingestionService}automations/prepareIngestionTest`, {
        method: 'POST',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          "user": firstName +' '+ lastName,
          "totalDocuments": totalDocuments,
          "batchSize": batchSize,
          "workFlowThreads": workFlowThreads,
          "createSnippets": createSnippets,
          "pdfTagging": pdfTagging,
          "documentProcessorCron": documentProcessorCron,
          "nodes": nodes,
          "cores": cores,
          "couchBaseIOPS": couchBaseIOPS
        })
      })).json();

      const code = response.error.code;
      if (code === 200 || code === 201) {
        toastr.success("Processing Ingestion Started");
      } else {
        toastr.error(response.error.message);
      }
    } catch (error) {
      showToastrOnError('Service unavailable');
    } finally {
      dispatch(isSavingConfiguration(false));
    }
  }
}

export function fetchStatus () {
  return async dispatch => {
    try {
      dispatch(isFetchingStatus(true));
      const response = await (await fetch(`${ingestionStatus}ingestion-status`)).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
        dispatch(setStatus(response.data));
      } else {
        showToastrOnError(response.error.message);
      }
    } catch (error) {
      showToastrOnError('Service unavailable');
    } finally {
      dispatch(isFetchingStatus(false));
    }
  }
}

export function fetchBatches () {
  return async dispatch => {
    try {
      dispatch(isFetchingBatches(true));
      const response = await (await fetch(`${ingestionStatus}/batch-executions/summary`)).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
        dispatch(setBatches(response.data));
      } else {
        showToastrOnError(response.error.message);
      }
    } catch (error) {
      showToastrOnError('Service unavailable');
    } finally {
      dispatch(isFetchingBatches(false));
    }
  }
}

export function fetchSummary () {
  return async dispatch => {
    try {
      dispatch(isFetchingSummary(true));
      const response = await (await fetch(`${ingestionStatus}ingestion-trackers/summary`)).json();
      const code = response.error.code;
      if (code === 200 || code === 201) {
        dispatch(setSummary(response.data));
      } else {
        showToastrOnError(response.error.message);
      }
    } catch (error) {
      showToastrOnError('Service unavailable');
    } finally {
      dispatch(isFetchingSummary(false));
    }
  }
}

// Utils
export function cleanActivities (isUpdate, newValue = false) {
  return async (dispatch,getState) => {
    try {
      const { firstName } = getState().auth.user;
      const querystring = isUpdate ? `?newValue=${newValue}&user=${firstName}` : '';
      const response = await (await fetch(`${ingestionService}app/parameters/wfexecution${querystring}`, {
        method: isUpdate ? 'put' : 'get',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        }
      })).json();

      const code = response.error.code;
      const value = isUpdate ? newValue : (response.data === "true" ? true : false);
      if (code === 200 || code === 201) {
        if (newValue) {
          toastr.success("Activities will be cleaned soon.");
        }
        dispatch(setActivityStatus(value));
      } else {
        showToastrOnError(response.error.message);
      }
    } catch (error) {
      showToastrOnError('Service unavailable');
    }
  }
}

export function documentProcessorStatus (isUpdate, newValue = false) {
  return async (dispatch, getState) => {
    try {
      const { firstName } = getState().auth.user;
      const querystring = isUpdate ? `?newValue=${newValue}&updatedBy=${firstName}` : '';
      const response = await (await fetch(`${ingestionStatus}app/parameters/shutdown${querystring}`, {
        method: isUpdate ? 'put' : 'get',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        }
      })).json();
      const code = response.error.code;
      const value = isUpdate ? newValue : (response.data.value === "true" ? true : false);
      if (code === 200 || code === 201) {
        dispatch(setDocumentProcessorStatus(value));
      } else {
        showToastrOnError(response.error.message);
      }
    } catch (error) {
      showToastrOnError('Service unavailable');
    }
  }
}

export function processMetrics () {
  return async (dispatch,getState) => {
    try {
      dispatch(isStartingMetrics(true));
      const { firstName, lastName } = getState().auth.user;
      const { workerCPUMedian, couchbaseCPUMax, auroraCPUMax, tomcatRAMPercent } = getState().ingestionConfiguration;

      const response = await (await fetch(`${ingestionService}automations/metrics`, {
        method: 'POST',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          "user": firstName +' '+ lastName,
          "workerCPUMedian": workerCPUMedian,
          "couchbaseCPUMax": couchbaseCPUMax,
          "auroraCPUMax": auroraCPUMax,
          "tomcatRAMPercent": tomcatRAMPercent
        })
      })).json();

      const code = response.error.code;
      if (code === 200 || code === 201) {
        toastr.success("Processing Ingestion Metrics");
      } else {
        toastr.error(response.error.message);
      }
    } catch (error) {
      showToastrOnError('Service unavailable');
    } finally {
      dispatch(isStartingMetrics(false));
    }
  }
}

export function processLevel () {
  return async (dispatch,getState) => {
    try {
      let { level } = getState().ingestionConfiguration;
      level = level.toUpperCase();
      const query = `/app/log?level=${level}`;

      await Promise.all([
        await fetch(`${mediaMetadata}${query}`),
        await fetch(`${profileService}${query}`),
        await fetch(`${utilitiesService}${query}`),
        await fetch(`${ingestionService}${query}`),
        await fetch(`${portfolioService}${query}`),
        await fetch(`/ingestion-states${query}`),
        await fetch(`/media-ingestions${query}`),
        await fetch(`/accounts${query}`),
        await fetch(`/account-metadata${query}`),
        await fetch(`/media-history${query}`)
      ]);

      toastr.success(`Log Level set to ${level}`);
    } catch (error) {
      showToastrOnError('Service unavailable');
    }
  }
}

function showToastrOnError(message) {
  toastr.options = baseToastrConfig();
  toastr.error(message);
}

import {
  SET_STATUS,
  SET_BATCHES,
  SET_SUMMARY,
  SET_ENVIRONMENT,
  SET_CREATE_SNIPPETS,
  SET_PDF_TAGGING,
  SET_DOCUMENT_TO_PROCESS,
  SET_BATCH_SIZE,
  SET_WORKFLOW_THREADS,
  SET_DOCUMENT_PROCESSOR_CRON,
  SET_STOP_BATCH_RUN,
  CLEAN_CONFIGURATION,
  IS_SAVING_CONFIGURATION,
  IS_STARTING_METRICS,
  SET_NODES,
  SET_CORES,
  SET_COUCHBASE_IOPS,
  IS_FETCHING_STATUS,
  IS_FETCHING_BATCHES,
  IS_FETCHING_SUMMARY,
  SET_ACTIVITIES_STATUS,
  SET_DOCUMENT_PROCESSOR_STATUS,
  SET_WORKER_CPU_MEDIAN,
  SET_COUCHBASE_CPU_MAX,
  SET_AURORA_CPU_MAX,
  SET_TOMCAT_RAM_PERCENT,
  SET_LEVEL
} from 'actions/ingestionConfiguration';

// The initial application state
const initialState = {
  status: [],
  batches: [],
  summary: [],
  environment: "",
  totalDocuments: 1,
  createSnippets: true,
  pdfTagging: true,
  batchSize: 1,
  workFlowThreads: 1,
  documentProcessorCron: "0 0/5 * * * ?",
  nodes: 1,
  cores: 1,
  couchBaseIOPS: 1,
  stopBatchRun: false,
  isSavingConfiguration: false,
  isStartingMetrics: false,
  isFetchingBatches: false,
  isFetchingSummary: false,
  isFetchingStatus: false,
  documentProcesorStatus: false,
  activitiesStatus: false,
  workerCPUMedian: "",
  couchbaseCPUMax: "",
  auroraCPUMax: "",
  tomcatRAMPercent: "",
  level: ""
};

export default function ingestionConfiguration(state = initialState, action) {
  switch (action.type) {
    case CLEAN_CONFIGURATION:
      return Object.assign({}, initialState);

    case SET_STATUS:
      return Object.assign({}, state, { status: action.value });

    case SET_BATCHES:
      return Object.assign({}, state, { batches: action.value });

    case SET_SUMMARY:
      return Object.assign({}, state, { summary: action.value });

    case SET_ENVIRONMENT:
      return Object.assign({}, state, { environment: action.value });

    case SET_DOCUMENT_TO_PROCESS:
      return Object.assign({}, state, { totalDocuments: parseFloat(action.value) });

    case SET_CREATE_SNIPPETS:
      return Object.assign({}, state, { createSnippets: action.value });

    case SET_PDF_TAGGING:
      return Object.assign({}, state, { pdfTagging: action.value });

    case SET_BATCH_SIZE:
      return Object.assign({}, state, { batchSize: parseFloat(action.value) });

    case SET_WORKFLOW_THREADS:
      return Object.assign({}, state, { workFlowThreads: parseFloat(action.value) });

    case SET_DOCUMENT_PROCESSOR_CRON:
      return Object.assign({}, state, { documentProcessorCron: action.value });

    case SET_STOP_BATCH_RUN:
      return Object.assign({}, state, { stopBatchRun: action.value });

    case SET_NODES:
      return Object.assign({}, state, { nodes: parseFloat(action.value) });

    case SET_CORES:
      return Object.assign({}, state, { cores: parseFloat(action.value) });

    case SET_COUCHBASE_IOPS:
      return Object.assign({}, state, { couchBaseIOPS: parseFloat(action.value) });

    case IS_SAVING_CONFIGURATION:
      return Object.assign({}, state, { isSavingConfiguration: action.value });

    case SET_DOCUMENT_PROCESSOR_STATUS:
      return Object.assign({}, state, { documentProcesorStatus: action.value });

    case SET_ACTIVITIES_STATUS:
      return Object.assign({}, state, { activitiesStatus: action.value });

    case SET_WORKER_CPU_MEDIAN:
      return Object.assign({}, state, { workerCPUMedian: action.value });

    case SET_COUCHBASE_CPU_MAX:
      return Object.assign({}, state, { couchbaseCPUMax: action.value });

    case SET_AURORA_CPU_MAX:
      return Object.assign({}, state, { auroraCPUMax: action.value });

    case SET_TOMCAT_RAM_PERCENT:
      return Object.assign({}, state, { tomcatRAMPercent: action.value });

    case SET_LEVEL:
      return Object.assign({}, state, { level: action.value });

    case IS_FETCHING_STATUS:
      return Object.assign({}, state, { isFetchingStatus: action.value });

    case IS_FETCHING_SUMMARY:
      return Object.assign({}, state, { isFetchingSummary: action.value });

    case IS_FETCHING_BATCHES:
      return Object.assign({}, state, { isFetchingBatches: action.value });

    case IS_STARTING_METRICS:
      return Object.assign({}, state, { isStartingMetrics: action.value });

    default:
      return state;
  }
}

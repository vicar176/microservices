import React, { Component } from 'react';
import { connect } from 'react-redux';
import { isFormValid } from 'actions/common';
// Components
import MainContent from 'components/Common/MainContent';
import IngestionMetricsFields from 'IngestionMetricsFields';
import UtilsTab from 'UtilsTab';
import IngestionStatus from './IngestionStatus/index';
import MetricsStatus from './MetricsStatus/index';

import {
  setEnvironment,
  setDocumentToProcess,
  setBatchSize,
  setWorkFlowThreads,
  setDocumentProcessorCron,
  setStopBatchRun,
  setCouchbaseIops,
  setCores,
  setNodes,
  setCreateSnippets,
  setPdfTagging,
  isSavingConfiguration,
  processIngestion,
  cleanActivities,
  fetchStatus,
  fetchBatches,
  fetchSummary,
  documentProcessorStatus,
  processMetrics,
  setWorkerCPUMedian,
  setCouchbaseCPUMax,
  setAuroraCPUMax,
  setTomcatRAMPercent,
  setLevel,
  processLevel
} from 'actions/ingestionConfiguration';

class IngestionConfiguration extends Component {
  constructor(props) {
    super(props);
    this.state = {
      validationTotalDocumentsLegend: "",
      validationBatchSizeLegend: "",
      validationWorkFlowThreadsLegend: "",
      validationProcessorJobLegend: "",
      validationNodesLegend: "",
      validationCoresLegend: "",
      validationIOPSLegend: "",
      subTab: "batchExecutions",
      validationWorkerCPUMedian: "",
      validationCouchbaseCPUMax: "",
      validationAuroraCPUMax: "",
      validationTomcatRAMPercent: ""
    };
  }

  componentDidMount() {
    this.props.dispatch(fetchStatus());
    this.props.dispatch(fetchSummary());
    this.props.dispatch(fetchBatches());
    this.props.dispatch(documentProcessorStatus());
  }

  onSaveIngestionConfiguration = () => {
    const { dispatch } = this.props;

    this.getValidationDocumentProcessor();
    this.getValidationBatchSize();
    this.getValidationThreadCounts();
    this.getValidationProcessorJob();
    this.getValidationNodes();
    this.getValidationCores();
    this.getValidationIOPS();

    const isFormValidated = !this.state.validationTotalDocumentsLegend.length &&
                            !this.state.validationBatchSizeLegend.length &&
                            !this.state.validationWorkFlowThreadsLegend.length &&
                            !this.state.validationProcessorJobLegend.length &&
                            !this.state.validationNodesLegend.length &&
                            !this.state.validationCoresLegend.length &&
                            !this.state.validationIOPSLegend.length;

    dispatch(isFormValid(isFormValidated));
    if (isFormValidated) {
      this.props.dispatch(processIngestion());
    }
  }

  onSaveIngestionMetrics = () => {
    const { dispatch } = this.props;

    this.getValidationWorkerCPU();
    this.getValidationCouchbaseCPUMax();
    this.getValidationAuroraCPUMax();
    this.getValidationTomcatRAMPercent();

    const isFormValidated = !this.state.validationWorkerCPUMedian.length &&
                            !this.state.validationCouchbaseCPUMax.length &&
                            !this.state.validationAuroraCPUMax.length &&
                            !this.state.validationTomcatRAMPercent.length;

    dispatch(isFormValid(isFormValidated));
    if (isFormValidated) {
      this.props.dispatch(processMetrics());
    }
  }

  getValidationDocumentProcessor = () => {
    const value = document.getElementById('documentsProcess').value;
    this.state.validationTotalDocumentsLegend = '';
    if ( !value.length ) {
      this.state.validationTotalDocumentsLegend = "Documents to Process is required";
    }
  }

  getValidationBatchSize = () => {
    this.state.validationBatchSizeLegend = "";
    const value = document.getElementById('batchSize').value;
    if ( !value.length ) {
      this.state.validationBatchSizeLegend = "Batch Size is required";
    }
  }

  getValidationThreadCounts = () => {
    this.state.validationWorkFlowThreadsLegend = "";
    const value = document.getElementById('workFlowThreads').value;
    if ( !value.length ) {
      this.state.validationWorkFlowThreadsLegend = "Thread Counts is required";
    }
  }

  getValidationProcessorJob = () => {
    this.state.validationProcessorJobLegend = "";
    const value = document.getElementById('documentProcessorJob').value;
    if ( !value.length ) {
      this.state.validationProcessorJobLegend = "Document Processor Job is required";
    } else {
      if (value > 3500) {
        this.state.validationProcessorJobLegend = "The number can not be greater than 3500"
      }
      if (value < 1) {
        this.state.validationProcessorJobLegend = "The number can not be lower than 1"
      }
    }
  }

  getValidationNodes = () => {
    this.state.validationNodesLegend = "";
    const value = document.getElementById('nodes').value;
    if ( !value.length ) {
      this.state.validationNodesLegend = "Nodes are required";
    }
  }

  getValidationCores = () => {
    this.state.validationCoresLegend = "";
    const value = document.getElementById('cores').value;
    if ( !value.length ) {
      this.state.validationCoresLegend = "Cores are required";
    }
  }

  getValidationIOPS = () => {
    this.state.validationIOPSLegend = "";
    const value = document.getElementById('iops').value;
    if ( !value.length ) {
      this.state.validationIOPSLegend = "Couchbase IOPS is required";
    }
  }

  getValidationWorkerCPU = () => {
    this.state.validationWorkerCPUMedian = "";
    const { workerCPUMedian } = this.props.ingestionConfiguration;
    if ( !workerCPUMedian.length ) {
      this.state.validationWorkerCPUMedian = "Worker CPU Median is required";
    }
  }

  getValidationCouchbaseCPUMax = () => {
    this.state.validationCouchbaseCPUMax = "";
    const { couchbaseCPUMax } = this.props.ingestionConfiguration;
    if ( !couchbaseCPUMax.length ) {
      this.state.validationCouchbaseCPUMax = "Couchbase CPU Max is required";
    }
  }

  getValidationAuroraCPUMax = () => {
    this.state.validationAuroraCPUMax = "";
    const { auroraCPUMax } = this.props.ingestionConfiguration;
    if ( !auroraCPUMax.length ) {
      this.state.validationAuroraCPUMax = "Aurora CPU Max is required";
    }
  }

  getValidationTomcatRAMPercent = () => {
    this.state.validationTomcatRAMPercent = "";
    const { tomcatRAMPercent } = this.props.ingestionConfiguration;
    if ( !tomcatRAMPercent.length ) {
      this.state.validationTomcatRAMPercent = "Tomcat RAM Percent is required";
    }
  }

  onSubTabChange = (tab) =>{
    this.setState({subTab: tab});
  }

  onClickCallback = val => {
    this.setState({
      validationTotalDocumentsLegend: "",
      validationBatchSizeLegend: "",
      validationWorkFlowThreadsLegend: "",
      validationProcessorJobLegend: "",
      validationNodesLegend: "",
      validationCoresLegend: "",
      validationIOPSLegend: ""
    });
    if (val === 'utils') {
      this.props.dispatch(cleanActivities(false));
      this.props.dispatch(documentProcessorStatus(false));
    }
  }

  onSwitchChange = e => {
    const val = e.target.checked;
    switch (e.target.id) {
      case 'activities':
        this.props.dispatch(cleanActivities(true, val));
        break;
      case 'documents':
        this.props.dispatch(documentProcessorStatus(true, !val));
        break;
      default:
    }
  }

  onLevelChange = e => {
    const { dispatch } = this.props;
    dispatch(setLevel(e.target.value));
    dispatch(processLevel());
  }

  render() {
    const { selectedTab } = this.props.common;
    const {
      configuration,
      stopBatchRun,
      status,
      batches,
      summary,
      isFetchingStatus,
      isFetchingBatches,
      isFetchingSummary,
      documentProcesorStatus,
      activitiesStatus,
      createSnippets,
      pdfTagging,
      isSavingConfiguration,
      isStartingMetrics
    } = this.props.ingestionConfiguration;

    return (
      <MainContent
        routes={this.props.routes}
        mainTitle='Ingestion Test'
        onClickCallback={this.onClickCallback}
        tabs={[{id: 'configuration', name: 'Configuration'},{id: 'status', name: 'Status'},{id: 'metrics', name: 'Metrics'}, {id: 'utils', name: 'Utils'}]}>
            { (selectedTab === 'configuration') &&
              <div role="tabpanel" className="tab-pane active" id="IngestionConfiguration">
                <div className="wrap-forms">
                <form onSubmit={ e => e.preventDefault() } style={{position: 'relative'}}>
                  <div className="tab-header">
                    <h2 className="page-header">Define Ingestion Configuration</h2>
                  </div>
                  <IngestionMetricsFields
                    configuration={configuration}
                    onChangeEnvironment={(value) => this.props.dispatch(setEnvironment(value))}
                    onChangeDocumentToProcess={(value) => this.props.dispatch(setDocumentToProcess(value))}
                    onChangeThreadCounts={(value) => this.props.dispatch(setWorkFlowThreads(value))}
                    onChangeWorkFlowThreads={(value) => this.props.dispatch(setWorkFlowThreads(value))}
                    onChangeBatchSize={(value) => this.props.dispatch(setBatchSize(value))}
                    onChangeDocumentProcessorCron={(value) => this.props.dispatch(setDocumentProcessorCron(value))}
                    onChangeStopBatchRun={(value) => this.props.dispatch(setStopBatchRun(value))}
                    onChangeNodes={(value) => this.props.dispatch(setNodes(value))}
                    onChangeCores={(value) => this.props.dispatch(setCores(value))}
                    onChangeCouchbaseIops={(value) => this.props.dispatch(setCouchbaseIops(value))}
                    onChangeCreateSnippets={(value) => this.props.dispatch(setCreateSnippets(value))}
                    onChangePdfTagging={(value) => this.props.dispatch(setPdfTagging(value))}
                    isPdfTaggingActive={pdfTagging}
                    isCreateSnippetsActive={createSnippets}
                    stopBatchRun={stopBatchRun}
                    validationTotalDocumentsLegend={this.state.validationTotalDocumentsLegend}
                    validationBatchSizeLegend={this.state.validationBatchSizeLegend}
                    validationWorkFlowThreadsLegend={this.state.validationWorkFlowThreadsLegend}
                    validationProcessorJobLegend={this.state.validationProcessorJobLegend}
                    validationNodesLegend={this.state.validationNodesLegend}
                    validationCoresLegend={this.state.validationCoresLegend}
                    validationIOPSLegend={this.state.validationIOPSLegend}/>
                 <hr/>
                  <div className="row">
                    <div className="col-xs-12 text-center">
                      <button
                        name="save"
                        className="btn btn-primary"
                        onClick={() => this.onSaveIngestionConfiguration()}
                        disabled={isSavingConfiguration}>
                        { isSavingConfiguration ? "Processing" : "Start" }
                      </button>
                    </div>
                  </div>
                </form>
              </div>
            </div>
            }
            { (selectedTab === 'status') &&
              <div role="tabpanel" className="tab-pane active" id="IngestionStatus">
                <div className="wrap-forms form-horizontal">
                  <div className="tab-header">
                    <h2 className="page-header">Ingestion Status</h2>
                  </div>
                  <IngestionStatus
                    status={status}
                    summary={summary}
                    batches={batches}
                    isFetchingStatus={isFetchingStatus}
                    isFetchingBatches={isFetchingBatches}
                    isFetchingSummary={isFetchingSummary}
                    getStatus={() => this.props.dispatch(fetchStatus())}
                    getSummary={() => this.props.dispatch(fetchSummary())}
                    getBatches={() => this.props.dispatch(fetchBatches())}
                    subTab={this.state.subTab}
                    onSubTabChange={(tab) => this.onSubTabChange(tab)}/>
                </div>
              </div>
            }
            { (selectedTab === 'metrics') &&
              <div role="tabpanel" className="tab-pane active" id="IngestionConfiguration">
                <div className="wrap-forms">
                  <form onSubmit={ e => e.preventDefault() } style={{position: 'relative'}}>
                    <div className="tab-header">
                      <h2 className="page-header">Ingestion Metrics</h2>
                    </div>
                    <MetricsStatus
                      onChangeWorkerCPU={(value) => this.props.dispatch(setWorkerCPUMedian(value))}
                      onChangeCouchbaseCPUMax={(value) => this.props.dispatch(setCouchbaseCPUMax(value))}
                      onChangeAuroraCPUMax={(value) => this.props.dispatch(setAuroraCPUMax(value))}
                      onChangeTomcatRAMPercent={(value) => this.props.dispatch(setTomcatRAMPercent(value))}
                      validationWorkerCPUMedian={this.state.validationWorkerCPUMedian}
                      validationCouchbaseCPUMax={this.state.validationCouchbaseCPUMax}
                      validationAuroraCPUMax={this.state.validationAuroraCPUMax}
                      validationTomcatRAMPercent={this.state.validationTomcatRAMPercent}/>
                   <hr/>
                    <div className="row">
                      <div className="col-xs-12 text-center">
                        <button
                          name="save"
                          className="btn btn-primary"
                          onClick={() => this.onSaveIngestionMetrics()}
                          disabled={isStartingMetrics}>
                          { isStartingMetrics ? "Processing" : "Start" }
                        </button>
                      </div>
                    </div>
                  </form>
                </div>
              </div>
            }
            { (selectedTab === 'utils') &&
              <div role="tabpanel" className="tab-pane active" id="IngestionConfiguration">
                <div className="wrap-forms">
                  <div className="tab-header">
                    <h2 className="page-header">Ingestion Utils</h2>
                  </div>
                  <UtilsTab
                    documentProcesorStatus={documentProcesorStatus}
                    activitiesStatus={activitiesStatus}
                    onLevelChange={this.onLevelChange}
                    onSwitchChange={this.onSwitchChange} />
                </div>
              </div>
            }
      </MainContent>
    );
  }
}

function mapStateToProps(state) {
  return {
    common: state.common,
    ingestionConfiguration: state.ingestionConfiguration,
    auth: state.auth
  };
}

export default connect(mapStateToProps)(IngestionConfiguration);

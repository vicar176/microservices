import React from 'react';

// Components
import { Tooltip, OverlayTrigger} from 'react-bootstrap';
import ValidationTooltip from 'components/Common/ValidationTooltip';
import FormBoolean from 'components/Common/FormBoolean';

export default function IngestionMetricsFields (props) {
  const {
    onChangeEnvironment,
    onChangeDocumentToProcess,
    onChangeThreadCounts,
    onChangeWorkFlowThreads,
    onChangeDocumentProcessorCron,
    onChangeStopBatchRun,
    onChangeBatchSize,
    stopBatchRun,
    onChangeCouchbaseIops,
    onChangeCores,
    onChangeNodes,
    onChangeCreateSnippets,
    onChangePdfTagging,
    isCreateSnippetsActive,
    isPdfTaggingActive,
    // validation
    validationTotalDocumentsLegend,
    validationBatchSizeLegend,
    validationWorkFlowThreadsLegend,
    validationProcessorJobLegend,
    validationNodesLegend,
    validationCoresLegend,
    validationIOPSLegend
  } = props;

  return (
    <div className="form-group">
      <div className="row">
        <div className="col-xs-2">
          <label className="control-label">Documents to Process<span className="text-error">*</span>:</label>
        </div>
        <div className="col-xs-5">
          <ValidationTooltip message={validationTotalDocumentsLegend}>
             <input
                type="number"
                name="totalDocuments"
                className="form-control"
                id="documentsProcess"
                onChange={(e) => onChangeDocumentToProcess(e.target.value)}/>
            </ValidationTooltip>
        </div>
      </div>
      <hr/>
      <div className="row">
        <div className="col-xs-2">
          <label className="control-label">Batch Size<span className="text-error">*</span>:</label>
        </div>
        <div className="col-xs-5">
          <ValidationTooltip message={validationBatchSizeLegend}>
             <input
                type="number"
                name="batchSize"
                id="batchSize"
                className="form-control"
                onChange={(e) => onChangeBatchSize(e.target.value)}/>
            </ValidationTooltip>
        </div>
      </div>
      <hr/>
      <div className="row">
        <div className="col-xs-2">
          <label className="control-label">Thread Counts<span className="text-error">*</span>:</label>
        </div>
        <div className="col-xs-5">
          <ValidationTooltip message={validationWorkFlowThreadsLegend}>
             <input
                type="number"
                name="workFlowThreads"
                id="workFlowThreads"
                className="form-control"
                onChange={(e) => onChangeWorkFlowThreads(e.target.value)}/>
            </ValidationTooltip>
        </div>
      </div>
      <hr/>
      <div className="row">
        <div className="col-xs-2">
          <label className="control-label">Create Snippets<span className="text-error">*</span>:</label>
        </div>
        <div className="col-xs-5">
          <FormBoolean
            isActive={isCreateSnippetsActive}
            onChange={onChangeCreateSnippets}/>
        </div>
      </div>
      <hr/>
      <div className="row">
        <div className="col-xs-2">
          <label className="control-label">Pdf Tagging<span className="text-error">*</span>:</label>
        </div>
        <div className="col-xs-5">
          <FormBoolean
            isActive={isPdfTaggingActive}
            onChange={onChangePdfTagging}/>
        </div>
      </div>
      <hr/>
      <div className="row">
        <div className="col-xs-2">
          <label className="control-label">Document Processor Job in seconds<span className="text-error">*</span>:</label>
        </div>
        <div className="col-xs-5">
          <ValidationTooltip message={validationProcessorJobLegend}>
            <input
              type="text"
              name="documentProcessorJob"
              id="documentProcessorJob"
              className="form-control"
              onChange={(e) => onChangeDocumentProcessorCron(e.target.value)}/>
          </ValidationTooltip>
        </div>
      </div>
      <hr/>
      <div className="row">
        <div className="col-xs-2">
          <label className="control-label">Nodes<span className="text-error">*</span>:</label>
        </div>
        <div className="col-xs-5">
          <ValidationTooltip message={validationNodesLegend}>
            <input
              type="number"
              name="nodes"
              id="nodes"
              className="form-control"
              onChange={(e) => onChangeNodes(e.target.value)}/>
            </ValidationTooltip>
        </div>
      </div>
      <hr/>
      <div className="row">
        <div className="col-xs-2">
          <label className="control-label">Cores<span className="text-error">*</span>:</label>
        </div>
        <div className="col-xs-5">
          <ValidationTooltip message={validationCoresLegend}>
            <input
              type="number"
              name="cores"
              id="cores"
              className="form-control"
              onChange={(e) => onChangeCores(e.target.value)}/>
          </ValidationTooltip>
        </div>
      </div>
      <hr/>
      <div className="row">
        <div className="col-xs-2">
          <label className="control-label">Couchbase IOPS<span className="text-error">*</span>:</label>
        </div>
        <div className="col-xs-5">
          <ValidationTooltip message={validationIOPSLegend}>
            <input
              type="number"
              name="couchbaseIops"
              id="iops"
              className="form-control"
              onChange={(e) => onChangeCouchbaseIops(e.target.value)}/>
          </ValidationTooltip>
        </div>
      </div>
    </div>
  );
}

import React from 'react';

// Components
import { Tooltip, OverlayTrigger} from 'react-bootstrap';
import ValidationTooltip from 'components/Common/ValidationTooltip';
import FormBoolean from 'components/Common/FormBoolean';

export default function IngestionMetricsFields (props) {
  const {
    onChangeWorkerCPU,
    onChangeCouchbaseCPUMax,
    onChangeAuroraCPUMax,
    onChangeTomcatRAMPercent,
    validationWorkerCPUMedian,
    validationCouchbaseCPUMax,
    validationAuroraCPUMax,
    validationTomcatRAMPercent
  } = props;

  return (
    <div className="form-group">
      <div className="row">
        <div className="col-xs-2">
          <label className="control-label">Worker CPU (Median)}<span className="text-error">*</span>:</label>
        </div>
        <div className="col-xs-5">
          <ValidationTooltip message={validationWorkerCPUMedian}>
             <input
                type="number"
                name="workerCPU"
                className="form-control"
                id="workerCPU"
                onChange={(e) => onChangeWorkerCPU(e.target.value)}/>
            </ValidationTooltip>
        </div>
      </div>
      <hr/>
      <div className="row">
        <div className="col-xs-2">
          <label className="control-label">Couchbase CPU Max<span className="text-error">*</span>:</label>
        </div>
        <div className="col-xs-5">
          <ValidationTooltip message={validationCouchbaseCPUMax}>
             <input
                type="number"
                name="couchbaseCPUMax"
                className="form-control"
                id="couchbaseCPUMax"
                onChange={(e) => onChangeCouchbaseCPUMax(e.target.value)}/>
            </ValidationTooltip>
        </div>
      </div>
      <hr/>
      <div className="row">
        <div className="col-xs-2">
          <label className="control-label">Aurora CPU Max<span className="text-error">*</span>:</label>
        </div>
        <div className="col-xs-5">
          <ValidationTooltip message={validationAuroraCPUMax}>
             <input
                type="number"
                name="auroraCPUMax"
                className="form-control"
                id="auroraCPUMax"
                onChange={(e) => onChangeAuroraCPUMax(e.target.value)}/>
            </ValidationTooltip>
        </div>
      </div>
      <hr/>
      <div className="row">
        <div className="col-xs-2">
          <label className="control-label">Tomcat RAM Percent<span className="text-error">*</span>:</label>
        </div>
        <div className="col-xs-5">
          <ValidationTooltip message={validationTomcatRAMPercent}>
             <input
                type="number"
                name="tomcatRAMPercent"
                className="form-control"
                id="tomcatRAMPercent"
                onChange={(e) => onChangeTomcatRAMPercent(e.target.value)}/>
            </ValidationTooltip>
        </div>
      </div>
    </div>
  );
}

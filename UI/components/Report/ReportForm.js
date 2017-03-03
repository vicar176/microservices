import React from 'react';

export default function ReportForm (props) {
  const { reportProfile, onNameChange, onDescriptionChange, onUrlChange, onPreviewReport, onCategoryChange, onSubCategoryChange, onManageCategoryList, onManageSubCategoryList } = props;
  return(
    <div className="form-group">
      <div className="row sec-divider">
        <div className="col-md-6">
          <label className="control-label">Name:</label>
          <input className="form-control" value={reportProfile.reportName} onChange={e => onNameChange(e.target.value.trim())} />
        </div>
        <div className="col-md-6">
          <label className="control-label">Description:</label>
          <textarea className="form-control" value={reportProfile.reportDescription} onChange={e => onDescriptionChange(e.target.value)} />
        </div>
      </div>
      <div className="row sec-divider">
        <div className="col-md-10">
          <label className="control-label">URL:</label>
          <input className="form-control" value={reportProfile.reportUrl} onChange={e => onUrlChange(e.target.value.trim())} />
        </div>
        <div className="col-md-2" style={{top: "1.7em"}}>
          <button className="btn btn-block btn-primary" onClick={ () => onPreviewReport() }>Preview</button>
        </div>
      </div>
      <div className="row sec-divider">
        <div className="col-md-5">
          <label className="control-label">Category:</label>
          <select className="form-control" onChange={ e=> onCategoryChange(e.target.value) } value={reportProfile.reportCategory}>
            <option value="0">Account</option>
            <option value="1">Document</option>
            <option value="2">Other</option>
            <option value="3">Portfolio</option>
            <option value="4">Process</option>
            <option value="5">Profile</option>
          </select>
        </div>
        <div className="col-md-1" style={{top: "1.7em"}}>
          <a
             href="#"
             title='Management List'
             style={{fontSize: '30px'}}
             onClick={e => {
               e.preventDefault();
               onManageCategoryList();
             }}
             className="glyphicon glyphicon-edit"></a>
        </div>
        <div className="col-md-5">
          <label className="control-label">Sub Category:</label>
          <select className="form-control" onChange={ e=> onSubCategoryChange(e.target.value) } value={reportProfile.reportSubCategory}>
            <option value="0">OALD</option>
            <option value="1">OTHER</option>
            <option value="2">VALIDATION</option>
            <option value="3">METADATA</option>
            <option value="4">VERIFICATION</option>
          </select>
        </div>
        <div className="col-md-1" style={{top: "1.7em"}}>
          <a
             href="#"
             title='Management List'
             style={{fontSize: '30px'}}
             onClick={e => {
               e.preventDefault();
               onManageSubCategoryList();
             }}
             className="glyphicon glyphicon-edit"></a>
        </div>
      </div>
    </div>
  );
}

import React from 'react';

export default function ControlsDocumentUpload () {
  return(
  <div className="form-group sec-divider">
    <div className="row">
      <div className="col-xs-12 col-sm-4">
        <label className="control-label">Search with:</label>
        <ul className="list-inline" style={{'marginTop': '.7em', 'marginBottom': '0'}}>
          <li>
            <label htmlFor="mcm-account" className="radio-inline">
            <input
              id="mcm-account"
              type="radio"
              value="mcm-account"
              name="account-type" />
            MCM Account</label>
          </li>
          <li>
            <label htmlFor="original-account" className="radio-inline">
            <input
              id="original-account"
              type="radio"
              value="original-account"
              name="account-type" />
            Original Account</label>
          </li>
        </ul>
      </div>
      <div className="col-xs-12 col-sm-8">
        <label className="control-label">Upload Spreadsheet:</label>
        <input
          type="file"
          className="form-control"
          name="file" />
      </div>
    </div>
  </div>
  );
}

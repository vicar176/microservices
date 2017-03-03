import React from 'react';

export default function ControlsPortfolioSearch () {
  return(
    <div className="form-group sec-divider">
      <div className="row">
        <div className="col-xs-10">
          <label className="control-label">Enter Portfolio Number:</label>
          <input
            id="list"
            type="text"
            className="form-control"
            name="portfolio-number" />
        </div>
        <div className="col-xs-2">
          <button className="btn btn-block btn-primary" style={{top: "1.7em", position: 'relative'}}>Search</button>
        </div>
      </div>
    </div>
  );
}

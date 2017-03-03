import React from 'react';

export default function NavigationMethodType ({ selectedReviewMethod, onSelectRewiewMethod }) {
  return(
    <div className="form-group account-navigation">
      <div className="row">
        <div className="col-xs-12 col-sm-6 col-sm-offset-3">
          <ul className="btn-group btn-group-justified" role="group">
            <li className="btn-group" role="group">
              <button
                id="list"
                type="button"
                value="list"
                className={selectedReviewMethod === "list" ? "active btn btn-default" : "btn btn-default" }
                onClick={ e => onSelectRewiewMethod(e.target.value) }
                name="review-method">From Exception List</button>
            </li>
            <li className="btn-group" role="group">
              <button
                id="portfolio"
                type="button"
                value="portfolio"
                className={selectedReviewMethod === "portfolio" ? "active btn btn-default" : "btn btn-default" }
                onClick={ e => onSelectRewiewMethod(e.target.value) }
                name="review-method">By Portfolio</button>
            </li>
            <li className="btn-group" role="group">
              <button
                id="upload"
                type="button"
                value="upload"
                className={selectedReviewMethod === "upload" ? "active btn btn-default" : "btn btn-default" }
                onClick={ e => onSelectRewiewMethod(e.target.value) }
                name="review-method">Upload Spreadsheet</button>
            </li>
          </ul>
        </div>
      </div>
    </div>
  );
}

import React from 'react';

export default function SelectDocumentReview ({ onReviewMethod, selectedReviewMethod }) {
  return(
    <div className="form-group account-navigation">
      <div className="row">
        <div className="col-xs-12 col-sm-8 col-sm-offset-2">
          <ul className="btn-group btn-group-justified" role="group">
            <li className="btn-group" role="group">
              <button
                id="exceptionList"
                type="button"
                value="exceptionList"
                className={"btn btn-default " + (selectedReviewMethod === "exceptionList" ? "active" : "")}
                onClick={ e => onReviewMethod(e.target.value) }
                name="review-method">From Exception List</button>
            </li>
              <li className="btn-group" role="group">
                <button
                  id="byPortfolio"
                  type="button"
                  value="byPortfolio"
                  className={"btn btn-default " + (selectedReviewMethod === "byPortfolio" ? "active" : "")}
                  onClick={ e => onReviewMethod(e.target.value) }
                  name="review-method">By Portfolio</button>
              </li>
            <li className="btn-group" role="group">
              <button
                id="manualList"
                type="button"
                value="manualList"
                className={"btn btn-default " + (selectedReviewMethod === "manualList" ? "active" : "")}
                onClick={ e => onReviewMethod(e.target.value) }
                name="review-method">Manual List</button>
            </li>
          </ul>
        </div>
      </div>
    </div>
  );
}

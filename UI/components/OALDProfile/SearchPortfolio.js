import React from 'react';
import CssLoad from 'components/Common/cssLoad';

export default function SearchPortfolio ({ onPortfolioSearch, isFetching, selectedPortfolio, onPortfolioChange, isDisabled }) {
  return (
    <div className="col-xs-12 col-sm-6">
      <div className="row">
        <div className="col-xs-9">
          <label className="control-label">Enter Portfolio:</label>
          {
            isFetching ?
            <CssLoad /> :
            <input
              disabled={isDisabled}
              className="form-control"
              value={selectedPortfolio ? selectedPortfolio : ''}
              onChange={ e => onPortfolioChange(e.target.value.trim(), selectedPortfolio)}
            />
          }
        </div>
        <div className="col-xs-2" style={{top: "1.7em"}}>
          <button disabled={isFetching || isDisabled} className="btn btn-block btn-primary" name="submit" onClick={ () => onPortfolioSearch(selectedPortfolio) }>Submit</button>
        </div>
      </div>
    </div>
  );
}

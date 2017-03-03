import React, { Component } from 'react';

class SearchPortfolio extends Component {

  render() {
    const { onPortfolioSearch } = this.props;

    return (
      <div className="row">
        <div className="col col-1-4 form-label">
          <label>Search Portfolio:</label>
        </div>
        <div className="col col-3-4 form-input form-inline-group">
          <input type="number" defaultValue={''} onKeyDown={onPortfolioSearch} name="portfolio"/>
          <button className="btn" onClick={onPortfolioSearch}>Submit</button>
        </div>
      </div>
    );
  }

}

export default SearchPortfolio;
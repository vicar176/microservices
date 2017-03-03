import React, { Component } from 'react';
import SearchPortfolio from './SearchPortfolio';
import OrderingPeriod from './OrderingPeriod';
import AssociatedDocuments from './AssociatedDocuments';

class PortfolioProfile extends Component {

  constructor() {
    super();
    this.state = {
      portfolioId: ''
    };
  }

  onPortfolioSearch = (e) => {
    if (e.which && e.which !== 13) {
      return;
    }

    this.setState({
      portfolioId: '12345',
      orderingPeriod: '30',
      sources: [
        {
          "id": 1,
          "profileName": "Argent_FTP"
        },
        {
          "id": 2,
          "profileName": "Argent_HTTP"
        }
      ]
    });
  };

  render() {
    const { portfolioId, orderingPeriod, sources } = this.state;

    return (
      <div className="PortfolioProfile">
        <h1>Portfolio Profile</h1>
        <form onSubmit={ e => e.preventDefault() }>
          <SearchPortfolio onPortfolioSearch={this.onPortfolioSearch} />
          { portfolioId && <OrderingPeriod period={orderingPeriod} /> }
          { portfolioId && <AssociatedDocuments sources={sources} /> }
          { portfolioId && this.renderButtons() }
        </form>
      </div>
    );
  }

  renderButtons() {
    return (
      <div className="row">
        <div className="col col-1-2 center form-inline-group">
          <button className="btn">Save</button>
          <button className="btn">Cancel</button>
          <button className="btn">Delete</button>
        </div>
      </div>
    );
  }

}

export default PortfolioProfile;

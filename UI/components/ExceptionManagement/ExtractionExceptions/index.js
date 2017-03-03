import React, { Component } from 'react';
import { connect } from 'react-redux';

// Components
import SelectPortfolio from 'SelectPortfolio';
import PortfoliosSummary from 'PortfoliosSummary';

// Actions
import {
  cleanPortfoliosId,
  cleanPortfolioDetail,
  selectPortfoliosId,
  fetchPortfoliosId,
  fetchPortfolioSumary,
  // fetchPortfolioDetail,
  setPortfolioActiveTab,
  isPortfolioSelected
} from 'actions/extractionExceptions';

import { cleanProfileGrid } from 'actions/viewProfiles';
import { cleanProfileData } from 'actions/common';

class ExtractionExceptions extends Component {

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch(cleanPortfoliosId());
    dispatch(fetchPortfoliosId());
    dispatch(cleanProfileData());
  }

  onSelectPortfolioId = (portfolios) => {
    this.props.dispatch(selectPortfoliosId(portfolios));
  }

  onSearchPortfolios = e => {
    const { dispatch } = this.props;
    let { selectedPortfoliosId } = this.props.extractionExceptions;

    selectedPortfoliosId = (selectedPortfoliosId === null)? [] : selectedPortfoliosId;
    dispatch(setPortfolioActiveTab(null));
    dispatch(isPortfolioSelected(false));

    if (selectedPortfoliosId.length) {
      const portfoliosId = selectedPortfoliosId.map( portfolio => portfolio.id);
      dispatch(isPortfolioSelected(true));
      dispatch(fetchPortfolioSumary(portfoliosId));
    }

    e.preventDefault();
  }

  onClickPortFolioRow = portfolioId => {

    const { dispatch, extractionExceptions } = this.props;
    dispatch(cleanPortfolioDetail());
    dispatch(cleanProfileGrid());
    if (extractionExceptions.activePortfolioTabId !== portfolioId) {
      dispatch(setPortfolioActiveTab(portfolioId));
    } else {
      dispatch(setPortfolioActiveTab(null));
    }
  }

  render() {
    const {
      receivedPortfoliosId,
      selectedPortfoliosId,
      activePortfolioTabId,
      portfolioSummaryList,
      isPortfolioSelected,
      isFetchingPortfoliosId,
      isFetchingPortfolioSummary,
    } = this.props.extractionExceptions;

    return (
      <div>
        <SelectPortfolio
          receivedPortfoliosId={receivedPortfoliosId}
          selectedPortfoliosId={selectedPortfoliosId}
          onSearchPortfolios={this.onSearchPortfolios}
          onSelectPortfolioId={this.onSelectPortfolioId}
          isFetchingPortfoliosId={isFetchingPortfoliosId}
          isDisabled={isFetchingPortfolioSummary} />

        { isPortfolioSelected &&
          <PortfoliosSummary
            portfoliosList={portfolioSummaryList}
            activePortfolioTabId={activePortfolioTabId}
            onClickPortFolioRow={this.onClickPortFolioRow}
            isFetchingPortfolioSummary={isFetchingPortfolioSummary} /> }
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    common: state.common,
    viewProfiles: state.viewProfiles,
    extractionExceptions: state.extractionExceptions
  };
}

export default connect(mapStateToProps)(ExtractionExceptions);

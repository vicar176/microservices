import React from 'react';
import CssLoad from 'components/Common/cssLoad';
import PortfolioRow from 'PortfolioRow';

// styles
import styles from 'index.scss';

export default function PortfoliosSummary ({ portfoliosList, activePortfolioTabId, onClickPortFolioRow }) {
  return(
    <div className={styles.accordionTabs+" panel-group"} id="portfolios-accordion" role="tablist" aria-multiselectable="true">
      <hr />
      <div className={styles.accordionHeading+" panel panel-default"}>
        <div className="panel-heading clearfix">
          <div className="col-sm-2">Portfolio #</div>
          <div className="col-sm-7">Original Seller</div>
          <div className="col-sm-2 text-center">Documents failed</div>
        </div>
      </div>
      { !portfoliosList.length ? <CssLoad /> :
        portfoliosList.map((portfolio, i) => {
          return(
            <PortfolioRow
              key={i}
              portfolio={portfolio}
              onClickPortFolioRow={onClickPortFolioRow}
              activePortfolioTabId={activePortfolioTabId} />
          )
        })
      }
    </div>
  );
}

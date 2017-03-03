import React from 'react';
import PortfolioDetail from 'PortfolioDetail';
import CssLoad from 'components/Common/cssLoad';
// styles
import styles from 'index.scss';

export default function PortfolioRow ({ portfolio, activePortfolioTabId, onClickPortFolioRow }) {
  const { portfolioNumber, documentsFailed, originalSeller, templateFoundCount, templateNotFoundCount } = portfolio;
  const isPortfolioActive = activePortfolioTabId === portfolioNumber;
  return(
    <div key={portfolioNumber} className={"panel panel-"+(activePortfolioTabId === portfolioNumber ? 'primary' : 'default')}>
      <div role="tab"
           data-toggle="collapse"
           data-parent="#portfolios-accordion"
           data-target={"#portfolio-"+portfolioNumber}
           id={"portfolio-heading-"+portfolioNumber}
           className={styles.accordionTab+" panel-heading collapsed clearfix"}
           onClick={() => onClickPortFolioRow(portfolioNumber)}>

        <div className="col-sm-2">{portfolioNumber}</div>
        <div className="col-sm-7">{originalSeller}</div>
        <div className="col-sm-2 text-center">{documentsFailed}</div>
        <div className="col-sm-1 text-right">
          <span className={"glyphicon glyphicon-triangle-" + (isPortfolioActive ? 'top' : 'bottom')}></span>
        </div>
      </div>
      { isPortfolioActive &&
        <div id={"portfolio-"+portfolioNumber} className="panel-collapse collapse" role="tabpanel" aria-labelledby={"portfolio-"+portfolioNumber}>
          <div className={styles.accordionDetailContent + " panel-body"}>
            <PortfolioDetail templateData={{ portfolioNumber, templateNotFoundCount, templateFoundCount }} />
          </div>
        </div>
      }
    </div>
  );
}

import React from 'react';
import Select from 'react-select';
import CssLoad from 'components/Common/cssLoad';
import styles from './index.scss';

export default function SelectPortfolio (props) {
  const {
    receivedPortfoliosId,
    selectedPortfoliosId,
    isFetchingPortfoliosId,
    onSelectPortfolioId,
    onSearchPortfolios,
    isDisabled
  } = props;

  return(
    <div className="form-group">
      <div className="row">
        <div className="col-xs-10">
          <label className="control-label">Portfolio to review:</label>
          {
            isFetchingPortfoliosId ? <CssLoad /> :
            <Select
              name="documentIds"
              valueKey="id"
              labelKey="id"
              placeholder="Portfolio Number"
              multi={true}
              options={receivedPortfoliosId}
              value={selectedPortfoliosId}
              onChange={onSelectPortfolioId}
              className={styles.selectMultiple}
            />
          }
        </div>
        <div className="col-xs-2" style={{top: "1.7em", position: 'relative'}}>
          <button className="btn btn-block btn-primary" disabled={isDisabled} onClick={onSearchPortfolios}>Search</button>
        </div>
      </div>
    </div>
  );
}

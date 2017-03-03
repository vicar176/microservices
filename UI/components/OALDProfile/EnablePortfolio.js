import React, { Component } from 'react';
import { Tooltip, OverlayTrigger} from 'react-bootstrap';

export default class EnablePortfolio extends Component {
  render() {
    const { isPortfolioEnabled, onPortfolioEnable, isDisabled } = this.props;

    const tooltip = <Tooltip id="tooltip-oald-profile">OALD associated for the portfolio/original will completely overwrite OALD associated at the product type level.</Tooltip>

    return (
      <div className={"form-boolean" + (isDisabled ? ' is-disabled' : '') + ' col-xs-12 col-sm-2'}>
        <label className="control-label">Enable:</label>
        <OverlayTrigger placement="right" overlay={tooltip}>
          <i className="glyphicon glyphicon-info-sign" style={ {'marginLeft': 5, 'color': 'gray'} } />
        </OverlayTrigger>

        <span className="btn-group btn-group-xs" style={{ margin: '8px 0 0 0', display: 'block' }}>
          <label className={"btn btn-default "+(isPortfolioEnabled ? 'active' : '')} htmlFor="enablePortfolio">
            <input
              value="yes"
              type="checkbox"
              name="enablePortfolio"
              id="enablePortfolio"
              checked={isPortfolioEnabled}
              disabled={isDisabled}
              onChange={() => onPortfolioEnable(true)} />
            Yes
          </label>
          <label className={"btn btn-default "+(!isPortfolioEnabled ? 'active' : '')} htmlFor="disablePortfolio">
            <input
              value="no"
              type="checkbox"
              name="disablePortfolio"
              id="disablePortfolio"
              checked={!isPortfolioEnabled}
              disabled={isDisabled}
              onChange={() => onPortfolioEnable(false)} />
            No
          </label>
        </span>
      </div>
    );
  }
}

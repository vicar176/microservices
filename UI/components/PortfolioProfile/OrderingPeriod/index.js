import React, { Component } from 'react';

class OrderingPeriod extends Component {

  render() {
    const { period } = this.props;

    return (
      <div className="OrderingPeriod">
        <div className="row">
          <div className="col col-1-4 form-label">
            <label>Automated Ordering Period:</label>
          </div>
          <div className="col col-3-4 form-input form-inline-group">
            <select name="orderingPeriod" defaultValue={period}>
              <option value="">15 days</option>
              <option value="">30 days</option>
              <option value="">45 days</option>
              <option value="">60 days</option>
              <option value="">90 days</option>
              <option value="">120 days</option>
            </select>
            <button className="btn">Run orders now</button>
          </div>
        </div>
      </div>
    );
  }

}

export default OrderingPeriod;
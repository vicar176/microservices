import React, { Component } from 'react';

class AssociatedDocuments extends Component {

  render() {
    const { sources } = this.props;

    return (
      <div className="AssociatedDocuments">
        <div className="row">
          <div className="col col-1-4 form-label">
            <label>Associate Document Location Profile:</label>
          </div>
          <div className="col col-3-4 form-input">
            <div className="row">
              <div className="col">
                <p className="select-title">Profiles Available:</p>
                <select multiple name="notAssociated">
                  <option value="Atlantic">Atlantic</option>
                  <option value="Bank Of America">Bank Of America</option>
                  <option value="CapitalOne">CapitalOne</option>
                  <option value="Convoke">Convoke</option>
                  <option value="Fifth Third bank">Fifth Third bank</option>
                </select>
              </div>
              <div className="col">
                <div className="move">
                  <button className="btn"> &lt;&lt; move</button>
                  <button className="btn">move &gt;&gt;</button>
                </div>
              </div>
              <div className="col">
                <p className="select-title">Profiles Associated:</p>
                <select multiple name="associated">
                  {sources.map((source, i) => {
                    return <option key={i} value={source.id}>{source.profileName}</option>
                  })}
                </select>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

}

export default AssociatedDocuments;
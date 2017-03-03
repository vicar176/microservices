import React, { Component } from 'react';
import { pluck, where } from 'lodash';

class SelectAffinities extends Component {

  onRemove() {
    const values = pluck(where(this.refs.associated.options, {selected: true}), 'value');
    this.props.onRemoveLenderAffinities(values);
  }

  onAdd() {
    const values = pluck(where(this.refs.notAssociated.options, {selected: true}), 'value');
    this.props.onAddLenderAffinities(values);
  }

  render() {
    const {
      availableAffinities,
      associated,
      isTemplateNameValid,
      hasAvailableAffinities,
      isDisabled
    } = this.props;

    return (
        <div className="col-xs-12 required">
          <label>Affinities  Available:</label>
          <div className="inline-group">
          {
            !hasAvailableAffinities ?
            <p className="info">There are no affinities available to associate.
              Select a existing template to edit.</p> :
            <div className="row mod-switcher">
              <div className="col-md-5 mod-switcher-col">
                <p className="select-title">NOT Associated:</p>
                <select multiple disabled={isDisabled} name="notAssociated" ref="notAssociated" className="form-control">
                {availableAffinities.sort().map((affinity, i) => {
                  return <option title={affinity} key={i} value={affinity}>{affinity}</option>
                })}
                </select>
              </div>
              <div className="col-md-2 mod-switcher-controls">
                <div className="move">
                  <button className="btn btn-primary" disabled={!isTemplateNameValid} name="remove" onClick={this.onRemove.bind(this)}>&lt;&lt;</button>
                  <button className="btn btn-primary" disabled={!isTemplateNameValid} name="add" onClick={this.onAdd.bind(this)}>&gt;&gt;</button>
                </div>
              </div>
              <div className="col-md-5 mod-switcher-col">
                <p className="select-title">Associated:</p>
                <select disabled={isDisabled} name="associated" multiple ref="associated" className="form-control">
                  {associated.sort().map((affinity, i) => {
                    return <option title={affinity} key={i} value={affinity}>{affinity}</option>
                  })}
                </select>
              </div>
            </div>
          }
          </div>
          </div>
      );
  }

}

export default SelectAffinities;

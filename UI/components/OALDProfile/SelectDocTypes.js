import React, { Component } from 'react';
import { sortBy, pluck, where } from 'lodash';

class SelectDocTypes extends Component {

  filterDocTypes(docTypes, associatedDocTypes) {
    const ids = pluck(associatedDocTypes, 'id');
    const filtered = docTypes.filter( doc => {
      return ids.indexOf(doc.id) === -1;
    });
    return sortBy(filtered, 'code');
  }

  docTypesByIds = (ids) => {
    return this.props.docTypes.filter( doc => {
      return ids.indexOf(doc.id) !== -1;
    });
  };

  onRemove = () => {
    const ids = pluck(where(this.refs.associated.options, {selected: true}), 'value');
    const idsInt = ids.map(id => Number(id));
    this.props.onRemoveDocTypes(this.docTypesByIds(idsInt));
  };

  onAdd = () => {
    const ids = pluck(where(this.refs.notAssociated.options, {selected: true}), 'value');
    const idsInt = ids.map(id => Number(id));
    this.props.onAddDocTypes(this.docTypesByIds(idsInt));
  };

  render() {
    const { docTypes, associatedDocTypes, isDisabled, groupName } = this.props;

    return (
      <div className="form-group" style={{marginTop: '3em'}}>
        <h2 className="page-header">PRODUCT GROUP: {groupName}</h2>
        <div className="row" style={{marginBottom: '1em'}}>
          <div className="col-xs-12">
            <label>Associate Document Types:</label>
          </div>
        </div>
        <div className="row mod-switcher">
          <div className="col-xs-5 mod-switcher-col">
            <label>NOT Associated:</label>
            <select name="notAssociated" className="form-control" multiple disabled={isDisabled} ref="notAssociated">
              {this.filterDocTypes(docTypes, associatedDocTypes).map((docType, i) => {
                return <option key={i} value={docType.id}>{docType.code}</option>
              })}
            </select>
          </div>
          <div className="col-xs-2 mod-switcher-controls">
            <button name="remove" className="btn btn-primary" disabled={isDisabled} onClick={this.onRemove}>&lt;&lt;</button>
            <button name="add" className="btn btn-primary" disabled={isDisabled} onClick={this.onAdd}>&gt;&gt;</button>
          </div>
          <div className="col-xs-5 mod-switcher-col">
            <label>Associated:</label>
            <select name="associated" className="form-control" multiple disabled={isDisabled} ref="associated">
              {sortBy(associatedDocTypes, 'code').map((docType, i) => {
                return <option key={i} value={docType.id}>{docType.code}</option>
              })}
            </select>
          </div>
        </div>
      </div>
      );
  }

}

export default SelectDocTypes;

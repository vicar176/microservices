import React from 'react';

export default function TemplateName ({ selectedTemplateName, isShow }) {
  return (
    <div className="row required" style={ isShow ? {'display': 'block'} : {'display': 'none'} }>
      <div className="col-xs-12">
        <label>Template Name:</label>
        <div className="inline-group">
          <label className="form-control">{selectedTemplateName}</label>
        </div>
      </div>
    </div>
  );
}

import React from 'react';
import CssLoad from 'components/Common/cssLoad';

export default function SelectDocumentType ({ onTypeSelect, docTypes, selectedDocType, isDisabled }) {
  return(
    <div className="col-sm-4 required">
      <label>Select Document Type:</label>
      <div id="templateLender" className="inline-group">
        { !docTypes.length ? <CssLoad /> :
          <select
            disabled={isDisabled}
            className="form-control"
            onChange={ e=> onTypeSelect(e.target.value) }
            value={selectedDocType ? selectedDocType.documentType.id : ''}
            name="documentType">
            <option value="">Please select type</option>
            {docTypes.map((type, i) => {
              if (type.active) {
                return <option key={i} value={type.documentType.id}>{type.documentType.code}</option>;
              }
            })}
          </select> }
      </div>
    </div>
  )
}

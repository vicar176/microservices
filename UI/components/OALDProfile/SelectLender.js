import React from 'react';

export default function SelectLender ({ originalLenders, onLenderSelect, selectedLender, isDisabled }) {
  const value = selectedLender ? selectedLender.name : '';
  return (
    <div className="col-xs-12 col-sm-4">
      <div className="form-group" style={{marginBottom: 0}}>
        <label className="control-label">Select Original Lender:</label>
        <select className="form-control" disabled={isDisabled} value={value} onChange={ e => onLenderSelect(e.target.value)} name="originalLenders">
          <option value="">Select Original Lender</option>
          {originalLenders.map((lender, i) => {
            return <option key={i} value={lender.name}>{lender.name}</option>;
          })}
        </select>
      </div>
    </div>
  );
}

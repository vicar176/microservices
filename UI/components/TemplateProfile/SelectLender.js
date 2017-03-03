import React from 'react';
import Select from 'react-select';
import CssLoad from 'components/Common/cssLoad';

export default function SelectLender (props) {
  const {
    onSelectLender,
    selectedSeller,
    lenders,
    selectedLender,
    isFetching,
    isDisabled
  } = props;

  return (
      <div className="col-sm-4 required">
        <label>Select Lender:</label>
        <div id="templateLender" className="inline-group">
          { selectedSeller ?
            isFetching ? <CssLoad /> :
            <Select
              name="lender"
              valueKey="issuerId"
              labelKey="name"
              disabled={isDisabled}
              multi={false}
              placeholder="Select Lender"
              value={selectedLender}
              onChange={onSelectLender}
              options={lenders} />
            : <select className="form-control" disabled="disabled"><option>Seller must be selected</option></select>
          }
        </div>
      </div>
    );
}

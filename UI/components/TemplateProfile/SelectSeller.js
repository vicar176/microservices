import React from 'react';
import Select from 'react-select';
import CssLoad from 'components/Common/cssLoad';

export default function SelectSeller (props) {
  const {
    onSelectSeller,
    selectedSeller,
    sellers,
    selectedDocType,
    isFetching,
    isDisabled
  } = props;

  return (
    <div className="col-sm-4 required">
      <label>Select Seller:</label>
      <div id="templateSeller" className="inline-group">
        { selectedDocType ?
          isFetching ? <CssLoad /> :
          <Select
            name="seller"
            valueKey="id"
            labelKey="name"
            disabled={isDisabled}
            multi={false}
            placeholder="Select Seller"
            value={selectedSeller}
            onChange={onSelectSeller}
            options={sellers}
          />
          : <select className="form-control" disabled="disabled"><option>Document type must be selected</option></select>
        }
      </div>
    </div>
  );
}

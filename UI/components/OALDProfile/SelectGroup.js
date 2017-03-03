import React from 'react';
import CssLoad from 'components/Common/cssLoad';

export default function SelectGroup ({ onGroupSelect, productGroups, selectedProductGroup, isFetching, isDisabled }) {
  return (
    <div className="form-group">
      <div className="row">
        <div className="col-xs-12">
          <label className="control-label">Select Product Group:</label>
          {
            isFetching ?
            <CssLoad style={{margin: '15px 0'}} /> :
            <select
              onChange={ e => onGroupSelect(productGroups.find((group) => group.code === e.target.value)) }
              value={selectedProductGroup ? selectedProductGroup.code : ''}
              name="productGroup"
              disabled={isDisabled}
              className="form-control">
              <option value="">Select Group</option>
              {productGroups.map((group, i) => {
                return <option key={i} value={group.code}>{group.name}</option>;
              })}
            </select>
          }
        </div>
      </div>
    </div>
  );
}

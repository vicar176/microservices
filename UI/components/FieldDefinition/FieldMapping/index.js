import React from 'react';
import styles from '../index.scss';

export default function FieldMapping ({ fieldMappings, isDisabled, fieldMappingOptions, onFieldMappingChange, onAddMap, onRemoveMap }) {
  return(
    <div className="row">
      <div className="col-xs-12">
        <table className={"table table-striped " + styles.accountMapping}>
          <thead>
            <tr>
              <th className="col-sm-2"><strong>Mapping type</strong></th>
              <th className="col-sm-4"><strong>Field Mapping</strong></th>
              <th className="col-sm-3"><strong>Type</strong></th>
              <th className="col-sm-3"><strong>Actions</strong></th>
            </tr>
          </thead>
          <tbody>
            {
              fieldMappings.map((fieldMapping, i) => {
                return (
                <tr key={i}>
                  <td>{fieldMapping.joinType}</td>
                  <td>
                    <select className="form-control" disabled={isDisabled} onChange={ e=> onFieldMappingChange(fieldMapping.id, fieldMappingOptions.find(mapOption => mapOption.columnName === e.target.value)) } value={fieldMapping.mappingTo} name="mappingTo">
                      <option value="">Select</option>
                      {
                        fieldMappingOptions.map((option, i) => <option key={i} value={option.columnName}>{option.columnName}</option> )
                      }
                    </select>
                    {
                      fieldMapping.mappingTo === 'ORIGINAL ACCOUNT#' &&
                      <i
                        className="glyphicon glyphicon-info-sign"
                        data-toggle="tooltip"
                        data-placement="right"
                        style={ {'marginLeft': 5, 'color': 'gray', top: '-24px', left: '80%' } }
                        data-original-title="Look up logic: <br/> 1. Compare last 4 digits only <br/> 2. If no match found, compare last 4 digits with Description Line 1 (MSDES1)">
                      </i>
                    }
                  </td>
                  <td>{fieldMapping.type}</td>
                  <td>
                    <button disabled={isDisabled || !fieldMapping.mappingTo || fieldMapping.type !== 'Alphanumeric'} title="Append" className={'btn btn-default'} style={{'margin': '2px 10px 0px 10px', width: '35%', minWidth: '10%', padding: '8px 10px'}} onClick={() => onAddMap('APPEND')}>APPEND</button>
                    <button disabled={isDisabled || !fieldMapping.mappingTo || fieldMapping.type === 'Alphanumeric'} title="Add" className={'btn btn-default'} style={{'margin': '2px 5px 0px 5px', width: '12%', minWidth: '10%', padding: '8px 10px'}} onClick={() => onAddMap('ADD')}>+</button>
                    <button disabled={isDisabled || !fieldMapping.mappingTo || fieldMapping.type === 'Alphanumeric'} title="Substract" className={'btn btn-default'} style={{'margin': '2px 5px 0px 5px', width: '12%', minWidth: '10%', padding: '8px 10px'}} onClick={() => onAddMap('SUBTRACT')}>-</button>
                    <button disabled={isDisabled || fieldMappings.length === 1} title="Remove" className={'btn btn-default'} style={{'margin': '2px 0px 0px 5px', width: '12%', minWidth: '10%', padding: '8px 5px'}} onClick={() => onRemoveMap(fieldMapping.id)}>X</button>
                  </td>
                </tr>
                )
              })
            }
          </tbody>
        </table>
      </div>
    </div>
  );

}

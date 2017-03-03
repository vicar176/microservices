import React from 'react';
import { Tooltip, OverlayTrigger} from 'react-bootstrap';

// components
import ValidationTooltip from 'components/Common/ValidationTooltip';
import FieldMapping from 'FieldMapping';
import PolicyModal from 'PolicyModal';
import FormBoolean from 'components/Common/FormBoolean';

// styles
import styles from './index.scss';

export default function FieldProfile (props) {
  const {
    fieldDefinitionProfile,
    isDisabled,
    isFieldRecerved,
    validationNameLegend,
    validationDescriptionLegend,
    validationFieldTypeLegend,
    onFieldNameChange,
    onFieldDescriptionChange,
    onFieldTypeChange,
    onAccountMappingChange,
    onActiveChange,
    onFieldMappingChange,
    onAddMap,
    onRemoveMap,
    fieldMappingOptions,
    onModalClose,
    activeModal
  } = props;

  const nameTip = <Tooltip id="nameInfoTip">1. No Spaces <br/> 2. Only  Alpha characters <br/> 3. Use Camel Case (ie. consumerName)</Tooltip>;

  return(
    <div className="form-group">
      <table className={"table table-striped " + styles.fieldDefinition}>
        <thead>
          <tr>
            <td className="col-sm-3">
              <label className="control-label" htmlFor="fieldName">Field Name <sup className="text-">*</sup>:
                <OverlayTrigger placement="right" overlay={nameTip}>
                  <i className="glyphicon glyphicon-info-sign" style={ {'marginLeft': 5} } />
                </OverlayTrigger>
              </label>
            </td>
            <td className="col-sm-2">
              <label className="control-label" htmlFor="fieldType">Field Type <sup className="text-">*</sup>:</label>
            </td>
            <td className="col-sm-5">
              <label className="control-label" htmlFor="descriptionLegend">Description <sup className="text-">*</sup>:</label>
            </td>
            {/* <td className="col-sm-1">
              <label className="control-label" htmlFor="activate" style={{whiteSpace: 'nowrap'}}>Encrypt:
                <i
                  href="#policy-modal"
                  className="glyphicon glyphicon-info-sign"
                  data-target="policy-modal"
                  onClick={onModalOpen}
                  style={ {'margin': '0 5px', cursor: 'pointer'} } />
              </label>
            </td> */}
            <td className="col-sm-2">
              <label className="control-label" htmlFor="activate">Activate:</label>
            </td>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>
              <ValidationTooltip message={validationNameLegend}>
                <input id="fieldName" className="form-control" disabled={isDisabled || isFieldRecerved} value={fieldDefinitionProfile.fieldName} onChange={e => onFieldNameChange(e.target.value.trim())} />
              </ValidationTooltip>
            </td>
            <td>
              <ValidationTooltip message={validationFieldTypeLegend}>
              <select id="fieldType" className="form-control" disabled={isDisabled || isFieldRecerved} onChange={ e=> onFieldTypeChange(e.target.value) } value={fieldDefinitionProfile.fieldType} name="fieldType">
                <option value="">Select Field Type</option>
                <option value="alphanumeric">Alphanumeric</option>
                <option value="number">Numeric</option>
                <option value="date">Date</option>
              </select>
              </ValidationTooltip>
            </td>
            <td>
              <ValidationTooltip message={validationDescriptionLegend}>
                <textarea id="descriptionLegend" className="form-control" disabled={isDisabled} rows="1" value={fieldDefinitionProfile.fieldDescription} onChange={e => onFieldDescriptionChange(e.target.value)} />
              </ValidationTooltip>
            </td>
            {/* <td>
              <select id="encrypted" className="form-control" disabled={isDisabled || fieldDefinitionProfile.encrypt} onChange={onEncryptedChange} value={fieldDefinitionProfile.encrypt} name="encrypted">
                <option value={false}>No</option>
                <option value={true}>Yes</option>
              </select>
            </td> */}
            <td>
              <select id="activate" className="form-control" disabled={isDisabled || isFieldRecerved} onChange={onActiveChange} value={fieldDefinitionProfile.active} name="active">
                <option value={true}>Yes</option>
                <option value={false}>No</option>
              </select>
            </td>
          </tr>
          <tr className={styles.mappingCheck} style={{display: 'none'}}>
            <td colSpan="4">
              <FormBoolean
                isActive={fieldDefinitionProfile.accountMapping}
                onChange={onAccountMappingChange}
                title="Account Mapping" />
            </td>
          </tr>
        </tbody>
        <tfoot className={fieldDefinitionProfile.accountMapping || 'hidden'}>
          <tr>
            <td colSpan="4">
              <FieldMapping
                isDisabled={isDisabled}
                fieldDefinitionProfileId={fieldDefinitionProfile.id}
                fieldMappings={fieldDefinitionProfile.databaseMapping}
                fieldMappingOptions={fieldMappingOptions}
                onFieldMappingChange={onFieldMappingChange}
                onAddMap={onAddMap}
                onRemoveMap={onRemoveMap}
                />
            </td>
          </tr>
        </tfoot>
      </table>
      { isFieldRecerved &&
        <p>
          <small><i className="glyphicon glyphicon-exclamation-sign text-warning" style={{marginLeft: '.5em'}}/> This field is reserved in the system. You may only update the description field.</small>
        </p> }
      <PolicyModal activeModal={activeModal} onClose={onModalClose} />
    </div>
  );
}

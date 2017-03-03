import React from 'react';

// Components
import SelectFieldDefinition from 'SelectFieldDefinition';
import { Tooltip, OverlayTrigger} from 'react-bootstrap';

// styles
import styles from './index.scss';

export default function FieldDefinitionsRow (props) {
  const {
    currFieldDefinition,
    fieldDefinitionsDropdown,
    isFetchingFieldDefinitions,
    onRemoveFieldDefinition,
    onFieldDefinitionDropdownChange,
    getValidationFieldName,
    onSelectRequirementLevel,
    isFieldsActive,
    fieldNameRequired
  } = props;
  const { fieldDefinition } = currFieldDefinition;
  const fieldName = fieldDefinition ? fieldDefinition.fieldName : '';
  const lowFieldName = fieldName.toLocaleLowerCase();
  const isFieldRecerved = lowFieldName === 'statementbalance' || lowFieldName === 'statementdate';
  const nameTip = <Tooltip id="tooltip-reserved">This field cannot be removed, it is reserved.</Tooltip>;

  // Account verified not for next release
  // <td className="text-center">{currFieldDefinition.verification ? 'Yes' : 'No'}</td>

  return (
    <tr>
      <td>
        <SelectFieldDefinition
          isFieldRecerved={isFieldRecerved}
          getValidationFieldName={getValidationFieldName}
          selectedFieldOption={fieldName}
          fieldDefinitionsDropdown={fieldDefinitionsDropdown}
          isFetchingFieldDefinitions={isFetchingFieldDefinitions}
          onFieldDefinitionDropdownChange={onFieldDefinitionDropdownChange}
          isFieldsActive={isFieldsActive}
          fieldNameRequired={fieldNameRequired} />
      </td>
      <td>{fieldDefinition ? fieldDefinition.fieldDescription : ''}</td>
      <td>
        <select
          id="required"
          className="form-control"
          name="required"
          disabled={isFieldRecerved || isFieldsActive}
          value={currFieldDefinition.required || isFieldRecerved ? 'true' : 'false'}
          onChange={e => onSelectRequirementLevel(fieldDefinition.fieldName, e.target.value)}>
          <option value={false}>Optional</option>
          <option value={true}>Required</option>
        </select>
      </td>
      <td style={{textAlign: 'center', width: 120}}>
        {isFieldRecerved? <OverlayTrigger placement="top" overlay={nameTip}>
          <span className={'glyphicon glyphicon-exclamation-sign ' + styles.sign}></span>
          </OverlayTrigger>:
        <button
          className="btn btn-primary"
          disabled={isFieldsActive}
          onClick={() => onRemoveFieldDefinition(fieldDefinition.fieldName)}>Remove</button>}
      </td>
    </tr>
  )
}

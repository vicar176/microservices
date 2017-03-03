import React from 'react';
import { Button } from 'react-bootstrap';
import styles from './index.scss';
import FieldDefinitionsRow from 'FieldDefinitionsRow';

export default function FieldDefinitionsGrid (props) {
  const {
    isActive,
    documentFieldDefinitions,
    fieldDefinitionsDropdown,
    isFetchingFieldDefinitions,
    onRemoveFieldDefinition,
    onFieldDefinitionDropdownChange,
    getValidationFieldName,
    onSelectRequirementLevel,
    isFieldsActive,
    fieldNameRequired
  } = props;

  // Account verified not for next release
  // <th>For Verification</th>

  return (
    <table className={'table table-striped table-bordered ' + styles.fielDefinitionsGrid} style={{display: ( isActive || documentFieldDefinitions.length === 0)? 'table' : 'none'}}>
      <thead>
        <tr>
          <th width="250">Field Name *</th>
          <th>Field Description</th>
          <th>Requirement Level *</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        { documentFieldDefinitions &&
          documentFieldDefinitions.length === 0 ?
          <tr><td colSpan="5">No fields Added.</td></tr> :

          documentFieldDefinitions.map((currFieldDefinition, i) =>
            <FieldDefinitionsRow
              key={i}
              getValidationFieldName={getValidationFieldName}
              currFieldDefinition={currFieldDefinition}
              fieldDefinitionsDropdown={fieldDefinitionsDropdown}
              isFetchingFieldDefinitions={isFetchingFieldDefinitions}
              onRemoveFieldDefinition={onRemoveFieldDefinition}
              onFieldDefinitionDropdownChange={onFieldDefinitionDropdownChange}
              onSelectRequirementLevel={onSelectRequirementLevel}
              isFieldsActive={isFieldsActive}
              fieldNameRequired={fieldNameRequired} />
          )}
      </tbody>
    </table>
  );
}

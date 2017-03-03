import React from 'react';
import CssLoad from 'components/Common/cssLoad';
import ValidationTooltip from 'components/Common/ValidationTooltip';
export default function SelectFieldDefinition (props) {
  const {
    isFieldRecerved,
    selectedFieldOption,
    fieldDefinitionsDropdown,
    isFetchingFieldDefinitions,
    getValidationFieldName,
    onFieldDefinitionDropdownChange,
    isFieldsActive,
    fieldNameRequired
  } = props;

  function getValidationSelect () {
    return(
      <ValidationTooltip message={fieldNameRequired} >
       { renderSelect() }
      </ValidationTooltip>
    );
  }

  function renderSelect () {
    return(
      <select
        name="fieldDefinitionsDropdown"
        className="form-control"
        disabled={isFieldRecerved || isFieldsActive}
        value={selectedFieldOption}
        onChange={e => onFieldDefinitionDropdownChange(selectedFieldOption, e.target.value)}>
        <option value="">Select</option>
        {fieldDefinitionsDropdown.map((field, i) =>
            (field.active)?<option key={i} value={field.id}>{field.id} {field.active}</option>:""
        )}
      </select>
    );
  }

  function renderTemplate () {
    return(
      selectedFieldOption ? renderSelect() : getValidationSelect()
   );
  }


  return(
    <div className="dropdown-container">
      { isFetchingFieldDefinitions ? <CssLoad /> : renderTemplate() }
    </div>
  );
}

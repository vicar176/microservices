import React from 'react';
import { sortBy } from 'lodash';

export default function SelectTemplate ({onSelectTemplate, selectedTemplateName, isShow, templates, isDisabled}) {

  let id = '';
  if (selectedTemplateName) {
    const template = templates.find( template => template.name === selectedTemplateName);
    id = template ? template.id : '';
  }

  return (
    <div className="row required" style={ isShow? {'display': 'block'} : {'display': 'none'} }>
      <div className="col-xs-12">
        <label>Select Template:</label>
        <select
          disabled={isDisabled}
          className="form-control"
          name="template"
          value={id}
          onChange={e=> onSelectTemplate(e.target.value)}
        >
          {
            templates.length? <option value="">Select Template</option> : <option value="">No Template</option>
          }
          {sortBy(templates, 'name').map( (template, i) => {
            return <option key={i} value={template.id}>{template.name}</option>
          })}
        </select>
      </div>
    </div>
  );
}
